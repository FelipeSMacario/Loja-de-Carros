package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.ImagensRequest;
import com.javacar.lojadecarro.dto.response.ImagensResponse;
import com.javacar.lojadecarro.entity.Imagens;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImagensMapper {
    Imagens toEntity(ImagensRequest imagensRequest);
    ImagensResponse toResponse(Imagens imagens);
}
