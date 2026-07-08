package com.JavangularCar.LojadeCarro.factory.marca;

import com.JavangularCar.LojadeCarro.dto.request.MarcaRequest;

public class MarcaRequestFactory {
    private String nome;
    private String url;

    private MarcaRequestFactory(){}
    public static MarcaRequestFactory marcaRequestFactory() {
        return new MarcaRequestFactory();
    }

    public static MarcaRequestFactory criarRequest(){
        return new MarcaRequestFactory();
    }

    public MarcaRequestFactory comTodosOsCampos(){
        this.nome = "Ford";
        this.url = "https://www.google.com";
        return this;
    }

    public MarcaRequestFactory comTodosOsCamposExcetoNome(){
        this.url = "https://www.google.com";
        return this;
    }

    public MarcaRequest build(){
        return new MarcaRequest(nome, url);
    }
}
