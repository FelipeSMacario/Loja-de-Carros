package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Veiculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VeiculoMapper {
    Veiculo toEntity(VeiculoRequest carroDTO);

    @Mapping(source = "veiculo.modelo.marca.nome", target = "marca")
    @Mapping(source = "veiculo.modelo.nome", target = "modelo")
    @Mapping(source = "veiculo.carroceria.nome", target = "carroceria")
    @Mapping(source = "veiculo.cor.nome", target = "cor")
    @Mapping(source = "veiculo.combustivel.nome", target = "combustivel")
    @Mapping(
            source = "imagemPrincipalId",
            target = "imagemPrincipalId"
    )
    VeiculoResponse toResponse(
            Veiculo veiculo,
            Long imagemPrincipalId
    );

    void toUpdate(VeiculoRequest request, @MappingTarget Veiculo veiculo);
}
