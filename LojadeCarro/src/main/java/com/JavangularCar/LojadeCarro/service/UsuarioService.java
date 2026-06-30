package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.entity.Usuario;
import com.JavangularCar.LojadeCarro.repository.UsuarioRepository;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public Usuario createUsuario(@RequestBody Usuario usuario) {
        usuario.setPassword(encoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuario() {
        return usuarioRepository.findAll();
    }

    public Usuario findUsuarioBId(Long id) {
        return usuarioRepository.findById(id)
                .stream().findFirst()
                .orElseThrow(() -> new ServiceException("Usuário não encontrado"));
    }

    public ResponseEntity updateUsuario(@RequestBody Usuario usuario, Long id) {
        return usuarioRepository.findById(id)
                .map(record -> {
                    record.setCpf(usuario.getCpf());
                    record.setDtNascimento(usuario.getDtNascimento());
                    record.setNome(usuario.getNome());
                    record.setEmail(usuario.getEmail());
                    record.setPassword(encoder.encode(usuario.getPassword()));
                    Usuario update = usuarioRepository.save(record);
                    return ResponseEntity.ok().body(update);
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity deleteUsuario(Long id) {
        return usuarioRepository.findById(id)
                .map(record -> {
                    usuarioRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
