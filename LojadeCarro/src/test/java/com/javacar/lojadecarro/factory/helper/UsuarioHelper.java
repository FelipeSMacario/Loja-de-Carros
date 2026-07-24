package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioRolesResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.factory.role.RoleResponseFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioEntityFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioRequestFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioResponseFactory;

import java.util.List;

import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;

public final class UsuarioHelper extends BaseHelper {
    public static UsuarioRequest criarUsuarioRequest() {
        return UsuarioRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static Usuario criarUsuarioEntity() {
        return UsuarioEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    public static UsuarioResponse criarUsuarioResponse() {
        return UsuarioResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }

    public static void assertUsuarioResponse(UsuarioResponse response) {
        assertThat(response)
                .isNotNull()
                .extracting(
                        UsuarioResponse::id,
                        UsuarioResponse::nome,
                        UsuarioResponse::email
                ).containsExactly(
                        ID_VALIDO,
                        "Felipe",
                        "felipesmacario@gmail.com"
                );

    }

    public static UsuarioRolesResponse criarUsuarioRolesResponse() {
        var listRolesResponse = List.of(
                RoleResponseFactory
                        .criarResponse()
                .comTodosOsCampos()
                        .build(),
                RoleResponseFactory
                        .criarResponse()
                        .comNome("VENDEDOR")
                        .comId(2L)
                        .comAtivo(true)
                        .build()

        );

        return new UsuarioRolesResponse(1L,
                "Felipe Soares Macário",
                "1234567890",
                listRolesResponse);
    }

    public static UsuarioRolesResponse criarUsuarioRolesResponseDesvincula() {
        var listRolesResponse = List.of(
                RoleResponseFactory
                        .criarResponse()
                        .comNome("VENDEDOR")
                        .comId(2L)
                        .comAtivo(true)
                        .build()

        );

        return new UsuarioRolesResponse(1L,
                "Felipe Soares Macário",
                "1234567890",
                listRolesResponse);
    }

}
