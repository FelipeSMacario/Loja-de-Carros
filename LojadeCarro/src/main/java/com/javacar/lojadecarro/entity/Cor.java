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
public class Cor extends EntidadeBase {
    @Column(nullable = false, unique = true, length = 30)
    private String nome;
}
