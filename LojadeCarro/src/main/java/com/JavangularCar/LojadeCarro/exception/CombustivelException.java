package com.JavangularCar.LojadeCarro.exception;

public class CombustivelException extends RuntimeException {
    public CombustivelException(Long id) {
        super("Combustível não encontrado com o id: " + id);
    }
}
