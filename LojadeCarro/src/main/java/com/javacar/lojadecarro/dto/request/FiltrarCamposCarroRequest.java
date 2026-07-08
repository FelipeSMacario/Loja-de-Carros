package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para filtrar um carro")
public record FiltrarCamposCarroRequest(
        @Schema(example = "Chevrolet", description = "Marca do carro")
        String marca,
        @Schema(example = "Onix", description = "Modelo do carro")
        String modelo,
        @Schema(example = "2010", description = "Ano inicio do filtro")
        Integer anoInicio,
        @Schema(example = "2026", description = "Ano fim do filtro")
        Integer anoFim,
        @Schema(example = "50000", description = "Valor minimo para filtrar o carro")
        Double valorInicio,
        @Schema(example = "70000", description = "Valor maximo para filtrar o carro")
        Double valorFim,
        @Schema(example = "50000", description = "Quilometragem maxima para buscar o carro")
        Double quilometragem
) {
}
