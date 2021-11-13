package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Carro;
import com.JavangularCar.LojadeCarro.service.CarroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("carro")
public class CarroController {

    @Autowired
    CarroService carroService;

    @PostMapping
    public Carro createCarro(@RequestBody Carro carro) {
        return carroService.createCarro(carro);
    }

    @GetMapping
    public Page<Carro> listarCarros(@PageableDefault(size = 9) Pageable pageable) {

        return carroService.listarCarros(pageable);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Carro> findCarroById(@PathVariable Long id) {
        return carroService.findCarroById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateCarro(@RequestBody Carro carro, @PathVariable Long id) {
        return carroService.updateCarro(carro, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCarro(@PathVariable Long id) {
        return carroService.deleteCarro(id);
    }

    @GetMapping("/search")
    public List<Carro> FiltrarCampos(String marca, String modelo, Integer anoInicio, Integer anoFim,  Double valorInicio, Double valorFim, Double quilometragem){
        return carroService.FiltrarCampos(marca, modelo, anoInicio, anoFim, valorInicio, valorFim, quilometragem);
    }


}
