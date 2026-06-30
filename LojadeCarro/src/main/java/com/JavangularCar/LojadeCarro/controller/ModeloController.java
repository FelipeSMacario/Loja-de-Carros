package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.entity.Modelo;
import com.JavangularCar.LojadeCarro.service.ModeloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://192.168.49.2:30000")
@Tag(name = "modelo")
@RestController
@RequestMapping("/modelo")
public class ModeloController {
    @Autowired
    ModeloService modeloService;

    @PostMapping
    @Operation(summary = "Criar um novo modelo")
    public Modelo createModelo(@RequestBody Modelo modelo) {
        return modeloService.createModelo(modelo);
    }

    @GetMapping
    @Operation(summary = "Listar todos os modelos")
    public List<Modelo> listarModelo() {
        return modeloService.listarModelo();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar modelo por id")
    public ResponseEntity<Modelo> findModeloById(@PathVariable Long id) {
        var response = modeloService.findModeloById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{id}")
    @Operation(summary = "Atualizar modelo buscando por id")
    public ResponseEntity updateModelo(@RequestBody Modelo modelo, @PathVariable Long id) {
        return modeloService.updateModelo(modelo, id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma modelo buscando por id")
    public ResponseEntity deleteModelo(@PathVariable Long id) {
        return modeloService.deleteModelo(id);
    }
}
