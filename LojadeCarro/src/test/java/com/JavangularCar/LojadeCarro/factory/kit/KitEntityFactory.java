package com.JavangularCar.LojadeCarro.factory.kit;

import com.JavangularCar.LojadeCarro.entity.Kit;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class KitEntityFactory {

    private final Kit kit;

    private KitEntityFactory() {
        this.kit = new Kit();
    }

    public static KitEntityFactory criarEntity() {
        return new KitEntityFactory();
    }

    public KitEntityFactory comTodosOsCampos() {
        kit.setId(1L);
       // kit.setNome("Hatch");
        return this;
    }


    public KitEntityFactory comNome(String nome) {
       // kit.setNome(nome);
        return this;
    }

    public KitEntityFactory comId(Long id) {
        kit.setId(id);
        return this;
    }

    public Kit build() {
        return kit;
    }
}
