package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Usuario;
import com.JavangularCar.LojadeCarro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PostMapping
    public Usuario createUsuario (@RequestBody Usuario usuario){
        return usuarioService.createUsuario(usuario);
    }

    @GetMapping
    public List<Usuario> listarUsuario(){
        return usuarioService.listarUsuario();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findUsuarioBId (@PathVariable Long id){
        return usuarioService.findUsuarioBId(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity updateUsuario(@RequestBody Usuario usuario, @PathVariable Long id){
        return usuarioService.updateUsuario(usuario, id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteUsuario(@PathVariable Long id){
        return usuarioService.deleteUsuario(id);
    }
}
