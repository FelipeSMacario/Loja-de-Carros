package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.usuario.UsuarioTestContext;
import com.javacar.lojadecarro.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.ROLE;
import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.*;
import static com.javacar.lojadecarro.factory.usuario.UsuarioTestContext.criaUsuarioResponse;
import static com.javacar.lojadecarro.factory.usuario.UsuarioTestContext.criaUsuarioResponse2;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest extends BaseControllerTest {
    private static final String URL = "/usuarios";
    private static final String URL_ID = URL + "/" + ID_VALIDO;
    private static final String URL_ROLE = URL + "/" + ID_VALIDO + "/roles";
    private static final Long ID_ROLE = 2L;

    @MockitoBean
    private UsuarioService usuarioService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {
        @Test
        @DisplayName("Deve criar um usuário")
        void deveCriarUsuario() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.criar(cx.request))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPost(URL, cx.request);
            assertUsuario(
                    resultado,
                    status().isCreated(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "15153769788"
            );
            resultado.andExpect(header().exists("Location"));

            verify(usuarioService).criar(cx.request);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao criar um usuário")
        void deveRetornar400aoCriarUmUsuario() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();
            //Act + Assert
            var resultado = performPost(URL, cx.requestIncompleto);
            assertStatus400(resultado);

            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao acontecer um erro inesperado")
        void deveRetornar500aoAcontecerUmErro() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.criar(cx.request))
                    .thenThrow(new RuntimeException("Erro inesperado"));

            //Act + Assert
            var resultado = performPost(URL, cx.request);
            assertStatus500(resultado);

            verify(usuarioService).criar(cx.request);
            verifyNoMoreInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve utilizar ATIVAS como status padrão")
        void deveUtilizarAtivasComoStatusPadrao() throws Exception {
            //Arrange
            var response1 = criaUsuarioResponse(true);
            var response2 = criaUsuarioResponse2(true);

            var response = List.of(response1, response2);

            when(usuarioService.listar(ATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL);
            assertListUsuario(resultado,
                    ID_VALIDO,
                    2L,
                    "Felipe",
                    "Goku",
                    true,
                    true,
                    "felipesmacario@gmail.com",
                    "goku@gmail.com",
                    "15153769788",
                    "1234567890"
            );

            verify(usuarioService).listar(ATIVAS);
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
            var resultado = performGet(URL, "status", TODAS.toString());
            assertListUsuario(resultado,
                    ID_VALIDO,
                    2L,
                    "Felipe",
                    "Goku",
                    true,
                    false,
                    "felipesmacario@gmail.com",
                    "goku@gmail.com",
                    "15153769788",
                    "1234567890"
            );
            verify(usuarioService).listar(TODAS);
            verifyNoMoreInteractions(usuarioService);
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
            var resultado = performGet(URL_ID);
            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "15153769788"
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
            var resultado = performGet(URL_ID);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da atualização")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar um usuário")
        void deveAtualizarUmUsuario() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.atualizar(cx.request, ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPut(URL_ID, cx.request);
            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "15153769788"
            );

            verify(usuarioService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao atualizar um usuário sem senha")
        void deveRetornar400aoAtualizarUmUsuarioSemSenha() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();
            //Act + Assert
            var resultado = performPut(URL_ID, cx.requestIncompleto);
            assertStatus400(resultado);

            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar um usuário com ID errado")
        void deveRetornar404aoAtualizarUmUsuarioComIDErrado() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.atualizar(cx.request, ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performPut(URL_ID, cx.request);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).atualizar(cx.request, ID_VALIDO);
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
            var resultado = performPatch(URL_ID + "/status", status);
            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "15153769788"
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
            var resultado = performPatch(URL_ID + "/status", status);

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
            var resultado = performPatch(URL_ID + "/status", status);
            assertStatus404(resultado,
                    USUARIO,
                    ID_VALIDO);

            verify(usuarioService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da vinculação da role")
    class VinculaRole {
        @Test
        @DisplayName("Deve vincular uma role")
        void deveVincularUmaRole() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.vincularRole(ID_VALIDO, cx.listRoles.roles()))
                    .thenReturn(cx.usuarioRolesResponse);
            //Act + Assert
            var resultado = performPatch(URL_ROLE, cx.listRoles);
            assertUsuarioRole(resultado, ID_VALIDO, "Felipe Soares Macário", "1234567890");

            verify(usuarioService).vincularRole(ID_VALIDO, cx.listRoles.roles());
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao vincular uma role")
        void deveLancar400AoVincularUmaRole() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            //Act + Assert
            var resultado = performPatch(URL_ROLE, cx.listRolesIncompleta);
            assertStatus400(resultado);

            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao para um ID do usuário invalido")
        void deveLancar400AoInserirIdUsuarioInvalido() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            //Act + Assert
            var resultado = performPatch(URL + "/A/roles", cx.listRoles.roles());
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
            var resultado = performPatch(URL_ROLE, cx.listRoles);
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
            var resultado = performPatch(URL_ROLE, cx.listRoles);
            assertStatus500(resultado);

            verify(usuarioService).vincularRole(ID_VALIDO, cx.listRoles.roles());
            verifyNoMoreInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da desvinculação de uma role")
    class DesvinculaRole {
        @Test
        @DisplayName("Deve desvincular uma role")
        void deveDesvincularUmaRole() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.desvincularRole(ID_VALIDO, ID_ROLE))
                    .thenReturn(cx.usuarioRolesResponse);

            //Act + Assert
            var resultado = performDelete(URL_ROLE + "/" + ID_ROLE);
            assertUsuarioRole(resultado, ID_VALIDO, "Felipe Soares Macário", "1234567890");

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
            var resultado = performDelete(URL_ROLE + "/" + ID_ROLE);
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
            var resultado = performDelete(URL_ROLE + "/" + ID_ROLE);
            assertStatus404(resultado, ROLE, ID_ROLE);

            verify(usuarioService).desvincularRole(ID_VALIDO, ID_ROLE);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao desvincular uma role com ID role invalido")
        void deveLancar400AoInserirIdRoleInvalido() throws Exception {
            //Arrange
            when(usuarioService.desvincularRole(ID_VALIDO, ID_ROLE))
                    .thenThrow(new NotFoundException(ROLE, ID_ROLE));

            //Act + Assert
            var resultado = performDelete(URL_ROLE + "/" + "A");
            assertStatus400(resultado);

            verifyNoInteractions(usuarioService);
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
            var resultado = performGet(URL_ROLE);
            assertUsuarioRole(resultado, ID_VALIDO, "Felipe Soares Macário", "1234567890");

            verify(usuarioService).buscarRolesUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);

        }

        @Test
        @DisplayName("Deve lançar 404 ao buscar roles por usuário")
        void deveLancarBuscarRolesPorUsuario() throws Exception {
            //Arrange
            when(usuarioService.buscarRolesUsuario(ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performGet(URL_ROLE);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).buscarRolesUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);

        }
    }
}
