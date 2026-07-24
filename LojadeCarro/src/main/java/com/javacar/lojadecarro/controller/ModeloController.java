package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.ModeloRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.service.ModeloService;
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
    @Operation(summary = "Cadastrar um novo modelo")
    public ResponseEntity<ModeloResponse> criar(@RequestBody @Valid ModeloRequest request) {
        log.debug("Cadastrar um novo modelo com o corpo: {}", request);
        var response = modeloService.criar(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Modelo criado com sucesso com o id: {}", response.id());
        log.debug("Resposta uma novo modelo: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os modelos")
    public ResponseEntity<List<ModeloResponse>> listar(@RequestParam(defaultValue = "ATIVAS") StatusFiltro status) {
        log.debug("Buscando todos os modelos com o status: {}.", status);
        var response = modeloService.listar(status);

        log.debug("Consulta de todos as modelos com o status: {} realizada com sucesso", status);
        log.debug("A consulta de todos as modelos retornou com o tamanho de: {} valores", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar modelo por id")
    public ResponseEntity<ModeloResponse> buscarPorId(@PathVariable Long id) {
        log.debug("Buscando o modelo por id: {}", id);
        var response = modeloService.buscarPorId(id);

        log.info("Consulta do modelo realizada com sucesso. id={}", id);
        log.debug("Resposta do modelo por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar modelo buscando por id")
    public ResponseEntity<ModeloResponse> atualizar(@RequestBody @Valid ModeloRequest request, @PathVariable Long id) {
        log.debug("Atualizando o modelo com id: {} para o corpo: {}", id, request);
        var response = modeloService.atualizar(request, id);

        log.info("Modelo com o id: {} atualizado com sucesso", id);
        log.debug("Resposta para atualizar o modelo por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar o status de um modelo")
    public ResponseEntity<ModeloResponse> alterarStatus(@PathVariable Long id, @RequestBody @Valid StatusRequest request) {
        log.debug("Alterando status do modelo com id: {} para o status: {}", id, request.ativo());
        var response = modeloService.alterarStatus(id, request);

        log.info("Status do modelo com o id: {} alterado com sucesso", id);
        log.debug("Resposta da alteração de status para o id: {}. Resposta: {}", id, response);
        return ResponseEntity.ok(response);
    }

}
