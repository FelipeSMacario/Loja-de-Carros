package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.CombustivelRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.mapper.CombustivelMapper;
import com.javacar.lojadecarro.repository.CombustivelRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import jakarta.transaction.Transactional;
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

    @Transactional
    public CombustivelResponse createCombustivel(CombustivelRequest request) {
        var combustivelEntity = combustivelMapper.toEntity(request);
        var combustivel = combustivelRepository.save(combustivelEntity);

        return combustivelMapper.toResponse(combustivel);
    }

    public List<CombustivelResponse> listarCombustiveis(StatusFiltro status) {
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
        return combustivelMapper.toResponse(buscaCombustivel(id));
    }

    @Transactional
    public CombustivelResponse updateCombustivel(CombustivelRequest request, Long id) {
        var combustivel = buscaCombustivel(id);
        combustivelMapper.toUpdate(request, combustivel);

        return combustivelMapper.toResponse(combustivel);
    }

    @Transactional
    public CombustivelResponse alterarStatus(Long id, boolean status) {
        var combustivel = buscaCombustivel(id);
        combustivel.alteraStatus(status);

        return combustivelMapper.toResponse(combustivel);
    }

    public Combustivel buscaCombustivel(Long id) {
        return entityValidation.obterOuLancarErro(combustivelRepository.findById(id), COMBUSTIVEL, id);
    }
}
