package com.javacar.lojadecarro.factory.combustivel;

import com.javacar.lojadecarro.dto.request.CombustivelRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;

import static com.javacar.lojadecarro.factory.helper.CombustivelHelper.*;

public class CombustivelTestContext {
    public final CombustivelRequest combustivelRequest = criarCombustivelRequest();
    public final CombustivelRequest combustivelRequestIncompleto = CombustivelRequestFactory
            .criarRequest()
            .build();
    public final Combustivel combustivel = criarCombustivelEntity();
    public final Combustivel combustivelInativa = CombustivelEntityFactory
            .criarEntity()
            .comTodosOsCampos()
            .comAtivo(false)
            .build();
    public final CombustivelResponse combustivelResponse = criarCombustivelResponse();
    public final CombustivelResponse combustivelResponseInativa = CombustivelResponseFactory
            .criarResponse()
            .comTodosOsCampos()
            .comAtivo(false)
            .build();

    public static Combustivel combustivelEntity(Long id, String nome, boolean ativo) {
        return CombustivelEntityFactory
                .criarEntity()
                .comId(id)
                .comNome(nome)
                .comAtivo(ativo)
                .build();
    }

    public static CombustivelResponse combustivelResponse(Long id, String nome, boolean ativo) {
        return CombustivelResponseFactory
                .criarResponse()
                .comId(id)
                .comNome(nome)
                .comAtivo(ativo)
                .build();
    }


    public static CombustivelResponse criaCombustivelResponse(boolean ativo) {
        return CombustivelResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(ativo)
                .build();
    }

    public static CombustivelResponse criaCombustivelResponse2(boolean ativo) {
        return CombustivelResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Eletrico")
                .comAtivo(ativo)
                .build();
    }
}
