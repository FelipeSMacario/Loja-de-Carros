package com.javacar.lojadecarro.factory.cor;

import com.javacar.lojadecarro.dto.request.CorRequest;

public class CorRequestFactory {
    private String nome;

    private CorRequestFactory() {
    }

    public static CorRequestFactory coresRequestFactory() {
        return new CorRequestFactory();
    }

    public static CorRequestFactory criarRequest() {
        return new CorRequestFactory();
    }

    public CorRequestFactory comTodosOsCampos() {
        this.nome = "Branco";
        return this;
    }

    public CorRequestFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public CorRequest build() {
        return new CorRequest(nome);
    }
}
