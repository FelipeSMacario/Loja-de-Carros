package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.ModeloRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.mapper.ModeloMapper;
import com.javacar.lojadecarro.repository.ModeloRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Transactional
    public ModeloResponse criar(ModeloRequest request) {
        var modeloEntity = modeloMapper.toEntity(request);
        modeloEntity.setMarca(marcaService.buscaMarca(request.idMarca()));
        var modelo = modeloRepository.save(modeloEntity);

        return modeloMapper.toResponse(modelo);
    }

    public List<ModeloResponse> listar(StatusFiltro status) {
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

    public ModeloResponse buscarPorId(Long id) {
        return modeloMapper.toResponse(buscaModelo(id));
    }

    @Transactional
    public ModeloResponse atualizar(ModeloRequest request, Long id) {
        var modelo = buscaModelo(id);
        modeloMapper.toUpdate(request, modelo);
        modelo.setMarca(marcaService.buscaMarca(request.idMarca()));
        return modeloMapper.toResponse(modelo);
    }

    @Transactional
    public ModeloResponse alterarStatus(Long id, StatusRequest request) {
        var modelo = buscaModelo(id);
        modelo.alteraStatus(request.ativo());
        return modeloMapper.toResponse(modelo);
    }

    public Modelo buscaModelo(Long id) {
        return entityValidation.obterOuLancarErro(modeloRepository.findById(id), MODELO, id);
    }
}
