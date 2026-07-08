package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.dto.response.UsuarioResponse;
import com.JavangularCar.LojadeCarro.entity.Usuario;
import com.JavangularCar.LojadeCarro.service.LoginService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Hidden
@Tag(name = "Login")
@RequiredArgsConstructor
@RestController
@RequestMapping("login")
public class LoginController {

    private final LoginService loginService;

    @GetMapping()
    @Operation(summary = "Logar usuário na API")
    public ResponseEntity<UsuarioResponse> logar(@RequestParam String login,
                                                 @RequestParam String password){
        log.info("Iniciando login");
        var usuario = loginService.logar(login, password);

        return ResponseEntity.ok(usuario);
    }
}
