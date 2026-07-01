package com.JavangularCar.LojadeCarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para criação de uma marca")
public record MarcaRequest(@Schema(example = "Ford", description = "Nome da marca")
                           @NotBlank String nome,
                           @Schema(example = "https://upload.wikimedia.org/wikipedia/commons/3/3e/Ford_logo_flat.svg", description = "URL da marca")
                           @NotBlank String url) {
}
