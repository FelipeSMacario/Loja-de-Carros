package com.javacar.lojadecarro.enums;


public enum Entidade {
    CARROCERIA("A carroceria"),
    VEICULO("O veículo"),
    COR("A cor"),
    MARCA("A marca"),
    MODELO("O modelo"),
    COMBUSTIVEL("O combustivel"),
    OPCIONAL("O opcional"),
    IMAGEM("A imagem"),
    ROLE("A role"),
    USUARIO("O usuário(a)");

    private final String descricao;

    Entidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
    public String naoEncontrada() {
        return descricao + " não foi encontrado(a) com o id: ";
    }

    public String jaAtiva() {
        return descricao + " já está ativo(a).";
    }

    public String jaInativa() {
        return descricao + " já está inativo(a).";
    }
}
