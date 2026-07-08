package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.CoresRequest;
import com.javacar.lojadecarro.dto.response.CoresResponse;
import com.javacar.lojadecarro.entity.Cores;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoresMapper {
    Cores toEntity(CoresRequest request);
    CoresResponse toResponse(Cores cores);
}
