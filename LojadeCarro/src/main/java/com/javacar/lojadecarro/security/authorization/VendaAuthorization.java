package com.javacar.lojadecarro.security.authorization;

import com.javacar.lojadecarro.repository.VendasRepository;
import com.javacar.lojadecarro.security.service.UsuarioAutenticadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("vendaAuthorization")
@RequiredArgsConstructor
public class VendaAuthorization {

    private final VendasRepository vendasRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public boolean relacionadoAVenda(Long idVenda, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        var idUsuario = usuarioAutenticadoService.buscarId(authentication.getName());

        return vendasRepository.usuarioRelacionadoAVenda(idVenda, idUsuario);
    }

    public boolean ehVendedor(Long idVenda, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        var idUsuario = usuarioAutenticadoService.buscarId(authentication.getName());

        return vendasRepository.existsByIdAndVendedor_Id(idVenda, idUsuario);
    }
}