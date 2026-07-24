package com.javacar.lojadecarro.factory.role;

import com.javacar.lojadecarro.entity.Role;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class RoleEntityFactory {

    private final Role role;


    private RoleEntityFactory() {
        this.role = new Role();
    }

    public static RoleEntityFactory criarEntity() {
        return new RoleEntityFactory();
    }

    public RoleEntityFactory comTodosOsCampos() {
        role.setId(1L);
        role.setNome("ADMIN");
        role.setAtivo(true);
        return this;
    }

    public RoleEntityFactory comNome(String nome) {
        role.setNome(nome);
        return this;
    }

    public RoleEntityFactory comId(Long id) {
        role.setId(id);
        return this;
    }


    public RoleEntityFactory comAtivo(boolean ativo) {
        role.setAtivo(ativo);
        return this;
    }

    public Role build() {
        return role;
    }
}
