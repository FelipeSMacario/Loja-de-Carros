package com.JavangularCar.LojadeCarro.exception;

public class UsuarioException extends RuntimeException {
    public UsuarioException(Long id) {
        super("Usuario não encontrado com o id: " + id);
    }
}
