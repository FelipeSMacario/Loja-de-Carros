package com.javacar.lojadecarro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class EntidadeBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(
            nullable = false,
            updatable = false,
            insertable = false
    )
    protected LocalDateTime dataCadastro;

    @Column(nullable = false)
    protected boolean ativo = true;
}
