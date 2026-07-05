package com.JavangularCar.LojadeCarro.factory.carro;

import com.JavangularCar.LojadeCarro.dto.response.CarroResponse;
import com.JavangularCar.LojadeCarro.dto.response.CarroceriaResponse;

import java.math.BigDecimal;

public class CarroResponseFactory {
    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private BigDecimal valor;
    private Double quilometragem;
    private Integer anoFabricacao;
    private String url;
    private boolean ativo;

    private CarroResponseFactory() {
    }

    public static CarroResponseFactory criarResponse() {
        return new CarroResponseFactory();
    }

    public static CarroResponseFactory criarResponse(CarroceriaResponse marcaResponse) {
        return new CarroResponseFactory();
    }

    public CarroResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.placa = "QUV1F836";
        this.marca = "Chevrolet";
        this.modelo = "Onix";
        this.valor = new BigDecimal(58000);
        this.quilometragem = 67000.98;
        this.anoFabricacao = 2020;
        this.url = "www.google.com";
        this.ativo = true;
        return this;
    }


    public CarroResponseFactory comPlaca(String placa) {
        this.placa = placa;
        return this;
    }

    public CarroResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }


    public CarroResponse build() {
        return new CarroResponse(id,
                placa,
                marca,
                modelo,
                valor,
                quilometragem,
                anoFabricacao,
                url,
                ativo);
    }
}
