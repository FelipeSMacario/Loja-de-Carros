package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request para a compra do carro")
public record VendasRequest(@Schema(example = "1", description = "ID do carro que será vendido")
                             @NotNull Long veiculoId,
                            @Schema(example = "1", description = "ID do usuario que vai comprar o carro")
                             @NotNull Long compradorId,
                            @Schema(example = "1", description = "ID do usuario que vai vender o carro")
                             @NotNull Long vendedorId,
                            @Schema(example = "100.000", description = "Valor do carro")
                             @NotNull BigDecimal valor) {
}
