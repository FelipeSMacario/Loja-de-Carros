package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Carro;
import com.JavangularCar.LojadeCarro.service.CarroService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:4200")
@Api(tags = "Carro")
@RestController
@RequestMapping("carro")
public class CarroController {

    @Autowired
    CarroService carroService;

    @PostMapping
    @ApiOperation(value = "Criar um novo carro")
    public Carro createCarro(@RequestBody Carro carro) {
        return carroService.createCarro(carro);

    }

    @GetMapping
    @ApiOperation(value = "Listar todos os carros")
    public Page<Carro> listarCarros(@PageableDefault(size = 9) Pageable pageable) {
        return carroService.listarCarros(pageable);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Buscar um carro por id")
    public ResponseEntity<Carro> findCarroById(@PathVariable Long id) {
        return carroService.findCarroById(id);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Atualizar um carro buscando por id")
    public ResponseEntity updateCarro(@RequestBody Carro carro, @PathVariable Long id) {
        return carroService.updateCarro(carro, id);
    }

    @PutMapping("/vendido/{id}")
    @ApiOperation(value = "Marca o carro como vendido o retirando da lista de carros disponíveis")
    public ResponseEntity marcaVendido(@RequestBody Carro carro, @PathVariable Long id){
        return carroService.macarVendido(carro, id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletar um carro buscando por id")
    public ResponseEntity deleteCarro(@PathVariable Long id) {
        return carroService.deleteCarro(id);
    }

    @GetMapping("/search")
    @ApiOperation(value = "Paginação que busca o carro por parãmetros")
    public Page<Carro> FiltrarCampos(String marca, String modelo, Integer anoInicio, Integer anoFim, Double valorInicio, Double valorFim, Double quilometragem, @PageableDefault(size = 9) Pageable pageable) {
        return carroService.FiltrarCampos(marca, modelo, anoInicio, anoFim, valorInicio, valorFim, quilometragem, pageable);
    }

    @PutMapping("/vendido/{id}")
    @ApiOperation(value = "Marca o carro como vendido o retirando da lista de exibição")
    public ResponseEntity marcaVendido(@RequestBody Carro carro, @PathVariable Long id) {
        return carroService.marcaVendido(carro, id);
    }
}
