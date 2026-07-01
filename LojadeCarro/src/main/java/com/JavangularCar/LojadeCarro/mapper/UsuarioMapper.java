package com.JavangularCar.LojadeCarro.mapper;

import com.JavangularCar.LojadeCarro.dto.request.UsuarioRequest;
import com.JavangularCar.LojadeCarro.dto.response.UsuarioResponse;
import com.JavangularCar.LojadeCarro.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "sprint")
public interface UsuarioMapper {
    Usuario toEntity(UsuarioRequest usuarioRequest);
    UsuarioResponse toResponse(Usuario usuario);
}
