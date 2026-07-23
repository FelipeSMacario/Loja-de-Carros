package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.ModeloRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.factory.modelo.ModeloEntityFactory;
import com.javacar.lojadecarro.factory.modelo.ModeloRequestFactory;
import com.javacar.lojadecarro.factory.modelo.ModeloResponseFactory;

import static org.assertj.core.api.Assertions.assertThat;

public final class ModeloHelper extends BaseHelper {
    public static ModeloRequest criarModeloRequest() {
        return ModeloRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static ModeloResponse criarModeloResponse() {
        return ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

    }

    public static Modelo criarModeloEntity() {
        return ModeloEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }
    public static void assertModeloResponse(ModeloResponse resultado) {
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        ModeloResponse::id,
                        ModeloResponse::nome,
                        ModeloResponse::ativo
                ).containsExactly(
                        1L,
                        "Onix",
                        true
                );
    }

}
