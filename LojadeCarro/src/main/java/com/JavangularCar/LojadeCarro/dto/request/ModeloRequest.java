package com.JavangularCar.LojadeCarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para criação de um modelo")
public record ModeloRequest(@Schema(example = "Onix", description = "Nome do modelo")
                           @NotBlank String nome) {
}
