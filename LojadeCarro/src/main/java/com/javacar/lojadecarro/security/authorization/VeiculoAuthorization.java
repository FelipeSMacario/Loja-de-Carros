package com.javacar.lojadecarro.security.authorization;

import com.javacar.lojadecarro.repository.VeiculoRepository;
import com.javacar.lojadecarro.security.service.UsuarioAutenticadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("veiculoAuthorization")
@RequiredArgsConstructor
public class VeiculoAuthorization {

    private final VeiculoRepository veiculoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public boolean ehVendedor(Long idVeiculo, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        var idUsuario = usuarioAutenticadoService.buscarId(authentication.getName());

        return veiculoRepository.existsByIdAndVendedor_Id(idVeiculo, idUsuario);
    }
}