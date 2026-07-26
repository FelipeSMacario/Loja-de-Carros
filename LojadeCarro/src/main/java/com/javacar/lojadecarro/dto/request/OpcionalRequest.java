package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para criação de um opcional")
public record OpcionalRequest(@Schema(example = "Freio ABS", description = "Nome do opcional")
                              @NotBlank String nome) {
}
