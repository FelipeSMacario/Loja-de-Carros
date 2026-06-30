package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.CarroRequest;
import com.JavangularCar.LojadeCarro.dto.response.CarroResponse;
import com.JavangularCar.LojadeCarro.entity.Carro;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarroMapper {
    Carro toEntity(CarroRequest carroDTO);

    CarroResponse toRecord(Carro carro);
}
