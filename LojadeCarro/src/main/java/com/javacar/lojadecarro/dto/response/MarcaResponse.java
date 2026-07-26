package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta para criação das marcas")
public record MarcaResponse(@Schema(example = "1", description = "ID da cor")
                            Long id,
                            @Schema(example = "Ford", description = "Nome da marca")
                            String nome,
                            @Schema(example = "https://upload.wikimedia.org/wikipedia/commons/3/3e/Ford_logo_flat.svg", description = "URL da marca")
                            String url,
                            @Schema(example = "true", description = "Status da marca")
                            boolean ativo) {
}
