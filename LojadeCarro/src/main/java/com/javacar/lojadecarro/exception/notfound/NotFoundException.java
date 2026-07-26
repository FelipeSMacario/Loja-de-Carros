package com.javacar.lojadecarro.exception.notfound;

import com.javacar.lojadecarro.enums.Entidade;

public class NotFoundException extends RuntimeException{
    public NotFoundException(Entidade operacao, Long id) {
        super(operacao.naoEncontrada() + id);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
