package com.javacar.lojadecarro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Opcional extends EntidadeBase {
    @Column(nullable = false, unique = true, length = 50)
    private String nome;
}
