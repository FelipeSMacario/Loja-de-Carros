package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.CombustivelRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.mapper.CombustivelMapper;
import com.javacar.lojadecarro.repository.CombustivelRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import com.javacar.lojadecarro.validation.StatusValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;

@Slf4j
@Service
@RequiredArgsConstructor
public class CombustivelService {

    private final CombustivelRepository combustivelRepository;
    private final CombustivelMapper combustivelMapper;
    private final EntityValidation entityValidation;
    private final StatusValidation statusValidation;

    public CombustivelResponse createCombustivel(CombustivelRequest request) {
        log.debug("Inicio da criação do combustível com a request: {}", request);
        var combustivelEntity = combustivelMapper.toEntity(request);
        var combustivelResponse = combustivelRepository.save(combustivelEntity);

        return combustivelMapper.toResponse(combustivelResponse);
    }

    public List<CombustivelResponse> listarCombustivel(StatusFiltro status) {
        log.info("Inicio da listar combustiveis com o status: {}", status);

        var listaCombustiveis =
                switch (status) {
                    case TODAS -> combustivelRepository.findAll();
                    case INATIVAS -> combustivelRepository.findByAtivo(false);
                    case ATIVAS -> combustivelRepository.findByAtivo(true);
                };

        return listaCombustiveis.stream()
                .map(combustivelMapper::toResponse)
                .toList();
    }

    public CombustivelResponse findCombustivelById(Long id) {
        log.info("Inicio do filtro de combustível com id: {}", id);
        return combustivelRepository.findById(id)
                .map(combustivelMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(COMBUSTIVEL, id));
    }

    public CombustivelResponse updateCombustivel(CombustivelRequest request, Long id) {
        log.info("Inicio da atualização de combustivel com o id: {} e a request: {}", id, request);
        return combustivelRepository.findById(id)
                .map(combustivelEntity -> {
                    combustivelMapper.toUpdate(request, combustivelEntity);
                    var update = combustivelRepository.save(combustivelEntity);
                    log.info("Combustível atualizado com sucesso!");
                    return combustivelMapper.toResponse(update);
                })
                .orElseThrow(() -> new NotFoundException(COMBUSTIVEL, id));
    }

    public CombustivelResponse alterarStatus(Long id, boolean status) {
        log.info("Inicio da alteração de status do combustível com o id: {} para o status: {}", id, status ? "ATIVA" : "INATIVAS");
        var combustivelFiltrado = entityValidation.obterOuLancarErro(combustivelRepository.findById(id), COMBUSTIVEL, id);

        statusValidation.defineValidacao(status, COMBUSTIVEL, combustivelFiltrado);
        combustivelFiltrado.setAtivo(false);
        combustivelRepository.save(combustivelFiltrado);

        log.info("Combustível com o ID: {} foi desativado", id);
        return combustivelMapper.toResponse(combustivelFiltrado);
    }

    public Combustivel buscaCombustivel(Long id) {
        log.info("Inicio da busca por combustivel com id: {}", id);
        return combustivelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(COMBUSTIVEL, id));
    }
}
