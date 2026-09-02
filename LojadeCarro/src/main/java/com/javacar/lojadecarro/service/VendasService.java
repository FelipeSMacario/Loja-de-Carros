package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.VendaRequest;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Venda;
import com.javacar.lojadecarro.enums.StatusVenda;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.mapper.VendasMapper;
import com.javacar.lojadecarro.repository.VendasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.javacar.lojadecarro.enums.Entidade.VENDA;
import static com.javacar.lojadecarro.enums.StatusVenda.EM_ANDAMENTO;
import static com.javacar.lojadecarro.utils.Utils.ZONE;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendasService {
    private final VendasRepository vendasRepository;
    private final VendasMapper vendasMapper;
    private final UsuarioService usuarioService;
    private final VeiculoService veiculoService;

    @PreAuthorize("hasRole('USUARIO')")
    @Transactional
    public VendaResponse criar(VendaRequest request, Long idComprador) {
        validarVeiculoJaPossuiVenda(request.veiculoId());
        var veiculo = veiculoService.buscaVeiculo(request.veiculoId());

        var comprador = buscarEValidarComprador(idComprador, veiculo.getVendedor().getId());

        var vendaEntity = vendasMapper.toEntity(request);
        vendaEntity.setValorVenda(veiculo.getValor());
        vendaEntity.setDataVenda(LocalDateTime.now(ZONE));
        vendaEntity.setVeiculo(veiculo);
        vendaEntity.setComprador(comprador);
        vendaEntity.setVendedor(veiculo.getVendedor());
        veiculo.reservarVeiculo();
        vendaEntity.setStatusVenda(EM_ANDAMENTO);
        var venda = vendasRepository.save(vendaEntity);

        return vendasMapper.toResponse(venda);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<VendaResponse> listar(Pageable pageable, StatusVenda statusVenda) {
        if (statusVenda == null) {
            return vendasRepository.findAll(pageable)
                    .map(vendasMapper::toResponse);
        }

        return vendasRepository.findByStatusVenda(statusVenda, pageable)
                .map(vendasMapper::toResponse);
    }


    private Usuario buscarEValidarComprador(Long idComprador, Long idVendedor) {
        var comprador = usuarioService.buscaUsuarioAtivo(idComprador);

        if (comprador.getId().equals(idVendedor)) {
            throw new BusinessException("O comprador não pode ser o próprio vendedor.");
        }

        return comprador;
    }

    private void validarVeiculoJaPossuiVenda(Long idVeiculo) {

        if (vendasRepository.existsByVeiculoIdAndStatusVenda(idVeiculo, EM_ANDAMENTO)) {
            throw new BusinessException("O veículo já possui uma venda cadastrada.");
        }

    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@vendaAuthorization.relacionadoAVenda(#idVenda, authentication)"
    )
    @Transactional(readOnly = true)
    public VendaResponse buscarPorId(Long idVenda) {
        return vendasMapper.toResponse(buscarVenda(idVenda));
    }

    @PreAuthorize("hasRole('USUARIO')")
    @Transactional(readOnly = true)
    public Page<VendaResponse> buscarMinhasCompras(Long idComprador, Pageable pageable, StatusVenda statusVenda) {
        if (statusVenda == null) {
            return vendasRepository.
                    findByComprador_Id(idComprador, pageable)
                    .map(vendasMapper::toResponse);
        }
        return vendasRepository.
                findByComprador_IdAndStatusVenda(idComprador, pageable, statusVenda)
                .map(vendasMapper::toResponse);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @Transactional(readOnly = true)
    public Page<VendaResponse> buscarMinhasVendas(Long idVendedor, Pageable pageable, StatusVenda statusVenda) {
        if (statusVenda == null) {
            return vendasRepository.
                    findByVendedor_Id(idVendedor, pageable)
                    .map(vendasMapper::toResponse);
        }
        return vendasRepository.
                findByVendedor_IdAndStatusVenda(idVendedor, pageable, statusVenda)
                .map(vendasMapper::toResponse);
    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@vendaAuthorization.ehVendedor(#idVenda, authentication)"
    )
    @Transactional
    public VendaResponse cancelarVenda(Long idVenda) {
        var venda = buscarVenda(idVenda);
        venda.cancelarVenda();
        venda.getVeiculo().disponibilizarAnuncio();

        return vendasMapper.toResponse(venda);
    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
                    "@vendaAuthorization.relacionadoAVenda(#idVenda, authentication)"
    )
    @Transactional
    public VendaResponse concluirVenda(Long idVenda) {
        var venda = buscarVenda(idVenda);
        venda.concluirVenda();
        venda.getVeiculo().concluirVeiculo();

        return vendasMapper.toResponse(venda);
    }

    private Venda buscarVenda(Long idVenda) {
        return vendasRepository.findById(idVenda)
                .orElseThrow(() -> new NotFoundException(VENDA, idVenda));
    }
}
