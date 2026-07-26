package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para criação das cor")
public record CorResponse(@Schema(example = "1", description = "ID da cor")
                            Long id,
                          @Schema(example = "Branco", description = "Nome da cor")
                            String nome,
                          @Schema(example = "true", description = "Status da cor")
                          boolean ativo) {
}
