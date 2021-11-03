package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Combustivel;
import com.JavangularCar.LojadeCarro.service.CombustivelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("combustivel")
public class CombustivelController {
    @Autowired
    CombustivelService combustivelService;

    @PostMapping
    public Combustivel createCombustivel(@RequestBody Combustivel combustivel){
        return combustivelService.createCombustivel(combustivel);
    }
    @GetMapping
    public List<Combustivel> listarCombustivel(){
        return combustivelService.listarCombustivel();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Combustivel> findCombustivelById(@PathVariable Long id){
        return combustivelService.findCombustivelById(id);
    }
    @PutMapping("/{id")
    public ResponseEntity updateCombustivel(@RequestBody Combustivel combustivel, @PathVariable Long id){
        return combustivelService.updateCombustivel(combustivel, id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCombustivel(@PathVariable Long id){
        return combustivelService.deleteCombustivel(id);
    }
}
