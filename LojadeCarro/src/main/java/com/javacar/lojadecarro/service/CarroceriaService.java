package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import com.javacar.lojadecarro.exception.CarroceriaException;
import com.javacar.lojadecarro.mapper.CarroceriaMapper;
import com.javacar.lojadecarro.repository.CarroceriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarroceriaService {
    private final CarroceriaRepository carroceriaRepository;

    private final CarroceriaMapper carroceriaMapper;

    public CarroceriaResponse createCarroceria(CarroceriaRequest request) {
        log.debug("Inicio da createCarroceriaService com a response: {}", request);
        var carroceriaEntity = carroceriaMapper.toEntity(request);
        var carroceriaResponse = carroceriaRepository.save(carroceriaEntity);
        log.info("Carroceria salva com sucesso!");

        return carroceriaMapper.toResponse(carroceriaResponse);
    }

    public List<CarroceriaResponse> listarCarroceria() {
        log.info("Inicio da listarCarroceriaService");
        return carroceriaRepository
                .findAll()
                .stream()
                .map(carroceriaMapper::toResponse)
                .toList();
    }

    public CarroceriaResponse findCarroceriaById(Long id) {
        log.info("Inicio da findCarroceriaByIdService com id: {}", id);
        return carroceriaRepository.findById(id)
                .map(carroceriaMapper::toResponse)
                .orElseThrow(() -> new CarroceriaException(id));
    }

    public CarroceriaResponse updateCarroceria(CarroceriaRequest carroceriaRequest, Long id) {
        log.info("Inicio da updateCarroceriaService com o id: {}", id);
        return carroceriaRepository.findById(id)
                .map(carroceriaEntity -> {
                    carroceriaEntity.setNome(carroceriaRequest.nome());
                    var update = carroceriaRepository.save(carroceriaEntity);
                    log.info("Carroceria atualizado com sucesso!");
                    return carroceriaMapper.toResponse(update);
                }).orElseThrow(() -> new CarroceriaException(id));
    }

    public void deleteCarroceria(Long id) {
        log.info("Inicio da deleteCarroceriaService com o id: {}", id);
        var carroceriaEntity = carroceriaRepository.findById(id)
                .orElseThrow(() -> new CarroceriaException(id));

        carroceriaRepository.deleteById(carroceriaEntity.getId());
    }

    public Carroceria buscaCarroceria(Long id) {
        log.info("Inicio da buscaCarroceriaService com id: {}", id);
        return carroceriaRepository.findById(id)
                .orElseThrow(() -> new CarroceriaException(id));
    }
}
