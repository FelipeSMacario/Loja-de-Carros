package com.JavangularCar.LojadeCarro.exception;

public class MarcaException extends RuntimeException {
    public MarcaException(Long id) {
        super("Marca não encontrada com o id: " + id);
    }
}
