package com.javacar.lojadecarro.security.dto;

import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta da autenticação")
public record LoginResponse(
        @Schema(description = "Token de acesso JWT")
        String accessToken,

        @Schema(example = "Bearer", description = "Tipo do token")
        String tokenType,

        @Schema(description = "Usuário autenticado")
        UsuarioResponse usuario
) {
}
