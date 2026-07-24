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

    @Mapping(source = "modelo.marca.nome", target = "marca")
    @Mapping(source = "modelo.nome", target = "modelo")
    VeiculoResponse toResponse(Veiculo veiculo);

    void toUpdate(VeiculoRequest request, @MappingTarget Veiculo veiculo);
}
