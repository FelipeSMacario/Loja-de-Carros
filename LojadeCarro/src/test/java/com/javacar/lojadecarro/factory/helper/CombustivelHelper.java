package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.CombustivelRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import com.javacar.lojadecarro.factory.combustivel.CombustivelEntityFactory;
import com.javacar.lojadecarro.factory.combustivel.CombustivelRequestFactory;
import com.javacar.lojadecarro.factory.combustivel.CombustivelResponseFactory;

import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;

public final class CombustivelHelper extends BaseHelper {
    public static CombustivelRequest criarCombustivelRequest() {
        return CombustivelRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static CombustivelResponse criarCombustivelResponse() {
        return CombustivelResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

    }

    public static Combustivel criarCombustivelEntity() {
        return CombustivelEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }
    public static CombustivelRequest criarCombustivelPorNome(String nome) {
        return CombustivelRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .comNome(nome)
                .build();
    }
    public static void assertCombustivelResponse(CombustivelResponse resultado) {
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        CombustivelResponse::id,
                        CombustivelResponse::nome,
                        CombustivelResponse::ativo
                )
                .containsExactly(ID_VALIDO, "Gasolina", true);
    }

}
