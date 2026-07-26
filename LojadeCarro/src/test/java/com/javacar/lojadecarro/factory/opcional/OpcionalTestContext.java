package com.javacar.lojadecarro.factory.opcional;

import com.javacar.lojadecarro.dto.request.OpcionalRequest;
import com.javacar.lojadecarro.dto.response.OpcionalResponse;

import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.criarOpcionalRequest;
import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.criarOpcionalResponse;
import static com.javacar.lojadecarro.factory.opcional.OpcionalRequestFactory.criarRequest;

public class OpcionalTestContext {
    public final OpcionalRequest request = criarOpcionalRequest();
    public final OpcionalRequest requestIncompleto = criarRequest().build();
    public final OpcionalResponse response = criarOpcionalResponse();

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
