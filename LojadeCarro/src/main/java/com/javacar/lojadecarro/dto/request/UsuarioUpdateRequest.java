package com.javacar.lojadecarro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request para atualizacao do usuário")
public record UsuarioUpdateRequest(@Schema(example = "Felipe", description = "Nome do usuário")
                                   @NotBlank String nome,
                                   @Schema(description = "Data de nascimento",
                                           example = "1998-05-20")
                                   @NotNull LocalDate dataNascimento,
                                   @Schema(example = "felipesmacario@gmail.com", description = "Email do usuário")
                                   @NotBlank @Email String email) {
}
