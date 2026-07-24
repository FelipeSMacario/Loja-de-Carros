package com.javacar.lojadecarro.factory.veiculo;

import com.javacar.lojadecarro.dto.request.VeiculoRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class VeiculoRequestFactory {
    private Integer quilometragem;
    private BigDecimal valor;
    private String placa;
    private String motor;
    private String descricao;
    private Short anoFabricacao;
    private List<Long> idsOpcionais;
    private Long idCarroceria;
    private Long idCores;
    private Long idModelo;
    private Long idUsuario;
    private Long idCombustivel;

    private VeiculoRequestFactory() {
    }

    public static VeiculoRequestFactory veiculoRequestFactory() {
        return new VeiculoRequestFactory();
    }

    public static VeiculoRequestFactory criarRequest() {
        return new VeiculoRequestFactory();
    }

    public VeiculoRequestFactory comTodosOsCampos() {
        this.quilometragem = 67000;
        this.valor = new BigDecimal(58000);
        this.placa = "QUV1F836";
        this.motor = "1.0";
        this.descricao = "Documentos em dia";
        this.idsOpcionais = Arrays.asList(1L, 2L);
        this.anoFabricacao = 2020;
        this.idCarroceria = 1L;
        this.idCores = 1L;
        this.idModelo = 1L;
        this.idUsuario = 1L;
        this.idCombustivel = 1L;
        return this;
    }
    public VeiculoRequestFactory comMotor(String motor) {
        this.motor = motor;
        return this;
    }
    public VeiculoRequestFactory comDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }
    public VeiculoRequestFactory comOpcionais(List<Long> idsOpcionais) {
        this.idsOpcionais = idsOpcionais;
        return this;
    }

    public VeiculoRequest build() {
        return new VeiculoRequest(
                quilometragem,
                valor,
                placa,
                motor,
                descricao,
                anoFabricacao,
                idsOpcionais,
                idCarroceria,
                idCores,
                idModelo,
                idUsuario,
                idCombustivel);
    }
}
