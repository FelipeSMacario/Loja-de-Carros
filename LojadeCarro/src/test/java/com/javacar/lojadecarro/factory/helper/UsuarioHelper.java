package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.factory.usuario.UsuarioEntityFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioRequestFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioResponseFactory;

public final class UsuarioHelper {
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
}
