package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta com os dados de um opcional")
public record OpcionalResponse(

        @Schema(example = "1", description = "ID do opcional")
        Long id,

        @Schema(
                example = "Freio ABS",
                description = "Nome do opcional"
        )
        String nome,

        @Schema(
                example = "true",
                description = "Status do opcional"
        )
        boolean ativo
) {
}
