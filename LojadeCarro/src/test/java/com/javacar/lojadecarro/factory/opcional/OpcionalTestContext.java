package com.javacar.lojadecarro.factory.opcional;

import com.javacar.lojadecarro.dto.request.OpcionalRequest;
import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.entity.Opcional;

import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.criarOpcionalRequest;
import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.criarOpcionalResponse;
import static com.javacar.lojadecarro.factory.opcional.OpcionalRequestFactory.criarRequest;

public class OpcionalTestContext {
    public final OpcionalRequest request = criarOpcionalRequest();
    public final OpcionalRequest requestIncompleto = criarRequest().build();
    public final OpcionalResponse response = criarOpcionalResponse();

    public static Opcional criarOpcional(Long id, String nome, boolean ativo) {
        return OpcionalEntityFactory
                .criarEntity()
                .comId(id)
                .comNome(nome)
                .comAtivo(ativo)
                .build();
    }

    public static OpcionalResponse criaOpcionalResponse(Long id, String nome, boolean ativo) {
        return OpcionalResponseFactory
                .criarResponse()
                .comId(id)
                .comNome(nome)
                .comAtivo(ativo)
                .build();
    }

    public static OpcionalResponse criaOpcionalResponse(boolean ativo) {
        return OpcionalResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(ativo)
                .build();
    }

    public static OpcionalResponse criaOpcionalResponse2(boolean ativo) {
        return OpcionalResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Automatico")
                .comAtivo(ativo)
                .build();
    }
}
