package com.javacar.lojadecarro.factory.combustivel;

import com.javacar.lojadecarro.dto.response.CombustivelResponse;

public class CombustivelResponseFactory {
    private Long id;
    private String nome;

    private CombustivelResponseFactory() {}

    public static CombustivelResponseFactory criarResponse() {
        return new CombustivelResponseFactory();
    }

    public static CombustivelResponseFactory criarResponse (CombustivelResponse combustivelResponse) {
        return new CombustivelResponseFactory();
    }

    public CombustivelResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.nome = "Gasolina";
        return this;
    }

    public CombustivelResponseFactory comTodosOsCamposExcetoId() {
        this.nome = "Gasolina";
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


    public CombustivelResponse build() {
        return new CombustivelResponse(id, nome);
    }
}
