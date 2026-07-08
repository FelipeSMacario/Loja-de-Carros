package com.javacar.lojadecarro.factory.compra;

import com.javacar.lojadecarro.dto.response.ComprasResponse;

import java.math.BigDecimal;
import java.time.Instant;

public class ComprasResponseFactory {
    private Long id;
    private BigDecimal valor;
    private Instant dataVenda;

    private ComprasResponseFactory() {
    }

    public static ComprasResponseFactory criarResponse() {
        return new ComprasResponseFactory();
    }

    public ComprasResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.valor = BigDecimal.valueOf(200000);
        this.dataVenda = Instant.now();
        return this;
    }

    public ComprasResponseFactory comTodosOsCamposExcetoId() {
        this.valor = BigDecimal.valueOf(200000);
        this.dataVenda = Instant.now();
        return this;
    }

    public ComprasResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }


    public ComprasResponse build() {
        return new ComprasResponse(id, valor, dataVenda);
    }
}
