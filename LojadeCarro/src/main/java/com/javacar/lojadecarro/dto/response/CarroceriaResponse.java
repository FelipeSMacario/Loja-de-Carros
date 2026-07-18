package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de carroceria")
public record CarroceriaResponse(@Schema(example = "1", description = "ID da carroceria")
                                 Long id,
                                 @Schema(example = "Hatch", description = "Nome da carroceria")
                                 String nome,
                                 @Schema(example = "true", description = "Status da carroceria")
                                 String ativo) {
}
