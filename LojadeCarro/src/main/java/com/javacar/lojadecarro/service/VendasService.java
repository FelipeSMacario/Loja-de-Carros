package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.VendaRequest;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.mapper.VendasMapper;
import com.javacar.lojadecarro.repository.VendasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.javacar.lojadecarro.enums.StatusVeiculo.VENDIDO;
import static com.javacar.lojadecarro.utils.Utils.ZONE;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendasService {
    private final VendasRepository vendasRepository;
    private final VendasMapper vendasMapper;
    private final UsuarioService usuarioService;
    private final VeiculoService veiculoService;

    @Transactional
    public VendaResponse criar(VendaRequest request) {
        var veiculo = veiculoService.buscaVeiculo(request.veiculoId());

        var vendedor = validaVendedor(request.vendedorId(), veiculo);
        var comprador = validaComprador(request.compradorId(), vendedor);

        var vendaEntity = vendasMapper.toEntity(request);
        vendaEntity.setDataVenda(LocalDateTime.now(ZONE));
        vendaEntity.setVeiculo(veiculo);
        vendaEntity.setComprador(comprador);
        vendaEntity.setVendedor(vendedor);
        veiculo.alterarStatus(VENDIDO);
        var venda = vendasRepository.save(vendaEntity);

        return vendasMapper.toResponse(venda);

    }

    public Page<VendaResponse> listar(Pageable pageable) {
        return vendasRepository.findAll(pageable)
                .map(vendasMapper::toResponse);
    }

    private Usuario validaVendedor(Long idVendedor, Veiculo veiculo) {
        var vendedor = usuarioService.buscaUsuario(idVendedor);

        if (!vendedor.getId().equals(veiculo.getVendedor().getId()))
            throw new BusinessException("O vendedor informado não é o proprietário do veículo.");
        return vendedor;
    }

    private Usuario validaComprador(Long idComprador, Usuario vendedor) {
        var comprador = usuarioService.buscaUsuario(idComprador);

        if (comprador.getId().equals(vendedor.getId())) {
            throw new BusinessException("O comprador não pode ser o próprio vendedor.");
        }

        return comprador;
    }

}
