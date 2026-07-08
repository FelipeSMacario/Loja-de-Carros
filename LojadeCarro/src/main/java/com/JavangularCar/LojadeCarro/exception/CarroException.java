package com.JavangularCar.LojadeCarro.exception;

public class CarroException extends BusinessException {
    public CarroException(Long id) {
        super("Carro não encontrado(a) com o id: " + id);
    }
}
