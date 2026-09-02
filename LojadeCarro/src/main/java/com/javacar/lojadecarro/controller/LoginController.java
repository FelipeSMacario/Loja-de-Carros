package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.LoginRequest;
import com.javacar.lojadecarro.security.dto.LoginResponse;
import com.javacar.lojadecarro.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Login")
@RequiredArgsConstructor
@RestController
@RequestMapping("login")
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    @SecurityRequirements
    @Operation(summary = "Autenticar usuário")
    public ResponseEntity<LoginResponse> autenticar(
            @RequestBody @Valid LoginRequest loginRequest
    ) {
        var response = loginService.autenticar(loginRequest);

        log.info("Autenticação realizada com sucesso");

        return ResponseEntity.ok(response);
    }
}
