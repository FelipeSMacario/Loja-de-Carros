package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.KitRequest;
import com.JavangularCar.LojadeCarro.dto.response.KitResponse;
import com.JavangularCar.LojadeCarro.entity.Kit;
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

    void toUpdate(KitRequest request, @MappingTarget Kit record);
}
