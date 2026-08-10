package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.request.LoginRequest;
import com.javacar.lojadecarro.exception.security.LoginSenhaException;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.service.LoginService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes da service do login")
public class LoginServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private LoginService loginService;

    @Nested
    @DisplayName("Testes de autenticação")
    @Transactional
    class Autenticar {
        @Test
        @DisplayName("Deve autenticar")
        void deveAutenticar() {
            //Arrange
            var request = new LoginRequest(
                    "felipe.vendedor@gmail.com",
                    "123"
            );
            //ACT
            var response = loginService.autenticar(request);
            //Assert
//            assertThat(response)
//                    .isNotNull()
//                    .extracting(UsuarioResponse::email)
//                    .isEqualTo(request.login());


        }

        @Test
        @DisplayName("Deve lançar exceção de senha invalida")
        void deveLancarExcecaoDeSenhaInvalida() {
            //Arrange
            var request = new LoginRequest(
                    "felipe.vendedor@gmail.com",
                    "1233");
            //ACT
            var exception = assertThrows(BadCredentialsException.class,
                    () -> loginService.autenticar(request));
            //Assert
            assertThat(exception)
                    .hasMessage("Bad credentials");
        }

        @Test
        @DisplayName("Deve lançar exceção de usuario invalido")
        void deveLancarExcecaoDeUsuarioInvalido() {
            //Arrange
            var request = new LoginRequest(
                    "felipes.vendedor@gmail.com",
                    "123");
            //ACT
            var exception = assertThrows(BadCredentialsException.class,
                    () -> loginService.autenticar(request));
            //Assert
            assertThat(exception)
                    .hasMessage("Bad credentials");
        }

    }

}
