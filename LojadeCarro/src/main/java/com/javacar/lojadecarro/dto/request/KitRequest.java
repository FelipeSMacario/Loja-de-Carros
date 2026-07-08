package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request para criação de um kit")
public record KitRequest(@Schema(example = "true", description = "Carro possui freios ABS?")
                         @NotNull boolean freioABS,
                         @Schema(example = "true", description = "Carro possui rodas de liga leve?")
                         @NotNull boolean rodaLigaLeve,
                         @Schema(example = "true", description = "Carro é automático?")
                         @NotNull boolean automatico,
                         @Schema(example = "true", description = "Carro possui direção hidráulica?")
                         @NotNull boolean direcaoHidraulica,
                         @Schema(example = "true", description = "Carro possui ar condicionado?")
                         @NotNull boolean arCondicionado,
                         @Schema(example = "true", description = "Carro possui 4 portas?")
                         @NotNull boolean quatroPortas,
                         @Schema(example = "true", description = "Carro possui bancos de couro?")
                         @NotNull boolean bancoCouro,
                         @Schema(example = "1", description = "ID do carro com essas características")
                         @NotNull Long idCarro) {
}
