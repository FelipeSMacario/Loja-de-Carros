package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.request.UsuarioUpdateRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.enums.Entidade;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.usuario.UsuarioTestContext;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.integration.fixture.VendaIntegrationFixture;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import com.javacar.lojadecarro.service.UsuarioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.enums.StatusFiltro.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@Import(VendaIntegrationFixture.class)
@DisplayName("Testes de integração da service usuário")
public class UsuarioServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VendaIntegrationFixture vendaIntegrationFixture;

    @PersistenceContext
    private EntityManager entityManager;

    private static final String SUBJECT = "keycloak-sub-usuario-1";
    private static final String EMAIL = "usuario@email.com";

    @Nested
    @DisplayName("Testes de criação do usuário")
    class Criar {

        @Test
        @DisplayName("Deve criar o usuário")
        void deveCriarUsuario() {
            //Arrange
            var request = UsuarioTestContext.criarUsuarioValido();
            //ACT
            var response = usuarioService.criar(request, SUBJECT, EMAIL);
            entityManager.flush();
            entityManager.clear();
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
                            EMAIL,
                            true
                    );

            var usuario = usuarioRepository.findByEmail(EMAIL)
                    .orElseThrow();

            assertThat(usuario)
                    .extracting(
                            Usuario::getId,
                            Usuario::getDataCadastro
                    ).doesNotContainNull();

            assertThat(usuario.getCpf()).isEqualTo(request.cpf());
            assertThat(usuario.getEmail()).isEqualTo(EMAIL);
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
                    () -> usuarioService.criar(request, SUBJECT, EMAIL));
            //Assert
            assertThat(exception)
                    .hasMessage("O CPF informado já possui um cadastro.");

        }

        @Test
        @DisplayName("Deve validar o email unico")
        void deveLancarExcecaoQuandoEmailJaExistir() {
            //Arrange
            var usuario = vendaIntegrationFixture.criarUsuarioPersistido("USUARIO 1", "14569874122", "usuario1@email.com");
            entityManager.flush();
            entityManager.clear();
            var request =new UsuarioRequest("USUARIO 2", "12965478122", LocalDate.now());
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> usuarioService.criar(request, SUBJECT, usuario.getEmail()));
            //Assert
            assertThat(exception)
                    .hasMessage("O email informado já possui um cadastro.");

        }

        @Test
        @DisplayName("Deve buscar usuário pelo identificador do provedor de identidade")
        void deveBuscarUsuarioPeloIdentityProviderId() {
            //Arrange
            var usuario = vendaIntegrationFixture.criarUsuarioPersistido("USUARIO 1", "14569874122", "usuario1@email.com");
            entityManager.flush();
            entityManager.clear();
            //ACT
            var usuarioPorId = usuarioRepository.findByIdentityProviderId(usuario.getIdentityProviderId()).orElseThrow();
            //Assert
            assertThat(usuarioPorId)
                    .extracting(
                            Usuario::getId,
                            Usuario::getEmail,
                            Usuario::getCpf,
                            Usuario::getIdentityProviderId
                    )
                    .containsExactly(
                            usuario.getId(),
                            usuario.getEmail(),
                            usuario.getCpf(),
                            usuario.getIdentityProviderId()
                    );
        }

        @Test
        @DisplayName("Deve retornar vazio quando o identificador do provedor não existir")
        void deveRetornarVazioQuandoIdentityProviderIdNaoExistir() {
            //Arrange
            //ACT
            var usuarioPorId = usuarioRepository.findByIdentityProviderId("-99");
            //Assert
            assertThat(usuarioPorId)
                    .isEmpty();
        }

        @Test
        @DisplayName("Não deve permitir identificador do provedor duplicado")
        void naoDevePermitirIdentityProviderIdDuplicado() {
            //Arrange
            var usuario = vendaIntegrationFixture.criarUsuarioPersistido("USUARIO 1", "14569874122", "usuario1@email.com");
            entityManager.flush();
            entityManager.clear();

            var usuarioNovo = vendaIntegrationFixture.criarUsuarioPersistido("USUARIO 2", "14569874121", "usuario2@email.com");
            usuarioNovo.setIdentityProviderId(usuario.getIdentityProviderId());
            //ACT + Assert
            assertThrows(DataIntegrityViolationException.class,
                    () -> usuarioRepository.saveAndFlush(usuarioNovo));
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
            var usuario = vendaIntegrationFixture.criarUsuarioPersistido("USUARIO 1", "14569874122", "usuario1@email.com");
            entityManager.flush();
            entityManager.clear();


            var request = new UsuarioUpdateRequest("USUARIO 2", usuario.getDataNascimento());
            //ACT
            var response = usuarioService.atualizar(request, usuario.getId());
            entityManager.flush();
            entityManager.clear();

            var usuarioAtualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();

            //Assert
            assertThat(response)
                    .isNotNull()
                    .extracting(
                            UsuarioResponse::nome,
                            UsuarioResponse::cpf,
                            UsuarioResponse::ativo)
                    .containsExactly(
                            request.nome(),
                            usuarioAtualizado.getCpf(),
                            usuarioAtualizado.isAtivo()
                    );
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes para alterar o status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status para ativa")
        void deveAlterarStatusAtiva() {
            var usuario = usuarioRepository.findByEmail("batmaimMorcegao@gmail.com").orElseThrow();
            usuarioService.alterarStatus(usuario.getId(), new StatusRequest(true));

            var usuarioAtualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();

            assertThat(usuarioAtualizado.isAtivo()).isTrue();
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar ativar um usuário já ativo")
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
        void deveAlterarStatusInativa() {
            var usuario = usuarioRepository.findByEmail("carlos.oliveira@gmail.com").orElseThrow();
            usuarioService.alterarStatus(usuario.getId(), new StatusRequest(false));

            var usuarioAtualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();

            assertThat(usuarioAtualizado.isAtivo()).isFalse();
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar inativar um usuário já inativo")
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


}
