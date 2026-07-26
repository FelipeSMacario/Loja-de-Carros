package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Request para interação das roles com o usuário")
public record UsuarioRolesRequest(
        @Schema(example = "1", description = "Id da role")
        @NotEmpty
        List<@NotNull Long> roles
) {
}
