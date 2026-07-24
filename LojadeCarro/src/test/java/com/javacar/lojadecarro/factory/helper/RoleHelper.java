package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.RoleRequest;
import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.entity.Role;
import com.javacar.lojadecarro.factory.role.RoleEntityFactory;
import com.javacar.lojadecarro.factory.role.RoleRequestFactory;
import com.javacar.lojadecarro.factory.role.RoleResponseFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public final class RoleHelper extends BaseHelper {
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

    public static List<Role> criaListRole() {
        return List.of(
                RoleEntityFactory
                        .criarEntity()
                        .comTodosOsCampos()
                        .build(),
                RoleEntityFactory
                        .criarEntity()
                        .comId(2L)
                        .comNome("VENDEDOR")
                        .comAtivo(true)
                        .build()
        );
    }

    public static void assertRoleResponse(List<RoleResponse> response, boolean ativo1, boolean ativo2) {
        assertThat(response)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        RoleResponse::id,
                        RoleResponse::nome,
                        RoleResponse::ativo
                ).containsExactly(
                        tuple(1L, "ADMIN", ativo1),
                        tuple(2L, "VENDEDOR", ativo2)
                );
    }
}
