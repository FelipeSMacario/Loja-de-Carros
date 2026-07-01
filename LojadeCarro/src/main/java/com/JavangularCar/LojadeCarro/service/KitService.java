package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.KitRequest;
import com.JavangularCar.LojadeCarro.dto.response.KitResponse;
import com.JavangularCar.LojadeCarro.exception.KitException;
import com.JavangularCar.LojadeCarro.mapper.KitMapper;
import com.JavangularCar.LojadeCarro.repository.KitRepository;
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
                    record.setAutomatico(request.automatico());
                    record.setCarro(carroService.buscaCarro(request.idCarro()));
                    record.setArCondicionado(request.arCondicionado());
                    record.setDirecaoHidraulica(request.direcaoHidraulica());
                    record.setFreioABS(request.freioABS());
                    record.setRodaLigaLeve(request.rodaLigaLeve());
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
