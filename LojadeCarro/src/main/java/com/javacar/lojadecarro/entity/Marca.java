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
public class Marca extends EntidadeBase {
    @Column(nullable = false, unique = true, length = 20)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String url;
}
