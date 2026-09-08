package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.UsuarioController;
import com.javacar.lojadecarro.dto.request.UsuarioUpdateRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.exception.security.UsuarioNaoVinculadoException;
import com.javacar.lojadecarro.factory.usuario.UsuarioTestContext;
import com.javacar.lojadecarro.security.service.UsuarioAutenticadoService;
import com.javacar.lojadecarro.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.*;
import static com.javacar.lojadecarro.factory.usuario.UsuarioTestContext.atualizarUsuarioValido;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest extends BaseControllerTest {
    private static final String URL = "/usuarios";
    private static final String URL_ME = "/usuarios/me";
    private static final String URL_ME_DESATIVAR = URL_ME + "/desativar";
    private static final String SUBJECT = "keycloak-sub-usuario-1";
    private static final String EMAIL = "usuario@email.com";

    @MockitoBean
    private UsuarioService usuarioService;
    @MockitoBean
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {
        @Test
        @DisplayName("Deve criar um usuário")
        void deveCriarUsuario() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.criar(cx.request, SUBJECT, EMAIL))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPostComAutenticacao(
                    URL,
                    cx.request,
                    SUBJECT,
                    ROLE_ADM,
                    jwt -> jwt.claim(
                            "email",
                            EMAIL
                    )
            );
            assertUsuario(
                    resultado,
                    status().isCreated(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "12345678901"
            );
            resultado.andExpect(
                    header().string(
                            "Location",
                            "http://localhost/usuarios/" + ID_VALIDO
                    )
            );

            verify(usuarioService).criar(cx.request, SUBJECT, EMAIL);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao criar um usuário")
        void deveRetornar400AoCriarUmUsuario() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();
            //Act + Assert
            var resultado = performPostComAutenticacao(
                    URL,
                    cx.requestIncompleto,
                    SUBJECT,
                    ROLE_ADM,
                    jwt -> jwt.claim(
                            "email",
                            "felipesmacario@gmail.com"
                    )
            );
            assertStatus400(resultado);

            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao acontecer um erro inesperado")
        void deveRetornar500aoAcontecerUmErro() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.criar(cx.request, SUBJECT, EMAIL))
                    .thenThrow(new RuntimeException("Erro inesperado"));

            //Act + Assert
            var resultado = performPostComAutenticacao(
                    URL,
                    cx.request,
                    SUBJECT,
                    ROLE_ADM,
                    jwt -> jwt.claim(
                            "email",
                            EMAIL
                    )
            );
            assertStatus500(resultado);

            verify(usuarioService).criar(cx.request, SUBJECT, EMAIL);
            verifyNoMoreInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da busca do usuário autenticado")
    class BuscarUsuarioAutenticado {
        @Test
        @DisplayName("Deve buscar o usuário autenticado")
        void deveBuscarUsuarioAutenticado() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioAutenticadoService.buscarId(ID_JWT))
                    .thenReturn(ID_VALIDO);

            when(usuarioService.buscarMeuUsuario(ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ME, ID_JWT, ROLE_USUARIO);

            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "12345678901"
            );

            verify(usuarioAutenticadoService).buscarId(ID_JWT);
            verify(usuarioService).buscarMeuUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar o usuário autenticado")
        void deveRetornar404AoBuscarUmUsuario() throws Exception {
            //Arrange
            when(usuarioAutenticadoService.buscarId(ID_JWT))
                    .thenReturn(ID_VALIDO);

            when(usuarioService.buscarMeuUsuario(ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ME, ID_JWT, ROLE_USUARIO);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioAutenticadoService).buscarId(ID_JWT);
            verify(usuarioService).buscarMeuUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao buscar o usuário não autenticado")
        void deveRetornar401aoBuscarUsuarioNaoAutenticado() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performGet(URL_ME);
            assertStatus401(exception);
            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 403 quando o usuário autenticado não possuir vínculo local")
        void deveRetornar403QuandoUsuarioAutenticadoNaoPossuirVinculoLocal()
                throws Exception {

            when(usuarioAutenticadoService.buscarId(ID_JWT))
                    .thenThrow(new UsuarioNaoVinculadoException());

            var resultado = performGetComAutenticacao(
                    URL_ME,
                    ID_JWT,
                    ROLE_USUARIO
            );

            assertStatus403Autenticacao(resultado);

            verify(usuarioAutenticadoService).buscarId(ID_JWT);
            verifyNoInteractions(usuarioService);
            verifyNoMoreInteractions(usuarioAutenticadoService);
        }
    }

    @Nested
    @DisplayName("Testes de desativar o usuário autenticado")
    class DesativarUsuarioAutenticado {
        @Test
        @DisplayName("Deve desativar o usuário autenticado")
        void deveDesativarUsuarioAutenticado() throws Exception {
            //Arrange
            var response = criarUsuarioPadraoResponseInativo();

            when(usuarioAutenticadoService.buscarId(ID_JWT))
                    .thenReturn(ID_VALIDO);
            when(usuarioService.desativarUsuario(ID_VALIDO))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_ME_DESATIVAR, ID_JWT, ROLE_USUARIO);
            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    false,
                    "felipesmacario@gmail.com",
                    "12345678901"
            );
            verify(usuarioAutenticadoService).buscarId(ID_JWT);
            verify(usuarioService).desativarUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao não encontrar o usuário para desativar")
        void deveRetornar404AoDesativarUsuarioNaoEncontrado() throws Exception {
            //Arrange
            when(usuarioAutenticadoService.buscarId(ID_JWT))
                    .thenReturn(ID_VALIDO);
            when(usuarioService.desativarUsuario(ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_ME_DESATIVAR, ID_JWT, ROLE_USUARIO);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioAutenticadoService).buscarId(ID_JWT);
            verify(usuarioService).desativarUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao desativar o usuário não autenticado")
        void deveRetornar401AoDesativarUsuarioSemAutenticacao() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performPatch(URL_ME_DESATIVAR);
            assertStatus401(exception);
            verifyNoInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da atualização")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar um usuário")
        void deveAtualizarUsuario() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();
            var request = atualizarUsuarioValido();

            when(usuarioAutenticadoService.buscarId(ID_JWT))
                    .thenReturn(ID_VALIDO);
            when(usuarioService.atualizar(
                    request,
                    ID_VALIDO
            )).thenReturn(cx.response);
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ME, request, ID_JWT, ROLE_USUARIO);
            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "12345678901"
            );

            verify(usuarioAutenticadoService).buscarId(ID_JWT);
            verify(usuarioService).atualizar(request,
                    ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao atualizar usuário com dados inválidos")
        void deveRetornar400aoAtualizarUmUsuarioSemSenha() throws Exception {
            //Arrange
            var request = new UsuarioUpdateRequest(null, null, null);
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ME, request, ID_JWT, ROLE_USUARIO);
            assertStatus400(resultado);

            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar um usuário com ID errado")
        void deveRetornar404aoAtualizarUmUsuarioComIDErrado() throws Exception {
            //Arrange
            var request = atualizarUsuarioValido();
            when(usuarioAutenticadoService.buscarId(ID_JWT))
                    .thenReturn(ID_VALIDO);
            when(usuarioService.atualizar(request,
                    ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ME, request, ID_JWT, ROLE_USUARIO);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioAutenticadoService).buscarId(ID_JWT);
            verify(usuarioService).atualizar(request,
                    ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao atualizar usuário sem autenticação")
        void deveRetornar401AoAtualizarUsuarioSemAutenticacao() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performPut(URL_ME, atualizarUsuarioValido());

            assertStatus401(exception);
            verifyNoInteractions(usuarioService);
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
