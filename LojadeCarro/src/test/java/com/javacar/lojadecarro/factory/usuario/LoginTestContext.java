package com.javacar.lojadecarro.factory.usuario;

import com.javacar.lojadecarro.dto.request.LoginRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.security.dto.LoginResponse;

public class LoginTestContext {
    public final LoginRequest request = new LoginRequest("felipesmacario@gmail.com", "12345");
    public final LoginRequest requestIncompleta = new LoginRequest("felipesmacario@gmail.com", null);
    public final UsuarioResponse response = new UsuarioResponse(1L, "Felipe", "15153769788", "felipesmacario@gmail.com", true);
    public final LoginResponse loginResponse = new LoginResponse("abc", "123", UsuarioResponseFactory.criarResponse().comTodosOsCampos().build());
}
