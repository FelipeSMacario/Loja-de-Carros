package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.OpcionalRequest;
import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.mapper.OpcionalMapper;
import com.javacar.lojadecarro.repository.OpcionalRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import com.javacar.lojadecarro.validation.StatusValidation;
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
    private final StatusValidation statusValidation;

    public OpcionalResponse createKit(OpcionalRequest request) {
        log.debug("Inicio da createKitService com a response: {}", request);
        var kitEntity = opcionalMapper.toEntity(request);
        var kitResponse = opcionalRepository.save(kitEntity);

        log.info("Kit salva com sucesso!");

        return opcionalMapper.toResponse(kitResponse);
    }

    public List<OpcionalResponse> listarOpcionais(StatusFiltro status) {
        log.info("Inicio da listarKitService");
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

    public OpcionalResponse filtrarKit(Long id) {
        log.info("Inicio da findKitByIdService com id: {}", id);
        return opcionalRepository.findById(id)
                .map(opcionalMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(OPCIONAL, id));
    }


    public OpcionalResponse updateKit(OpcionalRequest request, Long id) {
        log.info("Inicio da updateKitService com o id: {}", id);
        return opcionalRepository.findById(id)
                .map(kitEntity -> {
                    opcionalMapper.toUpdate(request, kitEntity);
                    var update = opcionalRepository.save(kitEntity);
                    log.info("Kit atualizado com sucesso!");
                    return opcionalMapper.toResponse(update);
                }).orElseThrow(() -> new NotFoundException(OPCIONAL, id));
    }

    public OpcionalResponse alterarStatus(Long id, boolean status) {
        log.info("Inicio da deleteKitService com o id: {}", id);
        var opcionalFiltrado = entityValidation.obterOuLancarErro(
                opcionalRepository.findById(id),
                OPCIONAL,
                id
        );

        statusValidation.defineValidacao(
                status,
                OPCIONAL,
                opcionalFiltrado
        );

        opcionalFiltrado.setAtivo(status);
        var opcionalAtualizado = opcionalRepository.save(opcionalFiltrado);

        log.info("Opcional com o ID: {} foi desativado", id);
        return opcionalMapper.toResponse(opcionalAtualizado);


    }

    public List<Opcional> buscarOpcionais(List<Long> ids) {
      return opcionalRepository.findAllByIdIn(ids);
    }
}
