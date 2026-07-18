package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para autenticação do usuário")
public record LoginRequest(@Schema(example = "cristianoRonaldo@siiiiim.com", description = "Login de acesso")
                           @NotBlank @Email String login,
                           @Schema(example = "siiiiiiimmmmmm", description = "Senha de acesso")
                           @NotBlank String senha) {
}
