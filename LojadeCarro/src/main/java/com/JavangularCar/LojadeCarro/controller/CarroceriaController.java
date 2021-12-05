package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Carroceria;
import com.JavangularCar.LojadeCarro.service.CarroceriaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@Api(tags = "Carroceria")
@RestController
@RequestMapping("carroceria")
public class CarroceriaController {
    @Autowired
    CarroceriaService carroceriaService;

    @PostMapping
    @ApiOperation(value = "Criar uma nova carroceria")
    public Carroceria createCarroceria(@RequestBody Carroceria carroceria) {
        return carroceriaService.createCarroceria(carroceria);
    }

    @GetMapping()
    @ApiOperation(value = "Listar todas as carrocerias")
    public List<Carroceria> listarCarroceria() {
        return carroceriaService.listarCarroceria();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Buscar carroceria por id")
    public ResponseEntity<Carroceria> findCarroceriaById(@PathVariable Long id) {
        return carroceriaService.findCarroceriaById(id);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Atualizar carroceria buscando por id")
    public ResponseEntity updateCarroceria(@RequestBody Carroceria carroceria, @PathVariable Long id) {
        return carroceriaService.updateCarroceria(carroceria, id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletar uma carroceria buscando por id")
    public ResponseEntity deleteCarroceria(@PathVariable Long id) {
        return carroceriaService.deleteCarroceria(id);
    }
}
