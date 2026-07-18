package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.OpcionalRequest;
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
@Tag(name = "Opcional")
@RestController
@RequestMapping("/opcional")
public class OpcionalController {
    private final OpcionalService opcionalService;

    @PostMapping
    @Operation(summary = "Criar um novo kit")
    public ResponseEntity<OpcionalResponse> createKit(@RequestBody @Valid OpcionalRequest request) {
        log.info("Criando um novo kit");
        var response = opcionalService.createKit(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Kit criado com sucesso");
        log.debug("Resposta um novo kit: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os kits")
    public ResponseEntity<List<OpcionalResponse>> listarKit(@RequestParam(defaultValue = "ATIVAS") StatusFiltro status) {
        log.info("Buscando todos os kits.");
        var response = opcionalService.listarOpcionais(status);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar kit por id")
    public ResponseEntity<OpcionalResponse> findKitById(@PathVariable Long id) {
        log.info("Buscando o kit por id: {}", id);
        var response = opcionalService.filtrarKit(id);

        log.debug("Resposta do kit por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar kit buscando por id")
    public ResponseEntity<OpcionalResponse> updateKit(@RequestBody @Valid OpcionalRequest request, @PathVariable Long id) {
        log.info("Atualizando a kit por id: {}", id);
        var response = opcionalService.updateKit(request, id);

        log.debug("Resposta atualizar o kit por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/inativar/{id}")
    @Operation(summary = "Inativar um kit buscando por id")
    public ResponseEntity<OpcionalResponse> inativar(@PathVariable Long id) {
        log.info("Inativando um kit por id: {}", id);
        var response = opcionalService.alterarStatus(id, false);

        log.info("Kit inativado com sucesso. Id: {}", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/ativar/{id}")
    @Operation(summary = "Ativar um kit buscando por id")
    public ResponseEntity<OpcionalResponse> ativar(@PathVariable Long id) {
        log.info("Ativando um kit por id: {}", id);
        var response = opcionalService.alterarStatus(id, true);

        log.info("Kit ativado com sucesso. Id: {}", id);
        return ResponseEntity.ok(response);
    }
}
