package com.JavangularCar.LojadeCarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Resposta para criação de um carro para venda")
public record CarroResponse(@Schema(example = "1", description = "Id do carro") Long id,
                            @Schema(example = "QUV1F836", description = "Placa do carro") String placa,
                            @Schema(example = "Chevrolet", description = "Marca do carro") String marca,
                            @Schema(example = "Onix", description = "Modelo do carro") String modelo,
                            @Schema(example = "75990.00", description = "Valor do carro") BigDecimal valor,
                            @Schema(example = "70000", description = "Quilometragem do carro") Double quilometragem,
                            @Schema(example = "2023", description = "Ano de fabricação do carro") Integer anoFabricacao,
                            @Schema(example = "www.google.com", description = "URL da imagem do carro") String url,
                            @Schema(example = "true", description = "Carro está ativo?") boolean ativo) {
}
