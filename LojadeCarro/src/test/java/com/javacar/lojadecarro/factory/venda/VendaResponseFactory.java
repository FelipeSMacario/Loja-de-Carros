package com.javacar.lojadecarro.factory.venda;

import com.javacar.lojadecarro.dto.response.UsuarioResumoResponse;
import com.javacar.lojadecarro.dto.response.VeiculoVendaResponse;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.enums.StatusVenda;
import com.javacar.lojadecarro.factory.usuario.UsuarioResponseFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        this.statusVenda = StatusVenda.EM_ANDAMENTO;
        this.veiculo = criarVeiculoVendaResponse(1L, "Ford", "Mustang");
        this.vendedor = criarUsuarioResumo(1L, "Goku");
        this.comprador = criarUsuarioResumo(2L, "Vegeta");
        return this;
    }
    public VendaResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }


    public VendaResponse build() {
        return new VendaResponse(id, valorVenda, statusVenda, dataVenda, veiculo, vendedor, comprador);
    }
}
