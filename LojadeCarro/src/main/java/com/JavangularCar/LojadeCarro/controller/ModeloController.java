package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Modelo;
import com.JavangularCar.LojadeCarro.service.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/modelo")
public class ModeloController {
    @Autowired
    ModeloService modeloService;

    @PostMapping
    public Modelo createModelo(@RequestBody Modelo modelo) {
        return modeloService.createModelo(modelo);
    }

    @GetMapping
    public List<Modelo> listarModelo() {
        return modeloService.listarModelo();

    }

    @GetMapping("/{id}")
    public ResponseEntity<Modelo> findModeloById(@PathVariable Long id) {
        return modeloService.findModeloById(id);
    }

    @PostMapping("/{id}")
    public ResponseEntity updateModelo(@RequestBody Modelo modelo, @PathVariable Long id) {
        return modeloService.updateModelo(modelo, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteModelo(@PathVariable Long id) {
        return modeloService.deleteModelo(id);
    }
}
