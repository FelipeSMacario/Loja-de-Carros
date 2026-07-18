package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Request para o cadastro de um veiculo")
public record VeiculoRequest(@Schema(example = "123000", description = "quilometragem do veiculo")
                             @NotNull Double quilometragem,
                             @Schema(example = "100000", description = "Valor do veiculo")
                             @NotNull BigDecimal valor,
                             @Schema(example = "123", description = "Placa do veiculo")
                             @NotBlank @Size(min = 7, max = 7) String placa,
                             @Schema(example = "1.0", description = "Motor do veiculo")
                             @NotBlank String motor,
                             @Schema(example = "Veiculo em perfeito estado", description = "Descrição do veiculo")
                             @NotBlank String descricao,
                             @Schema(example = "1999", description = "Ano de fabricação do veiculo")
                             @NotNull Short anoFabricacao,
                             @Schema(example = "1", description = "Ids dos opcionais")
                             List<Long> idsOpcionais,
                             @Schema(example = "1", description = "Id da carroceria")
                             @NotNull Long idCarroceria,
                             @Schema(example = "1", description = "Id da cor")
                             @NotNull Long idCores,
                             @Schema(example = "1", description = "Id do modelo")
                             @NotNull Long idModelo,
                             @Schema(example = "1", description = "Id do usuario")
                             @NotNull Long idUsuario,
                             @Schema(example = "1", description = "Id do combustível")
                             @NotNull Long idCombustivel) {
}
