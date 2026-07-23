package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.enums.Entidade;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseHelper {
    public static void assertNotFoundResponseError(NotFoundException exception,
                                                   Entidade entidade,
                                                   Long idInvalido) {
        assertThat(exception)
                .hasMessage(entidade.naoEncontrada() + idInvalido);
    }

    public static void assertBusinessResponseError(BusinessException exception, Entidade entidade) {
        assertThat(exception)
                .hasMessage(entidade.jaAtiva());
    }

    public static void assertBusinessResponseError(BusinessException exception, String mensagem) {
        assertThat(exception)
                .hasMessage(mensagem);
    }
}
