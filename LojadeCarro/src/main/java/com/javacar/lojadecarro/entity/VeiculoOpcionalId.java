package com.javacar.lojadecarro.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoOpcionalId {
    private Long idVeiculo;
    private Long idOpcional;
}
