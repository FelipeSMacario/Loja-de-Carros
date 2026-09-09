package com.javacar.lojadecarro.security.service;

import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.exception.security.UsuarioNaoVinculadoException;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioAutenticadoService {

    private final UsuarioRepository usuarioRepository;

    public Usuario buscar(String subject) {
        return usuarioRepository
                .findByIdentityProviderId(subject)
                .orElseThrow(
                        UsuarioNaoVinculadoException::new
                );
    }

    public Long buscarId(String subject) {
        return buscar(subject).getId();
    }
}
