package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.CombustivelRequest;
import com.JavangularCar.LojadeCarro.dto.response.CombustivelResponse;
import com.JavangularCar.LojadeCarro.entity.Combustivel;
import com.JavangularCar.LojadeCarro.exception.CombustivelException;
import com.JavangularCar.LojadeCarro.mapper.CombustivelMapper;
import com.JavangularCar.LojadeCarro.repository.CombustivelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CombustivelService {

    private final CombustivelRepository combustivelRepository;
    private final CombustivelMapper combustivelMapper;

    public CombustivelResponse createCombustivel(CombustivelRequest request) {
        log.debug("Inicio da createCombustivelService com a response: {}", request);
        var combustivelEntity = combustivelMapper.toEntity(request);
        var combustivelResponse = combustivelRepository.save(combustivelEntity);

        return combustivelMapper.toResponse(combustivelResponse);
    }

    public List<CombustivelResponse> listarCombustivel() {
        log.info("Inicio da listarCarroceriaService");
        return combustivelRepository.findAll().stream()
                .map(combustivelMapper::toResponse)
                .toList();
    }

    public CombustivelResponse findCombustivelById(Long id) {
        log.info("Inicio da findCombustivelByIdService com id: {}", id);
        return combustivelRepository.findById(id)
                .map(combustivelMapper::toResponse)
                .orElseThrow(() -> new CombustivelException(id));
    }

    public CombustivelResponse updateCombustivel(CombustivelRequest request, Long id) {
        log.info("Inicio da updateCombustivelService com o id: {}", id);
        return combustivelRepository.findById(id)
                .map(record -> {
                    record.setNome(request.nome());
                    var update = combustivelRepository.save(record);
                    log.info("Combustível atualizado com sucesso!");
                    return combustivelMapper.toResponse(update);
                }).orElseThrow(() -> new CombustivelException(id));
    }

    public void deleteCombustivel(Long id) {
        log.info("Inicio da deleteCombustivelService com o id: {}", id);
        var combustivelEntity = combustivelRepository.findById(id)
                .orElseThrow(() -> new CombustivelException(id));

        combustivelRepository.deleteById(combustivelEntity.getId());
    }

    public Combustivel buscaCombustivel(Long id) {
        log.info("Inicio da buscaCombustivelService com id: {}", id);
        return combustivelRepository.findById(id)
                .orElseThrow(() -> new CombustivelException(id));
    }
}
