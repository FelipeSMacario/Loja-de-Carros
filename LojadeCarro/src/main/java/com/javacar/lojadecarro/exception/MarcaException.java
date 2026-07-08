package com.javacar.lojadecarro.exception;

public class MarcaException extends BusinessException {
    public MarcaException(Long id) {
        super("Marca não encontrado(a) com o id: " + id);
    }
}
