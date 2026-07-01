package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.ModeloRequest;
import com.JavangularCar.LojadeCarro.dto.response.ModeloResponse;
import com.JavangularCar.LojadeCarro.entity.Modelo;
import com.JavangularCar.LojadeCarro.exception.ModeloException;
import com.JavangularCar.LojadeCarro.mapper.ModeloMapper;
import com.JavangularCar.LojadeCarro.repository.MoleloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeloService {
    private final MoleloRepository moleloRepository;

    private final ModeloMapper modeloMapper;

    public ModeloResponse createModelo(ModeloRequest request) {
        log.debug("Inicio da createModeloService com a response: {}", request);
        var carroceriaEntity = modeloMapper.toEntity(request);
        var carroceriaResponse = moleloRepository.save(carroceriaEntity);

        log.info("Modelo salva com sucesso!");

        return modeloMapper.toRecord(carroceriaResponse);
    }

    public List<ModeloResponse> listarModelo() {
        log.info("Inicio da listarModeloService");
        return moleloRepository.findAll()
                .stream()
                .map(modeloMapper::toRecord)
                .toList();
    }

    public ModeloResponse findModeloById(Long id) {
        log.info("Inicio da findModeloByIdService com id: {}", id);
        return moleloRepository.findById(id)
                .map(modeloMapper::toRecord)
                .orElseThrow(() -> new ModeloException(id));
    }

    public ModeloResponse updateModelo(ModeloRequest request, Long id) {
        log.info("Inicio da updateModeloService com o id: {}", id);
        return moleloRepository.findById(id)
                .map(record -> {
                    record.setNome(request.nome());
                    var update = moleloRepository.save(record);
                    log.info("Modelo atualizado com sucesso!");
                    return modeloMapper.toRecord(update);
                }).orElseThrow(() -> new ModeloException(id));
    }

    public void deleteModelo(Long id) {
        log.info("Inicio da deleteModeloService com o id: {}", id);
        var modeloEntity = moleloRepository.findById(id)
                .orElseThrow(() -> new ModeloException(id));

        moleloRepository.deleteById(modeloEntity.getId());

    }

    public Modelo buscaModelo(Long id) {
        log.info("Inicio da buscaModeloService com o id: {}", id);
        return moleloRepository.findById(id)
                .orElseThrow(() -> new ModeloException(id));
    }
}
