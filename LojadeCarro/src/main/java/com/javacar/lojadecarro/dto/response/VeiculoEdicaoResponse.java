package com.javacar.lojadecarro.dto.response;

import com.javacar.lojadecarro.enums.StatusVeiculo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Dados de um veículo para edição")
public record VeiculoEdicaoResponse(
        Long id,
        String placa,
        Integer quilometragem,
        BigDecimal valor,
        String motor,
        String descricao,
        Short anoFabricacao,
        Long idCarroceria,
        Long idCor,
        Long idModelo,
        Long idCombustivel,
        List<Long> idsOpcionais,
        StatusVeiculo statusVeiculo,
        List<VeiculoImagemResponse> imagens
) {
}
