package com.javacar.lojadecarro.entity;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Table(name = "Kit")
@Entity
public class Kit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean freioABS;

    private boolean rodaLigaLeve;

    private boolean automatico;

    private boolean direcaoHidraulica;

    private boolean arCondicionado;

    private boolean quatroPortas;

    private boolean bancoCouro;

    @ManyToOne(optional = false)
    @JoinColumn(name = "carro_id", nullable = false)
    private Carro carro;
}
