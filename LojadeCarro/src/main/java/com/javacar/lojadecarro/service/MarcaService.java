package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.MarcaRequest;
import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.entity.Marca;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.mapper.MarcaMapper;
import com.javacar.lojadecarro.repository.MarcaRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import com.javacar.lojadecarro.validation.StatusValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.MARCA;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarcaService {
    private final MarcaRepository marcaRepository;
    private final MarcaMapper marcaMapper;
    private final EntityValidation entityValidation;
    private final StatusValidation statusValidation;

    public MarcaResponse createMarca(MarcaRequest request) {
        log.debug("Inicio da createMarcaService com a response: {}", request);
        var marca = marcaMapper.toEntity(request);
        var marcaEntity = marcaRepository.save(marca);
        log.info("Marca salva com sucesso!");

        return marcaMapper.toResponse(marcaEntity);
    }

    public List<MarcaResponse> listarMarcas(StatusFiltro status) {
        log.info("Inicio da listarMarcaService");

        var listaMarcas =
                switch (status) {
                    case TODAS -> marcaRepository.findAll();
                    case INATIVAS -> marcaRepository.findByAtivo(false);
                    case ATIVAS -> marcaRepository.findByAtivo(true);
                };

        return listaMarcas
                .stream()
                .map(marcaMapper::toResponse)
                .toList();
    }

    public MarcaResponse findMarcaById(Long id) {
        log.info("Inicio da findMarcaByIdService com id: {}", id);
        return marcaRepository.findById(id)
                .map(marcaMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(MARCA, id));
    }

    public MarcaResponse updateMarca(MarcaRequest request, Long id) {
        log.info("Inicio da updateMarcaService com o id: {}", id);
        return marcaRepository.findById(id)
                .map(marcaEntity -> {
                    marcaMapper.toUpdate(request, marcaEntity);
                    var update = marcaRepository.save(marcaEntity);
                    return marcaMapper.toResponse(update);
                }).orElseThrow(() -> new NotFoundException(MARCA, id));
    }

    public MarcaResponse alterarStatus(Long id, boolean status) {
        log.info("Inicio da deleteMarcaService com o id: {}", id);
        var marcaFiltrada = entityValidation.obterOuLancarErro(
                marcaRepository.findById(id),
                MARCA,
                id
        );
        statusValidation.defineValidacao(
                status,
                MARCA,
                marcaFiltrada
        );
        marcaFiltrada.setAtivo(false);

        var marcaAtualizada = marcaRepository.save(marcaFiltrada);

        log.info("Marca com o ID: {} foi desativada", id);
        return marcaMapper.toResponse(marcaAtualizada);

    }

    public Marca buscaMarca(Long id) {
        log.info("Inicio da buscaMarcaService com o id: {}", id);
        return marcaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MARCA, id));
    }
}
