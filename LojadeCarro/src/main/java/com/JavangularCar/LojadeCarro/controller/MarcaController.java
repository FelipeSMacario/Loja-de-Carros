package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Marca;
import com.JavangularCar.LojadeCarro.service.MarcaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@Api(tags = "Marca")
@RestController
@RequestMapping("marca")
public class MarcaController {

    @Autowired
    MarcaService marcaService;

    @PostMapping
    @ApiOperation(value = "Criar uma nova marca")
    public Marca createMarca(@RequestBody Marca marca) {
        return marcaService.createMarca(marca);
    }

    @GetMapping
    @ApiOperation(value = "Listar todas as marcas")
    public List<Marca> listarMarcas() {
        return marcaService.listarMarcas();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Buscar marca por id")
    public ResponseEntity<Marca> findMarcaById(@PathVariable Long id) {
        return marcaService.findMarcaById(id);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Atualizar marca buscando por id")
    public ResponseEntity findMarcaById(@RequestBody Marca marca, @PathVariable Long id) {
        return marcaService.updateMarca(marca, id);
    }

    @DeleteMapping("/{id")
    @ApiOperation(value = "Deletar uma marca buscando por id")
    public ResponseEntity deleteMarca(@PathVariable Long id) {
        return marcaService.deleteMarca(id);
    }
}
