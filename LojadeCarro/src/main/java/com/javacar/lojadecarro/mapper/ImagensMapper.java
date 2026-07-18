package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.ImagensRequest;
import com.javacar.lojadecarro.dto.response.ImagensResponse;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Imagem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImagensMapper {
    Imagem toEntity(ImagensRequest imagensRequest);
    ImagensResponse toResponse(Imagem imagens);
}
