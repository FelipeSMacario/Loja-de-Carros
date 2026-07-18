package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.VendasRequest;
import com.javacar.lojadecarro.dto.response.VendasResponse;
import com.javacar.lojadecarro.entity.Venda;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VendasMapper {
    @Mapping(source = "veiculoId", target = "carro.id")
    @Mapping(source = "compradorId", target = "comprador.id")
    @Mapping(source = "vendedorId", target = "vendedor.id")
    Venda toEntity(VendasRequest request);

    VendasResponse toResponse(Venda venda);
}
