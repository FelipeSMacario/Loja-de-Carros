package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.dto.request.MarcaRequest;
import com.JavangularCar.LojadeCarro.dto.response.MarcaResponse;
import com.JavangularCar.LojadeCarro.service.MarcaService;
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
@Tag(name = "Marca")
@RestController
@RequestMapping("/marcas")
public class MarcaController {

    private final MarcaService marcaService;

    @PostMapping
    @Operation(summary = "Criar uma nova marca")
    public ResponseEntity<MarcaResponse> create(@RequestBody @Valid MarcaRequest request) {
        log.info("Criando uma nova marca");
        var response = marcaService.createMarca(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Marca criado com sucesso");
        log.debug("Resposta uma nova marca: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as marcas")
    public ResponseEntity<List<MarcaResponse>> findAll() {
        log.info("Buscando todas as marcas.");
        var response = marcaService.listarMarcas();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar marca por id")
    public ResponseEntity<MarcaResponse> findById(@PathVariable Long id) {
        log.info("Buscando a marca por id: {}", id);
        var response = marcaService.findMarcaById(id);

        log.debug("Resposta da marca por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar marca buscando por id")
    public ResponseEntity<MarcaResponse> update(@RequestBody @Valid MarcaRequest request, @PathVariable Long id) {
        log.info("Atualizando a marca por id: {}", id);
        var response = marcaService.updateMarca(request, id);

        log.debug("Resposta atualizar marca por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma marca buscando por id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deletando a marca por id: {}", id);
        marcaService.deleteMarca(id);

        log.info("Marca deletada com sucesso. Id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
