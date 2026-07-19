//package com.javacar.lojadecarro.factory.kit;
//
//import com.javacar.lojadecarro.entity.Opcional;
//import com.javacar.lojadecarro.factory.carro.CarroEntityFactory;
//import lombok.RequiredArgsConstructor;
//
//import static lombok.AccessLevel.PRIVATE;
//
//@RequiredArgsConstructor(access = PRIVATE)
//public final class KitEntityFactory {
//
//    private final Opcional opcional;
//
//    private KitEntityFactory() {
//        this.opcional = new Opcional();
//    }
//
//    public static KitEntityFactory criarEntity() {
//        return new KitEntityFactory();
//    }
//
//    public KitEntityFactory comTodosOsCampos() {
//        opcional.setId(1L);
//        opcional.setFreioABS(true);
//        opcional.setRodaLigaLeve(true);
//        opcional.setAutomatico(true);
//        opcional.setDirecaoHidraulica(true);
//        opcional.setArCondicionado(true);
//        opcional.setQuatroPortas(true);
//        opcional.setBancoCouro(true);
//        opcional.setVeiculo(CarroEntityFactory.criarEntity().comTodosOsCampos().build());
//        return this;
//    }
//
//
//    public KitEntityFactory comId(Long id) {
//        opcional.setId(id);
//        return this;
//    }
//
//    public Opcional build() {
//        return opcional;
//    }
//}
