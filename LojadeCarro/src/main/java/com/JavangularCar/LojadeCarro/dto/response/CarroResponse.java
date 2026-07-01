package com.JavangularCar.LojadeCarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para criação de um carro para venda")
public record CarroResponse(@Schema(example = "1", description = "Id do carro") Long id,
                            @Schema(example = "123", description = "Placa do carro") String placa) {
}
