package com.JavangularCar.LojadeCarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request para o cadastro de um carro para venda")
public record CarroRequest(@Schema(example = "123000", description = "quilometragem do carro")
                           @NotNull Double quilometragem,
                           @Schema(example =
                           "https://4bossnews.com.br/wp-content/uploads/2024/10/Ferrari-F80-4Boss_01-1024x536-1.jpg",
                                   description = "Foto do carro")
                           @NotBlank String url,
                           @Schema(example = "100000", description = "Valor do carro")
                           @NotNull BigDecimal valor,
                           @Schema(example = "123", description = "Placa do carro")
                           @NotBlank String placa,
                           @Schema(example = "1.0", description = "Motor do carro")
                           @NotBlank String motor,
                           @Schema(example = "1999", description = "Ano de fabricação do carro")
                           @NotNull Integer anoFabricacao,
                           @Schema(example = "1", description = "Id da carroceria")
                           @NotNull Long idCarroceria,
                           @Schema(example = "1", description = "Id da marca")
                           @NotNull Long idMarca,
                           @Schema(example = "1", description = "Id da cor")
                           @NotNull Long idCores,
                           @Schema(example = "1", description = "Id do modelo")
                           @NotNull Long idModelo,
                           @Schema(example = "1", description = "Id do usuario")
                           @NotNull Long idUsuario,
                           @Schema(example = "1", description = "Id do combustível")
                           @NotNull Long idCombustivel) {
}
