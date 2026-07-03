package com.JavangularCar.LojadeCarro.exception;

public class ImagensException extends RuntimeException {
    public ImagensException(Long id) {
        super("Imagem não encontrado(a) com o id: " + id);
    }
}
