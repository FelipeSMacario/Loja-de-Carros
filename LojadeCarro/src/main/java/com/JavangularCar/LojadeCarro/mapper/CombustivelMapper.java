package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.CombustivelRequest;
import com.JavangularCar.LojadeCarro.dto.response.CombustivelResponse;
import com.JavangularCar.LojadeCarro.entity.Combustivel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CombustivelMapper {
    Combustivel toEntity(CombustivelRequest record);
    CombustivelResponse toResponse(Combustivel record);
}
