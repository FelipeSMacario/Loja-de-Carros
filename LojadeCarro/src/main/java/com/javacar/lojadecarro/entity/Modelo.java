package com.javacar.lojadecarro.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Modelo extends EntidadeBase {
    @Column(nullable = false, unique = true, length = 20)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    private Marca marca;
}
