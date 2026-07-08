package com.javacar.lojadecarro.exception;

public class KitException extends BusinessException {
    public KitException(Long id) {
        super("Kit não encontrado(a) com o id: " + id);
    }
}
