package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.VendaRequest;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.entity.Venda;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VendasMapper {
    Venda toEntity(VendaRequest request);

    VendaResponse toResponse(Venda venda);
}
