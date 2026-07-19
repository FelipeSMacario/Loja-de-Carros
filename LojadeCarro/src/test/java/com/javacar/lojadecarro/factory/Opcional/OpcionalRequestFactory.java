//package com.javacar.lojadecarro.factory.Opcional;
//
//import com.javacar.lojadecarro.dto.request.OpcionalRequest;
//
//import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
//
//public class KitRequestFactory {
//    private boolean freioABS;
//    private boolean rodaLigaLeve;
//    private boolean automatico;
//    private boolean direcaoHidraulica;
//    private boolean arCondicionado;
//    private boolean quatroPortas;
//    private boolean bancoCouro;
//    private Long idVeiculo;
//
//    private KitRequestFactory() {
//    }
//
//    public static KitRequestFactory kitRequestFactory() {
//        return new KitRequestFactory();
//    }
//
//    public static KitRequestFactory criarRequest() {
//        return new KitRequestFactory();
//    }
//
//    public KitRequestFactory comTodosOsCampos() {
//        this.freioABS = true;
//        this.rodaLigaLeve = true;
//        this.automatico = true;
//        this.direcaoHidraulica = true;
//        this.arCondicionado = true;
//        this.quatroPortas = true;
//        this.bancoCouro = true;
//        this.idVeiculo = ID_VALIDO;
//        return this;
//    }
//
//    public KitRequestFactory comTodosOSCamposExcetoFreio() {
//        this.rodaLigaLeve = true;
//        this.automatico = true;
//        this.direcaoHidraulica = true;
//        this.arCondicionado = true;
//        this.quatroPortas = true;
//        this.bancoCouro = true;
//        this.idVeiculo = ID_VALIDO;
//        return this;
//    }
//
//    public KitRequestFactory comFreio(boolean freioABS) {
//        this.freioABS = freioABS;
//        return this;
//    }
//
//    public KitRequestFactory comId(Long idVeiculo) {
//        this.idVeiculo = idVeiculo;
//        return this;
//    }
//
//    public OpcionalRequest build() {
//        return new OpcionalRequest(freioABS,
//                rodaLigaLeve,
//                automatico,
//                direcaoHidraulica,
//                arCondicionado,
//                quatroPortas,
//                bancoCouro,
//                idVeiculo);
//    }
//}
