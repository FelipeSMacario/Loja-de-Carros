package com.JavangularCar.LojadeCarro.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Modelo")
@Getter
@Setter
public class Modelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Nome")
    private String nome;

    @ManyToOne
    private Marca marca;
}
