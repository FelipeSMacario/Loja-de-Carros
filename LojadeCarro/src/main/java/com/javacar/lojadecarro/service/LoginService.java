package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.LoginRequest;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import com.javacar.lojadecarro.security.dto.LoginResponse;
import com.javacar.lojadecarro.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioMapper usuarioMapper;

    public LoginResponse autenticar(LoginRequest loginRequest) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.login(),
                        loginRequest.senha()
                )
        );

        var token = jwtService.gerarToken(authentication);

        var usuario = (Usuario) authentication.getPrincipal();
        var usuarioResponse = usuarioMapper.toResponse(usuario);

        return new LoginResponse(
                token,
                "Bearer",
                usuarioResponse
        );
    }

}
