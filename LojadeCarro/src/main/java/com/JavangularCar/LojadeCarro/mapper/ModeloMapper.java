package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.ModeloRequest;
import com.JavangularCar.LojadeCarro.dto.response.ModeloResponse;
import com.JavangularCar.LojadeCarro.entity.Modelo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MarcaMapper.class)
public interface ModeloMapper {
    Modelo toEntity(ModeloRequest record);

    @Mapping(source = "marca", target = "marcaResponse")
    ModeloResponse toResponse(Modelo modelo);
}
