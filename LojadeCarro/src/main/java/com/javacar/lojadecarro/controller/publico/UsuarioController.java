package com.javacar.lojadecarro.controller.publico;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.request.UsuarioUpdateRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.security.service.UsuarioAutenticadoService;
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
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    @PostMapping
    @Operation(summary = "Cadastrar o perfil de um novo usuário")
    public ResponseEntity<UsuarioResponse> criar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid UsuarioRequest request
    ) {
        var subject = jwt.getSubject();
        var email = jwt.getClaimAsString("email");

        log.debug(
                "Cadastrando perfil local para o usuário autenticado"
        );

        var response = usuarioService.criar(
                request,
                subject,
                email
        );

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info(
                "Perfil do usuário criado com sucesso. Id: {}",
                response.id()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Buscar dados do usuário autenticado")
    public ResponseEntity<UsuarioResponse> buscarMeuUsuario(@AuthenticationPrincipal Jwt jwt) {
        var id = usuarioAutenticadoService.buscarId(jwt.getSubject());
        log.debug("Usuário logado para buscar seu usuário com o id: {}", id);
        var response = usuarioService.buscarMeuUsuario(id);

        log.info("Usuário com o id: {} buscado com sucesso", id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/desativar")
    @Operation(summary = "Desativar usuário")
    public ResponseEntity<UsuarioResponse> desativarUsuario(@AuthenticationPrincipal Jwt jwt) {
        var id = usuarioAutenticadoService.buscarId(jwt.getSubject());
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
        var id = usuarioAutenticadoService.buscarId(jwt.getSubject());
        log.debug("Usuário logado para atualização com o id: {}", id);
        var response = usuarioService.atualizar(request, id);

        log.info("Usuário com o id: {} atualizado com sucesso", id);
        return ResponseEntity.ok(response);
    }

}
