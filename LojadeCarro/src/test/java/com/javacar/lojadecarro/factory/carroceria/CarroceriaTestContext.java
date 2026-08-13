package com.javacar.lojadecarro.factory.carroceria;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;

import static com.javacar.lojadecarro.factory.helper.CarroceriaHelper.*;

public class CarroceriaTestContext {
    public final CarroceriaRequest carroceriaRequest = criarCarroceriaRequest();
    public final CarroceriaRequest carroceriaRequestIncompleta = CarroceriaRequestFactory
            .criarRequest()
            .build();
    public final Carroceria carroceria = criarCarroceriaEntity();
    public final Carroceria carroceriaInativa = CarroceriaEntityFactory
            .criarEntity()
            .comTodosOsCampos()
            .comAtivo(false)
            .build();
    public final CarroceriaResponse carroceriaResponse = criarCarroceriaResponse();
    public final CarroceriaResponse carroceriaResponseInativa = CarroceriaResponseFactory.criarResponse()
            .comTodosOsCampos()
            .comAtivo(false)
            .build();
    public final CarroceriaResponse carroceriaResponse2 = CarroceriaResponseFactory
            .criarResponse()
            .comId(2L)
            .comNome("Sedan")
            .comAtivo(true)
            .build();

    public static Carroceria carroceriaEntity(Long idCarroceria, String nome, boolean ativo) {
        return CarroceriaEntityFactory
                .criarEntity()
                .comId(idCarroceria)
                .comNome(nome)
                .comAtivo(ativo)
                .build();
    }
    public static CarroceriaResponse carroceriaResponse(Long idCarroceria, String nome, boolean ativo) {
        return CarroceriaResponseFactory
                .criarResponse()
                .comId(idCarroceria)
                .comNome(nome)
                .comAtivo(ativo)
                .build();
    }
}
