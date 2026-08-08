package com.javacar.lojadecarro.dto.response;

import com.javacar.lojadecarro.enums.StatusVenda;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Resposta para a venda do veículo")
public record VendaResponse(
        @Schema(example = "1", description = "Identificação unica da venda do veiculo")
        Long id,
        @Schema(example = "25000", description = "Valor da venda do veiculo")
        BigDecimal valorVenda,
        @Schema(example = "EM_ANDAMENTO", description = "Status da venda do veiculo")
        StatusVenda statusVenda,
        @Schema(example = "2025-07-01T14:30:00", description = "Data da venda")
        LocalDateTime dataVenda,
        @Schema(example = "{id: 1, marca: Fiat, modelo: Argo}", description = "Dados resumidos do veiculo")
        VeiculoVendaResponse veiculo,
        @Schema(example = "{id: 1, nome: Felipe}", description = "Dados resumidos do vendedor do veiculo")
        UsuarioResumoResponse vendedor,
        @Schema(example = "{id: 1, nome: Goku}", description = "Dados resumidos do comprador do veiculo")
        UsuarioResumoResponse comprador
) {
}
