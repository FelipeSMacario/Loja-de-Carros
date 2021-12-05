package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Kit;
import com.JavangularCar.LojadeCarro.service.KitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@Api(tags = "Kit")
@RestController
@RequestMapping("kit")
public class KitController {
    @Autowired
    KitService kitService;

    @PostMapping
    @ApiOperation(value = "Criar um novo kit")
    public Kit createKit(@RequestBody Kit kit) {
        return kitService.createKit(kit);
    }
    @GetMapping
    @ApiOperation(value = "Listar todos os kits")
    public List<Kit> listarKit() {
        return kitService.listarKit();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Buscar kit por id")
    public Kit findKitById(@PathVariable Long id){
        return kitService.filtrarKit(id);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Atualizar kit buscando por id")
    public ResponseEntity updateKit(@RequestBody Kit kit, Long id){
        return kitService.updateKit(kit, id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletar um kit buscando por id")
    public ResponseEntity deleteKit(@PathVariable Long id){
        return kitService.deleteKit(id);
    }
}
