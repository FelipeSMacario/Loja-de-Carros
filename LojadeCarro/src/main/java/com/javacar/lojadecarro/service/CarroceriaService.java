package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.mapper.CarroceriaMapper;
import com.javacar.lojadecarro.repository.CarroceriaRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.CARROCERIA;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarroceriaService {
    private final CarroceriaRepository carroceriaRepository;
    private final EntityValidation entityValidation;
    private final CarroceriaMapper carroceriaMapper;

    @Transactional
    public CarroceriaResponse criar(CarroceriaRequest request) {
        var carroceriaEntity = carroceriaMapper.toEntity(request);
        var carroceriaResponse = carroceriaRepository.save(carroceriaEntity);

        return carroceriaMapper.toResponse(carroceriaResponse);
    }

    public List<CarroceriaResponse> listar(StatusFiltro status) {
        var listaCarroceria = switch (status) {
            case TODAS -> carroceriaRepository.findAll();
            case INATIVAS -> carroceriaRepository.findByAtivo(false);
            case ATIVAS -> carroceriaRepository.findByAtivo(true);
        };

        return listaCarroceria.stream().map(carroceriaMapper::toResponse).toList();
    }

    public CarroceriaResponse buscaPorId(Long id) {
        var carroceria = buscaCarroceria(id);
        return carroceriaMapper.toResponse(carroceria);
    }

    @Transactional
    public CarroceriaResponse atualizar(CarroceriaRequest carroceriaRequest, Long id) {
        var carroceria = buscaCarroceria(id);
        carroceriaMapper.toUpdate(carroceriaRequest, carroceria);
        return carroceriaMapper.toResponse(carroceria);
    }

    @Transactional
    public CarroceriaResponse alterarStatus(Long id, StatusRequest request) {
        var carroceria = buscaCarroceria(id);
        carroceria.alterarStatus(request.ativo());
        return carroceriaMapper.toResponse(carroceria);
    }

    public Carroceria buscaCarroceria(Long id) {
        return entityValidation.obterOuLancarErro(carroceriaRepository.findById(id), CARROCERIA, id);
    }
}
