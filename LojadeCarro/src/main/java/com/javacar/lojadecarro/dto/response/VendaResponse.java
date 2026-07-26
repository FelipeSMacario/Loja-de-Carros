package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Resposta para a compra do veiculo")
public record VendaResponse(@Schema(example = "1", description = "ID da compra") Long id,
                            @Schema(example = "100.00", description = "Valor do veiculo") BigDecimal valorVenda,
                            @Schema(example = "2025-07-01T14:30:00Z", description = "Data da compra") LocalDateTime dataVenda) {
}
