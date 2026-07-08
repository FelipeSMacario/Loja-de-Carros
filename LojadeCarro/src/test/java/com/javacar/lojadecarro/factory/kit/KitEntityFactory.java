package com.javacar.lojadecarro.factory.kit;

import com.javacar.lojadecarro.entity.Kit;
import com.javacar.lojadecarro.factory.carro.CarroEntityFactory;
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
        kit.setFreioABS(true);
        kit.setRodaLigaLeve(true);
        kit.setAutomatico(true);
        kit.setDirecaoHidraulica(true);
        kit.setArCondicionado(true);
        kit.setQuatroPortas(true);
        kit.setBancoCouro(true);
        kit.setCarro(CarroEntityFactory.criarEntity().comTodosOsCampos().build());
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
