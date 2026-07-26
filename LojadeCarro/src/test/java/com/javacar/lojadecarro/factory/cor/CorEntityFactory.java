package com.javacar.lojadecarro.factory.cor;

import com.javacar.lojadecarro.entity.Cor;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class CorEntityFactory {

    private final Cor cor;

    private CorEntityFactory() {
        this.cor = new Cor();
    }

    public static CorEntityFactory criarEntity() {
        return new CorEntityFactory();
    }

    public CorEntityFactory comTodosOsCampos() {
        cor.setId(1L);
        cor.setNome("Branco");
        cor.setAtivo(true);
        return this;
    }


    public CorEntityFactory comNome(String nome) {
        cor.setNome(nome);
        return this;
    }

    public CorEntityFactory comId(Long id) {
        cor.setId(id);
        return this;
    }
    public CorEntityFactory comAtivo(boolean ativo) {
        cor.setAtivo(ativo);
        return this;
    }

    public Cor build() {
        return cor;
    }
}
