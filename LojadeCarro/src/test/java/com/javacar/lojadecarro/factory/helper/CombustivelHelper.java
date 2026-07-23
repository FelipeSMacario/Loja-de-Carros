package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.CombustivelRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import com.javacar.lojadecarro.factory.combustivel.CombustivelEntityFactory;
import com.javacar.lojadecarro.factory.combustivel.CombustivelRequestFactory;
import com.javacar.lojadecarro.factory.combustivel.CombustivelResponseFactory;

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


}
