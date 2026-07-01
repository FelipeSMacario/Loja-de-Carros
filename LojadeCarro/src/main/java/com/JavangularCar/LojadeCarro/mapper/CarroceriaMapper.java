package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.CarroceriaRequest;
import com.JavangularCar.LojadeCarro.dto.response.CarroceriaResponse;
import com.JavangularCar.LojadeCarro.entity.Carroceria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarroceriaMapper {
    Carroceria toEntity(CarroceriaRequest record);

    CarroceriaResponse toRecord(Carroceria carroceria);
}
