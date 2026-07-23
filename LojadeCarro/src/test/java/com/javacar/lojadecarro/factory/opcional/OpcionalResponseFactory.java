package com.javacar.lojadecarro.factory.opcional;

import com.javacar.lojadecarro.dto.response.OpcionalResponse;

public class OpcionalResponseFactory {
    private Long id;
    private String nome;
    private boolean ativo;

    private OpcionalResponseFactory() {
    }

    public static OpcionalResponseFactory criarResponse() {
        return new OpcionalResponseFactory();
    }

    public OpcionalResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.nome = "Freio ABS";
        this.ativo = true;
        return this;
    }


    public OpcionalResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }

    public OpcionalResponseFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public OpcionalResponseFactory comAtivo(boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public OpcionalResponse build() {
        return new OpcionalResponse(
                id,
                nome,
                ativo);
    }
}
