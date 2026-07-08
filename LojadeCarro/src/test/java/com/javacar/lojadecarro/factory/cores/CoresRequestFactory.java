package com.javacar.lojadecarro.factory.cores;

import com.javacar.lojadecarro.dto.request.CoresRequest;

public class CoresRequestFactory {
    private String nome;

    private CoresRequestFactory() {
    }

    public static CoresRequestFactory coresRequestFactory() {
        return new CoresRequestFactory();
    }

    public static CoresRequestFactory criarRequest() {
        return new CoresRequestFactory();
    }

    public CoresRequestFactory comTodosOsCampos() {
        this.nome = "Branco";
        return this;
    }

    public CoresRequest build() {
        return new CoresRequest(nome);
    }
}
