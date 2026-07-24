package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.CorRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.entity.Cor;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.mapper.CorMapper;
import com.javacar.lojadecarro.repository.CoresRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.COR;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoresService {

    private final CoresRepository coresRepository;
    private final CorMapper corMapper;
    private final EntityValidation entityValidation;

    @Transactional
    public CorResponse criar(CorRequest request) {
        var coresEntity = corMapper.toEntity(request);
        var cor = coresRepository.save(coresEntity);

        return corMapper.toResponse(cor);
    }

    public List<CorResponse> listar(StatusFiltro status) {
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

    public CorResponse buscarPorId(Long id) {
        return corMapper.toResponse(buscaCor(id));
    }

    @Transactional
    public CorResponse atualizar(CorRequest request, Long id) {
        var cor = buscaCor(id);
        corMapper.toUpdate(request, cor);
        return corMapper.toResponse(cor);
    }

    @Transactional
    public CorResponse alterarStatus(Long id, StatusRequest request) {
        var cor = buscaCor(id);
        cor.alteraStatus(request.ativo());

        return corMapper.toResponse(cor);
    }

    public Cor buscaCor(Long id) {
        return entityValidation.obterOuLancarErro(coresRepository.findById(id), COR, id);
    }

}
