//package com.javacar.lojadecarro.factory.cores;
//
//import com.javacar.lojadecarro.dto.request.CorRequest;
//
//public class CoresRequestFactory {
//    private String nome;
//
//    private CoresRequestFactory() {
//    }
//
//    public static CoresRequestFactory coresRequestFactory() {
//        return new CoresRequestFactory();
//    }
//
//    public static CoresRequestFactory criarRequest() {
//        return new CoresRequestFactory();
//    }
//
//    public CoresRequestFactory comTodosOsCampos() {
//        this.nome = "Branco";
//        return this;
//    }
//
//    public CorRequest build() {
//        return new CorRequest(nome);
//    }
//}
