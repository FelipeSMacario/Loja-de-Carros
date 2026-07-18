package com.javacar.lojadecarro.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            updatable = false,
            insertable = false
    )
    private LocalDateTime dataVenda;

    @Column(nullable = false)
    private BigDecimal valorVenda;

    @OneToOne(fetch = FetchType.LAZY)
    private Usuario vendedor;

    @OneToOne(fetch = FetchType.LAZY)
    private Usuario comprador;

    @OneToOne(fetch = FetchType.LAZY)
    private Veiculo veiculo;
}
