package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.UsuarioController;
import com.javacar.lojadecarro.dto.request.UsuarioUpdateRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.usuario.UsuarioTestContext;
import com.javacar.lojadecarro.service.UsuarioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc()
class UsuarioControllerTest extends BaseControllerTest {
    private static final String URL = "/usuarios";
    private static final String URL_ME = "/usuarios/me";

    @MockitoBean
    private UsuarioService usuarioService;

    @AfterEach
    void limparSecurityContext() {
        SecurityContextHolder.clearContext();
    }

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
            var resultado = performPostComAutenticacao(URL, cx.request, ID_JWT, ROLE_USUARIO);
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
            var resultado = performPostComAutenticacao(URL, cx.requestIncompleto, ID_JWT, ROLE_USUARIO);
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
            var resultado = performPostComAutenticacao(URL, cx.request, ID_JWT, ROLE_USUARIO);
            assertStatus500(resultado);

            verify(usuarioService).criar(cx.request);
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

            when(usuarioService.atualizar(
                    any(UsuarioUpdateRequest.class),
                    eq(ID_VALIDO)
            )).thenReturn(cx.response);
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ME, cx.request, ID_JWT, ROLE_USUARIO);
            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "15153769788"
            );

            verify(usuarioService).atualizar(any(UsuarioUpdateRequest.class),
                    eq(ID_VALIDO));
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao atualizar um usuário sem senha")
        void deveRetornar400aoAtualizarUmUsuarioSemSenha() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ME, cx.requestIncompleto, ID_JWT, ROLE_USUARIO);
            assertStatus400(resultado);

            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar um usuário com ID errado")
        void deveRetornar404aoAtualizarUmUsuarioComIDErrado() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.atualizar(any(UsuarioUpdateRequest.class),
                    eq(ID_VALIDO)))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ME, cx.request, ID_JWT, ROLE_USUARIO);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).atualizar(any(UsuarioUpdateRequest.class),
                    eq(ID_VALIDO));
            verifyNoMoreInteractions(usuarioService);
        }
    }

}
