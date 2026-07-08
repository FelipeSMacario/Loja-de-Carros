package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.ComprasRequest;
import com.javacar.lojadecarro.dto.response.ComprasResponse;
import com.javacar.lojadecarro.mapper.ComprasMapper;
import com.javacar.lojadecarro.repository.ComprasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComprasService {
    private final ComprasRepository comprasRepository;
    private final ComprasMapper comprasMapper;
    private final UsuarioService usuarioService;
    private final CarroService carroService;

    @Transactional
    public ComprasResponse createCompras(ComprasRequest request) {
        log.debug("Inicio da createComprasService com a response: {}", request);
        var compraEntity = comprasMapper.toEntity(request);
        compraEntity.setDataVenda(Instant.now());
        compraEntity.setCarro(carroService.buscaCarro(request.carroId()));
        compraEntity.setComprador(usuarioService.buscaUsuario(request.compradorId()));
        compraEntity.setVendedor(usuarioService.buscaUsuario(request.vendedorId()));
        var comprasResponse = comprasRepository.save(compraEntity);
        log.info("Compra salva com sucesso!");

        this.marcarComoVendido(request.carroId());
        return comprasMapper.toResponse(comprasResponse);

    }

    public List<ComprasResponse> listarCompras() {
        log.info("Inicio da listarComprasService");
        return comprasRepository.findAll()
                .stream().map(comprasMapper::toResponse)
                .toList();
    }

    public void marcarComoVendido(Long idCarro) {
        comprasRepository.marcaVendido(idCarro);
    }
}
