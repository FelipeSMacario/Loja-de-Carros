package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarroceriaMapper {
    Carroceria toEntity(CarroceriaRequest carroceriaRequest);

    Carroceria toUpdate(CarroceriaRequest request, @MappingTarget Carroceria carroceria);

    CarroceriaResponse toResponse(Carroceria carroceria);
}
