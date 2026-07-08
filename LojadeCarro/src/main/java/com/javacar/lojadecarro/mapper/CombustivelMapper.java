package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.CombustivelRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CombustivelMapper {
    Combustivel toEntity(CombustivelRequest record);
    CombustivelResponse toResponse(Combustivel record);
}
