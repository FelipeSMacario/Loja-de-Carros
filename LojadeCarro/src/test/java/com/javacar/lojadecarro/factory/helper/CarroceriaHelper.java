package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaEntityFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaRequestFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaResponseFactory;
import org.springframework.test.web.servlet.ResultActions;

import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    public static void assertResultadoCarroceria(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.nome").value("Hatch"))
                .andExpect(jsonPath("$.ativo").value(true));
    }


}
