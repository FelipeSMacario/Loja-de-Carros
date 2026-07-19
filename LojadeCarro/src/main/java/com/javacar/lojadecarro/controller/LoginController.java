package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.LoginRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Login")
@RequiredArgsConstructor
@RestController
@RequestMapping("login")
public class LoginController {

    private final LoginService loginService;

    @PostMapping()
    @Operation(summary = "Autenticar usuário")
    public ResponseEntity<UsuarioResponse> autenticar(@RequestBody @Valid LoginRequest loginRequest) {
        var usuario = loginService.autenticar(loginRequest);
        log.info("Autenticação realizada com sucesso");

        return ResponseEntity.ok(usuario);
    }
}
