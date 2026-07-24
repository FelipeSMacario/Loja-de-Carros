package com.javacar.lojadecarro.factory.role;

import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.entity.Role;

public class RoleTestContext {
    public final Role mockEntidade1(boolean ativo){
        return RoleEntityFactory
                .criarEntity()
                .comId(1L)
                .comNome("ADMIN")
                .comAtivo(ativo)
                .build();
    }
    public final Role mockEntidade2(boolean ativo){
        return RoleEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("VENDEDOR")
                .comAtivo(ativo)
                .build();
    }
    public final RoleResponse mockResponse1(boolean ativo){
        return RoleResponseFactory
                .criarResponse()
                .comId(1L)
                .comNome("ADMIN")
                .comAtivo(ativo)
                .build();
    }
    public final RoleResponse mockResponse2(boolean ativo){
        return RoleResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("VENDEDOR")
                .comAtivo(ativo)
                .build();
    }


}
