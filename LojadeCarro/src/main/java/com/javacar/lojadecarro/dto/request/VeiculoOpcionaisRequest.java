package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Request para interação das opcionais com o veiculo")
public record VeiculoOpcionaisRequest(
        @Schema(example = "1", description = "Id do opcional")
        @NotEmpty
        List<@NotNull Long> opcionais
) {
}
