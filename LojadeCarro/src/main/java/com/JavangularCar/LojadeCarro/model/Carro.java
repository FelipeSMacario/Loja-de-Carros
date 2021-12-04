package com.JavangularCar.LojadeCarro.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "Carro")
@Entity
public class Carro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Quilometragem", nullable = false)
    private Double quilometragem;

    @Column(name = "Url", nullable = false)
    private String url;

    @Column(name = "Valor", nullable = false)
    private Double valor;

    @Column(name = "Placa", nullable = false)
    private String placa;

    @Column(name = "Motor", nullable = false)
    private String motor;

    @Column(name = "AnoFabricacao", nullable = false)
    private int anoFabricacao;

    @Column(name = "DataCadastro", nullable = false)
    private LocalDateTime dtCadastro;

    @Column(name = "Ativo", nullable = false)
    private boolean ativo;

    @ManyToOne
    @JoinColumn(name = "carroceria_id", nullable = false)
    private Carroceria carroceria;

    @ManyToOne
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @ManyToOne
    @JoinColumn(name = "cores_id", nullable = false)
    private Cores cores;

    @ManyToOne
    @JoinColumn(name = "modelo_id", nullable = false)
    private Modelo modelo;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "combustivel_id", nullable = false)
    private Combustivel combustivel;

}
