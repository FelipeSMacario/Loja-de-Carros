package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request para a compra do veiculo")
public record VendaRequest(@Schema(example = "1", description = "ID do veiculo que será vendido")
                           @NotNull Long veiculoId) {
}
