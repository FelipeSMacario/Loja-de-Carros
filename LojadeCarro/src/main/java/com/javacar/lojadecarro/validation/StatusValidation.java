package com.javacar.lojadecarro.validation;

import com.javacar.lojadecarro.entity.EntidadeBase;
import com.javacar.lojadecarro.enums.Entidade;
import com.javacar.lojadecarro.exception.business.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class StatusValidation {

    public void defineValidacao(boolean ativo, Entidade entidade, EntidadeBase entidadeBase){
        if (ativo){
            this.validarAtivacao(entidade, entidadeBase);
        }else {
            this.validarInativacao(entidade, entidadeBase);
        }
    }

    public void validarAtivacao(Entidade entidade, EntidadeBase entidadeBase) {
        if (entidadeBase.isAtivo()) {
            throw new BusinessException(entidade.jaAtiva());
        }
    }

    public void validarInativacao(Entidade entidade, EntidadeBase entidadeBasee) {
        if (entidadeBasee.isAtivo()) {
            throw new BusinessException(entidade.jaInativa());
        }
    }
}
