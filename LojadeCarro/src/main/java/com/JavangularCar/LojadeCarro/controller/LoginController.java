package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    LoginService loginService;

    @GetMapping()
    public ResponseEntity<Boolean> logar(@RequestParam String login, @RequestParam String password){
        return loginService.logar(login, password);
    }
}
