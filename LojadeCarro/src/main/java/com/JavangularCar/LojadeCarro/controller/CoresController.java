package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Cores;
import com.JavangularCar.LojadeCarro.service.CoresService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@Api(tags = "Cores")
@RestController
@RequestMapping("cores")
public class CoresController {
    @Autowired
    CoresService coresService;

    @PostMapping
    @ApiOperation(value = "Criar uma nova cor")
    public Cores createCores(@RequestBody Cores cores){
        return coresService.createCores(cores);
    }

    @GetMapping
    @ApiOperation(value = "Listar todas as cores")
    public List<Cores> listarCores(){
        return coresService.listarCores();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Buscar cor por id")
    public ResponseEntity<Cores> findCoresById(@PathVariable Long id){
        return coresService.findCoresById(id);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Atualizar cor buscando por id")
    public ResponseEntity updateCores(@RequestBody Cores cores, @PathVariable Long id){
        return coresService.updateCores(cores, id);
    }
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletar uma cor buscando por id")
    public ResponseEntity deleteCores(@PathVariable Long id){
        return coresService.deleteCores(id);
    }
}
