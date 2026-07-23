package com.javacar.lojadecarro.factory.opcional;

import com.javacar.lojadecarro.entity.Opcional;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class OpcionalEntityFactory {

    private final Opcional opcional;

    private OpcionalEntityFactory() {
        this.opcional = new Opcional();
    }

    public static OpcionalEntityFactory criarEntity() {
        return new OpcionalEntityFactory();
    }

    public OpcionalEntityFactory comTodosOsCampos() {
        opcional.setId(1L);
        opcional.setNome("Freio ABS");
        opcional.setAtivo(true);
        return this;
    }


    public OpcionalEntityFactory comId(Long id) {
        opcional.setId(id);
        return this;
    }
    public OpcionalEntityFactory comNome(String nome) {
        opcional.setNome(nome);
        return this;
    }
    public OpcionalEntityFactory comAtivo(boolean ativo) {
        opcional.setAtivo(ativo);
        return this;
    }

    public Opcional build() {
        return opcional;
    }
}
