package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.CarroRequest;
import com.javacar.lojadecarro.dto.response.CarroResponse;
import com.javacar.lojadecarro.entity.Carro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarroMapper {
    Carro toEntity(CarroRequest carroDTO);

    @Mapping(source = "marca.nome", target = "marca")
    @Mapping(source = "modelo.nome", target = "modelo")
    CarroResponse toResponse(Carro carro);

    void toUpdate(CarroRequest request, @MappingTarget Carro carro);
}
