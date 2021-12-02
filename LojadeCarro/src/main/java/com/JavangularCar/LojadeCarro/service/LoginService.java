package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.model.Usuario;
import com.JavangularCar.LojadeCarro.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    LoginRepository loginRepository;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ResponseEntity<Usuario> logar(String login, String password) {

        Optional<Usuario> usuario = loginRepository.findByEmail(login);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            Boolean valid = encoder.matches(password, usuario.get().getPassword());
            if (valid) return ResponseEntity.status(HttpStatus.OK).body(usuario.get());
            else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }


    }


    public ResponseEntity<Boolean> logar2(String login, String password) {

        Optional<Usuario> usuario = loginRepository.findByEmail(login);

        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);

        Boolean valid = false;

        Usuario usu = usuario.get();
        valid = encoder.matches(password, usu.getPassword());

        HttpStatus status = ((valid) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);

        return ResponseEntity.status(status).body(valid);

    }
}
