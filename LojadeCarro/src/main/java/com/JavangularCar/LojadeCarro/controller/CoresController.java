package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Cores;
import com.JavangularCar.LojadeCarro.service.CoresService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://192.168.49.2:30000")
@Tag(name = "Cores")
@RestController
@RequestMapping("cores")
public class CoresController {
    @Autowired
    CoresService coresService;

    @PostMapping
    @Operation(summary = "Criar uma nova cor")
    public Cores createCores(@RequestBody Cores cores){
        return coresService.createCores(cores);
    }

    @GetMapping
    @Operation(summary = "Listar todas as cores")
    public List<Cores> listarCores(){
        return coresService.listarCores();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cor por id")
    public ResponseEntity<Cores> findCoresById(@PathVariable Long id){
        return coresService.findCoresById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cor buscando por id")
    public ResponseEntity updateCores(@RequestBody Cores cores, @PathVariable Long id){
        return coresService.updateCores(cores, id);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma cor buscando por id")
    public ResponseEntity deleteCores(@PathVariable Long id){
        return coresService.deleteCores(id);
    }
}
