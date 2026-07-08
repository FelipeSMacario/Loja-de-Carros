package com.javacar.lojadecarro.exception;

public class CarroceriaException extends BusinessException {
    public  CarroceriaException(Long id) {
        super("Carroceria não encontrado(a) com o id: " + id);
    }
}
