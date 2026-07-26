package com.javacar.lojadecarro.factory.cor;

import com.javacar.lojadecarro.dto.response.CorResponse;

public class CorResponseFactory {
    private Long id;
    private String nome;
    private boolean ativo;

    private CorResponseFactory() {
    }

    public static CorResponseFactory criarResponse() {
        return new CorResponseFactory();
    }


    public CorResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.nome = "Branco";
        this.ativo = true;
        return this;
    }

    public CorResponseFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public CorResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }
    public CorResponseFactory comAtivo(boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public CorResponse build() {
        return new CorResponse(id, nome, ativo);
    }
}
