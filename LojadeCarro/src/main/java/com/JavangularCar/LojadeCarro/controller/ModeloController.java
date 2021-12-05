package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Modelo;
import com.JavangularCar.LojadeCarro.service.ModeloService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@Api(tags = "modelo")
@RestController
@RequestMapping("/modelo")
public class ModeloController {
    @Autowired
    ModeloService modeloService;

    @PostMapping
    @ApiOperation(value = "Criar um novo modelo")
    public Modelo createModelo(@RequestBody Modelo modelo) {
        return modeloService.createModelo(modelo);
    }

    @GetMapping
    @ApiOperation(value = "Listar todos os modelos")
    public List<Modelo> listarModelo() {
        return modeloService.listarModelo();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Buscar modelo por id")
    public ResponseEntity<Modelo> findModeloById(@PathVariable Long id) {
        return modeloService.findModeloById(id);
    }

    @PostMapping("/{id}")
    @ApiOperation(value = "Atualizar modelo buscando por id")
    public ResponseEntity updateModelo(@RequestBody Modelo modelo, @PathVariable Long id) {
        return modeloService.updateModelo(modelo, id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletar uma modelo buscando por id")
    public ResponseEntity deleteModelo(@PathVariable Long id) {
        return modeloService.deleteModelo(id);
    }
}
