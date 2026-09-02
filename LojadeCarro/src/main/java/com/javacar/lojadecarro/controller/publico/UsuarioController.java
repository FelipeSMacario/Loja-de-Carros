package com.javacar.lojadecarro.controller.publico;

import com.javacar.lojadecarro.dto.request.AlteracaoSenhaRequest;
import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.request.UsuarioUpdateRequest;
import com.javacar.lojadecarro.dto.response.AlteracaoSenhaResponse;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Cadastrar um novo usuário")
    public ResponseEntity<UsuarioResponse> criar(@RequestBody @Valid UsuarioRequest request) {
        log.debug("Cadastrar um novo usuário ");
        var response = usuarioService.criar(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Usuário criado com sucesso com o id: {}", response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Buscar dados do usuário autenticado")
    public ResponseEntity<UsuarioResponse> buscarMeuUsuario(@AuthenticationPrincipal Jwt jwt) {
        var id = Long.valueOf(jwt.getSubject());
        log.debug("Usuário logado para buscar seu usuário com o id: {}", id);
        var response = usuarioService.buscarMeuUsuario(id);

        log.info("Usuário com o id: {} buscado com sucesso", id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/desativar")
    @Operation(summary = "Desativar usuário")
    public ResponseEntity<UsuarioResponse> desativarUsuario(@AuthenticationPrincipal Jwt jwt) {
        var id = Long.valueOf(jwt.getSubject());
        log.debug("Usuário logado para ser desativado com o id: {}", id);
        var response = usuarioService.desativarUsuario(id);

        log.info("Usuário com o id: {} desativado com sucesso", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @Operation(summary = "Atualizar dados do usuário autenticado")
    public ResponseEntity<UsuarioResponse> atualizar(@RequestBody
                                                     @Valid UsuarioUpdateRequest request,
                                                     @AuthenticationPrincipal Jwt jwt) {
        var id = Long.valueOf(jwt.getSubject());
        log.debug("Usuário logado para atualização com o id: {}", id);
        var response = usuarioService.atualizar(request, id);

        log.info("Usuário com o id: {} atualizado com sucesso", id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/senha")
    @Operation(summary = "Atualizar a senha do usuário")
    public ResponseEntity<AlteracaoSenhaResponse> alterarSenha(@RequestBody
                                                               @Valid AlteracaoSenhaRequest request,
                                                               @AuthenticationPrincipal Jwt jwt) {
        var id = Long.valueOf(jwt.getSubject());
        log.debug("Usuário logado para alteração de senha com o id: {}", id);

        var response = usuarioService.alterarSenha(request, id);
        log.info("Usuário com o id: {} teve a senha atualizada com sucesso", id);
        return ResponseEntity.ok(response);
    }
}
