package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.MarcaRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.entity.Marca;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.mapper.MarcaMapper;
import com.javacar.lojadecarro.repository.MarcaRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
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

    @Transactional
    public MarcaResponse criar(MarcaRequest request) {
        validarNomeUnico(request.nome());
        validarUrlUnica(request.url());
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
        if (!request.nome().equals(marca.getNome())) {
            validarNomeUnico(request.nome());
        }
        if (!request.url().equals(marca.getUrl())) {
            validarUrlUnica(request.url());
        }
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
    private void validarUrlUnica(String url) {
        if (marcaRepository.existsByUrl(url)){
            throw new BusinessException("A URL informada já possui um cadastro.");
        }
    }

    private void validarNomeUnico(String nome) {
        if (marcaRepository.existsByNome(nome)){
            throw new BusinessException("O nome informado já possui um cadastro.");
        }
    }
}
