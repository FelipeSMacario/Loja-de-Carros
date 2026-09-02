package com.javacar.lojadecarro.dto.response;

import com.javacar.lojadecarro.enums.StatusVeiculo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo do veículo de uma venda")
public record VeiculoVendaResponse(
        @Schema(example = "1", description = "ID do da venda")
        Long id,
        @Schema(example = "Chevrolet", description = "Nome da marca")
        String marca,
        @Schema(example = "Onix", description = "Nome do modelo")
        String modelo,
        @Schema(example = "Disponível", description = "status do anúncio do veículo")
        StatusVeiculo status
) {
}