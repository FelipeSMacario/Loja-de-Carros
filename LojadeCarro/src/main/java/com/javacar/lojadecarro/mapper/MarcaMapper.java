package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.MarcaRequest;
import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.entity.Marca;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MarcaMapper {
    Marca toEntity(MarcaRequest marcaRequest);
    void toUpdate(MarcaRequest marcaRequest, @MappingTarget Marca marcaEntity);

    MarcaResponse toResponse(Marca marca);
}
