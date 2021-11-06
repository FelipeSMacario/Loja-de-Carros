package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Cores;
import com.JavangularCar.LojadeCarro.service.CoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("cores")
public class CoresController {
    @Autowired
    CoresService coresService;

    @PostMapping
    public Cores createCores(@RequestBody Cores cores){
        return coresService.createCores(cores);
    }
    @GetMapping
    public List<Cores> listarCores(){
        return coresService.listarCores();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Cores> findCoresById(@PathVariable Long id){
        return coresService.findCoresById(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity updateCores(@RequestBody Cores cores, @PathVariable Long id){
        return coresService.updateCores(cores, id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCores(@PathVariable Long id){
        return coresService.deleteCores(id);
    }
}
