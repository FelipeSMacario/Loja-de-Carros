package com.JavangularCar.LojadeCarro.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
@Table(name = "Kit")
@Entity
@Getter
@Setter
public class Kit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean freioABS;

    private boolean rodaLigaLeve;

    private boolean automatico;

    private boolean direcaoHidraulica;

    private boolean arCondicionado;

    @ManyToOne
    private Carro carro;


}
