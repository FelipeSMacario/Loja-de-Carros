//package com.javacar.lojadecarro.factory.cores;
//
//import com.javacar.lojadecarro.dto.response.CorResponse;
//
//public class CoresResponseFactory {
//    private Long id;
//    private String nome;
//
//    private CoresResponseFactory() {
//    }
//
//    public static CoresResponseFactory criarResponse() {
//        return new CoresResponseFactory();
//    }
//
//
//    public CoresResponseFactory comTodosOsCampos() {
//        this.id = 1L;
//        this.nome = "Branco";
//        return this;
//    }
//
//    public CoresResponseFactory comTodosOsCamposExcetoId() {
//        this.nome = "Branco";
//        return this;
//    }
//
//    public CoresResponseFactory comNome(String nome) {
//        this.nome = nome;
//        return this;
//    }
//
//    public CoresResponseFactory comId(Long id) {
//        this.id = id;
//        return this;
//    }
//
//    public CorResponse build() {
//        return new CorResponse(id, nome);
//    }
//}
