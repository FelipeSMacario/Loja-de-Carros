package com.JavangularCar.LojadeCarro.exception;

public class ImagensException extends BusinessException {
    public ImagensException(Long id) {
        super("Imagem não encontrado(a) com o id: " + id);
    }
}
