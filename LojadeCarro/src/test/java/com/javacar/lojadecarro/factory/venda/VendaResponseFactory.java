package com.javacar.lojadecarro.factory.venda;

import com.javacar.lojadecarro.dto.response.UsuarioResumoResponse;
import com.javacar.lojadecarro.dto.response.VeiculoVendaResponse;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.enums.StatusVenda;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.javacar.lojadecarro.enums.StatusVenda.EM_ANDAMENTO;
import static com.javacar.lojadecarro.factory.helper.VeiculoTestContext.criarVeiculoVendaResponse;
import static com.javacar.lojadecarro.factory.usuario.UsuarioTestContext.criarUsuarioResumo;
import static com.javacar.lojadecarro.utils.Utils.ZONE;

public class VendaResponseFactory {
    private Long id;
    private BigDecimal valorVenda;
    private StatusVenda statusVenda;
    private LocalDateTime dataVenda;
    private VeiculoVendaResponse veiculo;
    private UsuarioResumoResponse vendedor;
    private UsuarioResumoResponse comprador;

    private VendaResponseFactory() {
    }

    public static VendaResponseFactory criarResponse() {
        return new VendaResponseFactory();
    }


    public VendaResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.valorVenda = BigDecimal.valueOf(200000);
        this.dataVenda = (LocalDateTime.now(ZONE));
        this.statusVenda = EM_ANDAMENTO;
        this.veiculo = criarVeiculoVendaResponse(1L, "Ford", "Mustang", StatusVeiculo.DISPONIVEL);
        this.vendedor = criarUsuarioResumo(1L, "Goku");
        this.comprador = criarUsuarioResumo(2L, "Vegeta");
        return this;
    }

    public VendaResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }

    public VendaResponseFactory comStatusVenda(StatusVenda statusVenda) {
        this.statusVenda = statusVenda;
        return this;
    }

    public VendaResponseFactory comValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
        return this;
    }

    public VendaResponseFactory comVeiculo(VeiculoVendaResponse veiculoResponse) {
        this.veiculo = veiculoResponse;
        return this;
    }

    public VendaResponse build() {
        return new VendaResponse(id, valorVenda, statusVenda, dataVenda, veiculo, vendedor, comprador);
    }
}
