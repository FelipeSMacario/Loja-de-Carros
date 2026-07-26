package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para criação da imagem")
public record ImagemResponse(@Schema(example = "1", description = "ID da imagem")
                              Long id,
                             @Schema(example = "foto", description = "nome original da imagem")
                              String nomeOriginal,
                             @Schema(example = "imagens/2026/foto.jpg", description = "objectKey da imagem")
                              String objectKey,
                             @Schema(example = "true", description = "Identificar se a imagem é a do perfil")
                              boolean principal) {
}
