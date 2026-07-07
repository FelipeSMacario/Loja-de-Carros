package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.dto.request.UsuarioRequest;
import com.JavangularCar.LojadeCarro.dto.response.UsuarioResponse;
import com.JavangularCar.LojadeCarro.service.UsuarioService;
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
@RequestMapping("usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Criar um novo usuário")
    public ResponseEntity<UsuarioResponse> createUsuario(@RequestBody @Valid UsuarioRequest usuario) {
        log.info("Criando uma novo usuário");
        var response = usuarioService.createUsuario(usuario);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Usuário criado com sucesso");
        log.debug("Resposta uma novo usuário: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuário")
    public ResponseEntity<List<UsuarioResponse>> listarUsuario() {
        log.info("Buscando todos os usuários.");
        var response = usuarioService.listarUsuario();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por id")
    public ResponseEntity<UsuarioResponse> findUsuarioBId(@PathVariable Long id) {
        log.info("Buscando o usuário por id: {}", id);
        var response = usuarioService.findUsuarioBId(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário buscando por id")
    public ResponseEntity<UsuarioResponse> updateUsuario(@RequestBody @Valid UsuarioRequest usuario, @PathVariable Long id) {
        log.info("Atualizando o usuário por id: {}", id);
        var response = usuarioService.updateUsuario(usuario, id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma usuário buscando por id")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        log.info("Deletando o usuário por id: {}", id);
        usuarioService.deleteUsuario(id);

        log.info("Usuário deletado com sucesso. Id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
