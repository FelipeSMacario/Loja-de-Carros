package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.VendaRequest;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.entity.Venda;
import com.javacar.lojadecarro.factory.venda.VendaEntityFactory;
import com.javacar.lojadecarro.factory.venda.VendaRequestFactory;
import com.javacar.lojadecarro.factory.venda.VendaResponseFactory;

public final class VendaHelper extends BaseHelper {
    public static VendaRequest criarVendaRequest() {
        return VendaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static Venda criarVendaEntity() {
        return VendaEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    public static VendaResponse criarVendaResponse() {
        return VendaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }
}
