package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.mapper.CarroceriaMapper;
import com.javacar.lojadecarro.repository.CarroceriaRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.CARROCERIA;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarroceriaService {
    private final CarroceriaRepository carroceriaRepository;
    private final EntityValidation entityValidation;
    private final CarroceriaMapper carroceriaMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CarroceriaResponse criar(CarroceriaRequest request) {
        validarNomeUnico(request.nome());
        var carroceriaEntity = carroceriaMapper.toEntity(request);
        var carroceriaResponse = carroceriaRepository.save(carroceriaEntity);

        return carroceriaMapper.toResponse(carroceriaResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<CarroceriaResponse> listarAdministracao(StatusFiltro status) {
        var listaCarroceria = switch (status) {
            case TODAS -> carroceriaRepository.findAll();
            case INATIVAS -> carroceriaRepository.findByAtivo(false);
            case ATIVAS -> carroceriaRepository.findByAtivo(true);
        };

        return listaCarroceria.stream().map(carroceriaMapper::toResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public CarroceriaResponse buscarPorIdAdministracao(Long id) {
        var carroceria = buscaCarroceria(id);
        return carroceriaMapper.toResponse(carroceria);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CarroceriaResponse atualizar(CarroceriaRequest request, Long id) {
        var carroceria = buscaCarroceria(id);
        if (!request.nome().equals(carroceria.getNome())) {
            validarNomeUnico(request.nome());
        }
        carroceriaMapper.toUpdate(request, carroceria);
        return carroceriaMapper.toResponse(carroceria);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CarroceriaResponse alterarStatus(Long id, StatusRequest request) {
        var carroceria = buscaCarroceria(id);
        carroceria.alterarStatus(request.ativo());
        return carroceriaMapper.toResponse(carroceria);
    }

    public Carroceria buscaCarroceria(Long id) {
        return entityValidation.obterOuLancarErro(carroceriaRepository.findById(id), CARROCERIA, id);
    }

    public Carroceria buscaCarroceriaAtiva(Long id) {
        return entityValidation.obterOuLancarErro(carroceriaRepository.findByIdAndAtivoTrue(id), CARROCERIA, id);
    }

    private void validarNomeUnico(String nome) {
        if (carroceriaRepository.existsByNome(nome)) {
            throw new BusinessException(CARROCERIA.nomeJaExistente());
        }
    }

    @Transactional(readOnly = true)
    public CarroceriaResponse buscarCarroceriaAtivaPorId(Long id) {
        return carroceriaMapper.toResponse(buscaCarroceriaAtiva(id));
    }

    @Transactional(readOnly = true)
    public List<CarroceriaResponse> listarCarroceriasAtivas() {
        return carroceriaRepository.findByAtivo(true)
                .stream()
                .map(carroceriaMapper::toResponse)
                .toList();
    }
}
