package com.javacar.lojadecarro.entity;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "Modelo")
public class Modelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Nome")
    private String nome;

    @ManyToOne
    private Marca marca;
}
