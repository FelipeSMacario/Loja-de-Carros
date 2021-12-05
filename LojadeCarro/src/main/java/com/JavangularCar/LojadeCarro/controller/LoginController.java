package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Usuario;
import com.JavangularCar.LojadeCarro.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@Api(tags = "Login")
@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    LoginService loginService;

    @GetMapping()
    @ApiOperation(value = "Logar usuário na API")
    public ResponseEntity<Usuario> logar(@RequestParam String login, @RequestParam String password){
        return loginService.logar(login, password);
    }
}
