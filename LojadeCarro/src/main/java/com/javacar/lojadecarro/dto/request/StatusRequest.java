package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request para alteração de status de alguma entidade")
public record StatusRequest(@Schema(example = "true", description = "Valor qual a entidade terá seu status alterado")
                            @NotNull Boolean ativo) {
}
