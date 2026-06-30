package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.entity.Kit;
import com.JavangularCar.LojadeCarro.service.KitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://192.168.49.2:30000")
@Tag(name = "Kit")
@RestController
@RequestMapping("kit")
public class KitController {
    @Autowired
    KitService kitService;

    @PostMapping
    @Operation(summary = "Criar um novo kit")
    public Kit createKit(@RequestBody Kit kit) {
        return kitService.createKit(kit);
    }
    @GetMapping
    @Operation(summary = "Listar todos os kits")
    public List<Kit> listarKit() {
        return kitService.listarKit();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar kit por id")
    public Kit findKitById(@PathVariable Long id){
        return kitService.filtrarKit(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar kit buscando por id")
    public ResponseEntity updateKit(@RequestBody Kit kit, Long id){
        return kitService.updateKit(kit, id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um kit buscando por id")
    public ResponseEntity deleteKit(@PathVariable Long id){
        return kitService.deleteKit(id);
    }
}
