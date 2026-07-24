package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toResponse(Role role);
}
