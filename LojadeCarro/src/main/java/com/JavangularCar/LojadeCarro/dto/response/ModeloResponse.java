package com.JavangularCar.LojadeCarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para criação dos modelo")
public record ModeloResponse(@Schema(example = "1", description = "ID do modelo")
                             Long id,
                             @Schema(example = "Onix", description = "Nome do modelo")
                             String nome,
                             @Schema(example = "{ \"id\": 3, \"nome\": \"Chevrolet\", \"url\": \"https://www.chevrolet.com.br\" }")
                             MarcaResponse marcaResponse) {
}
