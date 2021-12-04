package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Combustivel;
import com.JavangularCar.LojadeCarro.repository.CombustivelRepositor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class CombustivelService {
    @Autowired
    CombustivelRepositor combustivelRepositor;


    public Combustivel createCombustivel(@RequestBody Combustivel combustivel){
        return combustivelRepositor.save(combustivel);
    }
    public List<Combustivel> listarCombustivel(){
        return combustivelRepositor.findAll();
    }
    public ResponseEntity<Combustivel> findCombustivelById(Long id){
        return combustivelRepositor.findById(id)
                                    .map(record -> ResponseEntity.ok().body(record))
                                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    public ResponseEntity updateCombustivel(@RequestBody Combustivel combustivel, Long id){
        return combustivelRepositor.findById(id)
                                    .map(record -> {
                                        record.setNome(combustivel.getNome());
                                        Combustivel update = combustivelRepositor.save(record);
                                        return ResponseEntity.ok().body(update);
                                    }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    public ResponseEntity deleteCombustivel(Long id){
        return combustivelRepositor.findById(id)
                                    .map(record -> {
                                        combustivelRepositor.deleteById(id);
                                        return ResponseEntity.ok().build();
                                    }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
