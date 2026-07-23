package com.javacar.lojadecarro.factory.venda;

import com.javacar.lojadecarro.dto.request.VendaRequest;

import java.math.BigDecimal;

public class VendaRequestFactory {
    private Long veiculoId;
    private Long compradorId;
    private Long vendedorId;
    private BigDecimal valor;

    private VendaRequestFactory() {
    }

    public static VendaRequestFactory vendaRequestFactory() {
        return new VendaRequestFactory();
    }

    public static VendaRequestFactory criarRequest() {
        return new VendaRequestFactory();
    }

    public VendaRequestFactory comTodosOsCampos() {
        this.veiculoId = 1L;
        this.compradorId = 1L;
        this.vendedorId = 1L;
        this.valor = BigDecimal.valueOf(200000);
        return this;
    }

    public VendaRequest build() {
        return new VendaRequest(veiculoId, compradorId, vendedorId, valor);
    }
}
