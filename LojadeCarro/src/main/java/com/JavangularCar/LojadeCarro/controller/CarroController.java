package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Carro;
import com.JavangularCar.LojadeCarro.service.CarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("carro")
public class CarroController {

    @Autowired
    CarroService carroService;

    @PostMapping
    public Carro createCarro(@RequestBody Carro carro){
        return carroService.createCarro(carro);
    }

    @GetMapping
    public List<Carro> listarCarros(){
        return carroService.listarCarros();

    }
    @GetMapping("/{id}")
    public ResponseEntity<Carro> findCarroById(@PathVariable Long id){
        return carroService.findCarroById(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity updateCarro(@RequestBody Carro carro, @PathVariable Long id){
        return carroService.updateCarro(carro, id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCarro(@PathVariable Long id){
        return carroService.deleteCarro(id);
    }

    @GetMapping("/Marca/{Marca}")
    public List<Carro> findByMarca(@PathVariable String Marca){
        return carroService.findByMarca(Marca);
    }

    @GetMapping("/Marca/{Marca}/Modelo/{Modelo}")
    public List<Carro> findByModelo(@PathVariable String Marca, @PathVariable String Modelo){
        return carroService.findByModelo(Marca, Modelo);
    }

    @GetMapping("/Valor/{valor1}/{valor2}")
    public List<Carro> findByValorBetween(@PathVariable Double valor1, @PathVariable Double valor2){
        return carroService.findByValorBetween(valor1, valor2);
    }

    @GetMapping("/AnoCarro/{valor1}/{valor2}")
    public List<Carro> findByAnoFabricacaoBetween(@PathVariable int valor1, @PathVariable int valor2){
        return carroService.findByAnoFabricacaoBetween(valor1, valor2);
    }
    @GetMapping("/Quilometragem/{valor}")
    public List<Carro> findByQuilometragemLessThanEqual(@PathVariable Double valor){
        return carroService.findByQuilometragemLessThanEqual(valor);
    }
}
