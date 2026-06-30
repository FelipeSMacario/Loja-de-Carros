package com.JavangularCar.LojadeCarro.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Data
@Table(name = "Imagens")
@Entity
@Getter
@Setter
public class Imagens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne
    private com.JavangularCar.LojadeCarro.entity.Carro carro;

}
