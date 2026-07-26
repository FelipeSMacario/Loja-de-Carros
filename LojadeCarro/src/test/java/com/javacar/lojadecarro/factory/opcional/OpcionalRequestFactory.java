package com.javacar.lojadecarro.factory.opcional;

import com.javacar.lojadecarro.dto.request.OpcionalRequest;

public class OpcionalRequestFactory {
    private String nome;

    private OpcionalRequestFactory() {
    }

    public static OpcionalRequestFactory opcionalRequestFactory() {
        return new OpcionalRequestFactory();
    }

    public static OpcionalRequestFactory criarRequest() {
        return new OpcionalRequestFactory();
    }

    public OpcionalRequestFactory comTodosOsCampos() {
        this.nome = "Freio ABS";
        return this;
    }
    public OpcionalRequestFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public OpcionalRequest build() {
        return new OpcionalRequest(nome);
    }
}
