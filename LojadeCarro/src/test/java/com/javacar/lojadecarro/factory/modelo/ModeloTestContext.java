package com.javacar.lojadecarro.factory.modelo;

import com.javacar.lojadecarro.dto.request.ModeloRequest;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;

import static com.javacar.lojadecarro.factory.helper.ModeloHelper.*;
import static com.javacar.lojadecarro.factory.modelo.ModeloRequestFactory.criarRequest;

public class ModeloTestContext {
    public final ModeloRequest request = criarModeloRequest();
    public final ModeloRequest requestIncompleto = criarRequest().build();
    public final Modelo modelo = criarModeloEntity();
    public final ModeloResponse response = criarModeloResponse();

    public static ModeloRequest criaModeloRequest(String nome, Long idMarca) {
        return ModeloRequestFactory
                .criarRequest()
                .comNome(nome)
                .comIdMarca(idMarca)
                .build();
    }

    public static Modelo criaModelo(Long id, String nome, boolean ativo) {
        return ModeloEntityFactory
                .criarEntity()
                .comId(id)
                .comNome(nome)
                .comAtivo(ativo)
                .comMarca()
                .build();
    }

    public static ModeloResponse criaModeloResponse(Long id, String nome, boolean ativo) {
        return ModeloResponseFactory
                .criarResponse()
                .comId(id)
                .comNome(nome)
                .comAtivo(ativo)
                .comMarca()
                .build();
    }

    public static ModeloResponse criaModeloResponse(boolean ativo) {
        return ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(ativo)
                .build();
    }

    public static ModeloResponse criaModeloResponse2(boolean ativo) {
        return ModeloResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Celta")
                .comAtivo(ativo)
                .build();
    }
}
