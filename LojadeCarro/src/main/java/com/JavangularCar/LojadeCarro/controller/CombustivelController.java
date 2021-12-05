package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Combustivel;
import com.JavangularCar.LojadeCarro.service.CombustivelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@Api(tags = "Combustível")
@RestController
@RequestMapping("combustivel")
public class CombustivelController {
    @Autowired
    CombustivelService combustivelService;

    @PostMapping
    @ApiOperation(value = "Criar um novo combustível")
    public Combustivel createCombustivel(@RequestBody Combustivel combustivel){
        return combustivelService.createCombustivel(combustivel);
    }
    @GetMapping
    @ApiOperation(value = "Listar todos os combustíveis")
    public List<Combustivel> listarCombustivel(){
        return combustivelService.listarCombustivel();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Buscar combustível por id")
    public ResponseEntity<Combustivel> findCombustivelById(@PathVariable Long id){
        return combustivelService.findCombustivelById(id);
    }
    @PutMapping("/{id")
    @ApiOperation(value = "Atualizar combustível buscando por id")
    public ResponseEntity updateCombustivel(@RequestBody Combustivel combustivel, @PathVariable Long id){
        return combustivelService.updateCombustivel(combustivel, id);
    }
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletar um combustível buscando por id")
    public ResponseEntity deleteCombustivel(@PathVariable Long id){
        return combustivelService.deleteCombustivel(id);
    }
}
