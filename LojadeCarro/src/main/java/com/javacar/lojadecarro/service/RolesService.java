package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.entity.Role;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.mapper.RoleMapper;
import com.javacar.lojadecarro.repository.RoleRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.ROLE;

@Slf4j
@RequiredArgsConstructor
@Service
public class RolesService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final EntityValidation entityValidation;

    public List<RoleResponse> listar(StatusFiltro status) {
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

    public Role buscaRole(Long roleId) {
        return entityValidation.obterOuLancarErro(roleRepository.findById(roleId), ROLE, roleId);
    }

    public List<Role> buscaRoles(List<Long> requests) {
        var roleList = roleRepository.findAllByIdIn(requests);
        if (roleList.size() != requests.size()) {
            throw new BusinessException("Uma ou mais roles informadas não foram encontradas.");
        }
        return roleList;
    }
}
