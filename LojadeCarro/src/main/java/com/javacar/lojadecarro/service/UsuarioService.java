package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.RoleRequest;
import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioRolesResponse;
import com.javacar.lojadecarro.entity.Role;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.UsuarioRole;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import com.javacar.lojadecarro.repository.RoleRepository;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import com.javacar.lojadecarro.repository.UsuarioRoleRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.javacar.lojadecarro.enums.Entidade.ROLE;
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
    private final UsuarioRoleRepository usuarioRoleRepository;

    public UsuarioResponse createUsuario(UsuarioRequest usuario) {
        log.debug("Inicio da createUsuarioService com a response: {}", usuario);
        var usuarioEntity = usuarioMapper.toEntity(usuario);
        usuarioEntity.setPassword(encoder.encode(usuario.password()));
        var usuarioResponse = usuarioRepository.save(usuarioEntity);

        log.info("Usuário salva com sucesso!");

        return usuarioMapper.toResponse(usuarioResponse);
    }

    public List<UsuarioResponse> listarUsuario(StatusFiltro status) {
        log.info("Inicio da listarUsuarioService");
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

    public UsuarioResponse findUsuarioBId(Long id) {
        log.info("Inicio da findMarcaByIdService com id: {}", id);
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(USUARIO, id));
    }

    public UsuarioResponse updateUsuario(UsuarioRequest usuario, Long id) {
        log.info("Inicio da updateUsuarioService com o id: {}", id);
        return usuarioRepository.findById(id)
                .map(usuarioEntity -> {
                    usuarioMapper.toUpdate(usuario, usuarioEntity);
                    usuarioEntity.setPassword(encoder.encode(usuario.password()));
                    var update = usuarioRepository.save(usuarioEntity);
                    log.info("Usuário com o ID: {} foi atualizado", id);
                    return usuarioMapper.toResponse(update);
                }).orElseThrow(() -> new NotFoundException(USUARIO, id));
    }

    public UsuarioResponse alterarStatus(Long id, boolean status) {
        log.info("Inicio da deleteUsuarioService com o id: {}", id);

        var usuarioValidado = entityValidation.obterOuLancarErro(
                usuarioRepository.findById(id),
                USUARIO,
                id);

        if (usuarioValidado.isAtivo() && status) {
            throw new BusinessException(status ? USUARIO.jaAtiva() : USUARIO.jaInativa());
        }

        usuarioValidado.setAtivo(status);
        var usuarioAtualizado = usuarioRepository.save(usuarioValidado);
        log.info("Usuário com o ID: {} foi desativado", id);

        return usuarioMapper.toResponse(usuarioAtualizado);
    }

    public Usuario buscaUsuario(Long id) {
        log.info("Inicio da buscaUsuarioService com o id: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USUARIO, id));
    }

    @Transactional
    public UsuarioRolesResponse adicionarRoles(Long id, List<RoleRequest> requests) {
        var usuario = entityValidation.obterOuLancarErro(
                usuarioRepository.findById(id),
                USUARIO,
                id);

        validaRolesDuplicadas(requests);
        List<Role> roles = buscarRoles(requests);
        validaRolesJaCadastradas(usuario, roles);

        adicionarRoles(usuario, roles);

        usuarioRepository.save(usuario);

        return usuarioMapper.toUsuarioRoleResponse(usuario);
    }


    private List<Role> buscarRoles(List<RoleRequest> requests) {
        List<Role> roles = new ArrayList<>();

        requests.forEach(roleRequest ->
                roles.add(roleRepository.findById(roleRequest.id())
                        .orElseThrow(() -> new NotFoundException(ROLE, roleRequest.id())))

        );

        return roles;
    }

    private void validaRolesDuplicadas(List<RoleRequest> requests) {
        Set<Long> ids = requests
                .stream()
                .map(RoleRequest::id)
                .collect(Collectors.toSet());

        if (ids.size() != requests.size()) {
            throw new BusinessException("A requisição possui roles duplicadas.");
        }
    }

    private void validaRolesJaCadastradas(Usuario usuario, List<Role> roles) {

        Set<Long> idsUsuario = usuario.getRoles().stream()
                .map(ur -> ur.getRole().getId())
                .collect(Collectors.toSet());

        for (Role role : roles) {
            if (idsUsuario.contains(role.getId())) {
                throw new BusinessException(ROLE.jaAtiva());
            }
        }
    }

    private void adicionarRoles(Usuario usuario, List<Role> roles) {

        roles.forEach(role ->
                usuario.getRoles().add(new UsuarioRole(usuario, role))
        );
    }

    @Transactional
    public UsuarioRolesResponse desvincularRole(Long id, Long roleId) {
        var usuario = entityValidation.obterOuLancarErro(
                usuarioRepository.findById(id),
                USUARIO,
                id);
        validarUsuarioPossuiRole(roleId, usuario);

        usuario.getRoles().removeIf(
                usuarioRole -> usuarioRole.getRole().getId().equals(roleId)
        );

        return usuarioMapper.toUsuarioRoleResponse(usuario);
    }

    private void validarUsuarioPossuiRole(Long roleId, Usuario usuario) {
        if (usuario.getRoles()
                .stream()
                .noneMatch(role -> role.getRole().getId().equals(roleId))){

            throw new BusinessException("O usuário não possui uma role com o id informado.");
        }
    }

    public UsuarioRolesResponse buscarRoleUsuario(Long id) {
        var usuario = entityValidation.obterOuLancarErro(
                usuarioRepository.findById(id),
                USUARIO,
                id);

        return usuarioMapper.toUsuarioRoleResponse(usuario);
    }
}
