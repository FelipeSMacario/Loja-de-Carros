package com.javacar.lojadecarro.security.authorization;

import com.javacar.lojadecarro.repository.ImagensRepository;
import com.javacar.lojadecarro.security.service.UsuarioAutenticadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("imagemAuthorization")
@RequiredArgsConstructor
public class ImagemAuthorization {
    private final ImagensRepository imagensRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public boolean ehVendedor(Long idImagem, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        var idUsuario = usuarioAutenticadoService.buscarId(authentication.getName());

        return imagensRepository.existsByIdAndVeiculo_Vendedor_Id(idImagem, idUsuario);
    }
}
