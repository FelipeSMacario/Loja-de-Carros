package com.JavangularCar.LojadeCarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para criação dos modelo")
public record ModeloResponse(@Schema(example = "1", description = "ID do modelo")
                            String id,
                             @Schema(example = "Onix", description = "Nome do modelo")
                            String nome) {
}
