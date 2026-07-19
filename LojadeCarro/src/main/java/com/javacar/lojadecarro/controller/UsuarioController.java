package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.RoleRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioRolesResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuario")
@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Cadastrar um novo usuário")
    public ResponseEntity<UsuarioResponse> criar(@RequestBody @Valid UsuarioRequest request) {
        log.debug("Cadastrar um novo usuário com o corpo: {}", request);
        var response = usuarioService.criar(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Usuário criado com sucesso com o id: {}", response.id());
        log.debug("Resposta um novo usuário: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuário")
    public ResponseEntity<List<UsuarioResponse>> listar(@RequestParam(defaultValue = "ATIVAS") StatusFiltro status) {
        log.debug("Buscando todos os usuários com o status: {}.", status);
        var response = usuarioService.listar(status);

        log.debug("Consulta de todos os usuários com o status: {} realizada com sucesso", status);
        log.debug("A consulta de todos os usuários retornou com o tamanho de: {} valores", response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por id")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        log.debug("Buscando o usuário por id: {}", id);
        var response = usuarioService.buscarPorId(id);
        log.info("Consulta do usuário realizada com sucesso. id={}", id);
        log.debug("Resposta do usuário por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário buscando por id")
    public ResponseEntity<UsuarioResponse> atualizar(@RequestBody @Valid UsuarioRequest request, @PathVariable Long id) {
        log.debug("Atualizando o usuário com id: {} para o corpo: {}", id, request);
        var response = usuarioService.atualizar(request, id);

        log.info("Usuário com o id: {} atualizado com sucesso", id);
        log.debug("Resposta para atualizar o usuário por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar o status de um usuário")
    public ResponseEntity<UsuarioResponse> alterarStatus(@PathVariable Long id,
                                                         @RequestBody @Valid StatusRequest request) {
        log.debug("Alterando status do usuário com id: {} para o status: {}", id, request.ativo());
        var response = usuarioService.alterarStatus(id, request);

        log.info("Status do usuário com o id: {} alterado com sucesso", id);
        log.debug("Resposta da alteração de status para o id: {}. Resposta: {}", id, response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/roles")
    @Operation(summary = "Cadastrar uma role para um usuário")
    public ResponseEntity<UsuarioRolesResponse> vincularRole(@PathVariable Long id,
                                                          @RequestBody List<RoleRequest> requests) {
        log.debug("Cadastrando roles para o usuário com id: {}", id);
        var response = usuarioService.vincularRole(id, requests);

        log.info("Roles vinculadas com sucesso ao usuário id={}", id);
        log.debug("Resposta da vinculação das roles: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    @Operation(summary = "Desvincular uma role para um usuário")
    public ResponseEntity<UsuarioRolesResponse> desvincularRole(@PathVariable Long id,
                                                                @PathVariable Long roleId) {
        log.debug("Desvinculando role {} do usuário {}", roleId, id);
        var response = usuarioService.desvincularRole(id, roleId);

        log.info("Roles removidas com sucesso. Id: {}", id);
        log.debug("Resposta da remoção da role: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "Buscar as roles para um usuário")
    public ResponseEntity<UsuarioRolesResponse> buscarRolesUsuario(@PathVariable Long id) {
        log.debug("Buscando todas as roles para o usuário com id: {}.", id);
        var response = usuarioService.buscarRolesUsuario(id);
        log.info("Consulta das roles do usuário realizada com sucesso. id={}", id);
        log.debug("A consulta de todos as roles retornou com o tamanho de: {} valores", response.roles().size());
        return ResponseEntity.ok(response);
    }
}
