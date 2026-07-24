package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resposta para o vinculo das roles")
public record UsuarioRolesResponse
        (@Schema(example = "1", description = "Id do usuário")
         Long id,
         @Schema(example = "Felipe", description = "Nome do usuário")
         String nome,
         @Schema(example = "15135736744", description = "Cpf do usuário")
         String cpf,
         @Schema(example = "{id: 1, nome: ADMIN, ativo: true}", description = "Roles que o usuario possui")
         List<RoleResponse> roles) {
}
