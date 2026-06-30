package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.MarcaRequest;
import com.JavangularCar.LojadeCarro.dto.response.MarcaResponse;
import com.JavangularCar.LojadeCarro.entity.Marca;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarcaMapper {
    Marca toEntity(MarcaRequest record);

    MarcaResponse toRecord(Marca marca);
}
