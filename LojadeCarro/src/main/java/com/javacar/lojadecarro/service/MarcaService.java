package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.MarcaRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.entity.Marca;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.mapper.MarcaMapper;
import com.javacar.lojadecarro.repository.MarcaRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import com.javacar.lojadecarro.validation.StatusValidation;
import jakarta.transaction.Transactional;
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

    @Transactional
    public MarcaResponse criar(MarcaRequest request) {
        var marcaEntity = marcaMapper.toEntity(request);
        var marca = marcaRepository.save(marcaEntity);

        return marcaMapper.toResponse(marca);
    }

    public List<MarcaResponse> listar(StatusFiltro status) {
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

    public MarcaResponse buscarPorId(Long id) {
        return marcaMapper.toResponse(buscaMarca(id));
    }

    @Transactional
    public MarcaResponse atualizar(MarcaRequest request, Long id) {
        var marca = buscaMarca(id);
        marcaMapper.toUpdate(request, marca);
        return marcaMapper.toResponse(marca);
    }

    @Transactional
    public MarcaResponse alterarStatus(Long id, StatusRequest request) {
        var marca = buscaMarca(id);
        marca.alterarStatus(request.ativo());
        return marcaMapper.toResponse(marca);

    }

    public Marca buscaMarca(Long id) {
        return entityValidation.obterOuLancarErro(marcaRepository.findById(id), MARCA, id);
    }
}
