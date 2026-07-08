package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para criação das kits")
public record KitResponse(@Schema(example = "1", description = "Id do kit")
                          Long id,
                          @Schema(example = "true", description = "Carro possui freios ABS?")
                          boolean freioABS,
                          @Schema(example = "true", description = "Carro possui rodas de liga leve?")
                          boolean rodaLigaLeve,
                          @Schema(example = "true", description = "Carro é automático?")
                          boolean automatico,
                          @Schema(example = "true", description = "Carro possui direção hidráulica?")
                          boolean direcaoHidraulica,
                          @Schema(example = "true", description = "Carro possui ar condicionado?")
                          boolean arCondicionado,
                          @Schema(example = "true", description = "Carro possui 4 portas?")
                          boolean quatroPortas,
                          @Schema(example = "true", description = "Carro possui bancos de couro?")
                          boolean bancoCouro,
                          @Schema(example = "1", description = "ID do carro com essas características")
                          Long idCarro,
                          @Schema(example = "Chevrolet", description = "Descrição da marca do carro")
                          String marca,
                          @Schema(example = "Onix", description = "Descrição do modelo do carro")
                          String modelo) {
}
