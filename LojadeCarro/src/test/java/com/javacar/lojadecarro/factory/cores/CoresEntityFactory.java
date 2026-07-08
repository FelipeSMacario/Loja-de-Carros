package com.javacar.lojadecarro.factory.cores;

import com.javacar.lojadecarro.entity.Cores;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class CoresEntityFactory {

    private final Cores cores;

    private CoresEntityFactory() {
        this.cores = new Cores();
    }

    public static CoresEntityFactory criarEntity() {
        return new CoresEntityFactory();
    }

    public CoresEntityFactory comTodosOsCampos() {
        cores.setId(1L);
        cores.setNome("Branco");
        return this;
    }

    public CoresEntityFactory comTodosOsCamposExcetoId() {
        cores.setNome("Branco");
        return this;
    }

    public CoresEntityFactory comNome(String nome) {
        cores.setNome(nome);
        return this;
    }

    public CoresEntityFactory comId(Long id) {
        cores.setId(id);
        return this;
    }

    public Cores build() {
        return cores;
    }
}
