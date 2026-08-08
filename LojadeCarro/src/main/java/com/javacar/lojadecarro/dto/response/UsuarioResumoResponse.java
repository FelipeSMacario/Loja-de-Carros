package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo de um usuário")
public record UsuarioResumoResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "Felipe")
        String nome
) {
}