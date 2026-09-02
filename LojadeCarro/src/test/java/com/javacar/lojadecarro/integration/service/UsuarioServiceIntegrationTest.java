package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.enums.Entidade;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.usuario.UsuarioTestContext;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import com.javacar.lojadecarro.service.UsuarioService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.ROLE;
import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.enums.StatusFiltro.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes de integração da service usuário")
public class UsuarioServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Nested
    @DisplayName("Testes de criação do usuário")
    class Criar {

        @Test
        @DisplayName("Deve criar o usuário")
        void deveCriarUsuario() {
            //Arrange
            var request = UsuarioTestContext.criarUsuarioValido();
            //ACT
            var response = usuarioService.criar(request);
            //Assert

            assertThat(response.id())
                    .isNotNull();

            assertThat(response)
                    .isNotNull()
                    .extracting(
                            UsuarioResponse::nome,
                            UsuarioResponse::cpf,
                            UsuarioResponse::email,
                            UsuarioResponse::ativo
                    ).containsExactly(
                            request.nome(),
                            request.cpf(),
                            request.email(),
                            true
                    );

            var usuario = usuarioRepository.findByEmail(request.email())
                    .orElseThrow();

            assertThat(usuario)
                    .extracting(
                            Usuario::getId,
                            Usuario::getDataCadastro
                    ).doesNotContainNull();

            assertThat(
                    encoder.matches(
                            request.password(),
                            usuario.getPassword()
                    )
            ).isTrue();
            assertThat(usuario.getCpf()).isEqualTo(request.cpf());
            assertThat(usuario.getEmail()).isEqualTo(request.email());
            assertThat(usuario.isAtivo())
                    .isTrue();
        }

        @Test
        @DisplayName("Deve validar o CPF unico")
        void deveLancarExcecaoQuandoCpfJaExistir() {
            //Arrange
            var request = UsuarioTestContext.criarUsuarioCPFRepetidoValido();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage("O CPF informado já possui um cadastro.");

        }

        @Test
        @DisplayName("Deve validar o email unico")
        void deveLancarExcecaoQuandoEmailJaExistir() {
            //Arrange
            var request = UsuarioTestContext.criarUsuarioEmailRepetidoValido();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage("O email informado já possui um cadastro.");

        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da listagem de usuários")
    class Listar {
        @Test
        @DisplayName("Deve listar usuários ativos")
        void deveListarUsuariosAtivos() {
            var response = usuarioService.listar(ATIVAS);
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(UsuarioResponse::ativo);
        }

        @Test
        @DisplayName("Deve listar usuários inativos")
        void deveListarUsuariosInativos() {
            var response = usuarioService.listar(INATIVAS);
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(u -> !u.ativo());
        }

        @Test
        @DisplayName("Deve listar todos os usuários")
        void deveListarTodosOsUsuarios() {
            var response = usuarioService.listar(TODAS);
            assertThat(response)
                    .filteredOn(UsuarioResponse::ativo)
                    .isNotEmpty();

            assertThat(response)
                    .filteredOn(u -> !u.ativo())
                    .isNotEmpty();
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da busca do usuário")
    class Buscar {
        @Test
        @DisplayName("Deve buscar o usuário")
        void deveBuscarUsuario() {
            var response = usuarioService.buscarPorId(1L);
            assertThat(response)
                    .isNotNull();

            assertThat(response)
                    .extracting(
                            UsuarioResponse::id,
                            UsuarioResponse::nome,
                            UsuarioResponse::email,
                            UsuarioResponse::cpf,
                            UsuarioResponse::ativo
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar usuário")
        void deveLancarExcecaoNaoEncontrarUsuario() {
            var exception = assertThrows(NotFoundException.class,
                    () -> usuarioService.buscarPorId(-1L));

            assertThat(exception)
                    .hasMessage(Entidade.USUARIO.naoEncontrada() + -1L);
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes para atualizar o usuário")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o usuário")
        void deveAtualizarUsuario() {
            //Arrange
            var request = UsuarioTestContext.atualizarUsuarioValido();
            //ACT
            var usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();
            var response = usuarioService.atualizar(request, usuario.getId());

            //Assert
            assertThat(response)
                    .isNotNull()
                    .extracting(
                            UsuarioResponse::nome,
                            UsuarioResponse::email
                    ).containsExactly(
                            request.nome(),
                            request.email()
                    );
        }


        @Test
        @DisplayName("Deve validar o email unico na atualização")
        void deveLancarExcecaoQuandoAtualizarComEmailJaExistir() {
            //Arrange
            var request = UsuarioTestContext.atualizarUsuarioEmailInvalido();
            //ACT
            var usuario = usuarioRepository.findByEmail("felipe.vendedor@gmail.com").orElseThrow();
            var usuarioId = usuario.getId();
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.atualizar(request, usuarioId));
            //Assert
            assertThat(exception)
                    .hasMessage("O email informado já possui um cadastro.");
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes para alterar o status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status para ativa")
        @Transactional
        void deveAlterarStatusAtiva() {
            var usuario = usuarioRepository.findByEmail("batmaimMorcegao@gmail.com").orElseThrow();
            usuarioService.alterarStatus(usuario.getId(), new StatusRequest(true));

            var usuarioAtualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();

            assertThat(usuarioAtualizado.isAtivo()).isTrue();
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar ativar um usuário já ativo")
        @Transactional
        void deveLancarExcecaoQuandoUsuarioJaAtivo() {
            var usuario = usuarioRepository.findByEmail("maria.santos@gmail.com").orElseThrow();
            var usuarioId = usuario.getId();
            var status = new StatusRequest(true);
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.alterarStatus(usuarioId, status));

            assertThat(exception)
                    .hasMessage(USUARIO.jaAtiva());
        }

        @Test
        @DisplayName("Deve alterar o status para inativa")
        @Transactional
        void deveAlterarStatusInativa() {
            var usuario = usuarioRepository.findByEmail("carlos.oliveira@gmail.com").orElseThrow();
            usuarioService.alterarStatus(usuario.getId(), new StatusRequest(false));

            var usuarioAtualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();

            assertThat(usuarioAtualizado.isAtivo()).isFalse();
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar inativar um usuário já inativo")
        @Transactional
        void deveLancarExcecaoQuandoUsuarioJainativo() {
            var usuario = usuarioRepository.findByEmail("robin@gmail.com").orElseThrow();
            var usuarioId = usuario.getId();
            var status = new StatusRequest(false);
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.alterarStatus(usuarioId, status));

            assertThat(exception)
                    .hasMessage(USUARIO.jaInativa());
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes para desvincular uma role")
    class DesvincularRole {
        @Test
        @DisplayName("Deve desvincular uma role")
        @Transactional
        void deveDesvincularRole() {
            var usuario = usuarioRepository.findByEmail("joao.silva@gmail.com").orElseThrow();
            var idRole = usuario.getRoles()
                    .stream()
                    .findFirst()
                    .stream()
                    .findFirst()
                    .map(role -> role.getRole().getId())
                    .orElseThrow();
            usuarioService.desvincularRole(usuario.getId(), idRole);

            var usuarioAtualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();

            assertThat(usuarioAtualizado.getRoles())
                    .hasSize(1)
                    .allMatch(role -> !role.getRole().getId().equals(idRole));
        }

        @Test
        @DisplayName("Deve lançar exceção de role inexistente")
        void deveLancarExcecaoQuandoRoleInexistente() {
            var usuario = usuarioRepository.findByEmail("joao.silva@gmail.com").orElseThrow();
            var usuarioId = usuario.getId();
            var exception = assertThrows(NotFoundException.class,
                    () -> usuarioService.desvincularRole(usuarioId, -1L));

            assertThat(exception)
                    .hasMessage(ROLE.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar remover exceção que o usuário não possui")
        @Transactional
        void deveLancarExcecaoRemoverRoleQueUsuarioNaoPossui() {
            var usuario = usuarioRepository.findByEmail("felipe.vendedor@gmail.com").orElseThrow();
            var usuarioId = usuario.getId();
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.desvincularRole(usuarioId, 1L));
            assertThat(exception)
                    .hasMessage("O usuário não possui uma role com o id informado.");

        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes para vincular uma role ao usuário")
    class VincularRole {
        @Test
        @DisplayName("Deve vincular a role ao usuário")
        @Transactional
        void deveVincularRoleAoUsuario() {
            var usuario = usuarioRepository.findByEmail("felipe.vendedor@gmail.com").orElseThrow();
            var idsRole = List.of(1L);
            usuarioService.vincularRole(usuario.getId(), idsRole);

            var usuarioAtualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();
            assertThat(usuarioAtualizado.getRoles())
                    .extracting(ur -> ur.getRole().getId())
                    .containsExactlyInAnyOrder(1L, 2L);
        }

        @Test
        @DisplayName("Deve lançar exceção de role inexistente")
        void deveLancarExcecaoQuandoRoleInexistente() {
            var usuario = usuarioRepository.findByEmail("felipe.vendedor@gmail.com").orElseThrow();
            var idsRole = List.of(1L, 2L, -1L);
            var usuarioId = usuario.getId();

            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.vincularRole(usuarioId, idsRole));

            assertThat(exception)
                    .hasMessage("Uma ou mais roles informadas não foram encontradas.");
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar uma role que o usuário já possui")
        void deveLancarExcecaoQuandoUsuarioJaPossuiRole() {
            var usuario = usuarioRepository.findByEmail("felipe.vendedor@gmail.com").orElseThrow();
            var idsRole = List.of(1L, 2L, 3L);
            var id = usuario.getId();

            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.vincularRole(id, idsRole));

            assertThat(exception)
                    .hasMessage(ROLE.jaAtiva());
        }
    }
}
