package com.javacar.lojadecarro.factory.role;

import com.javacar.lojadecarro.dto.response.RoleResponse;

public class RoleResponseFactory {
    private Long id;
    private String nome;
    private boolean ativo;

    private RoleResponseFactory() {
    }

    public static RoleResponseFactory criarResponse() {
        return new RoleResponseFactory();
    }

    public RoleResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.nome = "ADMIN";
        this.ativo = true;
        return this;
    }

    public RoleResponseFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public RoleResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }

    public RoleResponseFactory comAtivo(boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public RoleResponse build() {
        return new RoleResponse(id, nome, ativo);
    }
}
