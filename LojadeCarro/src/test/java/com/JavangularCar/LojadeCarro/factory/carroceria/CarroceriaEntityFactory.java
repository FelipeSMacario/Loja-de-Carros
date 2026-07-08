package com.JavangularCar.LojadeCarro.factory.carroceria;

import com.JavangularCar.LojadeCarro.entity.Carroceria;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class CarroceriaEntityFactory {

    private final Carroceria carroceria;

    private CarroceriaEntityFactory() {
        this.carroceria = new Carroceria();
    }

    public static CarroceriaEntityFactory criarEntity() {
        return new CarroceriaEntityFactory();
    }

    public CarroceriaEntityFactory comTodosOsCampos() {
        carroceria.setId(1L);
        carroceria.setNome("Hatch");
        return this;
    }


    public CarroceriaEntityFactory comNome(String nome) {
        carroceria.setNome(nome);
        return this;
    }

    public CarroceriaEntityFactory comId(Long id) {
        carroceria.setId(id);
        return this;
    }

    public Carroceria build() {
        return carroceria;
    }
}
