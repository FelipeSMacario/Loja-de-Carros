package com.javacar.lojadecarro.entity;

import com.javacar.lojadecarro.exception.business.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.javacar.lojadecarro.enums.Entidade.COR;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Cor extends EntidadeBase {
    @Column(nullable = false, unique = true, length = 30)
    private String nome;

    public void alteraStatus(boolean novoStatus) {
        if (this.ativo == novoStatus) {
            throw new BusinessException(novoStatus ? COR.jaAtiva() : COR.jaInativa());
        }
        this.ativo = novoStatus;
    }
}
