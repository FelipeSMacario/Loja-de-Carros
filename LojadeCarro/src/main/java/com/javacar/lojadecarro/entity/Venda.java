package com.javacar.lojadecarro.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Venda")
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "data_venda",
            nullable = false,
            updatable = false,
            insertable = false
    )
    private LocalDateTime dataVenda;

    @Column(name = "valor_venda", nullable = false)
    private BigDecimal valorVenda;

    @OneToOne(fetch = FetchType.LAZY)
    private Usuario vendedor;

    @OneToOne(fetch = FetchType.LAZY)
    private Usuario comprador;

    @OneToOne(fetch = FetchType.LAZY)
    private Veiculo veiculo;
}
