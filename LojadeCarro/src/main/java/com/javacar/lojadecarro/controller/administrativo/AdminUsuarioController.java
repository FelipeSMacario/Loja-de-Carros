package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin - Usuarios")
@RestController
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar os usuário")
    public ResponseEntity<List<UsuarioResponse>> listar(@RequestParam(defaultValue = "TODAS") StatusFiltro status) {
        log.debug("Buscando os usuários com o status: {}.", status);
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
}
