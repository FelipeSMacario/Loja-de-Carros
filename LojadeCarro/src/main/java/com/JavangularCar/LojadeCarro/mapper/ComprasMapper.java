package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.ComprasRequest;
import com.JavangularCar.LojadeCarro.dto.response.ComprasResponse;
import com.JavangularCar.LojadeCarro.entity.Compras;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ComprasMapper {
    Compras toEntity(ComprasRequest request);

    ComprasResponse toResponse(Compras comprasResponse);
}
