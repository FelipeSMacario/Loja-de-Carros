package com.JavangularCar.LojadeCarro.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Table(name = "Compras")
@Entity
@Getter
@Setter
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

    private Double valor;

    private LocalDateTime dataVenda;
}
