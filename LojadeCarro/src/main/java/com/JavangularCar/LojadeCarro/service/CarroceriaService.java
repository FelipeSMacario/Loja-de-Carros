package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.CarroceriaRequest;
import com.JavangularCar.LojadeCarro.dto.response.CarroceriaResponse;
import com.JavangularCar.LojadeCarro.exception.CarroceriaException;
import com.JavangularCar.LojadeCarro.mapper.CarroceriaMapper;
import com.JavangularCar.LojadeCarro.repository.CarroceriaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CarroceriaService {
    @Autowired
    CarroceriaRepository carroceriaRepository;

    @Autowired
    CarroceriaMapper carroceriaMapper;

    public CarroceriaResponse createCarroceria(CarroceriaRequest carroceriaRequest) {
        log.info("Inicio da createCarroceria");
        var carroceriaEntity = carroceriaMapper.toEntity(carroceriaRequest);
        var carroceriaResponse = carroceriaRepository.save(carroceriaEntity);
        log.info("Carroceria salva com sucesso!");

        return carroceriaMapper.toRecord(carroceriaResponse);
    }

    public List<CarroceriaResponse> listarCarroceria() {
        log.info("Inicio da listarCarroceria");
        return carroceriaRepository
                .findAll()
                .stream()
                .map(carroceriaMapper::toRecord)
                .toList();
    }

    public CarroceriaResponse findCarroceriaById(Long id) {
        log.info("Inicio da findCarroceriaById com id: {}", id);
        var carroceriaEntity = carroceriaRepository.findById(id)
                .stream().findFirst()
                .orElseThrow(() -> new CarroceriaException(id));

        return carroceriaMapper.toRecord(carroceriaEntity);

    }

    public CarroceriaResponse updateCarroceria(CarroceriaRequest carroceriaRequest, Long id) {
        log.info("Inicio da updateCarroceria com o id: {}", id);
        return carroceriaRepository.findById(id)
                .map(record -> {
                    record.setNome(carroceriaRequest.nome());
                    var update = carroceriaRepository.save(record);
                    log.warn("Carroceria atualizado com sucesso!");
                    return carroceriaMapper.toRecord(update);
                }).orElseThrow(() -> new CarroceriaException(id));
    }

    public void deleteCarroceria(Long id) {
        log.info("Inicio da deleteCarroceria com o id: {}", id);
        var carroceriaEntity = carroceriaRepository.findById(id)
                .orElseThrow(() -> new CarroceriaException(id));

        carroceriaRepository.delete(carroceriaEntity);
    }
}
