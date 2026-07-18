package com.javacar.lojadecarro.entity;

import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Table(name = "veiculo")
@Entity
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Short anoFabricacao;

    @Column(nullable = false)
    private String motor;

    @Column(nullable = false, length = 7, unique = true)
    private String placa;

    @Column(nullable = false)
    private Integer quilometragem;

    @Column(nullable = false)
    private BigDecimal valor;

    private String descricao;

    @Column(

            nullable = false,
            updatable = false,
            insertable = false
    )
    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVeiculo statusVeiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carroceria_id", nullable = false)
    private Carroceria carroceria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cor_id", nullable = false)
    private Cor cor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo_id", nullable = false)
    private Modelo modelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Usuario vendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combustivel_id", nullable = false)
    private Combustivel combustivel;

    @OneToMany(
            mappedBy = "veiculo",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Imagem> imagens = new ArrayList<>();

    @OneToMany(
            mappedBy = "veiculo",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<VeiculoOpcional> opcionais = new ArrayList<>();


    public void adicionarImagem(Imagem imagem) {
        if (imagem == null) {
            throw new BusinessException("Imagem não pode ser nula");
        }
        if (imagens.isEmpty()) {
            imagem.setPrincipal(true);
        }

        imagem.setVeiculo(this);
        imagens.add(imagem);
    }

    public void adicionarOpcional(Opcional opcional) {
        opcionais.add(new VeiculoOpcional(this, opcional));
    }

    public void removerOpcional(Long idOpcional) {
        boolean removido = opcionais.removeIf(
                vo -> vo.getOpcional().getId().equals(idOpcional)
        );

        if (!removido) {
            throw new BusinessException("O Veiculo informado não possui esse opcional");
        }
    }


    public void alterarStatus(StatusVeiculo novoStatus) {

        if (novoStatus == null) {
            throw new BusinessException("Status inválido");
        }
        if (this.statusVeiculo == StatusVeiculo.VENDIDO) {
            throw new BusinessException(
                    "Um veículo vendido não pode ter seu status alterado."
            );
        }


        this.statusVeiculo = novoStatus;
    }
}
