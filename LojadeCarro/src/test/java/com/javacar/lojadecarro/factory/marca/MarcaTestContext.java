package com.javacar.lojadecarro.factory.marca;

import com.javacar.lojadecarro.dto.request.MarcaRequest;
import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.entity.Marca;

import static com.javacar.lojadecarro.factory.helper.MarcaHelper.*;
import static com.javacar.lojadecarro.factory.marca.MarcaRequestFactory.criarRequest;

public class MarcaTestContext {
    public final MarcaRequest request = criarMarcaRequest();
    public final MarcaRequest requestIncompleta = criarRequest().build();
    public final Marca marca = criarMarcaEntity();
    public final MarcaResponse response = criarMarcaResponse();

    public static MarcaResponse criaMarcaResponse(boolean ativo) {
        return MarcaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(ativo)
                .build();
    }

    public static MarcaResponse criaMarcaResponse2(boolean ativo) {
        return MarcaResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Fiat")
                .comAtivo(ativo)
                .build();
    }

}
