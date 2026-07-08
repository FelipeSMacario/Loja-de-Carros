package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para criação da imagem")
public record ImagensResponse(@Schema(example = "1", description = "ID da imagem")
                              Long id,
                              @Schema(example = "https://upload.wikimedia.org/wikipedia/commons/3/3e/Ford_logo_flat.svg", description = "URL da marca")
                              String url,
                              @Schema(example = "1", description = "ID do carro cadastrado")
                              Long idCarro) {
}
