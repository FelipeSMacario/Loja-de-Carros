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

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;
import static com.javacar.lojadecarro.enums.StatusVeiculo.*;


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
        imagem.setPrincipal(imagens.isEmpty());
        imagem.setVeiculo(this);
        imagens.add(imagem);
    }
    public void removerImagem(Imagem imagem) {
        if (imagem == null) {
            throw new BusinessException(
                    "Imagem não pode ser nula"
            );
        }

        var eraPrincipal = imagem.isPrincipal();
        var removida = imagens.remove(imagem);

        if (!removida) {
            throw new BusinessException(
                    "O veículo não possui essa imagem"
            );
        }

        if (eraPrincipal && !imagens.isEmpty()) {
            imagens.getFirst().setPrincipal(true);
        }
    }


    public void removerOpcional(Long idOpcional) {
        boolean removido = opcionais.removeIf(
                vo -> vo.getOpcional().getId().equals(idOpcional)
        );

        if (!removido) {
            throw new BusinessException("O Veiculo informado não possui esse opcional");
        }
    }

    private boolean possuiOpcional(Long idOpcional) {
        return this.getOpcionais().stream().anyMatch(op -> op.getOpcional().getId().equals(idOpcional));
    }

    public void adicionarOpcional(Opcional opcional) {

        if (possuiOpcional(opcional.getId())) {
            throw new BusinessException(OPCIONAL.jaAtiva());
        }

        opcionais.add(new VeiculoOpcional(this, opcional));
    }

    public void reativarAnuncio(){
        if (this.statusVeiculo != PAUSADO){
            throw new BusinessException("Somente um veículo pausado pode ser reativado");
        }
        this.statusVeiculo = DISPONIVEL;
    }

    public void pausarAnuncio(){
        if (this.statusVeiculo != DISPONIVEL){
            throw new BusinessException("Somente um veículo disponível pode ser pausado");
        }
        this.statusVeiculo = PAUSADO;
    }

    public void disponibilizarAnuncio() {
        if (this.statusVeiculo != RESERVADO) {
            throw new BusinessException(
                    "Somente um veículo reservado pode ser disponibilizado."
            );
        }

        this.statusVeiculo = DISPONIVEL;
    }

    public void concluirVeiculo() {
        if (this.statusVeiculo != RESERVADO) {
            throw new BusinessException(
                    "Somente um veículo reservado pode ser vendido."
            );
        }

        this.statusVeiculo = VENDIDO;
    }

    public void reservarVeiculo() {
        if (this.statusVeiculo != DISPONIVEL){
            throw new BusinessException("Somente um veiculo disponível pode ser reservado");
        }
        this.statusVeiculo = RESERVADO;
    }

    public void validarPodeSerEditado () {
        if (this.statusVeiculo != DISPONIVEL && this.statusVeiculo != PAUSADO){
            throw new BusinessException("Somente anúncios disponíveis ou pausados podem ser editados.");
        }
    }
}
