package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.CorRequest;
import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.entity.Cor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CorMapper {
    Cor toEntity(CorRequest request);

    void toUpdate(CorRequest request, @MappingTarget Cor cor);

    CorResponse toResponse(Cor cor);
}
