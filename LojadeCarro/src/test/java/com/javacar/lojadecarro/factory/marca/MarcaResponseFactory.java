package com.javacar.lojadecarro.factory.marca;

import com.javacar.lojadecarro.dto.response.MarcaResponse;

public class MarcaResponseFactory {
    private Long id;
    private String nome;
    private String url;

    private MarcaResponseFactory() {}

    public static MarcaResponseFactory criarResponse() {
        return new MarcaResponseFactory();
    }

    public static MarcaResponseFactory criarResponse (MarcaResponse marcaResponse) {
        return new MarcaResponseFactory();
    }

    public MarcaResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.nome = "Ford";
        this.url = "https://www.google.com";
        return this;
    }

    public MarcaResponseFactory comTodosOsCamposExcetoId() {
        this.nome = "Ford";
        this.url = "https://www.google.com";
        return this;
    }

    public MarcaResponseFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public MarcaResponseFactory comId(Long id) {
        this.id= id;
        return this;
    }

    public MarcaResponseFactory comURL(String url) {
        this.url= url;
        return this;
    }

    public MarcaResponse build() {
        return new MarcaResponse(id, nome, url);
    }
}
