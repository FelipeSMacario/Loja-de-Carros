package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.KitRequest;
import com.javacar.lojadecarro.dto.response.KitResponse;
import com.javacar.lojadecarro.exception.KitException;
import com.javacar.lojadecarro.mapper.KitMapper;
import com.javacar.lojadecarro.repository.KitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KitService {

    private final KitRepository kitRepository;
    private final KitMapper kitMapper;
    private final CarroService carroService;

    public KitResponse createKit(KitRequest request) {
        log.debug("Inicio da createKitService com a response: {}", request);
        var kitEntity = kitMapper.toEntity(request);
        var carro = carroService.buscaCarro(request.idCarro());
        kitEntity.setCarro(carro);
        var kitResponse = kitRepository.save(kitEntity);

        log.info("Kit salva com sucesso!");

        return kitMapper.toResponse(kitResponse);
    }

    public List<KitResponse> listarKit() {
        log.info("Inicio da listarKitService");
        return kitRepository
                .findAll()
                .stream()
                .map(kitMapper::toResponse)
                .toList();

    }

    public KitResponse filtrarKit(Long id) {
        log.info("Inicio da findKitByIdService com id: {}", id);
        return kitRepository.findById(id)
                .map(kitMapper::toResponse)
                .orElseThrow(() -> new KitException(id));
    }


    public KitResponse updateKit(KitRequest request, Long id) {
        log.info("Inicio da updateKitService com o id: {}", id);
        return kitRepository.findById(id)
                .map(record -> {
                    kitMapper.toUpdate(request, record);
                    record.setCarro(carroService.buscaCarro(request.idCarro()));
                    var update = kitRepository.save(record);
                    log.info("Kit atualizado com sucesso!");
                    return kitMapper.toResponse(update);
                }).orElseThrow(() -> new KitException(id));
    }

    public void deleteKit(Long id) {
        log.info("Inicio da deleteKitService com o id: {}", id);
        kitRepository.findById(id)
                .orElseThrow(() -> new KitException(id));

        kitRepository.deleteById(id);
    }
}
