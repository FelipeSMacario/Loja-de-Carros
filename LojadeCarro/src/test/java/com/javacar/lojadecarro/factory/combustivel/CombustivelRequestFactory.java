package com.javacar.lojadecarro.factory.combustivel;

import com.javacar.lojadecarro.dto.request.CombustivelRequest;

public class CombustivelRequestFactory {
    private String nome;

    private CombustivelRequestFactory() {
    }

    public static CombustivelRequestFactory marcaRequestFactory() {
        return new CombustivelRequestFactory();
    }

    public static CombustivelRequestFactory criarRequest() {
        return new CombustivelRequestFactory();
    }

    public CombustivelRequestFactory comTodosOsCampos() {
        this.nome = "Gasolina";
        return this;
    }


    public CombustivelRequest build() {
        return new CombustivelRequest(nome);
    }
}
