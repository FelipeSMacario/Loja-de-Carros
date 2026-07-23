package com.javacar.lojadecarro.factory.modelo;

import com.javacar.lojadecarro.dto.request.ModeloRequest;

public class ModeloRequestFactory {
    private String nome;
    private Long idMarca;

    private ModeloRequestFactory() {
    }

    public static ModeloRequestFactory modeloRequestFactory() {
        return new ModeloRequestFactory();
    }

    public static ModeloRequestFactory criarRequest() {
        return new ModeloRequestFactory();
    }

    public ModeloRequestFactory comTodosOsCampos() {
        this.nome = "Onix";
        this.idMarca = 3L;
        return this;
    }

    public ModeloRequestFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public ModeloRequest build() {
        return new ModeloRequest(nome, idMarca);
    }
}
