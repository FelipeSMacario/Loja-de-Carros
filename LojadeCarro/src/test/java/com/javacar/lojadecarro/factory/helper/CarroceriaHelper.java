package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaEntityFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaRequestFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaResponseFactory;

import static org.assertj.core.api.Assertions.assertThat;

public final class CarroceriaHelper extends BaseHelper {
    public static CarroceriaRequest criarCarroceriaRequest() {
        return CarroceriaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static CarroceriaResponse criarCarroceriaResponse() {
        return CarroceriaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

    }

    public static Carroceria criarCarroceriaEntity() {
        return CarroceriaEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    public static void assertCarroceriaResponse(CarroceriaResponse resultado) {
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        CarroceriaResponse::id,
                        CarroceriaResponse::nome,
                        CarroceriaResponse::ativo
                ).containsExactly(
                        1L,
                        "Hatch",
                        true
                );
    }

}
