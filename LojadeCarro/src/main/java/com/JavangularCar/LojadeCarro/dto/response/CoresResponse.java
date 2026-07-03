package com.JavangularCar.LojadeCarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Resposta para criação das cores")
public record CoresResponse(@Schema(example = "1", description = "ID da cor")
                            Long id,
                            @Schema(example = "Branco", description = "Nome da cor")
                            String nome) {
}
