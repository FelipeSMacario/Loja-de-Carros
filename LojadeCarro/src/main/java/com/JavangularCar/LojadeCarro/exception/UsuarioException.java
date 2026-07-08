package com.JavangularCar.LojadeCarro.exception;

public class UsuarioException extends BusinessException {
    public UsuarioException(Long id) {
        super("Usuário não encontrado(a) com o id: " + id);
    }
}
