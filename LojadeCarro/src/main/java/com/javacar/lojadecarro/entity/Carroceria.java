package com.javacar.lojadecarro.entity;

import com.javacar.lojadecarro.exception.business.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.javacar.lojadecarro.enums.Entidade.CARROCERIA;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Carroceria extends EntidadeBase {
    @Column(nullable = false, unique = true, length = 30)
    private String nome;

    public void alterarStatus(boolean novoStatus) {
        if (this.ativo == novoStatus) {
            throw new BusinessException(novoStatus ? CARROCERIA.jaAtiva() : CARROCERIA.jaInativa());
        }
        this.ativo = novoStatus;
    }
}
