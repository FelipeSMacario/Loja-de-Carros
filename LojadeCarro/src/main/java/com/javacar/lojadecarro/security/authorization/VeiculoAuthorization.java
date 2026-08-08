package com.javacar.lojadecarro.security.authorization;

import org.springframework.security.core.Authentication;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("veiculoAuthorization")
@RequiredArgsConstructor
public class VeiculoAuthorization {
    private final VeiculoRepository veiculoRepository;

    public boolean ehVendedor(
            Long idVeiculo,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        var idUsuario = Long.valueOf(authentication.getName());

        return veiculoRepository.existsByIdAndVendedor_Id(
                idVeiculo,
                idUsuario
        );
    }
}
