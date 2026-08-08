package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.OpcionalRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.mapper.OpcionalMapper;
import com.javacar.lojadecarro.repository.OpcionalRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpcionalService {

    private final OpcionalRepository opcionalRepository;
    private final OpcionalMapper opcionalMapper;
    private final EntityValidation entityValidation;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public OpcionalResponse criar(OpcionalRequest request) {
        validarNomeUnico(request.nome());
        var opcionalEntity = opcionalMapper.toEntity(request);
        var opcional = opcionalRepository.save(opcionalEntity);

        return opcionalMapper.toResponse(opcional);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<OpcionalResponse> listarAdministracao(StatusFiltro status) {
        var listaOpcionais =
                switch (status) {
                    case TODAS -> opcionalRepository.findAll();
                    case INATIVAS -> opcionalRepository.findByAtivo(false);
                    case ATIVAS -> opcionalRepository.findByAtivo(true);
                };
        return listaOpcionais
                .stream()
                .map(opcionalMapper::toResponse)
                .toList();

    }

    @Transactional(readOnly = true)
    public List<OpcionalResponse> listarOpcionaisAtivas() {
        return opcionalRepository.findByAtivo(true)
                .stream()
                .map(opcionalMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public OpcionalResponse buscarPorIdAdministracao(Long id) {
        return opcionalMapper.toResponse(buscaOpcional(id));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public OpcionalResponse atualizar(OpcionalRequest request, Long id) {
        var opcional = buscaOpcional(id);
        if (!request.nome().equals(opcional.getNome())) {
            validarNomeUnico(request.nome());
        }
        opcionalMapper.toUpdate(request, opcional);

        return opcionalMapper.toResponse(opcional);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public OpcionalResponse alterarStatus(Long id, StatusRequest request) {
        var opcional = buscaOpcional(id);
        opcional.alteraStatus(request.ativo());

        return opcionalMapper.toResponse(opcional);
    }

    public List<Opcional> buscarOpcionais(List<Long> ids) {
        return opcionalRepository.findAllByIdIn(ids);
    }

    public List<Opcional> buscarOpcionaisAtivos(List<Long> ids) {
        return opcionalRepository.findAllByIdInAndAtivoTrue(ids);
    }

    public Opcional buscaOpcional(Long id) {
        return entityValidation.obterOuLancarErro(
                opcionalRepository.findById(id),
                OPCIONAL,
                id);
    }

    public Opcional buscaOpcionalAtivo(Long id) {
        return entityValidation.obterOuLancarErro(
                opcionalRepository.findByIdAndAtivoTrue(id),
                OPCIONAL,
                id);
    }

    private void validarNomeUnico(String nome) {
        if (opcionalRepository.existsByNome(nome)) {
            throw new BusinessException(OPCIONAL.nomeJaExistente());
        }
    }

    @Transactional(readOnly = true)
    public OpcionalResponse buscarOpcionalAtivoPorId(Long id) {
        return opcionalMapper.toResponse(buscaOpcionalAtivo(id));
    }

}
