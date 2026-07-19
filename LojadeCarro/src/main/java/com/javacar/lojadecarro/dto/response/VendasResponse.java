package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Schema(description = "Resposta para a compra do carro")
public record VendasResponse(@Schema(example = "1", description = "ID da compra") Long id,
                             @Schema(example = "100.00", description = "Valor do carro") BigDecimal valor,
                             @Schema(example = "2025-07-01T14:30:00Z", description = "Data da compra") LocalDateTime dataVenda) {
}
