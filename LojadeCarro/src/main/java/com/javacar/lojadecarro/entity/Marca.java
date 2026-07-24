package com.javacar.lojadecarro.entity;

import com.javacar.lojadecarro.exception.business.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.javacar.lojadecarro.enums.Entidade.MARCA;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Marca extends EntidadeBase {
    @Column(nullable = false, unique = true, length = 20)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String url;

    public void alterarStatus(boolean novoStatus) {
        if (this.ativo == novoStatus) {
            throw new BusinessException(novoStatus ? MARCA.jaAtiva() : MARCA.jaInativa());
        }
        this.ativo = novoStatus;
    }
}
