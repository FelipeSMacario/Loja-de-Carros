package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.KitRequest;
import com.javacar.lojadecarro.dto.response.KitResponse;
import com.javacar.lojadecarro.entity.Kit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface KitMapper {
    Kit toEntity(KitRequest request);
    @Mapping(source = "carro.id", target = "idCarro")
    @Mapping(source = "carro.marca.nome", target = "marca")
    @Mapping(source = "carro.modelo.nome", target = "modelo")
    KitResponse toResponse(Kit kit);

    void toUpdate(KitRequest request, @MappingTarget Kit kit);
}
