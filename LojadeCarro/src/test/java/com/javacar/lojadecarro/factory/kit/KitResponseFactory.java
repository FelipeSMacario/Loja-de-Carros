package com.javacar.lojadecarro.factory.kit;

import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.dto.response.KitResponse;

import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;

public class KitResponseFactory {
    private Long id;
    private boolean freioABS;
    private boolean rodaLigaLeve;
    private boolean automatico;
    private boolean direcaoHidraulica;
    private boolean arCondicionado;
    private boolean quatroPortas;
    private boolean bancoCouro;
    private Long idCarro;
    private String marca;
    private String modelo;

    private KitResponseFactory() {}

    public static KitResponseFactory criarResponse() {
        return new KitResponseFactory();
    }

    public static KitResponseFactory criarResponse (CarroceriaResponse marcaResponse) {
        return new KitResponseFactory();
    }

    public KitResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.freioABS = true;
        this.rodaLigaLeve = true;
        this.automatico = true;
        this.direcaoHidraulica = true;
        this.arCondicionado = true;
        this.quatroPortas = true;
        this.bancoCouro = true;
        this.idCarro = ID_VALIDO;
        this.marca = "Chevrolet";
        this.modelo = "Onix";
        return this;
    }

    public KitResponseFactory comTodosOsCamposExcetoId() {
        this.freioABS = true;
        this.rodaLigaLeve = true;
        this.automatico = true;
        this.direcaoHidraulica = true;
        this.arCondicionado = true;
        this.quatroPortas = true;
        this.bancoCouro = true;
        this.idCarro = ID_VALIDO;
        this.marca = "Chevrolet";
        this.modelo = "Onix";
        return this;
    }


    public KitResponseFactory comId(Long id) {
        this.id= id;
        return this;
    }
    public KitResponseFactory comFreio(boolean freioABS) {
        this.freioABS= freioABS;
        return this;
    }


    public KitResponse build() {
        return new KitResponse(
                id,
                freioABS,
                rodaLigaLeve,
                automatico,
                direcaoHidraulica,
                arCondicionado,
                quatroPortas,
                bancoCouro,
                idCarro,
                marca,
                modelo);
    }
}
