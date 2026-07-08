package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.dto.request.ModeloRequest;
import com.JavangularCar.LojadeCarro.dto.response.ModeloResponse;
import com.JavangularCar.LojadeCarro.service.ModeloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "modelo")
@RestController
@RequestMapping("/modelos")
public class ModeloController {
    private final ModeloService modeloService;

    @PostMapping
    @Operation(summary = "Criar um novo modelo")
    public ResponseEntity<ModeloResponse> createModelo(@RequestBody @Valid ModeloRequest request) {
        log.info("Criando um novo modelo");
        var response =  modeloService.createModelo(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Modelo criado com sucesso");
        log.debug("Resposta um novo modelo: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os modelos")
    public ResponseEntity<List<ModeloResponse>> listarModelo() {
        log.info("Buscando todos os modelos.");
        var response = modeloService.listarModelo();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar modelo por id")
    public ResponseEntity<ModeloResponse> findModeloById(@PathVariable Long id) {
        log.info("Buscando o modelo por id: {}", id);
        var response = modeloService.findModeloById(id);

        log.debug("Resposta o modelo por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar modelo buscando por id")
    public ResponseEntity<ModeloResponse> update(@RequestBody @Valid ModeloRequest request, @PathVariable Long id) {
        log.info("Atualizando o modelos por id: {}", id);
        var response = modeloService.updateModelo(request, id);

        log.debug("Resposta atualizar o modelos por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma modelo buscando por id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deletando o modelos por id: {}", id);
         modeloService.deleteModelo(id);

        log.info("Modelo deletado com sucesso. Id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
