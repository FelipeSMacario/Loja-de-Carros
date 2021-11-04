package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Kit;
import com.JavangularCar.LojadeCarro.service.KitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("kit")
public class KitController {
    @Autowired
    KitService kitService;

    @PostMapping
    public Kit createKit(@RequestBody Kit kit) {
        return kitService.createKit(kit);
    }
    @GetMapping
    public List<Kit> listarKit() {
        return kitService.listarKit();
    }
    @GetMapping("/{id}")
    public Kit findKitById(@PathVariable Long id){
        return kitService.filtrarKit(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity updateKit(@RequestBody Kit kit, Long id){
        return kitService.updateKit(kit, id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteKit(@PathVariable Long id){
        return kitService.deleteKit(id);
    }
}
