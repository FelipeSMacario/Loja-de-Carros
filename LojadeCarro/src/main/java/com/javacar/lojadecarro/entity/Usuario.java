package com.javacar.lojadecarro.entity;

import com.javacar.lojadecarro.exception.business.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import static com.javacar.lojadecarro.enums.Entidade.USUARIO;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Usuario extends EntidadeBase implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(
            name = "identity_provider_id",
            unique = true,
            length = 255
    )
    private String identityProviderId;

    public void ativar() {
        if (this.ativo) {
            throw new BusinessException(USUARIO.jaAtiva());
        }
        this.ativo = true;
    }

    public void desativar() {
        if (!this.ativo) {
            throw new BusinessException(USUARIO.jaInativa());
        }
        this.ativo = false;
    }
}
