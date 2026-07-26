package com.javacar.lojadecarro.entity;

import com.javacar.lojadecarro.exception.business.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Opcional extends EntidadeBase {
    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    public void alteraStatus(boolean novoStatus) {
        if (this.ativo == novoStatus) {
            throw new BusinessException(novoStatus ? OPCIONAL.jaAtiva() : OPCIONAL.jaInativa());
        }
        this.ativo = novoStatus;
    }
}
