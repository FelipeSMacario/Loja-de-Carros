package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para criação de um combustível")
public record CoresRequest(@Schema(example = "Branco", description = "Nome da cor")
                           @NotBlank String nome) {
}
