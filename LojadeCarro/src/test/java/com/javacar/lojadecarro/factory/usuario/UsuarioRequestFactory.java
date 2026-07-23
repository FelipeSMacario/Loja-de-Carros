package com.javacar.lojadecarro.factory.usuario;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;

import java.time.LocalDate;
import java.time.Month;

public class UsuarioRequestFactory {
    private String nome;
    private String password;
    private String cpf;
    private LocalDate dataNascimento;
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
        this.dataNascimento = LocalDate.of(1991, Month.MAY, 14);
        this.email = "felipesmacario@gmail.com";
        return this;
    }

    public UsuarioRequestFactory comNome(String nome) {
        this.nome = nome;
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

    public UsuarioRequestFactory comDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
        return this;
    }

    public UsuarioRequestFactory comEmail(String email) {
        this.email = email;
        return this;
    }

    public UsuarioRequest build() {
        return new UsuarioRequest(nome, password, cpf, dataNascimento, email);
    }
}
