package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request da alteração da senha")
public record AlteracaoSenhaRequest(
        @Schema(example = "123", description = "Senha anterior do usuário")
        @NotBlank String senhaAntiga,
        @Schema(example = "1234", description = "Nova senha do usuário")
        @NotBlank String senhaNova
) {

}
