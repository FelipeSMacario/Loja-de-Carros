package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.ComprasRequest;
import com.javacar.lojadecarro.dto.response.ComprasResponse;
import com.javacar.lojadecarro.entity.Compras;
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
