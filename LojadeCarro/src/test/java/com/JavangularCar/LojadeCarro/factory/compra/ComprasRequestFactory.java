package com.JavangularCar.LojadeCarro.factory.compra;

import com.JavangularCar.LojadeCarro.dto.request.ComprasRequest;

import java.math.BigDecimal;

public class ComprasRequestFactory {
    private Long carroId;
    private Long compradorId;
    private Long vendedorId;
    private BigDecimal valor;

    private ComprasRequestFactory() {
    }

    public static ComprasRequestFactory comprasRequestFactory() {
        return new ComprasRequestFactory();
    }

    public static ComprasRequestFactory criarRequest() {
        return new ComprasRequestFactory();
    }

    public ComprasRequestFactory comTodosOsCampos() {
        this.carroId = 1L;
        this.compradorId = 1L;
        this.vendedorId = 2L;
        this.valor = BigDecimal.valueOf(200000);
        return this;
    }


    public ComprasRequest build() {
        return new ComprasRequest(carroId, compradorId, vendedorId, valor);
    }
}
