package com.javacar.lojadecarro.exception;

public class ImagensException extends BusinessException {
    public ImagensException(Long id) {
        super("Imagem não encontrado(a) com o id: " + id);
    }
}
