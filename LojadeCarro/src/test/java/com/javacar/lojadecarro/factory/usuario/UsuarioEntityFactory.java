package com.javacar.lojadecarro.factory.usuario;

import com.javacar.lojadecarro.entity.Usuario;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class UsuarioEntityFactory {

    private final Usuario usuario;

    private UsuarioEntityFactory() {
        this.usuario = new Usuario();
    }

    public static UsuarioEntityFactory criarEntity() {
        return new UsuarioEntityFactory();
    }

    public UsuarioEntityFactory comTodosOsCampos() {
        usuario.setId(1L);
        usuario.setNome("Felipe Soares Macário");
        usuario.setPassword("123456");
        usuario.setCpf("1234567890");
        usuario.setDtNascimento(LocalDate.of(1991, 5, 14));
        usuario.setEmail("felipesmacario@gmail.com");
        return this;
    }
    public UsuarioEntityFactory comId(Long id) {
        usuario.setId(id);
        return this;
    }

    public UsuarioEntityFactory comNome(String nome) {
        usuario.setNome(nome);
        return this;
    }
    public UsuarioEntityFactory comPassword(String password) {
        usuario.setPassword(password);
        return this;
    }
    public UsuarioEntityFactory comCPF(String cpf) {
        usuario.setCpf(cpf);
        return this;
    }
    public UsuarioEntityFactory comDataNascimento(LocalDate dtNascimento) {
        usuario.setDtNascimento(dtNascimento);
        return this;
    }
    public UsuarioEntityFactory comEmail(String email) {
        usuario.setEmail(email);
        return this;
    }

    public Usuario build() {
        return usuario;
    }
}
