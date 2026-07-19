//package com.javacar.lojadecarro.factory.combustivel;
//
//import com.javacar.lojadecarro.entity.Combustivel;
//import lombok.RequiredArgsConstructor;
//
//import static lombok.AccessLevel.PRIVATE;
//
//@RequiredArgsConstructor(access = PRIVATE)
//public final class CombustivelEntityFactory {
//
//    private final Combustivel combustivel;
//
//    private CombustivelEntityFactory() {
//        this.combustivel = new Combustivel();
//    }
//
//    public static CombustivelEntityFactory criarEntity() {
//        return new CombustivelEntityFactory();
//    }
//
//    public CombustivelEntityFactory comTodosOsCampos() {
//        combustivel.setId(1L);
//        combustivel.setNome("Gasolina");
//        return this;
//    }
//
//    public CombustivelEntityFactory comTodosOsCamposExcetoId() {
//        combustivel.setNome("Gasolina");
//        return this;
//    }
//
//    public CombustivelEntityFactory comNome(String nome) {
//        combustivel.setNome(nome);
//        return this;
//    }
//
//    public CombustivelEntityFactory comId(Long id) {
//        combustivel.setId(id);
//        return this;
//    }
//
//
//    public Combustivel build() {
//        return combustivel;
//    }
//}
