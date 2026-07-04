package com.JavangularCar.LojadeCarro.factory.modelo;

import com.JavangularCar.LojadeCarro.entity.Marca;
import com.JavangularCar.LojadeCarro.entity.Modelo;
import com.JavangularCar.LojadeCarro.factory.marca.MarcaEntityFactory;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ModeloEntityFactory {

    private final Modelo modelo;
    Marca marca = MarcaEntityFactory
            .criaMarca()
            .comId(3L)
            .comNome("Chevrolet")
            .comURL("https://www.chevrolet.com")
            .build();

    private ModeloEntityFactory() {
        this.modelo = new Modelo();
    }

    public static ModeloEntityFactory criarEntity() {
        return new ModeloEntityFactory();
    }

    public ModeloEntityFactory comTodosOsCampos() {
        modelo.setId(1L);
        modelo.setNome("Onix");
        modelo.setMarca(marca);
        return this;
    }

    public ModeloEntityFactory comTodosOsCamposExcetoId() {
        modelo.setNome("Onix");
        modelo.setMarca(marca);
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

    public ModeloEntityFactory comMarca(Long id, String nome, String url) {
        modelo.setMarca(MarcaEntityFactory
                .criaMarca()
                .comId(id)
                .comNome(nome)
                .comURL(url)
                .build());

        return this;
    }

    public Modelo build() {
        return modelo;
    }
}
