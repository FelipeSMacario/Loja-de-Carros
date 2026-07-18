package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para criação de uma opcional")
public record OpcionalResponse(@Schema(example = "1", description = "Id do opcional")
                               Long id,
                               @Schema(example = "Freio ABS", description = "Nome do opcional")
                               String nome) {
}
