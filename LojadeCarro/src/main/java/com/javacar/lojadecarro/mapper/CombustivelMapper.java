package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.CombustivelRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CombustivelMapper {
    Combustivel toEntity(CombustivelRequest combustivelRequest);
    void toUpdate(CombustivelRequest  combustivelRequest, @MappingTarget Combustivel update);
    CombustivelResponse toResponse(Combustivel combustivel);
}
