package com.javacar.lojadecarro.entity;

import com.javacar.lojadecarro.exception.business.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.javacar.lojadecarro.enums.Entidade.MODELO;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Modelo extends EntidadeBase {
    @Column(nullable = false, unique = true, length = 20)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    private Marca marca;

    public void alteraStatus(boolean novoStatus) {
        if (this.ativo == novoStatus) {
            throw new BusinessException(novoStatus ? MODELO.jaAtiva() : MODELO.jaInativa());
        }
        this.ativo = novoStatus;
    }
}
