package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.OpcionalRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.mapper.OpcionalMapper;
import com.javacar.lojadecarro.repository.OpcionalRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpcionalService {

    private final OpcionalRepository opcionalRepository;
    private final OpcionalMapper opcionalMapper;
    private final EntityValidation entityValidation;

    @Transactional
    public OpcionalResponse criar(OpcionalRequest request) {
        var opcionalEntity = opcionalMapper.toEntity(request);
        var opcional = opcionalRepository.save(opcionalEntity);

        return opcionalMapper.toResponse(opcional);
    }

    public List<OpcionalResponse> listar(StatusFiltro status) {
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

    public OpcionalResponse buscarPorId(Long id) {
        return opcionalMapper.toResponse(buscaOpcional(id));
    }


    @Transactional
    public OpcionalResponse atualizar(OpcionalRequest request, Long id) {
        var opcional = buscaOpcional(id);
        opcionalMapper.toUpdate(request, opcional);

        return opcionalMapper.toResponse(opcional);
    }

    @Transactional
    public OpcionalResponse alterarStatus(Long id, StatusRequest request) {
        var opcional = buscaOpcional(id);
        opcional.alteraStatus(request.ativo());

        return opcionalMapper.toResponse(opcional);
    }

    public List<Opcional> buscarOpcionais(List<Long> ids) {
        return opcionalRepository.findAllByIdIn(ids);
    }

    public Opcional buscaOpcional(Long id) {
        return entityValidation.obterOuLancarErro(
                opcionalRepository.findById(id),
                OPCIONAL,
                id);
    }
}
