package com.JavangularCar.LojadeCarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de carroceria")
public record CarroceriaResponse(@Schema(example = "1", description = "ID da carroceria")
                                 String id,
                                 @Schema(example = "Hatch", description = "Nome da carroceria")
                                 String nome) {
}
