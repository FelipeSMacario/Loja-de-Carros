package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.ImagensRequest;
import com.JavangularCar.LojadeCarro.dto.response.ImagensResponse;
import com.JavangularCar.LojadeCarro.entity.Imagens;
import org.mapstruct.Mapper;

@Mapper(componentModel = "sprint")
public interface ImagensMapper {
    Imagens toEntity(ImagensRequest imagensRequest);
    ImagensResponse toResponse(Imagens imagens);
}
