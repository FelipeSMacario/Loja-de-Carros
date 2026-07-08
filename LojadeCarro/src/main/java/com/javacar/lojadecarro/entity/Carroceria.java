package com.javacar.lojadecarro.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "Caracteristica")
@Entity
public class Carroceria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Nome")
    private String nome;
}
