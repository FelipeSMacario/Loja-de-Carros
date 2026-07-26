package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.LoginRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.exception.security.LoginSenhaException;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import com.javacar.lojadecarro.repository.LoginRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioEntity;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioResponse;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private LoginRepository loginRepository;
    @Mock
    private BCryptPasswordEncoder encoder;
    @InjectMocks
    private LoginService loginService;

    private static final String PASSWORD = "123456";
    private static final String EMAIL = "felipesmacario@gmail.com";
    private static final LoginRequest loginRequest = new LoginRequest(EMAIL, PASSWORD);

    @Test
    @DisplayName("Deve logar o usuário")
    void logarUsuario() {
        //Arrange

        var entity = criarUsuarioEntity();

        var response = criarUsuarioResponse();

        when(loginRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(entity));

        when(encoder.matches(PASSWORD, entity.getPassword()))
                .thenReturn(true);

        when(usuarioMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = loginService.autenticar(loginRequest);
        //Assert

        assertThat(resultado)
                .isNotNull()
                .extracting(
                        UsuarioResponse::id,
                        UsuarioResponse::nome,
                        UsuarioResponse::email
                ).containsExactly(
                        ID_VALIDO,
                        "Felipe",
                        "felipesmacario@gmail.com"
                );
        verify(loginRepository).findByEmail(EMAIL);
        verify(encoder).matches(PASSWORD, entity.getPassword());
        verify(usuarioMapper).toResponse(entity);

        verifyNoMoreInteractions(loginRepository, encoder, usuarioMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção de email não encontrado")
    void deveLancarExecaoEmailNaoEncontrado() {
        //Arrange
        when(loginRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());
        //Act
        var excecao = assertThrows(LoginSenhaException.class,
                () -> loginService.autenticar(loginRequest));
        //Assert
        assertThat(excecao)
                .hasMessage("Usuário ou senha inválidos.");

        verify(loginRepository).findByEmail(EMAIL);

        verifyNoMoreInteractions(loginRepository);
        verifyNoInteractions(encoder, usuarioMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção de senha incorreta")
    void deveLancarExecaoSenhaIncorreta() {
        //Arrange
        var entity = criarUsuarioEntity();

        when(loginRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(entity));

        when(encoder.matches(PASSWORD, entity.getPassword()))
                .thenReturn(false);
        //Act
        var excecao = assertThrows(LoginSenhaException.class,
                () -> loginService.autenticar(loginRequest));
        //Assert
        assertThat(excecao)
                .hasMessage("Usuário ou senha inválidos.");

        verify(loginRepository).findByEmail(EMAIL);
        verify(encoder).matches(PASSWORD, entity.getPassword());

        verifyNoMoreInteractions(loginRepository, encoder);
        verifyNoInteractions(usuarioMapper);
    }
}
