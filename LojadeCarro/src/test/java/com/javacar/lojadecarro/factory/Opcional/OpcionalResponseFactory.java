//package com.javacar.lojadecarro.factory.Opcional;
//
//import com.javacar.lojadecarro.dto.response.OpcionalResponse;
//
//import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
//
//public class KitResponseFactory {
//    private Long id;
//    private boolean freioABS;
//    private boolean rodaLigaLeve;
//    private boolean automatico;
//    private boolean direcaoHidraulica;
//    private boolean arCondicionado;
//    private boolean quatroPortas;
//    private boolean bancoCouro;
//    private Long idVeiculo;
//    private String marca;
//    private String modelo;
//
//    private KitResponseFactory() {
//    }
//
//    public static KitResponseFactory criarResponse() {
//        return new KitResponseFactory();
//    }
//
//    public KitResponseFactory comTodosOsCampos() {
//        this.id = 1L;
//        this.freioABS = true;
//        this.rodaLigaLeve = true;
//        this.automatico = true;
//        this.direcaoHidraulica = true;
//        this.arCondicionado = true;
//        this.quatroPortas = true;
//        this.bancoCouro = true;
//        this.idVeiculo = ID_VALIDO;
//        this.marca = "Chevrolet";
//        this.modelo = "Onix";
//        return this;
//    }
//
//    public KitResponseFactory comTodosOsCamposExcetoId() {
//        this.freioABS = true;
//        this.rodaLigaLeve = true;
//        this.automatico = true;
//        this.direcaoHidraulica = true;
//        this.arCondicionado = true;
//        this.quatroPortas = true;
//        this.bancoCouro = true;
//        this.idVeiculo = ID_VALIDO;
//        this.marca = "Chevrolet";
//        this.modelo = "Onix";
//        return this;
//    }
//
//
//    public KitResponseFactory comId(Long id) {
//        this.id = id;
//        return this;
//    }
//
//    public KitResponseFactory comFreio(boolean freioABS) {
//        this.freioABS = freioABS;
//        return this;
//    }
//
//
//    public OpcionalResponse build() {
//        return new OpcionalResponse(
//                id,
//                freioABS,
//                rodaLigaLeve,
//                automatico,
//                direcaoHidraulica,
//                arCondicionado,
//                quatroPortas,
//                bancoCouro,
//                idVeiculo,
//                marca,
//                modelo);
//    }
//}
