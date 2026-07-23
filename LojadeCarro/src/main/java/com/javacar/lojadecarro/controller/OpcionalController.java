package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.OpcionalRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.service.OpcionalService;
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
@Tag(name = "opcional")
@RestController
@RequestMapping("/opcional")
public class OpcionalController {
    private final OpcionalService opcionalService;

    @PostMapping
    @Operation(summary = "Cadastrar um novo opcional")
    public ResponseEntity<OpcionalResponse> criar(@RequestBody @Valid OpcionalRequest request) {
        log.debug("Cadastrar uma novo opcional com o corpo: {}", request);
        var response = opcionalService.criar(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("opcional criado com sucesso com o id: {}", response.id());
        log.debug("Resposta uma novo opcional: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os opcionals")
    public ResponseEntity<List<OpcionalResponse>> listar(@RequestParam(defaultValue = "ATIVAS") StatusFiltro status) {
        log.debug("Buscando todos as opcionais com o status: {}.", status);
        var response = opcionalService.listar(status);

        log.debug("Consulta de todos as opcionais com o status: {} realizada com sucesso", status);
        log.debug("A consulta de todos as opcionais retornou com o tamanho de: {} valores", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar opcional por id")
    public ResponseEntity<OpcionalResponse> buscarPorId(@PathVariable Long id) {
        log.debug("Buscando o opcional por id: {}", id);
        var response = opcionalService.buscarPorId(id);

        log.info("Consulta do opcional realizada com sucesso. id={}", id);
        log.debug("Resposta do opcional por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar opcional buscando por id")
    public ResponseEntity<OpcionalResponse> atualizar(@RequestBody @Valid OpcionalRequest request, @PathVariable Long id) {
        log.debug("Atualizando o opcional com id: {} para o corpo: {}", id, request);
        var response = opcionalService.atualizar(request, id);

        log.debug("Resposta atualizar o opcional por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar o status de um opcional")
    public ResponseEntity<OpcionalResponse> alterarStatus(@PathVariable Long id, @RequestBody @Valid StatusRequest request) {
        log.debug("Alterando status da opcional com id: {} para o status: {}", id, request.ativo());
        var response = opcionalService.alterarStatus(id, request);

        log.info("Status do opcional com o id: {} alterado com sucesso", id);
        log.debug("Resposta da alteração de status para o id: {}. Resposta: {}", id, response);
        return ResponseEntity.ok(response);
    }
}
