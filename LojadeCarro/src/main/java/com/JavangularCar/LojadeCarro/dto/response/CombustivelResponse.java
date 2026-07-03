package com.JavangularCar.LojadeCarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta do combustível")
public record CombustivelResponse(@Schema(example = "1", description = "ID do combustível")
                                  Long id,
                                  @Schema(example = "Eletrico", description = "Nome do combustível")
                                  String nome) {
}
