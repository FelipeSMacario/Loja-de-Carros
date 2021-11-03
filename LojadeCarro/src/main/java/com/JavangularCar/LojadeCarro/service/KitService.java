package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Kit;
import com.JavangularCar.LojadeCarro.repository.KitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class KitService {
    @Autowired
    KitRepository kitRepository;

    public Kit createKit(@RequestBody Kit kit){
        return kitRepository.save(kit);
    }
    public List<Kit> listarKit(){
        return kitRepository.findAll();

    }

    public ResponseEntity<Kit> findKitById(Long id){
        return kitRepository.findById(id)
                            .map(record -> ResponseEntity.ok().body(record))
                            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    public ResponseEntity updateKit(@RequestBody Kit kit, Long id){
        return kitRepository.findById(id)
                            .map(record -> {
                                record.setAutomatico(kit.isAutomatico());
                                record.setCarro(kit.getCarro());
                                record.setArCondicionado(kit.isArCondicionado());
                                record.setDirecaoHidraulica(kit.isDirecaoHidraulica());
                                record.setFreioABS(kit.isFreioABS());
                                record.setRodaLigaLeve(kit.isRodaLigaLeve());
                                Kit update = kitRepository.save(record);
                                return ResponseEntity.ok().body(update);
                            }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    public ResponseEntity deleteKit(Long id){
        return kitRepository.findById(id)
                            .map(record -> {
                                kitRepository.deleteById(id);
                                return ResponseEntity.ok().build();
                            }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
