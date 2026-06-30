package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.dto.request.CombustivelRequest;
import com.JavangularCar.LojadeCarro.dto.response.CombustivelResponse;
import com.JavangularCar.LojadeCarro.service.CombustivelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Combustíveis")
@RestController
@RequestMapping("/combustiveis")
public class CombustivelController {
    private final CombustivelService combustivelService;

    @PostMapping
    @Operation(summary = "Criar um novo combustível")
    public ResponseEntity<CombustivelResponse> create(@RequestBody
                                                      @Valid CombustivelRequest request) {
        log.info("Criando um novo combustível");
        var response = combustivelService.createCombustivel(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Combustível criado com sucesso");
        log.debug("Resposta um novo combustível: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os combustíveis")
    public ResponseEntity<List<CombustivelResponse>> findAll() {
        log.info("Buscando todos as combustíveis.");
        var response = combustivelService.listarCombustivel();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar combustível por id")
    public ResponseEntity<CombustivelResponse> findById(@PathVariable Long id) {
        log.info("Buscando o combustível por id: {}", id);
        var response = combustivelService.findCombustivelById(id);

        log.debug("Resposta do combustível por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar combustível buscando por id")
    public ResponseEntity<CombustivelResponse> update(@RequestBody @Valid CombustivelRequest request, @PathVariable Long id) {
        log.info("Atualizando o combustível por id: {}", id);
        var response = combustivelService.updateCombustivel(request, id);


        log.debug("Resposta para atualizar  o combustível por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um combustível buscando por id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deletando o combustível por id: {}", id);
        combustivelService.deleteCombustivel(id);

        log.info("Combustível deletado com sucesso. Id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
