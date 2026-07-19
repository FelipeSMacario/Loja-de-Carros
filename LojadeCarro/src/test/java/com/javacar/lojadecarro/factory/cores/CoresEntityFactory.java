//package com.javacar.lojadecarro.factory.cores;
//
//import com.javacar.lojadecarro.entity.Cor;
//import lombok.RequiredArgsConstructor;
//
//import static lombok.AccessLevel.PRIVATE;
//
//@RequiredArgsConstructor(access = PRIVATE)
//public final class CoresEntityFactory {
//
//    private final Cor cor;
//
//    private CoresEntityFactory() {
//        this.cor = new Cor();
//    }
//
//    public static CoresEntityFactory criarEntity() {
//        return new CoresEntityFactory();
//    }
//
//    public CoresEntityFactory comTodosOsCampos() {
//        cor.setId(1L);
//        cor.setNome("Branco");
//        return this;
//    }
//
//    public CoresEntityFactory comTodosOsCamposExcetoId() {
//        cor.setNome("Branco");
//        return this;
//    }
//
//    public CoresEntityFactory comNome(String nome) {
//        cor.setNome(nome);
//        return this;
//    }
//
//    public CoresEntityFactory comId(Long id) {
//        cor.setId(id);
//        return this;
//    }
//
//    public Cor build() {
//        return cor;
//    }
//}
