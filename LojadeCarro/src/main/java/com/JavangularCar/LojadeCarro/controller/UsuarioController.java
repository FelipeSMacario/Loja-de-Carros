package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.model.Usuario;
import com.JavangularCar.LojadeCarro.service.UsuarioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@Api(tags = "Usuario")
@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PostMapping
    @ApiOperation(value = "Criar um novo usuário")
    public Usuario createUsuario (@RequestBody Usuario usuario){
        return usuarioService.createUsuario(usuario);
    }

    @GetMapping
    @ApiOperation(value = "Listar todos os usuário")
    public List<Usuario> listarUsuario(){
        return usuarioService.listarUsuario();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Buscar usuário por id")
    public ResponseEntity<Usuario> findUsuarioBId (@PathVariable Long id){
        return usuarioService.findUsuarioBId(id);
    }
    @PutMapping("/{id}")
    @ApiOperation(value = "Atualizar usuário buscando por id")
    public ResponseEntity updateUsuario(@RequestBody Usuario usuario, @PathVariable Long id){
        return usuarioService.updateUsuario(usuario, id);
    }
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletar uma usuário buscando por id")
    public ResponseEntity deleteUsuario(@PathVariable Long id){
        return usuarioService.deleteUsuario(id);
    }
}
