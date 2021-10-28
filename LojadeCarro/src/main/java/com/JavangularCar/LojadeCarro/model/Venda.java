package com.JavangularCar.LojadeCarro.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Entity
@Table(name = "Venda")
public class Venda {
    @Id
    private Long id;

    @Column(name = "Data")
    private LocalDateTime data;

    private String url;

    @Column(name = "ValorVenda")
    private Double valorVenda;

    @ManyToOne
    private Comprador comprador;

    @ManyToOne
    private Vendedor vendedor;

    @OneToOne
    private Carro carro;
}
