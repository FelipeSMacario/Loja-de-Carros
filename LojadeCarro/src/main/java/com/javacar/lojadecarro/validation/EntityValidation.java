package com.javacar.lojadecarro.validation;

import com.javacar.lojadecarro.enums.Entidade;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EntityValidation {
    public <T> T obterOuLancarErro(
            Optional<T> entidade,
            Entidade tipo,
            Long id
    ) {
        return entidade.orElseThrow(
                () -> new NotFoundException(tipo, id)
        );
    }
}
