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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Data")
    private LocalDateTime data;

    @OneToOne
    private Usuario usuario;

    @OneToOne
    private Carro carro;
}
