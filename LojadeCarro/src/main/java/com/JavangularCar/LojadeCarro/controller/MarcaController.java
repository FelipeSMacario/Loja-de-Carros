package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.dto.request.MarcaRequest;
import com.JavangularCar.LojadeCarro.dto.response.MarcaResponse;
import com.JavangularCar.LojadeCarro.entity.Marca;
import com.JavangularCar.LojadeCarro.service.MarcaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://192.168.49.2:30000")
@Tag(name = "Marca")
@RestController
@RequestMapping("marca")
public class MarcaController {

    @Autowired
    MarcaService marcaService;

    @PostMapping
    @Operation(summary = "Criar uma nova marca")
    public MarcaResponse createMarca(@RequestBody MarcaRequest marca) {
        return marcaService.createMarca(marca);
    }

    @GetMapping
    @Operation(summary = "Listar todas as marcas")
    public List<Marca> listarMarcas() {
        return marcaService.listarMarcas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar marca por id")
    public ResponseEntity<Marca> findMarcaById(@PathVariable Long id) {
        var response = marcaService.findMarcaById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar marca buscando por id")
    public ResponseEntity findMarcaById(@RequestBody Marca marca, @PathVariable Long id) {
        return marcaService.updateMarca(marca, id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma marca buscando por id")
    public ResponseEntity deleteMarca(@PathVariable Long id) {
        return marcaService.deleteMarca(id);
    }
}
