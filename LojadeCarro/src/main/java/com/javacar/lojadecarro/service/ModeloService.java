package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.ModeloRequest;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.exception.ModeloException;
import com.javacar.lojadecarro.mapper.ModeloMapper;
import com.javacar.lojadecarro.repository.ModeloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeloService {
    private final ModeloRepository modeloRepository;

    private final ModeloMapper modeloMapper;
    private final MarcaService marcaService;

    public ModeloResponse createModelo(ModeloRequest request) {
        log.debug("Inicio da createModeloService com a response: {}", request);
        var modeloEntity = modeloMapper.toEntity(request);
        modeloEntity.setMarca(marcaService.buscaMarca(request.idMarca()));
        var modeloResponse = modeloRepository.save(modeloEntity);

        log.info("Modelo salva com sucesso!");

        return modeloMapper.toResponse(modeloResponse);
    }

    public List<ModeloResponse> listarModelo() {
        log.info("Inicio da listarModeloService");
        return modeloRepository.findAll()
                .stream()
                .map(modeloMapper::toResponse)
                .toList();
    }

    public ModeloResponse findModeloById(Long id) {
        log.info("Inicio da findModeloByIdService com id: {}", id);
        return modeloRepository.findById(id)
                .map(modeloMapper::toResponse)
                .orElseThrow(() -> new ModeloException(id));
    }

    public ModeloResponse updateModelo(ModeloRequest request, Long id) {
        log.info("Inicio da updateModeloService com o id: {}", id);
        return modeloRepository.findById(id)
                .map(modeloEntity -> {
                    modeloEntity.setNome(request.nome());
                    modeloEntity.setMarca(marcaService.buscaMarca(request.idMarca()));
                    var update = modeloRepository.save(modeloEntity);
                    log.info("Modelo atualizado com sucesso!");
                    return modeloMapper.toResponse(update);
                }).orElseThrow(() -> new ModeloException(id));
    }

    public void deleteModelo(Long id) {
        log.info("Inicio da deleteModeloService com o id: {}", id);
        var modeloEntity = modeloRepository.findById(id)
                .orElseThrow(() -> new ModeloException(id));

        modeloRepository.deleteById(modeloEntity.getId());

    }

    public Modelo buscaModelo(Long id) {
        log.info("Inicio da buscaModeloService com o id: {}", id);
        return modeloRepository.findById(id)
                .orElseThrow(() -> new ModeloException(id));
    }
}
