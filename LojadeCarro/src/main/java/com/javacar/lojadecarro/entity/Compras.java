package com.javacar.lojadecarro.entity;

import lombok.Data;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Table(name = "Compras")
@Entity
public class Compras {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Carro carro;

    @ManyToOne
    private Usuario vendedor;

    @ManyToOne
    private Usuario comprador;

    private BigDecimal valor;

    private Instant dataVenda;
}
