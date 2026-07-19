package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.LoginRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.exception.security.LoginSenhaException;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import com.javacar.lojadecarro.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final LoginRepository loginRepository;

    private final BCryptPasswordEncoder encoder;
    private final UsuarioMapper usuarioMapper;

    public UsuarioResponse autenticar(LoginRequest loginRequest) {
        return loginRepository.findByEmail(loginRequest.login())
                .map(usuario -> {
                            if (!encoder.matches(loginRequest.senha(), usuario.getPassword())) {
                                throw new LoginSenhaException();
                            }
                            return usuarioMapper.toResponse(usuario);

                        }
                )
                .orElseThrow(LoginSenhaException::new);

    }

}
