package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.ModeloRequest;
import com.JavangularCar.LojadeCarro.dto.response.ModeloResponse;
import com.JavangularCar.LojadeCarro.entity.Modelo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ModeloMapper {
    Modelo toEntity(ModeloRequest record);
    ModeloResponse toRecord(Modelo modelo);
}
