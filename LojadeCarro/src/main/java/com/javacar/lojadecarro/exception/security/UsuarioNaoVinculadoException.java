package com.javacar.lojadecarro.exception.security;

public class UsuarioNaoVinculadoException
        extends RuntimeException {

    public UsuarioNaoVinculadoException() {
        super(
                "Usuário autenticado não possui cadastro local."
        );
    }
}
