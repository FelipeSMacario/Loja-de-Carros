package com.JavangularCar.LojadeCarro.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "Carro")
@Entity
public class Carro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Quilometragem")
    private double quilometragem;

    @Column(name = "Placa")
    private String placa;

    @Column(name = "AnoFabricacao")
    private int anoFabricacao;

    @ManyToOne
    private Modelo modelo;

    @ManyToOne
    private Marca marca;

    @ManyToOne
    private Cores cores;

    @ManyToMany
    private List<Combustivel> combustivel = new ArrayList<>();

}
