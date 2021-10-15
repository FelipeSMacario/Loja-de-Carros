package com.JavangularCar.LojadeCarro.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "Modelo")
@Entity
public class Modelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Nome")
    private String nome;
}
