package com.JavangularCar.LojadeCarro.exception;

public class KitException extends BusinessException {
    public KitException(Long id) {
        super("Kit não encontrado com o id: " + id);
    }
}
