//package com.javacar.lojadecarro.factory.carroceria;
//
//import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
//
//public class CarroceriaRequestFactory {
//    private String nome;
//
//    private CarroceriaRequestFactory() {
//    }
//
//    public static CarroceriaRequestFactory marcaRequestFactory() {
//        return new CarroceriaRequestFactory();
//    }
//
//    public static CarroceriaRequestFactory criarRequest() {
//        return new CarroceriaRequestFactory();
//    }
//
//    public CarroceriaRequestFactory comTodosOsCampos() {
//        this.nome = "Hatch";
//        return this;
//    }
//    public CarroceriaRequestFactory comNome(String nome) {
//        this.nome = nome;
//        return this;
//    }
//
//    public CarroceriaRequest build() {
//        return new CarroceriaRequest(nome);
//    }
//}
