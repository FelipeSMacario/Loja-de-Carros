package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioRolesResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.UsuarioRole;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.UsuarioHelper;
import com.javacar.lojadecarro.factory.usuario.UsuarioEntityFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioResponseFactory;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.ROLE;
import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.factory.helper.RoleHelper.criaListRole;
import static com.javacar.lojadecarro.factory.helper.RoleHelper.criarRoleEntity;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do serviço de usuários")
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private RolesService rolesService;
    @Mock
    private BCryptPasswordEncoder encoder;
    @Spy
    private EntityValidation entityValidation;
    @InjectMocks
    private UsuarioService usuarioService;

    @Nested
    @DisplayName("Testes da criação do usuário")
    class criarUsuario {
        @Test
        @DisplayName("Deve cadastrar um usuario")
        void deveCadastrarUmUsuario() {
            //Arrange
            var request = UsuarioHelper.criarUsuarioRequest();

            var entity = criarUsuarioEntity();

            var response = criarUsuarioResponse();

            when(usuarioMapper.toEntity(request))
                    .thenReturn(entity);
            when(encoder.encode(request.password()))
                    .thenReturn("senhaCriptografada");
            when(usuarioRepository.save(entity))
                    .thenReturn(entity);
            when(usuarioMapper.toResponse(entity))
                    .thenReturn(response);
            //Act
            var resultado = usuarioService.criar(request);
            //Assert
            assertUsuarioResponse(resultado);
            assertThat(entity.getPassword())
                    .isNotNull()
                    .isEqualTo("senhaCriptografada")
                    .isNotEqualTo(request.password());

            verify(usuarioMapper).toEntity(request);
            verify(usuarioMapper).toResponse(entity);
            verify(usuarioRepository).save(entity);
            verify(encoder).encode(request.password());

            verifyNoMoreInteractions(
                    usuarioRepository,
                    usuarioMapper,
                    encoder
            );
        }
    }

    @Nested
    @DisplayName("Teste da listagem de usuários")
    class listarUsuario {

        @Test
        @DisplayName("Deve listar usuários ativos")
        void deveListarUsuariosAtivos() {
            //Arrange
            var usuarioEntity1 = UsuarioEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comAtivo(true)
                    .build();
            var usuarioEntity2 = UsuarioEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comNome("goku")
                    .comAtivo(true)
                    .comId(2L)
                    .build();
            var listEntity = List.of(usuarioEntity1, usuarioEntity2);

            var usuarioResponse1 = UsuarioResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comAtivo(true)
                    .build();
            var usuarioResponse2 = UsuarioResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comNome("goku")
                    .comAtivo(true)
                    .comId(2L)
                    .build();

            when(usuarioRepository.findByAtivo(true))
                    .thenReturn(listEntity);

            when(usuarioMapper.toResponse(usuarioEntity1))
                    .thenReturn(usuarioResponse1);

            when(usuarioMapper.toResponse(usuarioEntity2))
                    .thenReturn(usuarioResponse2);
            //ACT
            var resultado = usuarioService.listar(StatusFiltro.ATIVAS);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            UsuarioResponse::id,
                            UsuarioResponse::nome,
                            UsuarioResponse::ativo)
                    .containsExactly(
                            tuple(1L, "Felipe", true),
                            tuple(2L, "goku", true)
                    );

            verify(usuarioRepository).findByAtivo(true);
            verify(usuarioRepository, never()).findAll();
            verify(usuarioMapper).toResponse(usuarioEntity1);
            verify(usuarioMapper).toResponse(usuarioEntity2);

            verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }

        @Test
        @DisplayName("Deve listar usuários inativos")
        void deveListarUsuariosInativos() {
            //Arrange
            var usuarioEntity1 = UsuarioEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comAtivo(false)
                    .build();
            var usuarioEntity2 = UsuarioEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comNome("goku")
                    .comAtivo(false)
                    .comId(2L)
                    .build();
            var listEntity = List.of(usuarioEntity1, usuarioEntity2);

            var usuarioResponse1 = UsuarioResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comAtivo(false)
                    .build();
            var usuarioResponse2 = UsuarioResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comNome("goku")
                    .comAtivo(false)
                    .comId(2L)
                    .build();

            when(usuarioRepository.findByAtivo(false))
                    .thenReturn(listEntity);

            when(usuarioMapper.toResponse(usuarioEntity1))
                    .thenReturn(usuarioResponse1);

            when(usuarioMapper.toResponse(usuarioEntity2))
                    .thenReturn(usuarioResponse2);
            //ACT
            var resultado = usuarioService.listar(StatusFiltro.INATIVAS);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            UsuarioResponse::id,
                            UsuarioResponse::nome,
                            UsuarioResponse::ativo)
                    .containsExactly(
                            tuple(1L, "Felipe", false),
                            tuple(2L, "goku", false)
                    );

            verify(usuarioRepository).findByAtivo(false);
            verify(usuarioRepository, never()).findAll();
            verify(usuarioMapper).toResponse(usuarioEntity1);
            verify(usuarioMapper).toResponse(usuarioEntity2);

            verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }

        @Test
        @DisplayName("Deve listar todos usuários")
        void deveListarTodosUsuarios() {
            //Arrange
            var usuarioEntity1 = UsuarioEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comAtivo(true)
                    .build();
            var usuarioEntity2 = UsuarioEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comNome("goku")
                    .comAtivo(false)
                    .comId(2L)
                    .build();
            var listEntity = List.of(usuarioEntity1, usuarioEntity2);

            var usuarioResponse1 = UsuarioResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comAtivo(true)
                    .build();
            var usuarioResponse2 = UsuarioResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comNome("goku")
                    .comAtivo(false)
                    .comId(2L)
                    .build();

            when(usuarioRepository.findAll())
                    .thenReturn(listEntity);

            when(usuarioMapper.toResponse(usuarioEntity1))
                    .thenReturn(usuarioResponse1);

            when(usuarioMapper.toResponse(usuarioEntity2))
                    .thenReturn(usuarioResponse2);
            //ACT
            var resultado = usuarioService.listar(StatusFiltro.TODAS);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            UsuarioResponse::id,
                            UsuarioResponse::nome,
                            UsuarioResponse::ativo)
                    .containsExactly(
                            tuple(1L, "Felipe", true),
                            tuple(2L, "goku", false)
                    );

            verify(usuarioRepository, never()).findByAtivo(anyBoolean());
            verify(usuarioRepository).findAll();
            verify(usuarioMapper).toResponse(usuarioEntity1);
            verify(usuarioMapper).toResponse(usuarioEntity2);

            verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }
    }

    @Nested
    @DisplayName("Testes para buscar o usuário")
    class buscarUsuarioEntity {
        @Test
        @DisplayName("Deve buscar o usuario por ID")
        void deveBuscarUsuarioPorId() {
            //Arrange
            var entity = criarUsuarioEntity();
            var response = criarUsuarioResponse();

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(usuarioMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = usuarioService.buscarPorId(ID_VALIDO);
            //Assert
            assertUsuarioResponse(resultado);

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(usuarioMapper).toResponse(entity);

            verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção buscar usuário por ID")
        void deveLancarExcecaoBuscarUsuarioPorId() {
            //Arrange
            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> usuarioService.buscarPorId(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, USUARIO, ID_VALIDO);

            verify(usuarioRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(usuarioMapper);
        }
    }

    @Nested
    @DisplayName("Testes de atualização do usuário")
    class atualizar {

        @Test
        @DisplayName("Deve atualizar o usuário")
        void deveAtualizarUsuario() {
            //Arrange
            var request = criarUsuarioRequest();
            var entity = criarUsuarioEntity();
            var response = criarUsuarioResponse();

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            doNothing().when(usuarioMapper)
                    .toUpdate(request, entity);

            when(encoder.encode(request.password()))
                    .thenReturn("senhaCriptografada");

            when(usuarioMapper.toResponse(entity))
                    .thenReturn(response);

            //ACT
            var resultado = usuarioService.atualizar(request, ID_VALIDO);
            //Assert
            assertUsuarioResponse(resultado);
            assertThat(entity.getPassword())
                    .isNotNull()
                    .isEqualTo("senhaCriptografada");
            verify(usuarioRepository).findById(ID_VALIDO);
            verify(encoder).encode(request.password());
            verify(usuarioMapper).toResponse(entity);
            verifyNoMoreInteractions(
                    usuarioRepository,
                    encoder,
                    usuarioMapper
            );
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao atualizar o usuário por ID")
        void deveLancarUmaExcecaoAoAtualizarUmUsuarioPorId() {
            //Arrange
            var request = criarUsuarioRequest();
            when(usuarioRepository.findById(ID_INVALIDO))
                    .thenReturn(Optional.empty());
            //Act
            var excecao = assertThrows(NotFoundException.class,
                    () -> usuarioService.atualizar(request, ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(excecao, USUARIO, ID_INVALIDO);

            verify(usuarioRepository).findById(ID_INVALIDO);
            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(
                    usuarioMapper,
                    encoder
            );
        }
    }

    @Nested
    @DisplayName("Testes para atualizar o status do usuário")
    class atualizarStatus {

        @Test
        @DisplayName("Deve alterar o status do usuário")
        void deveAlterarStatusDoCor() {
            //Arrange
            var entity = criarUsuarioEntity();
            var request = new StatusRequest(false);
            var response = UsuarioResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comAtivo(false)
                    .build();

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(usuarioMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = usuarioService.alterarStatus(ID_VALIDO, request);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo()).isFalse();
            assertThat(entity.isAtivo()).isFalse();

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(usuarioMapper).toResponse(entity);

            verifyNoMoreInteractions(usuarioRepository);
            verifyNoMoreInteractions(usuarioMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o status")
        void deveLancarExcecaoAoAlterarStatusCor() {
            //Arrange
            var entity = criarUsuarioEntity();
            var request = new StatusRequest(true);

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.alterarStatus(ID_VALIDO, request));

            //Assert
            assertBusinessResponseError(exception, USUARIO);

            assertThat(entity.isAtivo()).isTrue();

            verify(usuarioRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(usuarioMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção do usuário nao encontrado ao alterar status")
        void deveLancarExcecaoQuandoUsuarioNaoEncontradoAoAlterarStatus() {
            //Arrange
            var request = new StatusRequest(true);
            //Assert
            var exception = assertThrows(NotFoundException.class,
                    () -> usuarioService.alterarStatus(ID_INVALIDO, request));

            assertNotFoundResponseError(exception, USUARIO, ID_INVALIDO);
            verify(usuarioRepository).findById(ID_INVALIDO);

            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(usuarioMapper);
        }

    }

    @Nested
    @DisplayName("Testes da busca da entidade do usuário")
    class buscaUsuario {
        @Test
        @DisplayName("Deve buscar a entidade usuario por ID")
        void deveBuscarEntidadeUsuarioPorID() {
            //Arrange
            var entity = criarUsuarioEntity();

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            //ACT
            var resultado = usuarioService.buscaUsuario(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            Usuario::getId,
                            Usuario::getCpf,
                            Usuario::getDataNascimento,
                            Usuario::getEmail,
                            Usuario::getNome,
                            Usuario::getPassword,
                            Usuario::isAtivo
                    )
                    .containsExactly(
                            ID_VALIDO,
                            "1234567890",
                            LocalDate.of(1991, Month.MAY, 14),
                            "felipesmacario@gmail.com",
                            "Felipe Soares Macário",
                            "123456",
                            true
                    );

            verify(usuarioRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(usuarioRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção buscar entidade usuário")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            //Arrange
            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> usuarioService.buscarPorId(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, USUARIO, ID_VALIDO);

            verify(usuarioRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(usuarioMapper);
        }
    }

    @Nested
    @DisplayName("Testes para vincular uma role ao usuário")
    class vincularUsuario {
        @Test
        @DisplayName("Deve vincular uma role ao usuário")
        void deveVincularUmaRoleAoUsuario() {
            //Arrange
            var entity = criarUsuarioEntity();
            var response = criarUsuarioRolesResponse();
            var listRoles = List.of(1L, 2L);
            var roles = criaListRole();

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(rolesService.buscaRoles(listRoles))
                    .thenReturn(roles);

            when(usuarioMapper.toUsuarioRoleResponse(entity))
                    .thenReturn(response);

            //ACT
            var resultado = usuarioService.vincularRole(ID_VALIDO, listRoles);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            UsuarioRolesResponse::id,
                            UsuarioRolesResponse::nome,
                            UsuarioRolesResponse::cpf
                    )
                    .containsExactly(
                            ID_VALIDO,
                            "Felipe Soares Macário",
                            "1234567890"
                    );

            assertThat(resultado.roles())
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            RoleResponse::id
                    ).containsExactly(
                            listRoles.getFirst(),
                            listRoles.getLast()
                    );

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(rolesService).buscaRoles(listRoles);
            verifyNoMoreInteractions(
                    usuarioRepository,
                    rolesService
            );
        }

        @Test
        @DisplayName("Deve lançar exceção de roles duplicadas")
        void deveLancarExcecaoQuandoRolesDuplicadas() {
            //Arrange
            var listRoles = List.of(1L, 1L);

            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> usuarioService.vincularRole(ID_VALIDO, listRoles));
            //Assert
            assertBusinessResponseError(excecao, "A requisição possui roles duplicadas.");

            verifyNoInteractions(
                    usuarioRepository,
                    rolesService,
                    usuarioMapper
            );
        }

        @Test
        @DisplayName("Deve lançar exceção role já existente")
        void deveLancarExcecaoQuandoRoleJaExistente() {
            //Arrange
            var entity = criarUsuarioEntity();
            var listRoles = List.of(1L, 2L);
            var roles = criaListRole();

            roles.forEach(role ->
                    entity.getRoles().add(new UsuarioRole(entity, role))
            );

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(rolesService.buscaRoles(listRoles))
                    .thenReturn(roles);
            //ACT

            var excecao = assertThrows(BusinessException.class,
                    () -> usuarioService.vincularRole(ID_VALIDO, listRoles));
            //Assert
            assertBusinessResponseError(excecao, ROLE.jaAtiva());

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(rolesService).buscaRoles(listRoles);

            verifyNoMoreInteractions(
                    usuarioRepository,
                    rolesService
            );

            verifyNoInteractions(usuarioMapper);
        }
    }

    @Nested
    @DisplayName("Testes para desvincular uma role")
    class desvincularRole {
        @Test
        @DisplayName("Deve desvincular uma role")
        void deveDesvincularUmaRole() {
            //Arrange
            var idRole = 1L;
            var entity = criarUsuarioEntity();
            criaListRole().forEach(role ->
                    entity.getRoles().add(new UsuarioRole(entity, role))
            );
            var role = criarRoleEntity();
            var response = criarUsuarioRolesResponseDesvincula();

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(rolesService.buscaRole(idRole))
                    .thenReturn(role);

            when(usuarioMapper.toUsuarioRoleResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = usuarioService.desvincularRole(ID_VALIDO, idRole);
            //Assert
            assertThat(resultado.roles())
                    .extracting(RoleResponse::id)
                    .containsExactly(2L);


            verify(usuarioRepository).findById(ID_VALIDO);
            verify(rolesService).buscaRole(idRole);
            verify(usuarioMapper).toUsuarioRoleResponse(entity);

            verifyNoMoreInteractions(
                    usuarioRepository,
                    rolesService,
                    usuarioMapper
            );

        }

        @Test
        @DisplayName("Deve lançar exceção de usuario nao encontrado ao desvincular role")
        void deveLancarExcecaoQuandoUsuarioNaoExistente() {
            //Arrange
            when(usuarioRepository.findById(ID_VALIDO))
            .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> usuarioService.desvincularRole(ID_VALIDO, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, USUARIO, ID_VALIDO);

            verify(usuarioRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(
                    rolesService,
                    usuarioMapper
            );
        }
        @Test
        @DisplayName("Deve lançar exceção de role não encontrada ao desvincular role")
        void deveLancarExcecaoQuandoRoleNaoExistente() {
            //Arrange
            var entity = criarUsuarioEntity();
            when(usuarioRepository.findById(ID_VALIDO))
            .thenReturn(Optional.of(entity));

            when(rolesService.buscaRole(ID_VALIDO))
                    .thenThrow(new NotFoundException(ROLE, ID_VALIDO));
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> usuarioService.desvincularRole(ID_VALIDO, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, ROLE, ID_VALIDO);

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(rolesService).buscaRole(ID_VALIDO);

            verifyNoMoreInteractions(
                    usuarioRepository,
                    rolesService
            );

            verifyNoInteractions(usuarioMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção usuário não possui role")
        void deveLancarExcecaoQuandoUsuarioNaoPossuiRole() {
            //Arrange
            var idRole = 1L;
            var entity = criarUsuarioEntity();
            var role = criarRoleEntity();

            when(usuarioRepository.findById(ID_VALIDO))
            .thenReturn(Optional.of(entity));

            when(rolesService.buscaRole(idRole))
            .thenReturn(role);
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> usuarioService.desvincularRole(ID_VALIDO, idRole));
            //Assert
            assertBusinessResponseError(excecao, "O usuário não possui uma role com o id informado.");

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(rolesService).buscaRole(idRole);

            verifyNoMoreInteractions(usuarioRepository, rolesService);

            verifyNoInteractions(usuarioMapper);
        }
    }
}
