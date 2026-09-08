package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Schema(description = "Request para criação do perfil do usuário")
public record UsuarioRequest(
        @Schema(example = "Felipe", description = "Nome de exibição do usuário")
        @NotBlank
        String nome,
        @Schema(example = "15152736999", description = "CPF do usuário")
        @NotBlank
        @CPF
        String cpf,
        @Schema(description = "Data de nascimento", example = "1998-05-20")
        @NotNull
        LocalDate dataNascimento
) {
}
