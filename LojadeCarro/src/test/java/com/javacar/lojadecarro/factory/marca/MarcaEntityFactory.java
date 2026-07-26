package com.javacar.lojadecarro.factory.marca;

import com.javacar.lojadecarro.entity.Marca;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class MarcaEntityFactory {

    private final Marca marca;

    private MarcaEntityFactory() {
        this.marca = new Marca();
    }

    public static MarcaEntityFactory criarEntity() {
        return new MarcaEntityFactory();
    }

    public MarcaEntityFactory comTodosOsCampos() {
        marca.setId(1L);
        marca.setNome("Ford");
        marca.setUrl("https://www.google.com");
        marca.setAtivo(true);
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
    public MarcaEntityFactory comAtivo(boolean ativo) {
        marca.setAtivo(ativo);
        return this;
    }

    public Marca build() {
        return marca;
    }
}
