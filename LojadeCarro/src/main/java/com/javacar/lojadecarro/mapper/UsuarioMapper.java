package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioRolesResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.UsuarioRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuario toEntity(UsuarioRequest usuarioRequest);
    Usuario toUpdate(UsuarioRequest usuarioRequest, @MappingTarget Usuario usuario);
    UsuarioResponse toResponse(Usuario usuario);

    @Mapping(target = "roles", source = "roles")
    UsuarioRolesResponse toUsuarioRoleResponse(Usuario usuario);

    default List<RoleResponse> map(Set<UsuarioRole> roles) {
        return roles.stream()
                .map(usuarioRole -> new RoleResponse(
                        usuarioRole.getRole().getId(),
                        usuarioRole.getRole().getNome()
                ))
                .toList();
    }
}
