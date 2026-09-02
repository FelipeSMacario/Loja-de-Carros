package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.usuario.UsuarioTestContext;
import com.javacar.lojadecarro.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.ROLE;
import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.enums.StatusFiltro.*;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.*;
import static com.javacar.lojadecarro.factory.usuario.UsuarioTestContext.criaUsuarioResponse;
import static com.javacar.lojadecarro.factory.usuario.UsuarioTestContext.criaUsuarioResponse2;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUsuarioController.class)
class AdminUsuarioControllerTest extends BaseControllerTest {
    private static final String URL = "/admin/usuarios";
    private static final String URL_ID = URL + "/" + ID_VALIDO;
    private static final String URL_STATUS = URL + "/" + ID_VALIDO + "/status";
    private static final String URL_ROLE = URL + "/" + ID_VALIDO + "/roles";
    private static final Long ID_ROLE = 2L;
    private static final String URL_ROLE_ID = URL_ROLE + "/" + ID_ROLE;

    @MockitoBean
    private UsuarioService usuarioService;

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar usuários ativos")
        void deveListarUsuariosAtivos() throws Exception {
            //Arrange
            var response1 = criaUsuarioResponse(true);
            var response2 = criaUsuarioResponse2(true);

            var response = List.of(response1, response2);

            when(usuarioService.listar(ATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, "status", ATIVAS.toString(), ID_JWT, ROLE_ADM);
            assertListUsuario(resultado,
                    ID_VALIDO,
                    2L,
                    "Felipe",
                    "Goku",
                    true,
                    true,
                    "felipesmacario@gmail.com",
                    "goku@gmail.com",
                    "12345678901",
                    "12345678901"
            );

            verify(usuarioService).listar(ATIVAS);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve listar usuários inativos")
        void deveListarUsuariosInativos() throws Exception {
            //Arrange
            var response1 = criaUsuarioResponse(false);
            var response2 = criaUsuarioResponse2(false);

            var response = List.of(response1, response2);

            when(usuarioService.listar(INATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, "status", INATIVAS.toString(), ID_JWT, ROLE_ADM);
            assertListUsuario(resultado,
                    ID_VALIDO,
                    2L,
                    "Felipe",
                    "Goku",
                    false,
                    false,
                    "felipesmacario@gmail.com",
                    "goku@gmail.com",
                    "12345678901",
                    "12345678901"
            );

            verify(usuarioService).listar(INATIVAS);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve encaminhar o status informado para a service")
        void deveEncaminharStatusTodas() throws Exception {
            //Arrange
            var response1 = criaUsuarioResponse(true);
            var response2 = criaUsuarioResponse2(false);

            var response = List.of(response1, response2);

            when(usuarioService.listar(TODAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_ADM);
            assertListUsuario(resultado,
                    ID_VALIDO,
                    2L,
                    "Felipe",
                    "Goku",
                    true,
                    false,
                    "felipesmacario@gmail.com",
                    "goku@gmail.com",
                    "12345678901",
                    "12345678901"
            );
            verify(usuarioService).listar(TODAS);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 401 na listagem")
        void deveRetornar401NaListagem() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performGet(URL);
            assertStatus401(resultado);
            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 403 na listagem")
        void deveRetornar403NaListagem() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_USUARIO);
            assertStatus403(resultado);
            verifyNoInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da busca por ID")
    class Buscar {

        @Test
        @DisplayName("Deve buscar um usuário por ID")
        void deveBuscarUmUsuarioPorID() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.buscarPorId(ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "12345678901"
            );

            verify(usuarioService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve buscar um usuário inativo")
        void deveBuscarUmUsuarioInativo() throws Exception {
            //Arrange
            var response = criarUsuarioPadraoResponseInativo();

            when(usuarioService.buscarPorId(ID_VALIDO))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    false,
                    "felipesmacario@gmail.com",
                    "12345678901"
            );

            verify(usuarioService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar um usuário por ID")
        void deveRetornar404aoBuscarUmUsuarioPorID() throws Exception {
            //Arrange
            when(usuarioService.buscarPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();
            var status = new StatusRequest(true);

            when(usuarioService.alterarStatus(ID_VALIDO, status))
                    .thenReturn(cx.response);
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_STATUS, status, ID_JWT, ROLE_ADM);
            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "12345678901"
            );

            verify(usuarioService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao alterar status")
        void deveLancar400AoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(null);
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_STATUS, status, ID_JWT, ROLE_ADM);

            assertStatus400(resultado);
            verifyNoInteractions(usuarioService);

        }

        @Test
        @DisplayName("Deve lançar 404 ao alterar status")
        void deveLancar404aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            when(usuarioService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_STATUS, status, ID_JWT, ROLE_ADM);
            assertStatus404(resultado,
                    USUARIO,
                    ID_VALIDO);

            verify(usuarioService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da busca de roles por id do usuário")
    class BuscaRole {
        @Test
        @DisplayName("Deve buscar as roles por usuário")
        void deveBuscarRolesPorUsuario() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();
            when(usuarioService.buscarRolesUsuario(ID_VALIDO))
                    .thenReturn(cx.usuarioRolesResponse);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ROLE, ID_JWT, ROLE_ADM);
            assertUsuarioRole(resultado, ID_VALIDO, "Felipe Soares Macário", "12345678901");

            verify(usuarioService).buscarRolesUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);

        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar roles por usuário")
        void deveRetornar404AoBuscarRolesPorUsuario() throws Exception {
            //Arrange
            when(usuarioService.buscarRolesUsuario(ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ROLE, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).buscarRolesUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);

        }
    }

    @Nested
    @DisplayName("Testes da vinculação da role")
    class VincularRole {
        @Test
        @DisplayName("Deve vincular uma role")
        void deveVincularUmaRole() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.vincularRole(ID_VALIDO, cx.listRoles.roles()))
                    .thenReturn(cx.usuarioRolesResponse);
            //Act + Assert
            var resultado = performPostComAutenticacao(URL_ROLE, cx.listRoles, ID_JWT, ROLE_ADM);
            assertUsuarioRole(resultado, ID_VALIDO, "Felipe Soares Macário", "12345678901");

            verify(usuarioService).vincularRole(ID_VALIDO, cx.listRoles.roles());
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao vincular uma role")
        void deveLancar400AoVincularUmaRole() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            //Act + Assert
            var resultado = performPostComAutenticacao(URL_ROLE, cx.listRolesIncompleta, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao vincular uma role")
        void deveLancar404AoVincularUmaRole() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.vincularRole(ID_VALIDO, cx.listRoles.roles()))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performPostComAutenticacao(URL_ROLE, cx.listRoles, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).vincularRole(ID_VALIDO, cx.listRoles.roles());
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao vincular uma role")
        void deveLancar500AoVincularUmaRole() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.vincularRole(ID_VALIDO, cx.listRoles.roles()))
                    .thenThrow(new RuntimeException("Erro inesperado"));

            //Act + Assert
            var resultado = performPostComAutenticacao(URL_ROLE, cx.listRoles, ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(usuarioService).vincularRole(ID_VALIDO, cx.listRoles.roles());
            verifyNoMoreInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da desvinculação de uma role")
    class DesvincularRole {
        @Test
        @DisplayName("Deve desvincular uma role")
        void deveDesvincularUmaRole() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.desvincularRole(ID_VALIDO, ID_ROLE))
                    .thenReturn(cx.usuarioRolesResponse);

            //Act + Assert
            var resultado = performDeleteComAutenticacao(URL_ROLE_ID, ID_JWT, ROLE_ADM);
            assertUsuarioRole(resultado, ID_VALIDO, "Felipe Soares Macário", "12345678901");

            verify(usuarioService).desvincularRole(ID_VALIDO, ID_ROLE);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao desvincular uma role com ID do usuário invalido")
        void deveLancar404IdUsuarioIncorreto() throws Exception {
            //Arrange
            when(usuarioService.desvincularRole(ID_VALIDO, ID_ROLE))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));

            //Act + Assert
            var resultado = performDeleteComAutenticacao(URL_ROLE_ID, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).desvincularRole(ID_VALIDO, ID_ROLE);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao desvincular uma role com ID da role invalida")
        void deveLancar404IdRoleIncorreta() throws Exception {
            //Arrange
            when(usuarioService.desvincularRole(ID_VALIDO, ID_ROLE))
                    .thenThrow(new NotFoundException(ROLE, ID_ROLE));

            //Act + Assert
            var resultado = performDeleteComAutenticacao(URL_ROLE_ID, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, ROLE, ID_ROLE);

            verify(usuarioService).desvincularRole(ID_VALIDO, ID_ROLE);
            verifyNoMoreInteractions(usuarioService);
        }

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

}
