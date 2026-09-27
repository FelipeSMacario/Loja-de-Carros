package com.javacar.lojadecarro.factory.veiculo;

import com.javacar.lojadecarro.dto.request.VeiculoFiltro;

import java.math.BigDecimal;

public class VeiculoFiltroFactory {
    private Long marcaId;
    private Long modeloId;
    private Long carroceriaId;
    private Long corId;
    private Long combustivelId;
    private Short anoMin;
    private Short anoMax;
    private BigDecimal valorMin;
    private BigDecimal valorMax;
    private Integer quilometragemMax;

    private VeiculoFiltroFactory() {
    }

    public static VeiculoFiltroFactory veiculoFiltroFactory() {
        return new VeiculoFiltroFactory();
    }

    public static VeiculoFiltroFactory criarFiltro() {
        return new VeiculoFiltroFactory();
    }

    public VeiculoFiltroFactory comTodosOsCampos() {
        this.marcaId = 1L;
        this.modeloId = 1L;
        this.carroceriaId = 1L;
        this.corId = 1L;
        this.combustivelId = 1L;
        this.anoMin = (short) 2020;
        this.anoMax = (short) 2026;
        this.valorMin = new BigDecimal("70000");
        this.valorMax = new BigDecimal("400000");
        this.quilometragemMax = 300000;
        return this;
    }

    public VeiculoFiltroFactory comMarca(Long marca) {
        this.marcaId = marca;
        return this;
    }

    public VeiculoFiltroFactory comModelo(Long modeloId) {
        this.modeloId = modeloId;
        return this;
    }

    public VeiculoFiltroFactory comCarroceria(Long carroceriaId) {
        this.carroceriaId = carroceriaId;
        return this;
    }

    public VeiculoFiltroFactory comCor(Long corId) {
        this.corId = corId;
        return this;
    }

    public VeiculoFiltroFactory comCombustivel(Long combustivelId) {
        this.combustivelId = combustivelId;
        return this;
    }

    public VeiculoFiltroFactory comAnoMinimo(Short anoMin) {
        this.anoMin = anoMin;
        return this;
    }

    public VeiculoFiltroFactory comAnoMaximo(Short anoMax) {
        this.anoMax = anoMax;
        return this;
    }

    public VeiculoFiltroFactory comValorMinimo(BigDecimal valorMin) {
        this.valorMin = valorMin;
        return this;
    }

    public VeiculoFiltroFactory comValorMaximo(BigDecimal valorMax) {
        this.valorMax = valorMax;
        return this;
    }

    public VeiculoFiltroFactory comValorQuilometragem(Integer quilometragemMax) {
        this.quilometragemMax = quilometragemMax;
        return this;
    }

    public VeiculoFiltro build() {
        return new VeiculoFiltro(
                marcaId,
                modeloId,
                carroceriaId,
                corId,
                combustivelId,
                anoMin,
                anoMax,
                valorMin,
                valorMax,
                quilometragemMax);
    }
}
