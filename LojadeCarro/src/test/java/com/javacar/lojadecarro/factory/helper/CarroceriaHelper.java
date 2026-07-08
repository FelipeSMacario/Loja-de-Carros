package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import com.javacar.lojadecarro.exception.CarroceriaException;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaEntityFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaRequestFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaResponseFactory;

import static com.javacar.lojadecarro.support.ErrorMessages.CARROCERIA;
import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public final class CarroceriaHelper {
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
                        CarroceriaResponse::nome
                ).containsExactly(
                        1L,
                        "Hatch"
                );
    }
    public static void assertCarroceriaResponseErrror(CarroceriaException exception, Long idInvalido) {
        assertThat(exception)
                .hasMessage(String.format(ID_NOT_FOUND, CARROCERIA, idInvalido));
    }
}
