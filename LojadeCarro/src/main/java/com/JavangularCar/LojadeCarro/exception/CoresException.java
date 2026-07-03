package com.JavangularCar.LojadeCarro.exception;

public class CoresException extends RuntimeException {
    public CoresException(Long id) {
        super("Cor não encontrado(a) com o id: " + id);
    }
}
