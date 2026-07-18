package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.CorRequest;
import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.entity.Cor;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.mapper.CorMapper;
import com.javacar.lojadecarro.repository.CoresRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import com.javacar.lojadecarro.validation.StatusValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.COR;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoresService {

    private final CoresRepository coresRepository;
    private final CorMapper corMapper;
    private final EntityValidation entityValidation;
    private final StatusValidation statusValidation;

    public CorResponse createCores(@RequestBody CorRequest request) {
        log.debug("Inicio da createCoresService com a request: {}", request);
        var coresEntity = corMapper.toEntity(request);
        var coresResponse = coresRepository.save(coresEntity);
        log.info("Cores salva com sucesso!");

        return corMapper.toResponse(coresResponse);
    }

    public List<CorResponse> listarCores(StatusFiltro status) {
        log.info("Inicio da listagem de cores com o filtro: {}", status);

        var listaCores =
                switch (status) {
                    case TODAS -> coresRepository.findAll();
                    case INATIVAS -> coresRepository.findByAtivo(false);
                    case ATIVAS -> coresRepository.findByAtivo(true);
                };
        return listaCores
                .stream()
                .map(corMapper::toResponse)
                .toList();
    }

    public CorResponse findCoresById(Long id) {
        log.info("Inicio da findCoresByIdService com id: {}", id);
        return coresRepository.findById(id)
                .map(corMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(COR, id));
    }

    public CorResponse updateCores(CorRequest request, Long id) {
        log.info("Inicio da atualização da cor com o id: {} e a request: {}", id, request);
        return coresRepository.findById(id)
                .map(coresEntity -> {
                    corMapper.toUpdate(request, coresEntity);
                    var update = coresRepository.save(coresEntity);
                    log.info("cor atualizado com sucesso!");
                    return corMapper.toResponse(update);
                }).orElseThrow(() -> new NotFoundException(COR, id));
    }

    public CorResponse alterarStatus(Long id, boolean status) {
        log.info("Inicio da alteração de status da cor com o id: {} para o status: {}", id, status ? "ATIVA" : "INATIVAS");
        var corValidada = entityValidation.obterOuLancarErro(
                coresRepository.findById(id),
                COR,
                id
        );
        statusValidation.defineValidacao(
                status,
                COR,
                corValidada
        );
        corValidada.setAtivo(false);

        var corAtualizada = coresRepository.save(corValidada);
        log.info("Cor com o ID: {} foi desativada", id);

        return corMapper.toResponse(corAtualizada);
    }

    public Cor buscaCores(Long id) {
        log.info("Inicio da busca da cor com o id: {}", id);
        return coresRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(COR, id));
    }

}
