package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Combustivel;
import com.JavangularCar.LojadeCarro.service.CombustivelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://192.168.49.2:30000")
@Tag(name = "Combustível")
@RestController
@RequestMapping("combustivel")
public class CombustivelController {
    @Autowired
    CombustivelService combustivelService;

    @PostMapping
    @Operation(summary = "Criar um novo combustível")
    public Combustivel createCombustivel(@RequestBody Combustivel combustivel){
        return combustivelService.createCombustivel(combustivel);
    }
    @GetMapping
    @Operation(summary = "Listar todos os combustíveis")
    public List<Combustivel> listarCombustivel(){
        return combustivelService.listarCombustivel();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar combustível por id")
    public ResponseEntity<Combustivel> findCombustivelById(@PathVariable Long id){
        return combustivelService.findCombustivelById(id);
    }
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar combustível buscando por id")
    public ResponseEntity updateCombustivel(@RequestBody Combustivel combustivel, @PathVariable Long id){
        return combustivelService.updateCombustivel(combustivel, id);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um combustível buscando por id")
    public ResponseEntity deleteCombustivel(@PathVariable Long id){
        return combustivelService.deleteCombustivel(id);
    }
}
