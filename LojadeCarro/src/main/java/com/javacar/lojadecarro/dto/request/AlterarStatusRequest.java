package com.javacar.lojadecarro.dto.request;

import com.javacar.lojadecarro.enums.StatusVeiculo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request para atualização de status do veiculo")
public record AlterarStatusRequest(
        @Schema(example = "VENDIDO", description = "Descrição do status do veiculo")
        @NotNull StatusVeiculo status
) {
}
