package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.mapper.CarroceriaMapper;
import com.javacar.lojadecarro.repository.CarroceriaRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import com.javacar.lojadecarro.validation.StatusValidation;
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
    private final StatusValidation statusValidation;

    public CarroceriaResponse createCarroceria(CarroceriaRequest request) {
        log.debug("Inicio da createCarroceriaService com a request: {}", request);
        var carroceriaEntity = carroceriaMapper.toEntity(request);
        var carroceriaResponse = carroceriaRepository.save(carroceriaEntity);
        log.info("Carroceria salva com sucesso!");

        return carroceriaMapper.toResponse(carroceriaResponse);
    }

    public List<CarroceriaResponse> listarCarroceria(StatusFiltro status) {
        log.info("Listando carrocerias. Status: {}", status);

        var listaCarroceria =
                switch (status) {
                    case TODAS -> carroceriaRepository.findAll();
                    case INATIVAS -> carroceriaRepository.findByAtivo(false);
                    case ATIVAS -> carroceriaRepository.findByAtivo(true);
                };

        return listaCarroceria
                .stream()
                .map(carroceriaMapper::toResponse)
                .toList();
    }

    public CarroceriaResponse findCarroceriaById(Long id) {
        log.info("Inicio da findCarroceriaByIdService com id: {}", id);
        return carroceriaRepository.findById(id)
                .map(carroceriaMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(CARROCERIA, id));
    }

    public CarroceriaResponse updateCarroceria(CarroceriaRequest carroceriaRequest, Long id) {
        log.info("Inicio da updateCarroceriaService com o id: {}", id);

        var carroceriaExistente = entityValidation.obterOuLancarErro(
                carroceriaRepository.findById(id),
                CARROCERIA,
                id
        );
        carroceriaMapper.toUpdate(carroceriaRequest, carroceriaExistente);
        var carroceriaAtualizada = carroceriaRepository.save(carroceriaExistente);
        return carroceriaMapper.toResponse(carroceriaAtualizada);
    }

    public CarroceriaResponse alterarStatus(Long id, boolean ativo) {
        log.info("Alterando status da carroceria. Id: {}, Novo status: {}",
                id, ativo ? "ATIVA" : "INATIVA");

        var carroceria = entityValidation.obterOuLancarErro(
                carroceriaRepository.findById(id),
                CARROCERIA,
                id
        );

        statusValidation.defineValidacao(ativo, CARROCERIA, carroceria);

        return carroceriaMapper.toResponse(carroceria);

    }

    public Carroceria buscaCarroceria(Long id) {
        log.info("Inicio da buscaCarroceriaService com id: {}", id);
        return carroceriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CARROCERIA, id));
    }
}
