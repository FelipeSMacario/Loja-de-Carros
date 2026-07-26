package com.javacar.lojadecarro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity()
public class VeiculoOpcional {
    @EmbeddedId
    private VeiculoOpcionalId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idVeiculo")
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idOpcional")
    @JoinColumn(name = "opcional_id")
    private Opcional opcional;

    public VeiculoOpcional(Veiculo veiculo, Opcional opcional) {
        this.id = new VeiculoOpcionalId(veiculo.getId(), opcional.getId());
        this.veiculo = veiculo;
        this.opcional = opcional;
    }
}
