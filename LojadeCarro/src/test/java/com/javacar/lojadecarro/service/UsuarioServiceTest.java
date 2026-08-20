package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.AlteracaoSenhaRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.request.UsuarioUpdateRequest;
import com.javacar.lojadecarro.dto.response.AlteracaoSenhaResponse;
import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioRolesResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.UsuarioRole;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.VeiculoHelper;
import com.javacar.lojadecarro.factory.usuario.UsuarioTestContext;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import com.javacar.lojadecarro.repository.VendasRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.ROLE;
import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.*;
import static com.javacar.lojadecarro.enums.StatusVenda.EM_ANDAMENTO;
import static com.javacar.lojadecarro.factory.helper.RoleHelper.*;
import static com.javacar.lojadecarro.factory.helper.RoleHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.RoleHelper.assertBusinessResponseErrorInativa;
import static com.javacar.lojadecarro.factory.helper.RoleHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.*;
import static com.javacar.lojadecarro.factory.usuario.UsuarioTestContext.atualizarUsuarioValido;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Testes do serviço de usuários")
class UsuarioServiceTest extends BaseServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private VendasRepository vendasRepository;
    @Mock
    private VeiculoRepository veiculoRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private RolesService rolesService;
    @Mock
    private BCryptPasswordEncoder encoder;
    @InjectMocks
    private UsuarioService usuarioService;

    @Nested
    @DisplayName("Testes da criação do usuário")
    class Criar {
        @Test
        @DisplayName("Deve cadastrar um usuario")
        void deveCadastrarUmUsuario() {
            //Arrange
            var cx = new UsuarioTestContext();
            var entity = criarUsuarioPadrao();
            var rolePadrao = roleUsuario();

            when(usuarioRepository.existsByCpf(cx.request.cpf()))
                    .thenReturn(false);

            when(usuarioRepository.existsByEmail(cx.request.email()))
                    .thenReturn(false);

            when(usuarioMapper.toEntity(cx.request))
                    .thenReturn(entity);
            when(encoder.encode(cx.request.password()))
                    .thenReturn("senhaCriptografada");

            when(rolesService.buscarPorNome(ROLE_USUARIO))
                    .thenReturn(rolePadrao);

            when(usuarioRepository.save(entity))
                    .thenReturn(entity);
            when(usuarioMapper.toResponse(entity))
                    .thenReturn(cx.response);
            //Act
            var resultado = usuarioService.criar(cx.request);
            //Assert
            assertUsuarioResponse(resultado);
            assertThat(entity.getPassword())
                    .isNotNull()
                    .isEqualTo("senhaCriptografada")
                    .isNotEqualTo(cx.request.password());

            assertThat(entity.getRoles())
                    .singleElement()
                    .satisfies(usuarioRole -> {
                        assertThat(usuarioRole.getUsuario())
                                .isSameAs(entity);

                        assertThat(usuarioRole.getRole())
                                .isSameAs(rolePadrao);

                        assertThat(usuarioRole.getRole().getNome())
                                .isEqualTo(ROLE_USUARIO);
                    });

            verify(usuarioMapper).toEntity(cx.request);
            verify(usuarioMapper).toResponse(entity);
            verify(usuarioRepository).existsByEmail(cx.request.email());
            verify(usuarioRepository).existsByCpf(cx.request.cpf());
            verify(usuarioRepository).save(entity);
            verify(encoder).encode(cx.request.password());
            verify(rolesService).buscarPorNome(ROLE_USUARIO);

            verifyNoMoreInteractions(
                    usuarioRepository,
                    usuarioMapper,
                    encoder,
                    rolesService
            );
        }

        @Test
        @DisplayName("Deve lançar exceção de CPF único")
        void deveLancarExcecaoCpfUnico() {
            //Arrange
            var cx = new UsuarioTestContext();
            when(usuarioRepository.existsByCpf(cx.request.cpf()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class, () ->
                    usuarioService.criar(cx.request));
            //Assert
            assertThat(exception)
                    .hasMessage("O CPF informado já possui um cadastro.");

            verify(usuarioRepository).existsByCpf(cx.request.cpf());
            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(usuarioMapper, rolesService, encoder);
        }

        @Test
        @DisplayName("Deve lançar exceção de email único")
        void deveLancarExcecaoEmailUnico() {
            //Arrange
            var cx = new UsuarioTestContext();
            when(usuarioRepository.existsByCpf(cx.request.cpf()))
                    .thenReturn(false);
            when(usuarioRepository.existsByEmail(cx.request.email()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class, () ->
                    usuarioService.criar(cx.request));
            //Assert
            assertThat(exception)
                    .hasMessage("O email informado já possui um cadastro.");

            verify(usuarioRepository).existsByCpf(cx.request.cpf());
            verify(usuarioRepository).existsByEmail(cx.request.email());
            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(usuarioMapper, rolesService, encoder);
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar role")
        void deveLancarExcecaoNaoEncontrarRole() {
            //Arrange
            var cx = new UsuarioTestContext();
            var entity = criarUsuarioPadrao();

            when(usuarioRepository.existsByCpf(cx.request.cpf()))
                    .thenReturn(false);
            when(usuarioRepository.existsByEmail(cx.request.email()))
                    .thenReturn(false);

            when(usuarioMapper.toEntity(cx.request))
                    .thenReturn(entity);
            when(encoder.encode(cx.request.password()))
                    .thenReturn("senhaCriptografada");

            when(rolesService.buscarPorNome(ROLE_USUARIO))
                    .thenThrow(new NotFoundException(ROLE.naoEncontrada()));
            //ACT
            var exception = assertThrows(NotFoundException.class, () ->
                    usuarioService.criar(cx.request));
            //Assert
            assertNotFoundResponseError(exception, ROLE);

            verify(usuarioRepository).existsByCpf(cx.request.cpf());
            verify(usuarioRepository).existsByEmail(cx.request.email());
            verify(usuarioMapper).toEntity(cx.request);
            verify(encoder).encode(cx.request.password());
            verify(rolesService).buscarPorNome(ROLE_USUARIO);

            verifyNoMoreInteractions(usuarioRepository, usuarioMapper, encoder, rolesService);
        }
    }

    @Nested
    @DisplayName("Teste da listagem de usuários")
    class Listar {

        @Test
        @DisplayName("Deve listar usuários ativos")
        void deveListarUsuariosAtivos() {
            //Arrange
            var usuarioEntity1 = criarUsuarioPadrao();
            var usuarioEntity2 = UsuarioTestContext
                    .criarUsuario(2L, "Goku", "goku@gmail.com", "12345678901", "12345", true);

            var listEntity = List.of(usuarioEntity1, usuarioEntity2);

            var usuarioResponse1 = criarUsuarioPadraoResponse();
            var usuarioResponse2 = UsuarioTestContext
                    .criaUsuarioResponse(2L, "Goku", "goku@gmail.com", "14714814966", true);


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
                            UsuarioResponse::email,
                            UsuarioResponse::cpf,
                            UsuarioResponse::ativo)
                    .containsExactly(
                            tuple(1L, "Felipe", "felipesmacario@gmail.com", "12345678901", true),
                            tuple(2L, "Goku", "goku@gmail.com", "14714814966", true)
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
            var usuarioEntity1 = criarUsuarioPadraoInativo();
            var usuarioEntity2 = UsuarioTestContext
                    .criarUsuario(2L, "Goku", "goku@gmail.com", "12345678901", "12345", false);

            var listEntity = List.of(usuarioEntity1, usuarioEntity2);

            var usuarioResponse1 = criarUsuarioPadraoResponseInativo();
            var usuarioResponse2 = UsuarioTestContext
                    .criaUsuarioResponse(2L, "Goku", "goku@gmail.com", "14714814966", false);

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
                            UsuarioResponse::email,
                            UsuarioResponse::cpf,
                            UsuarioResponse::ativo)
                    .containsExactly(
                            tuple(1L, "Felipe", "felipesmacario@gmail.com", "12345678901", false),
                            tuple(2L, "Goku", "goku@gmail.com", "14714814966", false)
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
            var usuarioEntity1 = criarUsuarioPadrao();
            var usuarioEntity2 = UsuarioTestContext
                    .criarUsuario(2L, "Goku", "goku@gmail.com", "12345678901", "12345", false);

            var listEntity = List.of(usuarioEntity1, usuarioEntity2);

            var usuarioResponse1 = criarUsuarioPadraoResponse();
            var usuarioResponse2 = UsuarioTestContext
                    .criaUsuarioResponse(2L, "Goku", "goku@gmail.com", "14714814966", false);

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
                            UsuarioResponse::email,
                            UsuarioResponse::cpf,
                            UsuarioResponse::ativo)
                    .containsExactly(
                            tuple(1L, "Felipe", "felipesmacario@gmail.com", "12345678901", true),
                            tuple(2L, "Goku", "goku@gmail.com", "14714814966", false)
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
    class Buscar {
        @Test
        @DisplayName("Deve buscar o usuario")
        void deveBuscarUsuario() {
            //Arrange
            var cx = new UsuarioTestContext();
            var entity = criarUsuarioPadrao();

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(usuarioMapper.toResponse(entity))
                    .thenReturn(cx.response);
            //ACT
            var resultado = usuarioService.buscarPorId(ID_VALIDO);
            //Assert
            assertUsuarioResponse(resultado);

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(usuarioMapper).toResponse(entity);

            verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }

        @Test
        @DisplayName("Deve buscar o usuario inativo")
        void deveBuscarUsuarioInativo() {
            //Arrange
            var entity = criarUsuarioPadraoInativo();
            var response = criarUsuarioPadraoResponseInativo();

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(usuarioMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = usuarioService.buscarPorId(ID_VALIDO);
            //Assert
            assertUsuarioResponseInativo(resultado);

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(usuarioMapper).toResponse(entity);

            verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção buscar usuário")
        void develancarExcecaoAoBuscarUsuario() {
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
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o usuário")
        void deveAtualizarUsuario() {
            //Arrange
            var request = usuarioAtualizacaoRequestPadrao();
            var entity = criarUsuarioPadrao();
            var response = UsuarioTestContext
                    .criaUsuarioResponse(ID_VALIDO, "Felipe", "felipe2macario@gmail.com", "1234567890", true);


            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(usuarioRepository.existsByEmail(request.email()))
                    .thenReturn(false);


            when(usuarioMapper.toResponse(entity))
                    .thenReturn(response);

            //ACT
            var resultado = usuarioService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            UsuarioResponse::id,
                            UsuarioResponse::nome,
                            UsuarioResponse::email,
                            UsuarioResponse::cpf,
                            UsuarioResponse::ativo
                    ).containsExactly(
                            ID_VALIDO,
                            "Felipe",
                            request.email(),
                            "1234567890",
                            true
                    );
            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(usuarioRepository).existsByEmail(request.email());
            verify(usuarioMapper).toUpdate(request, entity);
            verify(usuarioMapper).toResponse(entity);

            verifyNoMoreInteractions(
                    usuarioRepository,
                    encoder,
                    usuarioMapper
            );
        }

        @Test
        @DisplayName("Deve atualizar o nome do usuário")
        void deveAtualizarNomeUsuario() {
            //Arrange
            var request = UsuarioTestContext
                    .atualizarUsuarioValido("Felipe2", LocalDate.of(1982, Month.JANUARY, 2), "felipesmacario@gmail.com");
            var entity = criarUsuarioPadrao();
            var response = UsuarioTestContext
                    .criaUsuarioResponse(ID_VALIDO, "Felipe2", "felipesmacario@gmail.com", "1234567890", true);


            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(usuarioMapper.toResponse(entity))
                    .thenReturn(response);

            //ACT
            var resultado = usuarioService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            UsuarioResponse::id,
                            UsuarioResponse::nome,
                            UsuarioResponse::email,
                            UsuarioResponse::cpf,
                            UsuarioResponse::ativo
                    ).containsExactly(
                            ID_VALIDO,
                            request.nome(),
                            "felipesmacario@gmail.com",
                            "1234567890",
                            true
                    );
            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(usuarioRepository, never()).existsByEmail(anyString());
            verify(usuarioMapper).toUpdate(request, entity);
            verify(usuarioMapper).toResponse(entity);

            verifyNoMoreInteractions(
                    usuarioRepository,
                    encoder,
                    usuarioMapper
            );
        }

        @Test
        @DisplayName("Deve lançar uma exceção não encontrar usuário")
        void deveLancarExcecaoNaoEncontrarUsuario() {
            //Arrange
            var request = usuarioAtualizacaoRequestPadrao();
            when(usuarioRepository.findByIdAndAtivoTrue(ID_INVALIDO))
                    .thenReturn(Optional.empty());
            //Act
            var excecao = assertThrows(NotFoundException.class,
                    () -> usuarioService.atualizar(request, ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(excecao, USUARIO, ID_INVALIDO);

            verify(usuarioRepository).findByIdAndAtivoTrue(ID_INVALIDO);
            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(
                    usuarioMapper,
                    encoder
            );
        }

        @Test
        @DisplayName("Deve lançar exceção de email unico")
        void deveLancarExcecaoAoAtualizarUmUsuarioPorEmail() {
            //Arrange
            var request = usuarioAtualizacaoRequestPadrao();
            var entity = criarUsuarioPadrao();

            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(usuarioRepository.existsByEmail(request.email()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.atualizar(request, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("O email informado já possui um cadastro.");

            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(usuarioRepository).existsByEmail(request.email());
            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(usuarioMapper);
        }
    }

    @Nested
    @DisplayName("Testes para atualizar o status do usuário")
    class AlterarStatus {

        @Test
        @DisplayName("Deve inativar o usuário")
        void deveInativarUsuario() {
            //Arrange
            var entity = criarUsuarioPadrao();
            var request = new StatusRequest(false);
            var response = criarUsuarioPadraoResponseInativo();

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(vendasRepository.existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO))
                    .thenReturn(false);

            when(vendasRepository.existsByComprador_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO))
                    .thenReturn(false);

            when(veiculoRepository.existsByVendedor_IdAndStatusVeiculo(ID_VALIDO, RESERVADO))
                    .thenReturn(false);

            when(veiculoRepository.findByVendedor_IdAndStatusVeiculo(ID_VALIDO, DISPONIVEL))
                    .thenReturn(Collections.emptyList());

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
            verify(vendasRepository).existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(vendasRepository).existsByComprador_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(veiculoRepository).existsByVendedor_IdAndStatusVeiculo(ID_VALIDO, RESERVADO);
            verify(veiculoRepository).findByVendedor_IdAndStatusVeiculo(ID_VALIDO, DISPONIVEL);
            verify(usuarioMapper).toResponse(entity);

            verifyNoMoreInteractions(usuarioRepository, usuarioMapper, vendasRepository, veiculoRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar usuário")
        void deveLancarExcecaoAoBuscarUsuario() {
            //Arrange
            var request = new StatusRequest(false);
            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> usuarioService.alterarStatus(ID_VALIDO, request));
            //Assert
            assertNotFoundResponseError(exception, USUARIO, ID_VALIDO);
            verify(usuarioRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(usuarioRepository);
            verifyNoInteractions(usuarioMapper, vendasRepository, veiculoRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao inativar usuário inativo")
        void deveLancarExcecaoAoInativarUsuarioInativo() {
            //Arrange
            var entity = criarUsuarioPadraoInativo();
            var request = new StatusRequest(false);

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(vendasRepository.existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO))
                    .thenReturn(false);

            when(vendasRepository.existsByComprador_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO))
                    .thenReturn(false);

            when(veiculoRepository.existsByVendedor_IdAndStatusVeiculo(ID_VALIDO, RESERVADO))
                    .thenReturn(false);

            when(veiculoRepository.findByVendedor_IdAndStatusVeiculo(ID_VALIDO, DISPONIVEL))
                    .thenReturn(Collections.emptyList());
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.alterarStatus(ID_VALIDO, request));

            //Assert
            assertBusinessResponseErrorInativa(exception, USUARIO);

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(vendasRepository).existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(vendasRepository).existsByComprador_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(veiculoRepository).existsByVendedor_IdAndStatusVeiculo(ID_VALIDO, RESERVADO);
            verify(veiculoRepository).findByVendedor_IdAndStatusVeiculo(ID_VALIDO, DISPONIVEL);

            verifyNoMoreInteractions(usuarioRepository, vendasRepository, veiculoRepository);

            verifyNoInteractions(usuarioMapper);
        }

        @Test
        @DisplayName("Deve ativar o usuário")
        void deveAtivarUsuario() {
            //Arrange
            var entity = criarUsuarioPadraoInativo();
            var request = new StatusRequest(true);
            var response = criarUsuarioPadraoResponse();

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(usuarioMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = usuarioService.alterarStatus(ID_VALIDO, request);
            //Assert
            assertThat(resultado.ativo()).isTrue();
            assertThat(entity.isAtivo()).isTrue();

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(usuarioMapper).toResponse(entity);

            verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
            verifyNoInteractions(vendasRepository, veiculoRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção de usuário já ativo")
        void deveLancarExcecaoUsuarioJaAtivo() {
            //Arrange
            var entity = criarUsuarioPadrao();
            var request = new StatusRequest(true);

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.alterarStatus(ID_VALIDO, request));
            //Assert
            assertBusinessResponseError(exception, USUARIO);

            verify(usuarioRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(usuarioRepository);
            verifyNoInteractions(vendasRepository, veiculoRepository, usuarioMapper);
        }
    }

    @Nested
    @DisplayName("Testes para vincular uma role ao usuário")
    class VincularRole {
        @Test
        @DisplayName("Deve vincular uma role ao usuário")
        void deveVincularUmaRoleAoUsuario() {
            //Arrange
            var entity = criarUsuarioPadrao();
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
                            "12345678901"
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
            assertThat(entity.getRoles())
                    .extracting(usuarioRole ->
                            usuarioRole.getRole().getId()
                    )
                    .containsExactlyInAnyOrder(1L, 2L);

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(rolesService).buscaRoles(listRoles);
            verify(usuarioMapper)
                    .toUsuarioRoleResponse(entity);
            verifyNoMoreInteractions(
                    usuarioRepository,
                    rolesService,
                    usuarioMapper
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

        @Test
        @DisplayName("Deve lançar exceção ao buscar usuário")
        void deveLancarExcecaoUsuario() {
            //Arrange
            var listRoles = List.of(1L, 2L);
            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> usuarioService.vincularRole(ID_VALIDO, listRoles));
            //Assert
            assertNotFoundResponseError(exception, USUARIO, ID_VALIDO);

            verify(usuarioRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(usuarioRepository);
            verifyNoInteractions(rolesService, usuarioMapper);
        }


    }

    @Nested
    @DisplayName("Testes para desvincular uma role")
    class DesvincularRole {
        @Test
        @DisplayName("Deve desvincular uma role")
        void deveDesvincularUmaRole() {
            //Arrange
            var idRole = 1L;
            var entity = criarUsuarioPadrao();
            criaListRole().forEach(role ->
                    entity.getRoles().add(new UsuarioRole(entity, role))
            );
            var role = criarRoleEntity();
            var response = criarUsuarioRolesResponseDesvincula();

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(rolesService.buscarPorId(idRole))
                    .thenReturn(role);

            when(usuarioMapper.toUsuarioRoleResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = usuarioService.desvincularRole(ID_VALIDO, idRole);
            //Assert
            assertThat(resultado.roles())
                    .extracting(RoleResponse::id)
                    .containsExactly(2L);

            assertThat(entity.getRoles())
                    .extracting(usuarioRole ->
                            usuarioRole.getRole().getId()
                    )
                    .containsExactly(2L);


            verify(usuarioRepository).findById(ID_VALIDO);
            verify(rolesService).buscarPorId(idRole);
            verify(usuarioMapper).toUsuarioRoleResponse(entity);

            verifyNoMoreInteractions(
                    usuarioRepository,
                    rolesService,
                    usuarioMapper
            );

        }

        @Test
        @DisplayName("Deve lançar exceção de usuario não encontrado ao desvincular role")
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

            when(rolesService.buscarPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(ROLE, ID_VALIDO));
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> usuarioService.desvincularRole(ID_VALIDO, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, ROLE, ID_VALIDO);

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(rolesService).buscarPorId(ID_VALIDO);

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

            when(rolesService.buscarPorId(idRole))
                    .thenReturn(role);
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> usuarioService.desvincularRole(ID_VALIDO, idRole));
            //Assert
            assertBusinessResponseError(excecao, "O usuário não possui uma role com o id informado.");

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(rolesService).buscarPorId(idRole);

            verifyNoMoreInteractions(usuarioRepository, rolesService);

            verifyNoInteractions(usuarioMapper);
        }
    }

    @Nested
    @DisplayName("Testes da busca de roles do usuário")
    class BuscarRole {
        @Test
        @DisplayName("Deve buscar uma role")
        void deveBuscarUmaRole() {
            //Arrange
            var entity = criarUsuarioPadrao();
            var role = UsuarioTestContext.criarRoleResponse(1L, "ROLE_USUARIO", true);
            var role2 = UsuarioTestContext.criarRoleResponse(2L, "ROLE_ADMIN", true);
            var listRoles = List.of(role, role2);
            var response = UsuarioTestContext.criarRoleUsuariosResponse(ID_VALIDO, "Felipe", "1234567890", listRoles);

            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(usuarioMapper.toUsuarioRoleResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = usuarioService.buscarRolesUsuario(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            UsuarioRolesResponse::id,
                            UsuarioRolesResponse::nome,
                            UsuarioRolesResponse::cpf
                    ).containsExactly(
                            ID_VALIDO,
                            "Felipe",
                            "1234567890"
                    );

            assertThat(resultado.roles())
                    .isNotEmpty()
                    .extracting(
                            RoleResponse::id,
                            RoleResponse::nome,
                            RoleResponse::ativo
                    ).containsExactly(
                            tuple(1L, "ROLE_USUARIO", true),
                            tuple(2L, "ROLE_ADMIN", true)
                    );

            verify(usuarioRepository).findById(ID_VALIDO);
            verify(usuarioMapper).toUsuarioRoleResponse(entity);

            verifyNoMoreInteractions(usuarioRepository, rolesService, usuarioMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar usuário")
        void deveLancarExcecaoQuandoUsuarioNaoExistente() {
            //Arrange
            when(usuarioRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> usuarioService.buscarRolesUsuario(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, USUARIO, ID_VALIDO);

            verify(usuarioRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(usuarioMapper, rolesService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração da senha")
    class AlterarSenha {
        @Test
        @DisplayName("Deve alterar a senha do usuário")
        void deveAlterarSenha() {
            // Arrange
            var request = new AlteracaoSenhaRequest(
                    "senhaAntiga",
                    "senhaNova"
            );

            var entity = criarUsuarioEntity();
            var passwordHashAnterior = entity.getPassword();
            var passwordHashNovo = "senhaCriptografada";

            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(encoder.matches(
                    request.senhaAntiga(),
                    passwordHashAnterior
            )).thenReturn(true);

            when(encoder.matches(
                    request.senhaNova(),
                    passwordHashAnterior
            )).thenReturn(false);

            when(encoder.encode(request.senhaNova()))
                    .thenReturn(passwordHashNovo);

            // Act
            var resultado = usuarioService.alterarSenha(
                    request,
                    ID_VALIDO
            );

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            AlteracaoSenhaResponse::email,
                            AlteracaoSenhaResponse::mensagem
                    )
                    .containsExactly(
                            "felipesmacario@gmail.com",
                            "Senha atualizada com sucesso"
                    );

            assertThat(entity.getPassword())
                    .isEqualTo(passwordHashNovo)
                    .isNotEqualTo(passwordHashAnterior);

            verify(usuarioRepository)
                    .findByIdAndAtivoTrue(ID_VALIDO);

            verify(encoder)
                    .matches(request.senhaAntiga(), passwordHashAnterior);

            verify(encoder)
                    .matches(request.senhaNova(), passwordHashAnterior);

            verify(encoder)
                    .encode(request.senhaNova());

            verifyNoMoreInteractions(usuarioRepository, encoder);
        }

        @Test
        @DisplayName("Deve lançar exceção de usuário não encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoExistente() {
            //Arrange
            var request = new AlteracaoSenhaRequest(
                    "senhaAntiga",
                    "senhaNova"
            );
            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> usuarioService.alterarSenha(request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, USUARIO, ID_VALIDO);
            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(encoder);
        }

        @Test
        @DisplayName("Deve lançar exceção de senha errada")
        void deveLancarExcecaoQuandoSenhaErrada() {
            //Arrange
            var entity = criarUsuarioPadrao();
            var request = new AlteracaoSenhaRequest(
                    "senhaAntiga",
                    "senhaNova"
            );
            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(encoder.matches(
                    request.senhaAntiga(),
                    entity.getPassword()
            )).thenReturn(false);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.alterarSenha(request, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("A senha informada não corresponde à senha atual.");

            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(encoder).matches(request.senhaAntiga(),
                    entity.getPassword());
            verify(encoder, never())
                    .encode(anyString());
            verifyNoMoreInteractions(usuarioRepository, encoder);
        }

        @Test
        @DisplayName("Deve lançar exceção quando a nova senha for a mesma que a anterior")
        void deveLancarExcecaoQuandoNovaSenhaMesmaAnterior() {
            //Arrange
            var entity = criarUsuarioPadrao();
            var request = new AlteracaoSenhaRequest(
                    "senhaAntiga",
                    "senhaNova"
            );
            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(encoder.matches(
                    request.senhaAntiga(),
                    entity.getPassword()
            )).thenReturn(true);

            when(encoder.matches(
                    request.senhaNova(),
                    entity.getPassword()
            )).thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.alterarSenha(request, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("A nova senha não pode ser igual à senha atual.");

            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(encoder).matches(request.senhaAntiga(),
                    entity.getPassword());
            verify(encoder)
                    .matches(request.senhaNova(), entity.getPassword());
            verify(encoder, never())
                    .encode(anyString());
            verifyNoMoreInteractions(usuarioRepository, encoder);
        }
    }

    @Nested
    @DisplayName("Testes da busca do usuário autenticado")
    class BuscarMeuUsuario {
        @Test
        @DisplayName("Deve buscar o usuário autenticado")
        void deveBuscarUsuarioAutenticado() {
            //Arrange
            var cx = new UsuarioTestContext();
            var entity = criarUsuarioPadrao();

            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(usuarioMapper.toResponse(entity))
                    .thenReturn(cx.response);
            //ACT
            var resultado = usuarioService.buscarMeuUsuario(ID_VALIDO);
            //Assert
            assertUsuarioResponse(resultado);

            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(usuarioMapper).toResponse(entity);
            verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção quando o usuário autenticado não estiver ativo")
        void deveLancarExcecaoQuandoUsuarioNaoEstiverAtivo() {
            // Arrange
            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.empty());

            // Act
            var exception = assertThrows(
                    NotFoundException.class,
                    () -> usuarioService.buscarMeuUsuario(ID_VALIDO)
            );

            // Assert
            assertNotFoundResponseError(
                    exception,
                    USUARIO,
                    ID_VALIDO
            );

            verify(usuarioRepository)
                    .findByIdAndAtivoTrue(ID_VALIDO);

            verifyNoInteractions(usuarioMapper);
            verifyNoMoreInteractions(usuarioRepository);
        }
    }

    @Nested
    @DisplayName("Testes para desativar o usuário autenticado")
    class DesativarUsuario {
        @Test
        @DisplayName("Deve desativar o usuário autenticado")
        void deveDesativarUsuarioAutenticado() {
            //Arrange
            var entity = criarUsuarioPadrao();
            var veiculo = VeiculoHelper.criarVeiculoEntity();
            veiculo.setStatusVeiculo(DISPONIVEL);
            var listVeiculo = List.of(veiculo);
            var response = criarUsuarioPadraoResponseInativo();

            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(vendasRepository.existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO))
                    .thenReturn(false);

            when(vendasRepository.existsByComprador_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO))
                    .thenReturn(false);

            when(veiculoRepository.existsByVendedor_IdAndStatusVeiculo(ID_VALIDO, RESERVADO))
                    .thenReturn(false);

            when(veiculoRepository.findByVendedor_IdAndStatusVeiculo(ID_VALIDO, DISPONIVEL))
                    .thenReturn(listVeiculo);

            when(usuarioMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = usuarioService.desativarUsuario(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(listVeiculo)
                    .extracting(Veiculo::getStatusVeiculo)
                    .containsOnly(PAUSADO);

            assertThat(resultado.ativo()).isFalse();
            assertThat(entity.isAtivo()).isFalse();

            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(vendasRepository).existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(vendasRepository).existsByComprador_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(veiculoRepository).existsByVendedor_IdAndStatusVeiculo(ID_VALIDO, RESERVADO);
            verify(veiculoRepository).findByVendedor_IdAndStatusVeiculo(ID_VALIDO, DISPONIVEL);
            verify(usuarioMapper).toResponse(entity);

            verifyNoMoreInteractions(usuarioRepository, usuarioMapper, vendasRepository, veiculoRepository);

        }

        @Test
        @DisplayName("Deve lançar exceção quando buscar usuário autenticado inativo")
        void deveLancarExcecaoQuandoBuscarUsuarioAutenticadoInativo() {
            //Arrange
            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> usuarioService.desativarUsuario(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, USUARIO, ID_VALIDO);
            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verifyNoMoreInteractions(usuarioRepository);

            verifyNoInteractions(usuarioMapper, vendasRepository, veiculoRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção venda em andamento")
        void deveLancarExcecaoVendaEmAndamento() {
            //Arrange
            var entity = criarUsuarioPadrao();

            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(vendasRepository.existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.desativarUsuario(ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("Não é possível desativar o usuário com uma ou mais vendas em andamento.");

            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(vendasRepository).existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);

            verifyNoMoreInteractions(usuarioRepository, vendasRepository);
            verifyNoInteractions(usuarioMapper, veiculoRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção compra em andamento")
        void deveLancarExcecaoCompraEmAndamento() {
            //Arrange
            var entity = criarUsuarioPadrao();

            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(vendasRepository.existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO))
                    .thenReturn(false);

            when(vendasRepository.existsByComprador_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.desativarUsuario(ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("Não é possível desativar o usuário com uma ou mais compras em andamento.");

            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(vendasRepository).existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(vendasRepository).existsByComprador_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);

            verifyNoMoreInteractions(usuarioRepository, vendasRepository);
            verifyNoInteractions(usuarioMapper, veiculoRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção veículo reservado")
        void deveLancarExcecaoVeiculoReservado() {
            //Arrange
            var entity = criarUsuarioPadrao();

            when(usuarioRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(vendasRepository.existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO))
                    .thenReturn(false);

            when(vendasRepository.existsByComprador_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO))
                    .thenReturn(false);

            when(veiculoRepository.existsByVendedor_IdAndStatusVeiculo(ID_VALIDO, RESERVADO))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.desativarUsuario(ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("Não é possível desativar um usuário com veículo reservado.");

            verify(usuarioRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(vendasRepository).existsByVendedor_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(vendasRepository).existsByComprador_IdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(veiculoRepository).existsByVendedor_IdAndStatusVeiculo(ID_VALIDO, RESERVADO);

            verifyNoMoreInteractions(usuarioRepository, vendasRepository, veiculoRepository);
            verifyNoInteractions(usuarioMapper);
        }
    }

    private Usuario criarUsuarioPadrao() {
        return UsuarioTestContext.criarUsuario(
                ID_VALIDO,
                "Felipe",
                "felipesmacario@gmail.com",
                "12345678901",
                "123456",
                true
        );
    }

    private Usuario criarUsuarioPadraoInativo() {
        return UsuarioTestContext.criarUsuario(
                ID_VALIDO,
                "Felipe",
                "felipesmacario@gmail.com",
                "12345678901",
                "123456",
                false
        );
    }

    private UsuarioResponse criarUsuarioPadraoResponse() {
        return UsuarioTestContext.criaUsuarioResponse(
                ID_VALIDO,
                "Felipe",
                "felipesmacario@gmail.com",
                "12345678901",
                true
        );
    }

    private UsuarioResponse criarUsuarioPadraoResponseInativo() {
        return UsuarioTestContext.criaUsuarioResponse(
                ID_VALIDO,
                "Felipe",
                "felipesmacario@gmail.com",
                "12345678901",
                false
        );
    }

    private UsuarioUpdateRequest usuarioAtualizacaoRequestPadrao() {
        return atualizarUsuarioValido("Felipe", LocalDate.of(1982, Month.JANUARY, 2), "felipe2macario@gmail.com");
    }
}
