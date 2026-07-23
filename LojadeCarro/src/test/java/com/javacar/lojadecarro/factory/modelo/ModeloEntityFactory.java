package com.javacar.lojadecarro.factory.modelo;

import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.factory.helper.MarcaHelper;
import lombok.RequiredArgsConstructor;

import static com.javacar.lojadecarro.factory.helper.MarcaHelper.criarMarcaEntity;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ModeloEntityFactory {

    private final Modelo modelo;

    private ModeloEntityFactory() {
        this.modelo = new Modelo();
    }

    public static ModeloEntityFactory criarEntity() {
        return new ModeloEntityFactory();
    }

    public ModeloEntityFactory comTodosOsCampos() {
        modelo.setId(1L);
        modelo.setNome("Onix");
        modelo.setMarca(criarMarcaEntity());
        modelo.setAtivo(true);
        return this;
    }

    public ModeloEntityFactory comNome(String nome) {
        modelo.setNome(nome);
        return this;
    }

    public ModeloEntityFactory comId(Long id) {
        modelo.setId(id);
        return this;
    }

    public ModeloEntityFactory comAtivo(boolean ativo) {
        modelo.setAtivo(ativo);
        return this;
    }

    public Modelo build() {
        return modelo;
    }
}
