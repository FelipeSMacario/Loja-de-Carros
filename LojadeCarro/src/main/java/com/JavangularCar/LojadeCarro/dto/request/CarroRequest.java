package com.JavangularCar.LojadeCarro.dto.request;

public record CarroRequest(String quilometragem,
                           String url,
                           Double valor,
                           String placa,
                           String motor,
                           Integer anoFabricacao,
                           Long idCarroceria,
                           Long idMarca,
                           Long idCores,
                           Long idModelo,
                           Long idUsuario,
                           Long idCombustivel) {
}
