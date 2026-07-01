package com.JavangularCar.LojadeCarro.exception;

public class ImagensException extends RuntimeException {
    public ImagensException(Long id) {
        super("Não foi possível identificar a imagem com esse id: " + id);
    }
}
