package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para as roles")
public record RoleResponse(
        @Schema(example = "1", description = "ID da role")
                            Long id,
                           @Schema(example = "ADMIN", description = "Descrição da role")
                            String nome,
                           @Schema(example = "true", description = "Status da role")
                           boolean ativo) {
}
