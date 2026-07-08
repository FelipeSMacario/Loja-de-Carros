package com.javacar.lojadecarro.factory.usuario;

import com.javacar.lojadecarro.dto.response.UsuarioResponse;

public class UsuarioResponseFactory {
    private Long id;
    private String nome;
    private String email;

    private UsuarioResponseFactory() {
    }

    public static UsuarioResponseFactory criarResponse() {
        return new UsuarioResponseFactory();
    }


    public UsuarioResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.nome = "Felipe";
        this.email = "felipesmacario@gmail.com";
        return this;
    }

    public UsuarioResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }

    public UsuarioResponseFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public UsuarioResponseFactory comEmail(String email) {
        this.email = email;
        return this;
    }

    public UsuarioResponse build() {
        return new UsuarioResponse(id, nome, email);
    }
}
