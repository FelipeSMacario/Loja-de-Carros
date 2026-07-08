package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.CombustivelRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import com.javacar.lojadecarro.exception.CombustivelException;
import com.javacar.lojadecarro.mapper.CombustivelMapper;
import com.javacar.lojadecarro.repository.CombustivelRepository;
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
                .map(combustivelEntity -> {
                    combustivelEntity.setNome(request.nome());
                    var update = combustivelRepository.save(combustivelEntity);
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
