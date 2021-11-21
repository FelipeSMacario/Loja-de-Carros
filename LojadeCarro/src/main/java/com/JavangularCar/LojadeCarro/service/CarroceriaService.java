package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Carroceria;
import com.JavangularCar.LojadeCarro.repository.CarroceriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class CarroceriaService {
    @Autowired
    CarroceriaRepository carroceriaRepository;

    public Carroceria createCarroceria (@RequestBody Carroceria carroceria){
        return carroceriaRepository.save(carroceria);
    }

    public List<Carroceria> listarCarroceria(){
        return carroceriaRepository.findAll();
    }
    public ResponseEntity<Carroceria> findCarroceriaById(Long id){
        return carroceriaRepository.findById(id)
                                                .map(record -> ResponseEntity.ok().body(record))
                                                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity updateCarroceria(@RequestBody Carroceria carroceria, Long id){
        return carroceriaRepository.findById(id)
                                                .map(record -> {
                                                    record.setNome(carroceria.getNome());
                                                    Carroceria update = carroceriaRepository.save(carroceria);
                                                    return  ResponseEntity.ok().body(update);
                                                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity deleteCarroceria(Long id){
        return carroceriaRepository.findById(id)
                                                .map(record -> {
                                                    carroceriaRepository.deleteById(id);
                                                    return ResponseEntity.ok().build();
                                                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
