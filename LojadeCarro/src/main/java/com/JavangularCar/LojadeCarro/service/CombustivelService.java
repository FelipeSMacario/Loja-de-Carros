package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.CombustivelRequest;
import com.JavangularCar.LojadeCarro.dto.response.CombustivelResponse;
import com.JavangularCar.LojadeCarro.entity.Combustivel;
import com.JavangularCar.LojadeCarro.exception.CombustivelException;
import com.JavangularCar.LojadeCarro.mapper.CombustivelMapper;
import com.JavangularCar.LojadeCarro.repository.CombustivelRepositor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CombustivelService {

    private final CombustivelRepositor combustivelRepositor;
    private final CombustivelMapper combustivelMapper;

    public CombustivelResponse createCombustivel(CombustivelRequest request) {
        log.debug("Inicio da createCombustivelService com a response: {}", request);
        var combustivelEntity = combustivelMapper.toEntity(request);
        var combustivelResponse = combustivelRepositor.save(combustivelEntity);

        return combustivelMapper.toRecord(combustivelResponse);
    }

    public List<CombustivelResponse> listarCombustivel() {
        log.info("Inicio da listarCarroceriaService");
        return combustivelRepositor.findAll().stream()
                .map(combustivelMapper::toRecord)
                .toList();
    }

    public CombustivelResponse findCombustivelById(Long id) {
        log.info("Inicio da findCombustivelByIdService com id: {}", id);
        return combustivelRepositor.findById(id)
                .map(combustivelMapper::toRecord)
                .orElseThrow(() -> new CombustivelException(id));
    }

    public CombustivelResponse updateCombustivel(CombustivelRequest request, Long id) {
        log.info("Inicio da updateCombustivelService com o id: {}", id);
        return combustivelRepositor.findById(id)
                .map(record -> {
                    record.setNome(request.nome());
                    var update = combustivelRepositor.save(record);
                    log.info("Combustível atualizado com sucesso!");
                    return combustivelMapper.toRecord(update);
                }).orElseThrow(() -> new CombustivelException(id));
    }

    public void deleteCombustivel(Long id) {
        log.info("Inicio da deleteCombustivelService com o id: {}", id);
        var combustivelEntity = combustivelRepositor.findById(id)
                .orElseThrow(() -> new CombustivelException(id));

        combustivelRepositor.deleteById(combustivelEntity.getId());
    }

    public Combustivel buscaCombustivel(Long id) {
        log.info("Inicio da buscaCombustivelService com id: {}", id);
        return combustivelRepositor.findById(id)
                .orElseThrow(() -> new CombustivelException(id));
    }
}
