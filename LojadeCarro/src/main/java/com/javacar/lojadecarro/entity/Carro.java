package com.javacar.lojadecarro.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Table(name = "Carro")
@Entity
public class Carro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Quilometragem", nullable = false)
    private Double quilometragem;

    @Column(name = "Url", nullable = true)
    private String url;

    @Column(name = "Valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "Placa", nullable = false)
    private String placa;

    @Column(name = "Motor", nullable = false)
    private String motor;

    @Column(name = "AnoFabricacao", nullable = false)
    private Integer anoFabricacao;

    @Column(name = "DataCadastro", nullable = false)
    private LocalDateTime dtCadastro;

    @Column(name = "Ativo", nullable = false)
    private boolean ativo = true;

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
