package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarroceriaMapper {
    Carroceria toEntity(CarroceriaRequest carroceriaRequest);

    CarroceriaResponse toResponse(Carroceria carroceria);
}
