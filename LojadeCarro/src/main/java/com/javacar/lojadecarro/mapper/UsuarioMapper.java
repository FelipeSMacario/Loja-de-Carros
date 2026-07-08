package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuario toEntity(UsuarioRequest usuarioRequest);
    UsuarioResponse toResponse(Usuario usuario);
}
