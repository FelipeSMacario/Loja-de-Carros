package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Carro;
import com.JavangularCar.LojadeCarro.repository.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class CarroService {
    @Autowired
    CarroRepository carroRepository;

    public Carro createCarro(Carro carro) {

        return carroRepository.save(carro);
    }

    public List<Carro> listarCarros() {
        return carroRepository.findAll();
    }

    public ResponseEntity<Carro> findCarroById(Long id){
        return carroRepository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }
    public ResponseEntity updateCarro(@RequestBody Carro carro, Long id){
        return carroRepository.findById(id)
                .map(record -> ResponseEntity.ok().body(carroRepository.save(carro)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    public ResponseEntity deleteCarro(Long id) {
        return carroRepository.findById(id)
                .map(record -> {
                    carroRepository.deleteById(id);
                    return ResponseEntity.ok().build(); })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }
}
