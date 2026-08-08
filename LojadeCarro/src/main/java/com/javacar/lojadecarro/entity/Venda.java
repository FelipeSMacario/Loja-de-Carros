package com.javacar.lojadecarro.entity;

import com.javacar.lojadecarro.enums.StatusVenda;
import com.javacar.lojadecarro.exception.business.BusinessException;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.javacar.lojadecarro.enums.StatusVenda.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVenda statusVenda;

    public void cancelarVenda() {
        if (this.statusVenda != EM_ANDAMENTO) {
            throw new BusinessException(
                    "Somente uma venda em andamento pode ser cancelada."
            );
        }

        this.statusVenda = CANCELADA;
    }
    public void concluirVenda() {
        if (this.statusVenda != EM_ANDAMENTO) {
            throw new BusinessException(
                    "Somente uma venda em andamento pode ser concluída."
            );
        }

        this.statusVenda = CONCLUIDA;
    }
}
