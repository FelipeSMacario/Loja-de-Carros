package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.AlteracaoSenhaRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.request.UsuarioUpdateRequest;
import com.javacar.lojadecarro.dto.response.AlteracaoSenhaResponse;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioRolesResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import com.javacar.lojadecarro.repository.VendasRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.enums.StatusVeiculo.RESERVADO;
import static com.javacar.lojadecarro.enums.StatusVenda.EM_ANDAMENTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder encoder;
    private final EntityValidation entityValidation;
    private final RolesService rolesService;
    private final VendasRepository vendasRepository;
    private final VeiculoRepository veiculoRepository;


    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        validarCpfUnico(request.cpf());
        validarEmailUnico(request.email());
        var usuarioEntity = usuarioMapper.toEntity(request);
        usuarioEntity.alterarSenha(encoder.encode(request.password()));
        var roleUsuario = rolesService.buscarPorNome("ROLE_USUARIO");
        usuarioEntity.adicionarRole(roleUsuario);
        var usuario = usuarioRepository.save(usuarioEntity);

        return usuarioMapper.toResponse(usuario);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return usuarioMapper.toResponse(buscaUsuario(id));
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public UsuarioResponse atualizar(UsuarioUpdateRequest request, Long idUsuario) {
        var usuario = buscaUsuarioAtivo(idUsuario);
        if (!usuario.getEmail().equals(request.email())) {
            validarEmailUnico(request.email());
        }
        usuarioMapper.toUpdate(request, usuario);

        return usuarioMapper.toResponse(usuario);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UsuarioResponse alterarStatus(Long id, StatusRequest request) {
        var usuario = buscaUsuario(id);
        if (request.ativo()) {
            usuario.ativar();
        } else {
            desativar(usuario);
        }

        return usuarioMapper.toResponse(usuario);
    }

    public Usuario buscaUsuario(Long id) {
        return entityValidation.obterOuLancarErro(usuarioRepository.findById(id), USUARIO, id);
    }

    public Usuario buscaUsuarioAtivo(Long id) {
        return usuarioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new NotFoundException(USUARIO, id));
    }


    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UsuarioRolesResponse desvincularRole(Long id, Long roleId) {
        var usuario = buscaUsuario(id);

        var role = rolesService.buscarPorId(roleId);

        usuario.removerRole(role.getId());

        return usuarioMapper.toUsuarioRoleResponse(usuario);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public UsuarioRolesResponse buscarRolesUsuario(Long id) {
        var usuario = buscaUsuario(id);

        return usuarioMapper.toUsuarioRoleResponse(usuario);
    }

    private void validarCpfUnico(String cpf) {
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new BusinessException("O CPF informado já possui um cadastro.");
        }
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new BusinessException("O email informado já possui um cadastro.");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public AlteracaoSenhaResponse alterarSenha(AlteracaoSenhaRequest request, Long idUsuario) {
        var usuario = buscaUsuarioAtivo(idUsuario);
        validarSenhaAtual(usuario.getPassword(), request.senhaAntiga());
        validarSenhaNova(usuario.getPassword(), request.senhaNova());

        usuario.alterarSenha(encoder.encode(request.senhaNova()));

        return new AlteracaoSenhaResponse(usuario.getEmail(), "Senha atualizada com sucesso");
    }

    private void validarSenhaAtual(
            String passwordHash,
            String senhaAtual
    ) {
        if (!encoder.matches(senhaAtual, passwordHash)) {
            throw new BusinessException(
                    "A senha informada não corresponde à senha atual."
            );
        }
    }

    private void validarSenhaNova(
            String passwordHash,
            String senhaNova
    ) {
        if (encoder.matches(senhaNova, passwordHash)) {
            throw new BusinessException(
                    "A nova senha não pode ser igual à senha atual."
            );
        }
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public UsuarioResponse buscarMeuUsuario(Long id) {
        return usuarioMapper.toResponse(buscaUsuarioAtivo(id));
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public UsuarioResponse desativarUsuario(Long id) {
        var usuario = buscaUsuarioAtivo(id);
        desativar(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    private void desativar(Usuario usuario) {
        validarPodeDesativar(usuario);
        pausarAnunciosDisponiveis(usuario.getId());
        usuario.desativar();
    }

    private void validarPodeDesativar(Usuario usuario) {
        if (vendasRepository.existsByVendedor_IdAndStatusVenda(usuario.getId(), EM_ANDAMENTO)) {
            throw new BusinessException("Não é possível desativar o usuário com uma ou mais vendas em andamento.");
        }
        if (vendasRepository.existsByComprador_IdAndStatusVenda(usuario.getId(), EM_ANDAMENTO)) {
            throw new BusinessException("Não é possível desativar o usuário com uma ou mais compras em andamento.");
        }
        if (veiculoRepository.existsByVendedor_IdAndStatusVeiculo(usuario.getId(), RESERVADO)) {
            throw new BusinessException("Não é possível desativar um usuário com veículo reservado.");
        }
    }

    private void pausarAnunciosDisponiveis(Long idUsuario) {
        var veiculos = veiculoRepository
                .findByVendedor_IdAndStatusVeiculo(
                        idUsuario,
                        DISPONIVEL
                );

        veiculos.forEach(Veiculo::pausarAnuncio);
    }
}
