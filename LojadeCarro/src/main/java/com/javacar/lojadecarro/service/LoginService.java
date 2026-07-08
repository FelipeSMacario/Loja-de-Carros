package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.exception.LoginSenhaException;
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

    public UsuarioResponse logar(String login, String password) {
        log.info("Buscando login {}", login);
        return loginRepository.findByEmail(login)
                .map(log -> {
                            if (!encoder.matches(password, log.getPassword())) {
                                throw new LoginSenhaException();
                            }
                            return usuarioMapper.toResponse(log);

                        }
                )
                .orElseThrow(LoginSenhaException::new);

    }

}
