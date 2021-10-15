package com.JavangularCar.LojadeCarro.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "Combustivel")
@Entity
public class Combustivel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Nome")
    private String nome;
}
