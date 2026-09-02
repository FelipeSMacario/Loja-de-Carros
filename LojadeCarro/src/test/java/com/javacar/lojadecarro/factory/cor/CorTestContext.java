package com.javacar.lojadecarro.factory.cor;

import com.javacar.lojadecarro.dto.request.CorRequest;
import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.entity.Cor;

import static com.javacar.lojadecarro.factory.cor.CorRequestFactory.criarRequest;
import static com.javacar.lojadecarro.factory.helper.CorHelper.*;

public class CorTestContext {
    public final CorRequest corRequest = criarCorRequest();
    public final CorRequest corRequestIncompleto = criarRequest().build();
    public final Cor cor = criarCorEntity();
    public final Cor corInativa = CorEntityFactory
            .criarEntity()
            .comTodosOsCampos()
            .comAtivo(false)
            .build();
    public final CorResponse corResponse = criarCorResponse();
    public final CorResponse corResponseInativa = CorResponseFactory
            .criarResponse()
            .comTodosOsCampos()
            .comAtivo(false)
            .build();

    public static Cor corEntity(Long id, String nome, boolean ativo) {
        return CorEntityFactory
                .criarEntity()
                .comId(id)
                .comNome(nome)
                .comAtivo(ativo)
                .build();
    }

    public static CorResponse corResponse(Long id, String nome, boolean ativo) {
        return CorResponseFactory
                .criarResponse()
                .comId(id)
                .comNome(nome)
                .comAtivo(ativo)
                .build();
    }

    public static CorResponse criaCorResponse(boolean ativo) {
        return CorResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(ativo)
                .build();
    }

    public static CorResponse criaCorResponse2(boolean ativo) {
        return CorResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Vermelho")
                .comAtivo(ativo)
                .build();
    }

}
