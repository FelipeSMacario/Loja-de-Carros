package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.dto.request.CarroRequest;
import com.JavangularCar.LojadeCarro.dto.response.CarroResponse;
import com.JavangularCar.LojadeCarro.entity.Carro;
import com.JavangularCar.LojadeCarro.service.CarroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://192.168.49.2:30000")
@Tag(name = "Carro")
@RestController
@RequestMapping("carro")
@Slf4j
public class CarroController {

    @Autowired
    CarroService carroService;

    @PostMapping
    @Operation(summary = "Criar um novo carro")
    public CarroResponse createCarro(@RequestBody CarroRequest carroDTO) {
        return carroService.createCarro(carroDTO);

    }

    @GetMapping
    @Operation(summary = "Listar todos os carros")
    public Page<Carro> listarCarros(@PageableDefault(size = 9) Pageable pageable) {
        return carroService.listarCarros(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um carro por id")
    public ResponseEntity<Carro> findCarroById(@PathVariable Long id) {
        return carroService.findCarroById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um carro buscando por id")
    public ResponseEntity updateCarro(@RequestBody Carro carro, @PathVariable Long id) {
        return carroService.updateCarro(carro, id);
    }

    @PutMapping("/vendido/{id}")
    @Operation(summary = "Marca o carro como vendido o retirando da lista de carros disponíveis")
    public ResponseEntity marcaVendido(@RequestBody Carro carro, @PathVariable Long id){
        return carroService.macarVendido(carro, id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um carro buscando por id")
    public ResponseEntity deleteCarro(@PathVariable Long id) {
        return carroService.deleteCarro(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Paginação que busca o carro por parãmetros")
    public Page<Carro> FiltrarCampos(String marca, String modelo, Integer anoInicio, Integer anoFim, Double valorInicio, Double valorFim, Double quilometragem, @PageableDefault(size = 9) Pageable pageable) {
        return carroService.FiltrarCampos(marca, modelo, anoInicio, anoFim, valorInicio, valorFim, quilometragem, pageable);
    }

}
