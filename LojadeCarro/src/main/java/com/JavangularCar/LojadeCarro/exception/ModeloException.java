package com.JavangularCar.LojadeCarro.exception;

public class ModeloException extends RuntimeException {
    public ModeloException(Long id) {
        super("Modelo não encontrado com o id: " + id);
    }
}
