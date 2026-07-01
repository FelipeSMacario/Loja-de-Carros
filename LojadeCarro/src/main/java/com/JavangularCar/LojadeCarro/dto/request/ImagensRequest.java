package com.JavangularCar.LojadeCarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para criação de uma imagem")
public record ImagensRequest(@Schema(example = "https://upload.wikimedia.org/wikipedia/commons/3/3e/Ford_logo_flat.svg", description = "URL da marca")
                             @NotBlank String url,
                             @Schema(example = "1", description = "ID da carro")
                             Long carroId) {
}
