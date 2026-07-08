package com.javacar.lojadecarro.factory.carro;

import com.javacar.lojadecarro.dto.request.CarroRequest;

import java.math.BigDecimal;

public class CarroRequestFactory {
    private Double quilometragem;
    private String url;
    private BigDecimal valor;
    private String placa;
    private String motor;
    private Integer anoFabricacao;
    private Long idCarroceria;
    private Long idMarca;
    private Long idCores;
    private Long idModelo;
    private Long idUsuario;
    private Long idCombustivel;

    private CarroRequestFactory() {
    }

    public static CarroRequestFactory marcaRequestFactory() {
        return new CarroRequestFactory();
    }

    public static CarroRequestFactory criarRequest() {
        return new CarroRequestFactory();
    }

    public CarroRequestFactory comTodosOsCampos() {
        this.quilometragem = 67000.98;
        this.url = "www.google.com";
        this.valor = new BigDecimal(58000);
        this.placa = "QUV1F836";
        this.motor = "1.0";
        this.anoFabricacao = 2020;
        this.idCarroceria = 1L;
        this.idMarca = 1L;
        this.idCores = 1L;
        this.idModelo = 1L;
        this.idUsuario = 1L;
        this.idCombustivel = 1L;
        return this;
    }
    public CarroRequestFactory comCores(Long cor) {
        this.idCores = cor;
        return this;
    }

    public CarroRequest build() {
        return new CarroRequest(
                quilometragem,
                url,
                valor,
                placa,
                motor,
                anoFabricacao,
                idCarroceria,
                idMarca,
                idCores,
                idModelo,
                idUsuario,
                idCombustivel);
    }
}
