package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.entity.Usuario;
import com.JavangularCar.LojadeCarro.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://192.168.49.2:30000")
@Tag(name = "Usuario")
@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Criar um novo usuário")
    public Usuario createUsuario (@RequestBody Usuario usuario){
        return usuarioService.createUsuario(usuario);
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuário")
    public List<Usuario> listarUsuario(){
        return usuarioService.listarUsuario();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por id")
    public ResponseEntity<Usuario> findUsuarioBId (@PathVariable Long id){
        var response = usuarioService.findUsuarioBId(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário buscando por id")
    public ResponseEntity updateUsuario(@RequestBody Usuario usuario, @PathVariable Long id){
        return usuarioService.updateUsuario(usuario, id);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma usuário buscando por id")
    public ResponseEntity deleteUsuario(@PathVariable Long id){
        return usuarioService.deleteUsuario(id);
    }
}
