package com.javacar.lojadecarro.factory.combustivel;

import com.javacar.lojadecarro.dto.response.CombustivelResponse;

public class CombustivelResponseFactory {
    private Long id;
    private String nome;
    private boolean ativo;

    private CombustivelResponseFactory() {}

    public static CombustivelResponseFactory criarResponse() {
        return new CombustivelResponseFactory();
    }

    public CombustivelResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.nome = "Gasolina";
        this.ativo = true;
        return this;
    }

    public CombustivelResponseFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public CombustivelResponseFactory comId(Long id) {
        this.id= id;
        return this;
    }

    public CombustivelResponseFactory comAtivo(boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public CombustivelResponse build() {
        return new CombustivelResponse(id, nome, ativo);
    }
}
