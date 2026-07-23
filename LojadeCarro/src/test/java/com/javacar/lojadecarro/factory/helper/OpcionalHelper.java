package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.OpcionalRequest;
import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.factory.opcional.OpcionalEntityFactory;
import com.javacar.lojadecarro.factory.opcional.OpcionalRequestFactory;
import com.javacar.lojadecarro.factory.opcional.OpcionalResponseFactory;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class OpcionalHelper extends BaseHelper {
    public static OpcionalRequest criarOpcionalRequest() {
        return OpcionalRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static Opcional criarOpcionalEntity() {
        return OpcionalEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    public static OpcionalResponse criarOpcionalResponse() {
        return OpcionalResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }

    public static List<Opcional> criarListaOpcionals(){
        Opcional opcional1 = OpcionalEntityFactory
                .criarEntity()
                .comId(1L)
                .comNome("Banco de couro")
                .comAtivo(true)
                .build();
        Opcional opcional2 = OpcionalEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Automatico")
                .comAtivo(true)
                .build();

        return List.of(opcional1, opcional2);
    }

    public static void assertOpcionalResponse(OpcionalResponse resultado) {
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        OpcionalResponse::id,
                        OpcionalResponse::nome,
                        OpcionalResponse::ativo
                ).containsExactly(
                        1L,
                        "Freio ABS",
                        true
                );
    }
}
