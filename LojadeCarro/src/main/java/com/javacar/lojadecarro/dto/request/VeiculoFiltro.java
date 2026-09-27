package com.javacar.lojadecarro.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record VeiculoFiltro(

        @Positive
        Long marcaId,

        @Positive
        Long modeloId,

        @Positive
        Long carroceriaId,

        @Positive
        Long corId,

        @Positive
        Long combustivelId,

        @Min(1886)
        Short anoMin,

        @Min(1886)
        Short anoMax,

        @DecimalMin("0.00")
        BigDecimal valorMin,

        @DecimalMin("0.00")
        BigDecimal valorMax,

        @PositiveOrZero
        Integer quilometragemMax
) {

    @JsonIgnore
    @AssertTrue(message = "O ano mínimo não pode ser maior que o ano máximo.")
    public boolean isIntervaloAnoValido() {
        return anoMin == null ||
                anoMax == null ||
                anoMin <= anoMax;
    }

    @JsonIgnore
    @AssertTrue(message = "O valor mínimo não pode ser maior que o valor máximo.")
    public boolean isIntervaloValorValido() {
        return valorMin == null ||
                valorMax == null ||
                valorMin.compareTo(valorMax) <= 0;
    }
}