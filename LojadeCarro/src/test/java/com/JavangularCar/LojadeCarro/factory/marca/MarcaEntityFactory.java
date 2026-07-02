package com.JavangularCar.LojadeCarro.factory.marca;

import com.JavangularCar.LojadeCarro.entity.Marca;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class MarcaEntityFactory {

    private final Marca marca;

    private MarcaEntityFactory() {
        this.marca = new Marca();
    }

    public static MarcaEntityFactory criaMarca() {
        return new MarcaEntityFactory();
    }

    public MarcaEntityFactory comTodosOsCampos() {
        marca.setId(1L);
        marca.setNome("Ford");
        marca.setUrl("https://www.google.com");
        return this;
    }

    public MarcaEntityFactory comTodosOsCamposExcetoId() {
        marca.setNome("Ford");
        marca.setUrl("https://www.google.com");
        return this;
    }

    public MarcaEntityFactory comNome(String nome) {
        marca.setNome(nome);
        return this;
    }

    public MarcaEntityFactory comId(Long id) {
        marca.setId(id);
        return this;
    }

    public MarcaEntityFactory comURL(String url) {
        marca.setUrl(url);
        return this;
    }

    public Marca build() {
        return marca;
    }
}
