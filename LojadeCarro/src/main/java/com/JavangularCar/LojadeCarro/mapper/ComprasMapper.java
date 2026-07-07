package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.ComprasRequest;
import com.JavangularCar.LojadeCarro.dto.response.ComprasResponse;
import com.JavangularCar.LojadeCarro.entity.Compras;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ComprasMapper {
    @Mapping(source = "carroId", target = "carro.id")
    @Mapping(source = "compradorId", target = "comprador.id")
    @Mapping(source = "vendedorId", target = "vendedor.id")
    Compras toEntity(ComprasRequest request);

    ComprasResponse toResponse(Compras comprasResponse);
}
