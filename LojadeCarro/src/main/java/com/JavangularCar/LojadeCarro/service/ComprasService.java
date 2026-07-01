package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.ComprasRequest;
import com.JavangularCar.LojadeCarro.dto.response.ComprasResponse;
import com.JavangularCar.LojadeCarro.mapper.ComprasMapper;
import com.JavangularCar.LojadeCarro.repository.ComprasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComprasService {
    private final ComprasRepository comprasRepository;
    private final ComprasMapper comprasMapper;
    private final CarroService carroService;

    public ComprasResponse createCompras(ComprasRequest request) {
        log.debug("Inicio da createComprasService com a response: {}", request);
        var compraEntity = comprasMapper.toEntity(request);

        this.marcaVendido(carroService.buscaCarro(request.carroId()).getId());

        var comprasResponse = comprasRepository.save(compraEntity);
        log.info("Compra salva com sucesso!");

        return comprasMapper.toResponse(comprasResponse);

    }

    public List<ComprasResponse> listarCompras() {
        log.info("Inicio da listarComprasService");
        return comprasRepository.findAll()
                .stream().map(comprasMapper::toResponse)
                .toList();
    }

    public void marcaVendido(Long idCarro) {
        comprasRepository.marcaVendido(idCarro);
    }
}
