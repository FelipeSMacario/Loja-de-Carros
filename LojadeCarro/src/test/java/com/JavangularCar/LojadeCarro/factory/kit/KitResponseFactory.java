package com.JavangularCar.LojadeCarro.factory.kit;

import com.JavangularCar.LojadeCarro.dto.response.CarroceriaResponse;

public class KitResponseFactory {
    private Long id;
    private String nome;

    private KitResponseFactory() {}

    public static KitResponseFactory criarResponse() {
        return new KitResponseFactory();
    }

    public static KitResponseFactory criarResponse (CarroceriaResponse marcaResponse) {
        return new KitResponseFactory();
    }

    public KitResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.nome = "Hatch";
        return this;
    }

    public KitResponseFactory comTodosOsCamposExcetoId() {
        this.nome = "Hatch";
        return this;
    }

    public KitResponseFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public KitResponseFactory comId(Long id) {
        this.id= id;
        return this;
    }


    public CarroceriaResponse build() {
        return new CarroceriaResponse(id, nome);
    }
}
