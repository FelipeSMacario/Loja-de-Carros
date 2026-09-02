package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.exception.security.LoginSenhaException;
import com.javacar.lojadecarro.factory.usuario.LoginTestContext;
import com.javacar.lojadecarro.service.LoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertStatus400;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertStatus401ProblemDetail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller de login")
class LoginControllerTest extends BaseControllerTest {
    private static final String URL = "/login";

    @MockitoBean
    private LoginService loginService;

    @Nested
    @DisplayName("Testes de autenticação")
    class Autenticacao {
        @Test
        @DisplayName("Deve logar com sucesso")
        void logarSucesso() throws Exception {
            //Arrange
            var cx = new LoginTestContext();

            when(loginService.autenticar(cx.request))
                    .thenReturn(cx.loginResponse);
            //Act + Assert
            var resultado = performPost(URL, cx.request);
            resultado.andExpect(status().isOk());

            verify(loginService).autenticar(cx.request);
            verifyNoMoreInteractions(loginService);
        }

        @Test
        @DisplayName("Deve lançar 401 quando as credenciais forem inválidas")
        void deveLancar401AoInserirCredenciaisInvalidas() throws Exception {
            //Arrange
            var cx = new LoginTestContext();

            when(loginService.autenticar(cx.request))
                    .thenThrow(new LoginSenhaException());
            //Act + Assert
            var resultado = performPost(URL, cx.request);
            assertStatus401ProblemDetail(resultado);

            verify(loginService).autenticar(cx.request);

            verifyNoMoreInteractions(loginService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao nao informar a senha")
        void deveLancar400AoNaoInformarSenha() throws Exception {
            //Arrange
            var cx = new LoginTestContext();
            //Act + Assert
            var resultado = performPost(URL, cx.requestIncompleta);
            assertStatus400(resultado);

            verifyNoInteractions(loginService);
        }
    }
}
