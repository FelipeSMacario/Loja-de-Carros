package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.CorRequest;
import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.service.CoresService;
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
@Tag(name = "Cores")
@RestController
@RequestMapping("cores")
public class CoresController {
    private final CoresService coresService;

    @PostMapping
    @Operation(summary = "Criar uma nova cor")
    public ResponseEntity<CorResponse> create(@RequestBody @Valid CorRequest request) {
        log.info("Criando uma nova cor");
        var response = coresService.createCores(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Cor criado com sucesso");
        log.debug("Resposta uma nova cor: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as cores")
    public ResponseEntity<List<CorResponse>> findAll(@RequestParam(defaultValue = "ATIVAS") StatusFiltro status) {
        log.info("Buscando todas as cores.");
        var response = coresService.listarCores(status);

        log.debug("Retorno da listagem das cores: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cor por id")
    public ResponseEntity<CorResponse> findCoresById(@PathVariable Long id) {
        log.info("Buscando a cor por id: {}", id);
        var response = coresService.findCoresById(id);

        log.debug("Resposta da cor por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cor buscando por id")
    public ResponseEntity<CorResponse> updateCores(@RequestBody @Valid CorRequest cores, @PathVariable Long id) {
        log.info("Atualizando a cor por id: {}", id);
        var response = coresService.updateCores(cores, id);

        log.debug("Resposta atualizar cor por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/inativar/{id}")
    @Operation(summary = "Inativa uma cor buscando por id")
    public ResponseEntity<CorResponse> inativar(@PathVariable Long id) {
        log.info("Inativando uma cor por id: {}", id);
        var response = coresService.alterarStatus(id, false);

        log.info("Cor inativada com sucesso. Id: {}", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/ativar/{id}")
    @Operation(summary = "Ativa uma cor buscando por id")
    public ResponseEntity<CorResponse> ativar(@PathVariable Long id) {
        log.info("Ativando uma cor por id: {}", id);
        var response = coresService.alterarStatus(id, true);

        log.info("Cor ativada com sucesso. Id: {}", id);
        return ResponseEntity.ok(response);
    }
}
