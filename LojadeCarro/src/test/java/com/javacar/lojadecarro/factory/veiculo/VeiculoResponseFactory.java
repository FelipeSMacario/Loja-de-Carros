package com.javacar.lojadecarro.factory.veiculo;

import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.enums.StatusVeiculo;

import java.math.BigDecimal;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;

public class VeiculoResponseFactory {
    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private BigDecimal valor;
    private Integer quilometragem;
    private Short anoFabricacao;
    private StatusVeiculo statusVeiculo;

    private VeiculoResponseFactory() {
    }

    public static VeiculoResponseFactory criarResponse() {
        return new VeiculoResponseFactory();
    }


    public VeiculoResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.placa = "QUV1F836";
        this.marca = "Chevrolet";
        this.modelo = "Onix";
        this.valor = new BigDecimal(58000);
        this.quilometragem = 67000;
        this.anoFabricacao = (short) 2020;
        this.statusVeiculo = DISPONIVEL;
        return this;
    }

    public VeiculoResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }
    public VeiculoResponseFactory comStatus(StatusVeiculo statusVeiculo) {
        this.statusVeiculo = statusVeiculo;
        return this;
    }

    public VeiculoResponse build() {
        return new VeiculoResponse(
                id,
                placa,
                marca,
                modelo,
                valor,
                quilometragem,
                anoFabricacao,
                statusVeiculo);
    }
}
