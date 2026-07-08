package com.JavangularCar.LojadeCarro.factory.modelo;

import com.JavangularCar.LojadeCarro.dto.response.CoresResponse;
import com.JavangularCar.LojadeCarro.dto.response.MarcaResponse;
import com.JavangularCar.LojadeCarro.dto.response.ModeloResponse;
import com.JavangularCar.LojadeCarro.factory.marca.MarcaResponseFactory;

public class ModeloResponseFactory {
    private Long id;
    private String nome;
    private MarcaResponse marcaResponse = MarcaResponseFactory
            .criarResponse()
            .comId(3L)
            .comNome("Chevrolet")
            .comURL("https://www.chevrolet.com")
            .build();;

    private ModeloResponseFactory() {
    }

    public static ModeloResponseFactory criarResponse() {
        return new ModeloResponseFactory();
    }

    public static ModeloResponseFactory criarResponse(CoresResponse coresResponse) {
        return new ModeloResponseFactory();
    }

    public ModeloResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.nome = "Onix";
        return this;
    }

    public ModeloResponseFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public ModeloResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }

    public ModeloResponse build() {
        return new ModeloResponse(id, nome, marcaResponse);
    }
}
