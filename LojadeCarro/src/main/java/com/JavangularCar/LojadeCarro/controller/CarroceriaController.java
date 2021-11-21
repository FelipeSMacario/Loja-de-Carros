package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Carroceria;
import com.JavangularCar.LojadeCarro.service.CarroceriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("carroceria")
public class CarroceriaController {
    @Autowired
    CarroceriaService carroceriaService;

    @PostMapping
    public Carroceria createCarroceria(@RequestBody Carroceria carroceria) {
        return carroceriaService.createCarroceria(carroceria);
    }

    @GetMapping()
    public List<Carroceria> listarCarroceria() {
        return carroceriaService.listarCarroceria();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carroceria> findCarroceriaById(@PathVariable Long id) {
        return carroceriaService.findCarroceriaById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateCarroceria(@RequestBody Carroceria carroceria, @PathVariable Long id) {
        return carroceriaService.updateCarroceria(carroceria, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCarroceria(@PathVariable Long id) {
        return carroceriaService.deleteCarroceria(id);
    }
}
