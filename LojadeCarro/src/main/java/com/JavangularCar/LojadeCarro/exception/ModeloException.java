package com.JavangularCar.LojadeCarro.exception;

public class ModeloException extends RuntimeException {
    public ModeloException(Long id) {
        super("Modelo não encontrado(a) com o id: " + id);
    }
}
