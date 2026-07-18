package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.VendasRequest;
import com.javacar.lojadecarro.dto.response.VendasResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.mapper.VendasMapper;
import com.javacar.lojadecarro.repository.VendasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public VendasResponse create(VendasRequest request) {
        log.debug("Inicio da createComprasService com a response: {}", request);
        var veiculo = veiculoService.buscaVeiculo(request.veiculoId());
        var venda = vendasMapper.toEntity(request);

        var vendedor = validaVendedor(request.vendedorId(), veiculo);
        var comprador = validaComprador(request.compradorId(), vendedor);

        venda.setDataVenda(LocalDateTime.now(ZONE));
        venda.setVeiculo(veiculo);
        venda.setComprador(comprador);
        venda.setVendedor(vendedor);
        veiculo.alterarStatus(VENDIDO);
        var comprasResponse = vendasRepository.save(venda);
        log.info("Compra salva com sucesso!");

        return vendasMapper.toResponse(comprasResponse);

    }

    public List<VendasResponse> listarVendas() {
        log.info("Inicio da listarComprasService");
        return vendasRepository.findAll()
                .stream().map(vendasMapper::toResponse)
                .toList();
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
