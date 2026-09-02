package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.dto.request.CombustivelRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.service.CombustivelService;
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
@Tag(name = "Admin - Combustíveis")
@RestController
@RequestMapping("/admin/combustiveis")
public class AdminCombustivelController {
    private final CombustivelService combustivelService;

    @PostMapping
    @Operation(summary = "Cadastrar um novo combustível")
    public ResponseEntity<CombustivelResponse> criar(@RequestBody
                                                     @Valid CombustivelRequest request) {
        log.debug("Cadastrando um novo combustível com o corpo: {}", request);
        var response = combustivelService.criar(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Combustível com o id: {} criado com sucesso", response.id());
        log.debug("Resposta um novo combustível: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os combustíveis")
    public ResponseEntity<List<CombustivelResponse>> listar(@RequestParam(defaultValue = "TODAS")
                                                            StatusFiltro status) {
        log.debug("Buscando todos as combustíveis com o status: {}.", status);
        var response = combustivelService.listarAdministracao(status);

        log.debug("Consulta de todos os combustiveis com o status: {} realizada com sucesso", status);
        log.debug("A consulta de todos os combustiveis retornou com o tamanho de: {} valores", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar combustível por id")
    public ResponseEntity<CombustivelResponse> buscaPorId(@PathVariable Long id) {
        log.debug("Buscando o combustível por id: {}", id);
        var response = combustivelService.buscarPorIdAdministracao(id);

        log.info("Busca do combustivel com id: {} realizada com sucesso", id);
        log.debug("Resposta do combustível por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar combustível buscando por id")
    public ResponseEntity<CombustivelResponse> atualizar(@RequestBody
                                                         @Valid CombustivelRequest request,
                                                         @PathVariable Long id) {
        log.debug("Atualizando o combustível com id: {} para o corpo: {}", id, request);
        var response = combustivelService.atualizar(request, id);

        log.info("Combustivel com o id: {} atualizado com sucesso", id);
        log.debug("Resposta para atualizar  o combustível por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar o status de um combustível")
    public ResponseEntity<CombustivelResponse> alterarStatus(@PathVariable Long id,
                                                             @RequestBody
                                                             @Valid StatusRequest request) {
        log.debug("Alterando status do combustível com id: {} para o status: {}", id, request.ativo());
        var response = combustivelService.alterarStatus(id, request);

        log.info("Status do combustivel com o id: {} alterado com sucesso", id);
        log.debug("Resposta da alteração de status para o id: {}. Resposta: {}", id, response);
        return ResponseEntity.ok(response);
    }

}
