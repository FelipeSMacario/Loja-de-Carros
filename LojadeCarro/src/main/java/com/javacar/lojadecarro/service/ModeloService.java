package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.ModeloRequest;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.mapper.ModeloMapper;
import com.javacar.lojadecarro.repository.ModeloRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import com.javacar.lojadecarro.validation.StatusValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.COR;
import static com.javacar.lojadecarro.enums.Entidade.MODELO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeloService {
    private final ModeloRepository modeloRepository;

    private final ModeloMapper modeloMapper;
    private final MarcaService marcaService;
    private final EntityValidation entityValidation;
    private final StatusValidation statusValidation;

    public ModeloResponse createModelo(ModeloRequest request) {
        log.debug("Inicio da createModeloService com a response: {}", request);
        var modeloEntity = modeloMapper.toEntity(request);
        modeloEntity.setMarca(marcaService.buscaMarca(request.idMarca()));
        var modeloResponse = modeloRepository.save(modeloEntity);

        log.info("Modelo salva com sucesso!");

        return modeloMapper.toResponse(modeloResponse);
    }

    public List<ModeloResponse> listarModelo(StatusFiltro status) {
        log.info("Inicio da listarModeloService");

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

    public ModeloResponse findModeloById(Long id) {
        log.info("Inicio da findModeloByIdService com id: {}", id);
        return modeloRepository.findById(id)
                .map(modeloMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(MODELO, id));
    }

    public ModeloResponse updateModelo(ModeloRequest request, Long id) {
        log.info("Inicio da updateModeloService com o id: {}", id);
        return modeloRepository.findById(id)
                .map(modeloEntity -> {
                    modeloMapper.toUpdate(request, modeloEntity);
                    modeloEntity.setMarca(marcaService.buscaMarca(request.idMarca()));
                    var update = modeloRepository.save(modeloEntity);
                    log.info("Modelo atualizado com sucesso!");
                    return modeloMapper.toResponse(update);
                }).orElseThrow(() -> new NotFoundException(MODELO, id));
    }

    public ModeloResponse alterarStatus(Long id, boolean status) {
        log.info("Inicio da deleteModeloService com o id: {}", id);
        var modeloFiltrado = entityValidation.obterOuLancarErro(
                modeloRepository.findById(id),
                MODELO,
                id
        );

        statusValidation.defineValidacao(
                status,
                COR,
                modeloFiltrado
        );

        modeloFiltrado.setAtivo(status);
        var modeloAtualizado = modeloRepository.save(modeloFiltrado);

        log.info("Modelo com o ID: {} foi desativado", id);
        return modeloMapper.toResponse(modeloAtualizado);
    }

    public Modelo buscaModelo(Long id) {
        log.info("Inicio da buscaModeloService com o id: {}", id);
        return modeloRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MODELO, id));
    }
}
