package com.javacar.lojadecarro.factory.marca;

import com.javacar.lojadecarro.dto.request.MarcaRequest;

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
    public MarcaRequestFactory comNome(String nome){
        this.nome = nome;
        return this;
    }
    public MarcaRequestFactory comUrl(String url){
        this.url = url;
        return this;
    }
    public MarcaRequest build(){
        return new MarcaRequest(nome, url);
    }
}
