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
    private String carroceria;
    private String cor;
    private String combustivel;
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
        this.placa = "QUV1F83";
        this.marca = "Chevrolet";
        this.modelo = "Onix";
        this.carroceria = "Hatch";
        this.cor = "Branco";
        this.combustivel = "Etanol";
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
    public VeiculoResponseFactory comPlaca(String placa) {
        this.placa = placa;
        return this;
    }
    public VeiculoResponseFactory comMarca(String marca) {
        this.marca = marca;
        return this;
    }
    public VeiculoResponseFactory comModelo(String modelo) {
        this.modelo = modelo;
        return this;
    }
    public VeiculoResponseFactory comCor(String cor) {
        this.cor = cor;
        return this;
    }
    public VeiculoResponseFactory comCarroceria(String carroceria) {
        this.carroceria = carroceria;
        return this;
    }
    public VeiculoResponseFactory comCombustivel(String combustivel) {
        this.combustivel = combustivel;
        return this;
    }
    public VeiculoResponseFactory comValor(BigDecimal valor) {
        this.valor = valor;
        return this;
    }

    public VeiculoResponse build() {
        return new VeiculoResponse(
                id,
                placa,
                marca,
                modelo,
                carroceria,
                cor,
                combustivel,
                valor,
                quilometragem,
                anoFabricacao,
                statusVeiculo);
    }
}
