package com.JavangularCar.LojadeCarro.factory.modelo;

import com.JavangularCar.LojadeCarro.dto.request.ModeloRequest;

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

    public ModeloRequest build() {
        return new ModeloRequest(nome, idMarca);
    }
}
