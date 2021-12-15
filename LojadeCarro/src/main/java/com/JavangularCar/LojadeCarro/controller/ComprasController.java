package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Compras;
import com.JavangularCar.LojadeCarro.service.ComprasService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@Api(tags = "Compras")
@RestController
@RequestMapping("/compras")
public class ComprasController {
    @Autowired
    ComprasService comprasService;

    @GetMapping
    public List<Compras> listarCompras(){
        return comprasService.listarCompras();
    }

    @PostMapping
    public Compras createCompras(@RequestBody Compras compras){
        //Função para marcar o carro como vendido.
        comprasService.marcaVendido(compras.getCarro().getId());
        return comprasService.createCompras(compras);
    }
}
