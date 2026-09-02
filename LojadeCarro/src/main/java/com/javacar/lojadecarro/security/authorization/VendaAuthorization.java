package com.javacar.lojadecarro.security.authorization;

import com.javacar.lojadecarro.repository.VendasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("vendaAuthorization")
@RequiredArgsConstructor
public class VendaAuthorization {
    private final VendasRepository vendasRepository;

    public boolean relacionadoAVenda(Long idVenda,
                                     Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        var idUsuario = Long.valueOf(authentication.getName());

        return vendasRepository.usuarioRelacionadoAVenda(idVenda, idUsuario);
    }
    public boolean ehVendedor(Long idVenda,
                              Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        var idUsuario = Long.valueOf(authentication.getName());

        return vendasRepository.existsByIdAndVendedor_Id(idVenda, idUsuario);
    }

}
