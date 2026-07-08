package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.MarcaRequest;
import com.JavangularCar.LojadeCarro.dto.response.MarcaResponse;
import com.JavangularCar.LojadeCarro.entity.Marca;
import com.JavangularCar.LojadeCarro.exception.MarcaException;
import com.JavangularCar.LojadeCarro.mapper.MarcaMapper;
import com.JavangularCar.LojadeCarro.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarcaService {
    private final MarcaRepository marcaRepository;

    private final MarcaMapper marcaMapper;

    public MarcaResponse createMarca(MarcaRequest request) {
        log.debug("Inicio da createMarcaService com a response: {}", request);
        var marca = marcaMapper.toEntity(request);
        var marcaEntity = marcaRepository.save(marca);
        log.info("Marca salva com sucesso!");

        return marcaMapper.toResponse(marcaEntity);
    }

    public List<MarcaResponse> listarMarcas() {
        log.info("Inicio da listarMarcaService");
        return marcaRepository.findByOrderByNomeAsc()
                .stream()
                .map(marcaMapper::toResponse)
                .toList();
    }

    public MarcaResponse findMarcaById(Long id) {
        log.info("Inicio da findMarcaByIdService com id: {}", id);
        return marcaRepository.findById(id)
                .map(marcaMapper::toResponse)
                .orElseThrow(() -> new MarcaException(id));
    }

    public MarcaResponse updateMarca(MarcaRequest request, Long id) {
        log.info("Inicio da updateMarcaService com o id: {}", id);
        return marcaRepository.findById(id)
                .map(record -> {
                    record.setNome(request.nome());
                    record.setUrl(request.url());
                    var update = marcaRepository.save(record);
                    return marcaMapper.toResponse(update);
                }).orElseThrow(() -> new MarcaException(id));
    }

    public void deleteMarca(Long id) {
        log.info("Inicio da deleteMarcaService com o id: {}", id);
        var marcaEntity = marcaRepository.findById(id)
                .orElseThrow(() -> new MarcaException(id));

        marcaRepository.deleteById(marcaEntity.getId());

    }

    public Marca buscaMarca(Long id) {
        log.info("Inicio da buscaMarcaService com o id: {}", id);
        return marcaRepository.findById(id)
                .orElseThrow(() -> new MarcaException(id));
    }
}
