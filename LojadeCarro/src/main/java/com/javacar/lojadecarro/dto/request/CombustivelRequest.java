package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para criação de um combustível")
public record CombustivelRequest(@Schema(example = "Eletrico", description = "Nome do combustível")
                                 @NotBlank String nome) {
}
