package com.JavangularCar.LojadeCarro.factory.kit;

import com.JavangularCar.LojadeCarro.dto.request.CarroceriaRequest;

public class KitRequestFactory {
    private String nome;

    private KitRequestFactory() {
    }

    public static KitRequestFactory marcaRequestFactory() {
        return new KitRequestFactory();
    }

    public static KitRequestFactory criarRequest() {
        return new KitRequestFactory();
    }

    public KitRequestFactory comTodosOsCampos() {
        this.nome = "Hatch";
        return this;
    }
    public KitRequestFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public CarroceriaRequest build() {
        return new CarroceriaRequest(nome);
    }
}
