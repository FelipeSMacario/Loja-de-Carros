package com.javacar.lojadecarro.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
public class UsuarioRoleId implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idUsuario;
    private Long idRole;
}
