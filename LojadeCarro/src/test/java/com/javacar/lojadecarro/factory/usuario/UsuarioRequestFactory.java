package com.javacar.lojadecarro.factory.usuario;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;

import java.time.LocalDate;
import java.time.Month;

public class UsuarioRequestFactory {
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;

    public static UsuarioRequestFactory usuarioRequestFactory() {
        return new UsuarioRequestFactory();
    }

    public static UsuarioRequestFactory criarRequest() {
        return new UsuarioRequestFactory();
    }

    public UsuarioRequestFactory comTodosOsCampos() {
        this.nome = "Felipe Soares Macário";
        this.cpf = "15152736799";
        this.dataNascimento = LocalDate.of(1991, Month.MAY, 14);
        return this;
    }

    public UsuarioRequestFactory comNome(String nome) {
        this.nome = nome;
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

    public UsuarioRequest build() {
        return new UsuarioRequest(nome, cpf, dataNascimento);
    }
}
