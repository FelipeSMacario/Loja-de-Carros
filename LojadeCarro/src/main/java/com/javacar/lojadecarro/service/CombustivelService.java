package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.CombustivelRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
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
    public CombustivelResponse criar(CombustivelRequest request) {
        validarNomeUnico(request.nome());
        var combustivelEntity = combustivelMapper.toEntity(request);
        var combustivel = combustivelRepository.save(combustivelEntity);

        return combustivelMapper.toResponse(combustivel);
    }

    public List<CombustivelResponse> listar(StatusFiltro status) {
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

    public CombustivelResponse buscarPorId(Long id) {
        return combustivelMapper.toResponse(buscaCombustivel(id));
    }

    @Transactional
    public CombustivelResponse atualizar(CombustivelRequest request, Long id) {

        var combustivel = buscaCombustivel(id);
        if (!request.nome().equals(combustivel.getNome())) {
            validarNomeUnico(request.nome());
        }
        combustivelMapper.toUpdate(request, combustivel);

        return combustivelMapper.toResponse(combustivel);
    }

    @Transactional
    public CombustivelResponse alterarStatus(Long id, StatusRequest request) {
        var combustivel = buscaCombustivel(id);
        combustivel.alteraStatus(request.ativo());

        return combustivelMapper.toResponse(combustivel);
    }

    public Combustivel buscaCombustivel(Long id) {
        return entityValidation.obterOuLancarErro(combustivelRepository.findById(id), COMBUSTIVEL, id);
    }
    private void validarNomeUnico(String nome) {
        if (combustivelRepository.existsByNome(nome)){
            throw new BusinessException(COMBUSTIVEL.nomeJaExistente());
        }
    }
}
