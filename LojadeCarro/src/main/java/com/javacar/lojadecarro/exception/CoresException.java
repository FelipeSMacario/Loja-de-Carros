package com.javacar.lojadecarro.exception;

public class CoresException extends BusinessException {
    public CoresException(Long id) {
        super("Cor não encontrado(a) com o id: " + id);
    }
}
