package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.CoresRequest;
import com.JavangularCar.LojadeCarro.dto.response.CoresResponse;
import com.JavangularCar.LojadeCarro.entity.Cores;
import com.JavangularCar.LojadeCarro.exception.CoresException;
import com.JavangularCar.LojadeCarro.mapper.CoresMapper;
import com.JavangularCar.LojadeCarro.repository.CoresRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoresService {

    private final CoresRepository coresRepository;

    private final CoresMapper coresMapper;

    public CoresResponse createCores(@RequestBody CoresRequest request) {
        log.debug("Inicio da createCoresService com a response: {}", request);
        var coresEntity = coresMapper.toEntity(request);
        var coresResponse = coresRepository.save(coresEntity);
        log.info("Cores salva com sucesso!");

        return coresMapper.toResponse(coresResponse);
    }

    public List<CoresResponse> listarCores() {
        log.info("Inicio da listarCoresService");
        return coresRepository.findAll()
                .stream()
                .map(coresMapper::toResponse)
                .toList();
    }

    public CoresResponse findCoresById(Long id) {
        log.info("Inicio da findCoresByIdService com id: {}", id);
        return coresRepository.findById(id)
                .map(coresMapper::toResponse)
                .orElseThrow(() -> new CoresException(id));
    }

    public CoresResponse updateCores(CoresRequest request, Long id) {
        log.info("Inicio da updateCoresService com o id: {}", id);
        return coresRepository.findById(id)
                .map(record -> {
                    record.setNome(request.nome());
                    var update = coresRepository.save(record);
                    return coresMapper.toResponse(update);
                }).orElseThrow(() -> new CoresException(id));
    }

    public void deleteCores(Long id) {
        log.info("Inicio da deleteCoresService com o id: {}", id);
        var corEntity = coresRepository.findById(id).orElseThrow(() -> new CoresException(id));
        coresRepository.deleteById(corEntity.getId());
    }

    public Cores buscaCores(Long id) {
        log.info("Inicio da buscaCoresService com o id: {}", id);
        return  coresRepository.findById(id)
                .orElseThrow(() -> new CoresException(id));
    }

}
