package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.CarroRequest;
import com.JavangularCar.LojadeCarro.dto.response.CarroResponse;
import com.JavangularCar.LojadeCarro.entity.Carro;
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
