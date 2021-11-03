package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Modelo;
import com.JavangularCar.LojadeCarro.repository.MoleloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModeloService {
    @Autowired
    MoleloRepository moleloRepository;

    public Modelo createModelo(Modelo modelo){
        return moleloRepository.save(modelo);
    }

    public List<Modelo> listarModelo(){
        return moleloRepository.findAll();
    }

    public ResponseEntity<Modelo> findModeloById(Long id){
        return moleloRepository.findById(id)
                                .map(record -> ResponseEntity.ok().body(record))
                                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    public ResponseEntity updateModelo(Modelo modelo, Long id){
        return moleloRepository.findById(id)
                                .map(record -> {
                                    record.setMarca(modelo.getMarca());
                                    record.setNome(modelo.getNome());
                                    Modelo update = moleloRepository.save(record);
                                    return ResponseEntity.ok().body(update);
                                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    public ResponseEntity deleteModelo(Long id){
        return moleloRepository.findById(id)
                                .map(record -> {
                                    moleloRepository.deleteById(id);
                                    return ResponseEntity.ok().build();
                                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

}
