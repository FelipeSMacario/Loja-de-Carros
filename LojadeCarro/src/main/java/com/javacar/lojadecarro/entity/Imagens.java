package com.javacar.lojadecarro.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Imagens")
@Data
public class Imagens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private String nomeArquivo;

    private String contentType;

    private Long tamanho;

    private Boolean principal;

    @ManyToOne(fetch = FetchType.LAZY)
    private Carro carro;
}
