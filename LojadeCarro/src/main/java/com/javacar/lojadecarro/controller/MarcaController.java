package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.MarcaRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.service.MarcaService;
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
    public ResponseEntity<MarcaResponse> criar(@RequestBody @Valid MarcaRequest request) {
        log.debug("Cadastrar uma nova marca com o corpo: {}", request);
        var response = marcaService.criar(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Marca criada com sucesso com o id: {}", response.id());
        log.debug("Resposta da criação da marca: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as marcas")
    public ResponseEntity<List<MarcaResponse>> listar(@RequestParam(defaultValue = "ATIVAS") StatusFiltro status) {
        log.debug("Buscando todas as marcas com o status: {}.", status);
        var response = marcaService.listar(status);

        log.debug("Consulta de todas as marcas com o status: {} realizada com sucesso", status);
        log.debug("A consulta de todos as marcas retornou com o tamanho de: {} valores", response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar marca por id")
    public ResponseEntity<MarcaResponse> buscarPorId(@PathVariable Long id) {
        log.debug("Buscando a marca por id: {}", id);
        var response = marcaService.buscarPorId(id);

        log.info("Consulta da marca realizada com sucesso. id={}", id);
        log.debug("Resposta da marca por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar marca buscando por id")
    public ResponseEntity<MarcaResponse> atualizar(@RequestBody @Valid MarcaRequest request,
                                                   @PathVariable Long id) {
        log.debug("Atualizando a marca com id: {} para o corpo: {}", id, request);
        var response = marcaService.atualizar(request, id);

        log.info("Marca com o id: {} atualizada com sucesso", id);
        log.debug("Resposta para atualizar  a marca por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar o status de uma marca")
    public ResponseEntity<MarcaResponse> alterarStatus(@PathVariable Long id,
                                                       @RequestBody @Valid StatusRequest request) {
        log.debug("Alterando status da marca com id: {} para o status: {}", id, request.ativo());
        var response = marcaService.alterarStatus(id, request);

        log.info("Status da marca com o id: {} alterado com sucesso", id);
        log.debug("Resposta da alteração de status para o id: {}. Resposta: {}", id, response);
        return ResponseEntity.ok(response);
    }

}
