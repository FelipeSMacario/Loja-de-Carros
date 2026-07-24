package com.javacar.lojadecarro.factory.carroceria;

import com.javacar.lojadecarro.dto.response.CarroceriaResponse;

public class CarroceriaResponseFactory {
    private Long id;
    private String nome;
    private boolean ativo;

    private CarroceriaResponseFactory() {}

    public static CarroceriaResponseFactory criarResponse() {
        return new CarroceriaResponseFactory();
    }

    public CarroceriaResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.nome = "Hatch";
        this.ativo = true;
        return this;
    }

    public CarroceriaResponseFactory comTodosOsCamposExcetoId() {
        this.nome = "Hatch";
        this.ativo = true;
        return this;
    }

    public CarroceriaResponseFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public CarroceriaResponseFactory comId(Long id) {
        this.id= id;
        return this;
    }
    public CarroceriaResponseFactory comAtivo(boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public CarroceriaResponse build() {
        return new CarroceriaResponse(id, nome, ativo);
    }
}
