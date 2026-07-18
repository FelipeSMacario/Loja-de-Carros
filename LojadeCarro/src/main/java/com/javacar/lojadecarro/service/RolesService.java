package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.mapper.RoleMapper;
import com.javacar.lojadecarro.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RolesService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleResponse> listarRoles(StatusFiltro status) {
        log.info("Inicio da listagem de roles com o filtro: {}", status);

        var listaRoles =
                switch (status) {
                    case TODAS -> roleRepository.findAll();
                    case INATIVAS -> roleRepository.findByAtivo(false);
                    case ATIVAS -> roleRepository.findByAtivo(true);
                };
        return listaRoles
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }
}
