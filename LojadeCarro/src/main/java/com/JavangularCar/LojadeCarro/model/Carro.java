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
    private Double quilometragem;

    @Column(name = "Url")
    private String url;

    @Column(name = "Valor")
    private Double valor;

    @Column(name = "Placa")
    private String placa;

    @Column(name = "Motor")
    private String motor;

    @Column(name = "AnoFabricacao")
    private int anoFabricacao;

    @ManyToOne
    private Carroceria carroceria;

    @ManyToOne
    @JoinColumn(name = "marca_id")
    private Marca marca;

    @ManyToOne
    private Cores cores;

    @ManyToOne
    private Modelo modelo;

    @ManyToOne
    private Usuario usuario;

    @ManyToMany
    private List<Combustivel> combustivel = new ArrayList<>();

}
