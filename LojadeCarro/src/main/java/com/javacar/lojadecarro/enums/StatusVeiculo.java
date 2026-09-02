package com.javacar.lojadecarro.enums;

public enum StatusVeiculo {
    DISPONIVEL("Disponível"),
    RESERVADO("Reservado"),
    PAUSADO("Pausado"),
    VENDIDO("Vendido");

    private final String descricao;

    StatusVeiculo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
