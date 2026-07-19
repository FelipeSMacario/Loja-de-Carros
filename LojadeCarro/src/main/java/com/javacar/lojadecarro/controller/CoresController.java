package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.CorRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
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
    @Operation(summary = "Cadastrar uma nova cor")
    public ResponseEntity<CorResponse> criar(@RequestBody @Valid CorRequest request) {
        log.debug("Cadastrar uma nova cor com o corpo: {}", request);
        var response = coresService.criar(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Cor criada com sucesso com o id: {}", response.id());
        log.debug("Resposta uma nova cor: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as cores")
    public ResponseEntity<List<CorResponse>> listar(@RequestParam(defaultValue = "ATIVAS") StatusFiltro status) {
        log.debug("Buscando todas as cores com o status: {}.", status);
        var response = coresService.listar(status);

        log.debug("Consulta de todas as cores com o status: {} realizada com sucesso", status);
        log.debug("A consulta de todos as cores retornou com o tamanho de: {} valores", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cor por id")
    public ResponseEntity<CorResponse> buscarPorId(@PathVariable Long id) {
        log.debug("Buscando a cor por id: {}", id);
        var response = coresService.buscarPorId(id);

        log.info("Consulta da cor realizada com sucesso. id={}", id);
        log.debug("Resposta da cor por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cor buscando por id")
    public ResponseEntity<CorResponse> atualizar(@RequestBody @Valid CorRequest request, @PathVariable Long id) {
        log.debug("Atualizando a cor com id: {} para o corpo: {}", id, request);
        var response = coresService.atualizar(request, id);

        log.info("Cor com o id: {} atualizada com sucesso", id);
        log.debug("Resposta para atualizar  a cor por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar o status de uma cor")
    public ResponseEntity<CorResponse> alterarStatus(@PathVariable Long id, @RequestBody @Valid StatusRequest request) {
        log.debug("Alterando status da cor com id: {} para o status: {}", id, request.ativo());
        var response = coresService.alterarStatus(id, request);

        log.info("Status da cor com o id: {} alterado com sucesso", id);
        log.debug("Resposta da alteração de status para o id: {}. Resposta: {}", id, response);
        return ResponseEntity.ok(response);
    }
}
