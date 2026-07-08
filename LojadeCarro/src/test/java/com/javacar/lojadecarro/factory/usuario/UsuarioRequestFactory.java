package com.javacar.lojadecarro.factory.usuario;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;

import java.time.LocalDate;

public class UsuarioRequestFactory {
    private String nome;
    private String password;
    private String cpf;
    private LocalDate dtNascimento;
    private String email;

    public static UsuarioRequestFactory usuarioRequestFactory() {
        return new UsuarioRequestFactory();
    }

    public static UsuarioRequestFactory criarRequest() {
        return new UsuarioRequestFactory();
    }

    public UsuarioRequestFactory comTodosOsCampos() {
        this.nome = "Felipe Soares Macário";
        this.password = "123456";
        this.cpf = "15152736799";
        this.dtNascimento = LocalDate.of(1991, 5, 14);
        this.email = "felipesmacario@gmail.com";
        return this;
    }

    public UsuarioRequestFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }
    public UsuarioRequestFactory comSenha(String senha) {
        this.password = senha;
        return this;
    }

    public UsuarioRequestFactory comPassword(String password) {
        this.password = password;
        return this;
    }

    public UsuarioRequestFactory comCPF(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public UsuarioRequestFactory comDataNascimento(LocalDate dtNascimento) {
        this.dtNascimento = dtNascimento;
        return this;
    }

    public UsuarioRequestFactory comEmail(String email) {
        this.email = email;
        return this;
    }

    public UsuarioRequest build() {
        return new UsuarioRequest(nome, password, cpf, dtNascimento, email);
    }
}
