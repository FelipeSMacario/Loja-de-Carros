package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.response.ImagemResponse;
import com.javacar.lojadecarro.entity.Imagem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImagemMapper {

    ImagemResponse toResponse(Imagem imagens);
}
