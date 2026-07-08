package com.javacar.lojadecarro.exception;

public class CombustivelException extends BusinessException {
    public CombustivelException(Long id) {
        super("Combustível não encontrado(a) com o id: " + id);
    }
}
