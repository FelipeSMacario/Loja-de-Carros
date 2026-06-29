package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Carroceria;
import com.JavangularCar.LojadeCarro.service.CarroceriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://192.168.49.2:30000")
@Tag(name = "Carroceria")
@RestController
@RequestMapping("carroceria")
public class CarroceriaController {
    @Autowired
    CarroceriaService carroceriaService;

    @PostMapping
    @Operation(summary = "Criar uma nova carroceria")
    public Carroceria createCarroceria(@RequestBody Carroceria carroceria) {
        return carroceriaService.createCarroceria(carroceria);
    }

    @GetMapping()
    @Operation(summary = "Listar todas as carrocerias")
    public List<Carroceria> listarCarroceria() {
        return carroceriaService.listarCarroceria();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar carroceria por id")
    public ResponseEntity<Carroceria> findCarroceriaById(@PathVariable Long id) {
        return carroceriaService.findCarroceriaById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar carroceria buscando por id")
    public ResponseEntity updateCarroceria(@RequestBody Carroceria carroceria, @PathVariable Long id) {
        return carroceriaService.updateCarroceria(carroceria, id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma carroceria buscando por id")
    public ResponseEntity deleteCarroceria(@PathVariable Long id) {
        return carroceriaService.deleteCarroceria(id);
    }
}
