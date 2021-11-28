package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Marca;
import com.JavangularCar.LojadeCarro.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("marca")
@EnableSwagger2
public class MarcaController {

    @Autowired
    MarcaService marcaService;

    @PostMapping
    public Marca createMarca(@RequestBody Marca marca) {
        return marcaService.createMarca(marca);
    }

    @GetMapping
    public List<Marca> listarMarcas() {
        return marcaService.listarMarcas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marca> findMarcaById(@PathVariable Long id) {
        return marcaService.findMarcaById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity findMarcaById(@RequestBody Marca marca, @PathVariable Long id) {
        return marcaService.updateMarca(marca, id);
    }

    @DeleteMapping("/{id")
    public ResponseEntity deleteMarca(@PathVariable Long id) {
        return marcaService.deleteMarca(id);
    }
}
