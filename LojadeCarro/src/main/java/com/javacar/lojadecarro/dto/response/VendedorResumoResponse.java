package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo público do vendedor")
public record VendedorResumoResponse(
        Long id,
        String nome
) {
}