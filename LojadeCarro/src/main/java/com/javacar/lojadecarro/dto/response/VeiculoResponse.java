package com.javacar.lojadecarro.dto.response;

import com.javacar.lojadecarro.enums.StatusVeiculo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Resposta para criação de um veiculo para venda")
public record VeiculoResponse(@Schema(example = "1", description = "Id do veiculo") Long id,
                              @Schema(example = "QUV1F836", description = "Placa do veiculo") String placa,
                              @Schema(example = "Chevrolet", description = "Marca do veiculo") String marca,
                              @Schema(example = "Onix", description = "Modelo do veiculo") String modelo,
                              @Schema(example = "75990.00", description = "Valor do veiculo") BigDecimal valor,
                              @Schema(example = "70000", description = "Quilometragem do veiculo") Integer quilometragem,
                              @Schema(example = "2023", description = "Ano de fabricação do veiculo") Short anoFabricacao,
                              @Schema(example = "DISPONIVEL", description = "Status do veiculo") StatusVeiculo statusVeiculo) {
}
