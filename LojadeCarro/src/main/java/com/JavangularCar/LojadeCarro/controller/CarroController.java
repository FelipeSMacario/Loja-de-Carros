package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Carro;
import com.JavangularCar.LojadeCarro.service.CarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("carro")
public class CarroController {

    @Autowired
    CarroService carroService;

    @PostMapping
    public Carro createCarro(@RequestBody Carro carro){
        return carroService.createCarro(carro);
    }

    @GetMapping
    public List<Carro> listarCarros(){
        return carroService.listarCarros();

    }
    @GetMapping("/{id}")
    public ResponseEntity<Carro> findCarroById(@PathVariable Long id){
        return carroService.findCarroById(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity updateCarro(@RequestBody Carro carro, @PathVariable Long id){
        return carroService.updateCarro(carro, id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCarro(@PathVariable Long id){
        return carroService.deleteCarro(id);
    }
}
