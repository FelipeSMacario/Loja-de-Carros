package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request vincular uma role ao usuário")
public record RoleRequest(@Schema(example = "1", description = "Id da role")
                          @NotNull Long id) {
}
