package com.javacar.lojadecarro.security.authorization;

import com.javacar.lojadecarro.repository.ImagensRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("imagemAuthorization")
@RequiredArgsConstructor
public class ImagemAuthorization {
    private final ImagensRepository imagensRepository;

    public boolean ehVendedor(
            Long idImagem,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        var idUsuario = Long.valueOf(authentication.getName());

        return imagensRepository.existsByIdAndVeiculo_Vendedor_Id(
                idImagem,
                idUsuario
        );
    }
}
