package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioRolesResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import com.javacar.lojadecarro.repository.RoleRepository;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.javacar.lojadecarro.enums.Entidade.USUARIO;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder encoder;
    private final EntityValidation entityValidation;
    private final RoleRepository roleRepository;
    private final RolesService rolesService;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        var usuarioEntity = usuarioMapper.toEntity(request);
        usuarioEntity.setPassword(encoder.encode(request.password()));
        var usuario = usuarioRepository.save(usuarioEntity);

        return usuarioMapper.toResponse(usuario);
    }

    public List<UsuarioResponse> listar(StatusFiltro status) {
        var listaUsuarios =
                switch (status) {
                    case TODAS -> usuarioRepository.findAll();
                    case INATIVAS -> usuarioRepository.findByAtivo(false);
                    case ATIVAS -> usuarioRepository.findByAtivo(true);
                };
        return listaUsuarios
                .stream().map(usuarioMapper::toResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        return usuarioMapper.toResponse(buscaUsuario(id));
    }

    @Transactional
    public UsuarioResponse atualizar(UsuarioRequest request, Long id) {
        var usuario = buscaUsuario(id);
        usuarioMapper.toUpdate(request, usuario);
        usuario.setPassword(encoder.encode(request.password()));

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse alterarStatus(Long id, StatusRequest request) {
        var usuario = buscaUsuario(id);
        usuario.alteraStatus(request.ativo());

        return usuarioMapper.toResponse(usuario);
    }

    public Usuario buscaUsuario(Long id) {
        return entityValidation.obterOuLancarErro(usuarioRepository.findById(id), USUARIO, id);
    }

    @Transactional
    public UsuarioRolesResponse vincularRole(Long id, List<Long> requests) {
        validaRolesDuplicadas(requests);

        var usuario = buscaUsuario(id);

        var roles = rolesService.buscaRoles(requests);

        roles.forEach(usuario::adicionarRole);

        return usuarioMapper.toUsuarioRoleResponse(usuario);
    }


    private void validaRolesDuplicadas(List<Long> idsRoles) {
        Set<Long> ids = new HashSet<>(idsRoles);

        if (ids.size() != idsRoles.size()) {
            throw new BusinessException("A requisição possui roles duplicadas.");
        }
    }

    @Transactional
    public UsuarioRolesResponse desvincularRole(Long id, Long roleId) {
        var usuario = buscaUsuario(id);

        var role = rolesService.buscaRole(roleId);

        usuario.removerRole(role.getId());


        return usuarioMapper.toUsuarioRoleResponse(usuario);
    }


    public UsuarioRolesResponse buscarRolesUsuario(Long id) {
        var usuario = buscaUsuario(id);

        return usuarioMapper.toUsuarioRoleResponse(usuario);
    }

}
