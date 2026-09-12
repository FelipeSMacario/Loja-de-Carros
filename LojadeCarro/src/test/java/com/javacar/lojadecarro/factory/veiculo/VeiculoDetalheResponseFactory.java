package com.javacar.lojadecarro.factory.veiculo;

import com.javacar.lojadecarro.dto.response.*;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.factory.opcional.OpcionalResponseFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;

public class VeiculoDetalheResponseFactory {
    private Long id;
    private String marca;
    private String modelo;
    private String carroceria;
    private String cor;
    private String combustivel;
    private String motor;
    private BigDecimal valor;
    private Integer quilometragem;
    private Short anoFabricacao;
    private StatusVeiculo statusVeiculo;
    private String descricao;
    private LocalDateTime dataCadastro;
    private VendedorResumoResponse vendedor;
    private List<VeiculoImagemResponse> imagens;
    private List<OpcionalResponse> opcionais;


    private VeiculoDetalheResponseFactory() {
    }

    public static VeiculoDetalheResponseFactory criarResponse() {
        return new VeiculoDetalheResponseFactory();
    }

    public VeiculoDetalheResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.marca = "Chevrolet";
        this.modelo = "Onix";
        this.carroceria = "Hatch";
        this.cor = "Branco";
        this.motor = "1.4";
        this.combustivel = "Etanol";
        this.valor = new BigDecimal(58000);
        this.quilometragem = 67000;
        this.anoFabricacao = (short) 2020;
        this.statusVeiculo = DISPONIVEL;
        this.descricao = "Veículo em perfeito estado";
        this.dataCadastro = LocalDateTime.now();
        this.imagens = List.of(
                new VeiculoImagemResponse(1L, true),
                new VeiculoImagemResponse(2L, false)
        );
        this.vendedor = new VendedorResumoResponse(1L, "Goku");
        this.opcionais = List.of(
                OpcionalResponseFactory.criarResponse().comTodosOsCampos().build(),
                OpcionalResponseFactory.criarResponse().comTodosOsCampos().comId(2L).comNome("Multimidia").build(),
                OpcionalResponseFactory.criarResponse().comTodosOsCampos().comId(3L).comNome("Roda liga leve").build()
        );
        return this;
    }

    public VeiculoDetalheResponseFactory comOpcionais(List<OpcionalResponse> opcionais) {
        this.opcionais = opcionais;
        return this;
    }

    public VeiculoDetalheResponseFactory comVendedor(VendedorResumoResponse vendedor) {
        this.vendedor = vendedor;
        return this;
    }

    public VeiculoDetalheResponseFactory comImagens(List<VeiculoImagemResponse> imagens) {
        this.imagens = imagens;
        return this;
    }

    public VeiculoDetalheResponse build() {
        return new VeiculoDetalheResponse(
                id,
                marca,
                modelo,
                carroceria,
                cor,
                combustivel,
                motor,
                valor,
                quilometragem,
                anoFabricacao,
                statusVeiculo,
                descricao,
                dataCadastro,
                vendedor,
                imagens,
                opcionais
        );
    }
}
