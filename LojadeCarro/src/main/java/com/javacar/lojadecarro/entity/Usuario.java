package com.javacar.lojadecarro.entity;

import com.javacar.lojadecarro.exception.business.BusinessException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.javacar.lojadecarro.enums.Entidade.ROLE;
import static com.javacar.lojadecarro.enums.Entidade.USUARIO;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Usuario extends EntidadeBase implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;


    @SuppressWarnings("java:S1948")
    @OneToMany(
            mappedBy = "usuario",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<UsuarioRole> roles = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }

    public void alteraStatus(boolean novoStatus) {
        if (this.ativo == novoStatus) {
            throw new BusinessException(novoStatus ? USUARIO.jaAtiva() : USUARIO.jaInativa());
        }
        this.ativo = novoStatus;
    }

    public void adicionarRole(Role role) {
        if (possuiRole(role.getId())) {
            throw new BusinessException(ROLE.jaAtiva());
        }

        roles.add(new UsuarioRole(this, role));
    }

    private boolean possuiRole(Long roleId) {
        return roles.stream()
                .anyMatch(usuarioRole -> usuarioRole.getRole().getId().equals(roleId));
    }
    public void removerRole(Long roleId) {
        boolean removido = roles.removeIf(
                usuarioRole -> usuarioRole.getRole().getId().equals(roleId)
        );

        if (!removido) {
            throw new BusinessException("O usuário não possui uma role com o id informado.");
        }
    }

}
