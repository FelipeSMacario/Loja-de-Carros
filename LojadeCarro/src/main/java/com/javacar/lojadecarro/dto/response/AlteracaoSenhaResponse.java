package com.javacar.lojadecarro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Responsa da alteração da senha")
public record AlteracaoSenhaResponse(
        @Schema(example = "felipe.vendedor@gmail.com", description = "Email do usuário")
        String email,
        @Schema(example = "Senha alterada com sucesso", description = "Mensagem de retorno da operação")
        String mensagem
) {
}
