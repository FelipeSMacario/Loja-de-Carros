package com.javacar.lojadecarro.enums;

public enum StatusVenda {
    CONCLUIDA("Concluída"),
    EM_ANDAMENTO("Em andamento"),
    CANCELADA("Cancelada"),
    PAUSADA("Pausada");

    private final String descricao;

    StatusVenda(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
