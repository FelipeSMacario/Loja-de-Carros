package com.javacar.lojadecarro.dto.response;

import com.javacar.lojadecarro.enums.StatusVeiculo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Detalhes de um veículo anunciado")
public record VeiculoDetalheResponse(
        Long id,
        String marca,
        String modelo,
        String carroceria,
        String cor,
        String combustivel,
        String motor,
        BigDecimal valor,
        Integer quilometragem,
        Short anoFabricacao,
        StatusVeiculo statusVeiculo,
        String descricao,
        LocalDateTime dataCadastro,
        VendedorResumoResponse vendedor,
        List<VeiculoImagemResponse> imagens,
        List<OpcionalResponse> opcionais
) {
}
