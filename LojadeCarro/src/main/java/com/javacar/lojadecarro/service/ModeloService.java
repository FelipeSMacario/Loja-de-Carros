package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.ModeloRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.mapper.ModeloMapper;
import com.javacar.lojadecarro.repository.ModeloRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.MODELO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeloService {
    private final ModeloRepository modeloRepository;

    private final ModeloMapper modeloMapper;
    private final MarcaService marcaService;
    private final EntityValidation entityValidation;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ModeloResponse criar(ModeloRequest request) {
        validarNomeUnico(request.nome());
        var modeloEntity = modeloMapper.toEntity(request);
        modeloEntity.setMarca(marcaService.buscaMarca(request.idMarca()));
        var modelo = modeloRepository.save(modeloEntity);

        return modeloMapper.toResponse(modelo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<ModeloResponse> listarAdministracao(StatusFiltro status) {
        var listaModelos =
                switch (status) {
                    case TODAS -> modeloRepository.findAll();
                    case INATIVAS -> modeloRepository.findByAtivo(false);
                    case ATIVAS -> modeloRepository.findByAtivo(true);

                };
        return listaModelos
                .stream()
                .map(modeloMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ModeloResponse> listarModelosAtivos() {
        return modeloRepository
                .findByAtivo(true)
                .stream()
                .map(modeloMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ModeloResponse buscarPorIdAdministracao(Long id) {
        return modeloMapper.toResponse(buscaModelo(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ModeloResponse atualizar(ModeloRequest request, Long id) {
        var modelo = buscaModelo(id);
        if (!request.nome().equals(modelo.getNome())) {
            validarNomeUnico(request.nome());
        }
        modeloMapper.toUpdate(request, modelo);
        modelo.setMarca(marcaService.buscaMarca(request.idMarca()));
        return modeloMapper.toResponse(modelo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ModeloResponse alterarStatus(Long id, StatusRequest request) {
        var modelo = buscaModelo(id);
        modelo.alteraStatus(request.ativo());
        return modeloMapper.toResponse(modelo);
    }

    public Modelo buscaModelo(Long id) {
        return entityValidation.obterOuLancarErro(modeloRepository.findById(id), MODELO, id);
    }

    public Modelo buscaModeloAtivo(Long id) {
        return entityValidation.obterOuLancarErro(modeloRepository.findByIdAndAtivoTrueAndMarca_AtivoTrue(id), MODELO, id);

    }

    private void validarNomeUnico(String nome) {
        if (modeloRepository.existsByNome(nome)) {
            throw new BusinessException(MODELO.nomeJaExistente());
        }
    }

    @Transactional(readOnly = true)
    public ModeloResponse buscarModeloAtivoPorId(Long id) {
        return modeloMapper.toResponse(buscaModeloAtivo(id));
    }
}
