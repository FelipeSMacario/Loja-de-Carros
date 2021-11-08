package com.JavangularCar.LojadeCarro.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
    private Carro carro;

}
