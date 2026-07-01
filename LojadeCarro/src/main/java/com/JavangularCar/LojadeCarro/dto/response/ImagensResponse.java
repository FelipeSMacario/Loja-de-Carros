package com.JavangularCar.LojadeCarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Resposta para criação da imagem")
public record ImagensResponse(@Schema(example = "1", description = "ID da imagem")
                              String id,
                              @Schema(example = "https://upload.wikimedia.org/wikipedia/commons/3/3e/Ford_logo_flat.svg", description = "URL da marca")
                              @NotBlank String url) {
}
