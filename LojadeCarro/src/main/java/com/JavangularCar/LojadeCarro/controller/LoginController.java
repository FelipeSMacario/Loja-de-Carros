package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Usuario;
import com.JavangularCar.LojadeCarro.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://192.168.49.2:30000")
@Tag(name = "Login")
@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    LoginService loginService;

    @GetMapping()
    @Operation(summary = "Logar usuário na API")
    public ResponseEntity<Usuario> logar(@RequestParam String login, @RequestParam String password){
        return loginService.logar(login, password);
    }
}
