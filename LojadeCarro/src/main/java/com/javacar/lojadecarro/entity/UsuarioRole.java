package com.javacar.lojadecarro.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@Entity
public class UsuarioRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    @EqualsAndHashCode.Include
    private UsuarioRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @SuppressWarnings("java:S1948")
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idRole")
    @JoinColumn(name = "role_id")
    private Role role;

    public UsuarioRole(Usuario usuario, Role role) {
        this.id = new UsuarioRoleId(usuario.getId(), role.getId());
        this.usuario = usuario;
        this.role = role;
    }
}
