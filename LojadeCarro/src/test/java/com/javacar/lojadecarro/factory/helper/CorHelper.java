package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.CorRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.entity.Cor;
import com.javacar.lojadecarro.factory.cor.CorEntityFactory;
import com.javacar.lojadecarro.factory.cor.CorRequestFactory;
import com.javacar.lojadecarro.factory.cor.CorResponseFactory;

import static org.assertj.core.api.Assertions.assertThat;

public final class CorHelper extends BaseHelper{
    public static CorRequest criarCorRequest() {
        return CorRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static Cor criarCorEntity() {
        return CorEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    public static CorResponse criarCorResponse() {
        return CorResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }

    public static void assertCorResponse(CorResponse resultado) {
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        CorResponse::id,
                        CorResponse::nome,
                        CorResponse::ativo
                ).containsExactly(
                        1L,
                        "Branco",
                        true
                );
    }
}
