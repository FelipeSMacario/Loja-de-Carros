package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.MarcaRequest;
import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.entity.Marca;
import com.javacar.lojadecarro.factory.marca.MarcaEntityFactory;
import com.javacar.lojadecarro.factory.marca.MarcaRequestFactory;
import com.javacar.lojadecarro.factory.marca.MarcaResponseFactory;

import static org.assertj.core.api.Assertions.assertThat;

public final class MarcaHelper extends BaseHelper {
    public static MarcaRequest criarMarcaRequest() {
        return MarcaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static Marca criarMarcaEntity() {
        return MarcaEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    public static MarcaResponse criarMarcaResponse() {
        return MarcaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }

    public static void assertMarcaResponse(MarcaResponse resultado) {
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        MarcaResponse::id,
                        MarcaResponse::nome,
                        MarcaResponse::ativo
                ).containsExactly(
                        1L,
                        "Ford",
                        true
                );
    }
}
