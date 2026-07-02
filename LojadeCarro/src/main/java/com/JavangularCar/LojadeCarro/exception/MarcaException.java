package com.JavangularCar.LojadeCarro.exception;

public class MarcaException extends BusinessException {
    public MarcaException(Long id) {
        super("Marca não encontrada com o id: " + id);
    }
}
