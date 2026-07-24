package com.javacar.lojadecarro.enums;

public enum StatusVeiculo {
    DISPONIVEL("Disponível"),
    RESERVADO("Reservado"),
    VENDIDO("Vendido"),
    PAUSADO("Pausado");

    private final String descricao;

    StatusVeiculo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
