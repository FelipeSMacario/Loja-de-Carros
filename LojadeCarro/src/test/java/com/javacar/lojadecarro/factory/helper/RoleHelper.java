package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.RoleRequest;
import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.entity.Role;
import com.javacar.lojadecarro.factory.role.RoleEntityFactory;
import com.javacar.lojadecarro.factory.role.RoleRequestFactory;
import com.javacar.lojadecarro.factory.role.RoleResponseFactory;

public final class RoleHelper {
    public static RoleRequest criarRoleRequest() {
        return RoleRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static Role criarRoleEntity() {
        return RoleEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    public static RoleResponse criarRoleResponse() {
        return RoleResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }
}
