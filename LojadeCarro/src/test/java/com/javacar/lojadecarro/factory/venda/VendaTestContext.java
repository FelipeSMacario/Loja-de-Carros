package com.javacar.lojadecarro.factory.venda;

import com.javacar.lojadecarro.dto.request.VendaRequest;
import com.javacar.lojadecarro.dto.response.VeiculoVendaResponse;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.entity.Venda;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.enums.StatusVenda;
import com.javacar.lojadecarro.factory.usuario.UsuarioEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoResponseFactory;

import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.enums.StatusVenda.*;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.criarVeiculoEntity;
import static com.javacar.lojadecarro.factory.helper.VendaHelper.*;

public class VendaTestContext {
    public final VendaRequest vendaRequest = criarVendaRequest();
    public final VendaRequest vendaRequestIncompleta = VendaRequestFactory.criarRequest().build();
    public final Venda vendaEntity = criarVendaEntity();
    public final Venda vendaEntity2 = VendaEntityFactory
            .criarEntity()
            .comTodosOsCampos()
            .comId(2L)
            .build();
    public final VendaResponse vendaResponse = criarVendaResponse();
    public final VendaResponse vendaResponse2 = VendaResponseFactory
            .criarResponse()
            .comTodosOsCampos()
            .comValorVenda(BigDecimal.valueOf(300000))
            .comId(2L)
            .build();
    public final Usuario vendedor = UsuarioEntityFactory.criarEntity().comTodosOsCampos().build();
    public final Veiculo veiculo = criarVeiculoEntity();
    public final Usuario comprador = UsuarioEntityFactory.criarEntity()
            .comTodosOsCampos()
            .comId(2L)
            .comNome("Goku")
            .comAtivo(true)
            .build();

    public static Venda criarVeiculo(StatusVeiculo statusVeiculo, StatusVenda statusVenda) {
        var veiculo = VeiculoEntityFactory.criarEntity().comTodosOsCampos().comStatus(statusVeiculo).build();
        var entity = VendaEntityFactory
                .criarEntity()
                .comId(1L)
                .comValorVenda(new BigDecimal(200000))
                .comStatusVenda(statusVenda)
                .build();
        entity.setVeiculo(veiculo);
        return entity;
    }

    public static VendaResponse criarVeiculoResponse(StatusVeiculo statusVeiculo, StatusVenda statusVenda) {
        var veiculo = VeiculoResponseFactory.criarResponse().comTodosOsCampos().comStatus(statusVeiculo).build();
        var veiculoVenda = new VeiculoVendaResponse(veiculo.id(), veiculo.marca(), veiculo.modelo(), statusVeiculo);
        return VendaResponseFactory
                .criarResponse()
                .comId(1L)
                .comValorVenda(new BigDecimal(200000))
                .comStatusVenda(statusVenda)
                .comVeiculo(veiculoVenda)
                .build();
    }

    public static List<Venda> criarListaVendas() {
        var entity = VendaEntityFactory
                .criarEntity()
                .comId(1L)
                .comValorVenda(new BigDecimal(200000))
                .comStatusVenda(CONCLUIDA)
                .build();

        var entity2 = VendaEntityFactory
                .criarEntity()
                .comId(2L)
                .comValorVenda(new BigDecimal(300000))
                .comStatusVenda(PAUSADA)
                .build();

        var entity3 = VendaEntityFactory
                .criarEntity()
                .comId(3L)
                .comValorVenda(new BigDecimal(400000))
                .comStatusVenda(CANCELADA)
                .build();

        var entity4 = VendaEntityFactory
                .criarEntity()
                .comId(4L)
                .comValorVenda(new BigDecimal(500000))
                .comStatusVenda(EM_ANDAMENTO)
                .build();

        return List.of(entity, entity2, entity3, entity4);
    }

    public static List<VendaResponse> criarListaVendasResponse() {
        var response = VendaResponseFactory
                .criarResponse()
                .comId(1L)
                .comValorVenda(new BigDecimal(200000))
                .comStatusVenda(CONCLUIDA)
                .build();

        var response2 = VendaResponseFactory
                .criarResponse()
                .comId(2L)
                .comValorVenda(new BigDecimal(300000))
                .comStatusVenda(PAUSADA)
                .build();

        var response3 = VendaResponseFactory
                .criarResponse()
                .comId(3L)
                .comValorVenda(new BigDecimal(400000))
                .comStatusVenda(CANCELADA)
                .build();

        var response4 = VendaResponseFactory
                .criarResponse()
                .comId(4L)
                .comValorVenda(new BigDecimal(500000))
                .comStatusVenda(EM_ANDAMENTO)
                .build();

        return List.of(response, response2, response3, response4);
    }

    public static List<Venda> criarListaVendas(StatusVenda statusVenda) {
        var entity = VendaEntityFactory
                .criarEntity()
                .comId(1L)
                .comValorVenda(new BigDecimal(200000))
                .comStatusVenda(statusVenda)
                .build();

        var entity2 = VendaEntityFactory
                .criarEntity()
                .comId(2L)
                .comValorVenda(new BigDecimal(300000))
                .comStatusVenda(statusVenda)
                .build();


        return List.of(entity, entity2);
    }

    public static List<VendaResponse> criarListaVendasResponse(StatusVenda statusVenda
    ) {
        var response = VendaResponseFactory
                .criarResponse()
                .comId(1L)
                .comValorVenda(new BigDecimal(200000))
                .comStatusVenda(statusVenda)
                .build();

        var response2 = VendaResponseFactory
                .criarResponse()
                .comId(2L)
                .comValorVenda(new BigDecimal(300000))
                .comStatusVenda(statusVenda)
                .build();

        return List.of(response, response2);
    }
}
