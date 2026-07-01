package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.KitRequest;
import com.JavangularCar.LojadeCarro.dto.response.KitResponse;
import com.JavangularCar.LojadeCarro.entity.Kit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KitMapper {
    Kit toEntity(KitRequest request);
    KitResponse toResponse(Kit kit);
}
