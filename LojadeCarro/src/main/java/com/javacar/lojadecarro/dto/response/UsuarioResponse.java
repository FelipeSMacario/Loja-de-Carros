package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta do usuário")
public record UsuarioResponse(@Schema(example = "1", description = "ID da usuário")
                              Long id,
                              @Schema(example = "Felipe", description = "Nome do usuário")
                              String nome,
                              @Schema(example = "15153769788", description = "CPF do usuário")
                              String cpf,
                              @Schema(example = "felipesmacario@gmail.com", description = "Email do usuário")
                              String email) {
}
