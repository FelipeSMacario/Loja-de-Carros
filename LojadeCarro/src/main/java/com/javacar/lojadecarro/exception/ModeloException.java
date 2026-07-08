package com.javacar.lojadecarro.exception;

public class ModeloException extends BusinessException {
    public ModeloException(Long id) {
        super("Modelo não encontrado(a) com o id: " + id);
    }
}
