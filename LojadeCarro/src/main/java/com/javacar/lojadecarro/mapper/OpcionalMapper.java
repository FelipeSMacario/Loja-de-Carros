package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.OpcionalRequest;
import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.entity.Opcional;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OpcionalMapper {
    Opcional toEntity(OpcionalRequest request);

    OpcionalResponse toResponse(Opcional opcional);

    void toUpdate(OpcionalRequest request, @MappingTarget Opcional opcional);
}
