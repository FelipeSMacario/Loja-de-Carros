package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.ModeloRequest;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = MarcaMapper.class)
public interface ModeloMapper {
    Modelo toEntity(ModeloRequest modeloRequest);

    void toUpdate(ModeloRequest modeloRequest, @MappingTarget Modelo modelo);

    @Mapping(source = "marca", target = "marcaResponse")
    ModeloResponse toResponse(Modelo modelo);
}
