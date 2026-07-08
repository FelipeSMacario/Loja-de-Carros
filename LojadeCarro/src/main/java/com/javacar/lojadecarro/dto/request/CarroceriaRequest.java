package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para criação de carroceria")
public record CarroceriaRequest(@Schema(example = "Hatch", description = "Nome da carroceria")
                                @NotBlank String nome) {
}
