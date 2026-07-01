package com.JavangularCar.LojadeCarro.exception;

public class CarroException extends RuntimeException {
    public CarroException() {
    }
    public CarroException(Long id) {
        super("Nenhum carro identificado com o id " + id);
    }
}
