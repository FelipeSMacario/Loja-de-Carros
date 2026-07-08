package com.JavangularCar.LojadeCarro.factory.kit;

import com.JavangularCar.LojadeCarro.dto.request.KitRequest;

import static com.JavangularCar.LojadeCarro.support.TestConstants.ID_VALIDO;

public class KitRequestFactory {
    private boolean freioABS;
    private boolean rodaLigaLeve;
    private boolean automatico;
    private boolean direcaoHidraulica;
    private boolean arCondicionado;
    private boolean quatroPortas;
    private boolean bancoCouro;
    private Long idCarro;

    private KitRequestFactory() {
    }

    public static KitRequestFactory kitRequestFactory() {
        return new KitRequestFactory();
    }

    public static KitRequestFactory criarRequest() {
        return new KitRequestFactory();
    }

    public KitRequestFactory comTodosOsCampos() {
        this.freioABS = true;
        this.rodaLigaLeve = true;
        this.automatico = true;
        this.direcaoHidraulica = true;
        this.arCondicionado = true;
        this.quatroPortas = true;
        this.bancoCouro = true;
        this.idCarro = ID_VALIDO;
        return this;
    }

    public KitRequestFactory comTodosOSCamposExcetoFreio() {
        this.rodaLigaLeve = true;
        this.automatico = true;
        this.direcaoHidraulica = true;
        this.arCondicionado = true;
        this.quatroPortas = true;
        this.bancoCouro = true;
        this.idCarro = ID_VALIDO;
        return this;
    }

    public KitRequestFactory comFreio(boolean freioABS) {
        this.freioABS = freioABS;
        return this;
    }

    public KitRequestFactory comId(Long idCarro) {
        this.idCarro = idCarro;
        return this;
    }

    public KitRequest build() {
        return new KitRequest(freioABS,
                rodaLigaLeve,
                automatico,
                direcaoHidraulica,
                arCondicionado,
                quatroPortas,
                bancoCouro,
                idCarro);
    }
}
