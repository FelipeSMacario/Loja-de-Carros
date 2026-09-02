package com.javacar.lojadecarro.factory.venda;

import com.javacar.lojadecarro.dto.request.VendaRequest;

public class VendaRequestFactory {
    private Long veiculoId;

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
        return this;
    }

    public VendaRequestFactory comVeiculoPorId(Long veiculoId) {
        this.veiculoId = veiculoId;
        return this;
    }

    public VendaRequest build() {
        return new VendaRequest(veiculoId);
    }
}
