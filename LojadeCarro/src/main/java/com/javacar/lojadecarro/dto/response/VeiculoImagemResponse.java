package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Imagem da galeria do veículo")
public record VeiculoImagemResponse(
        Long id,
        boolean principal
) {
}
