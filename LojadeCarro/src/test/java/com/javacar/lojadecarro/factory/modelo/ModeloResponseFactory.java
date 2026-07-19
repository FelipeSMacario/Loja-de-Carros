//package com.javacar.lojadecarro.factory.modelo;
//
//import com.javacar.lojadecarro.dto.response.MarcaResponse;
//import com.javacar.lojadecarro.dto.response.ModeloResponse;
//import com.javacar.lojadecarro.factory.marca.MarcaResponseFactory;
//
//public class ModeloResponseFactory {
//    private Long id;
//    private String nome;
//    private MarcaResponse marcaResponse = MarcaResponseFactory
//            .criarResponse()
//            .comId(3L)
//            .comNome("Chevrolet")
//            .comURL("https://www.chevrolet.com")
//            .build();
//
//    private ModeloResponseFactory() {
//    }
//
//    public static ModeloResponseFactory criarResponse() {
//        return new ModeloResponseFactory();
//    }
//
//    public ModeloResponseFactory comTodosOsCampos() {
//        this.id = 1L;
//        this.nome = "Onix";
//        return this;
//    }
//
//    public ModeloResponseFactory comNome(String nome) {
//        this.nome = nome;
//        return this;
//    }
//
//    public ModeloResponseFactory comId(Long id) {
//        this.id = id;
//        return this;
//    }
//
//    public ModeloResponse build() {
//        return new ModeloResponse(id, nome, marcaResponse);
//    }
//}
