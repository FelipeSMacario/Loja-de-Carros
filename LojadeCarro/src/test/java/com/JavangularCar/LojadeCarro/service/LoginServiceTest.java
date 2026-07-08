package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.response.UsuarioResponse;
import com.JavangularCar.LojadeCarro.exception.LoginSenhaException;
import com.JavangularCar.LojadeCarro.factory.usuario.UsuarioEntityFactory;
import com.JavangularCar.LojadeCarro.factory.usuario.UsuarioResponseFactory;
import com.JavangularCar.LojadeCarro.mapper.UsuarioMapper;
import com.JavangularCar.LojadeCarro.repository.LoginRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.JavangularCar.LojadeCarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {
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

    @Test
    @DisplayName("Deve logar o usuário")
    void logarUsuario() {
        //Arrange

        var entity = UsuarioEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        var response = UsuarioResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(loginRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(entity));

        when(encoder.matches(PASSWORD, entity.getPassword()))
                .thenReturn(true);

        when(usuarioMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = loginService.logar(EMAIL, PASSWORD);
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
                () -> loginService.logar(EMAIL, PASSWORD));
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
        var entity = UsuarioEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        when(loginRepository.findByEmail(EMAIL))
        .thenReturn(Optional.of(entity));

        when(encoder.matches(PASSWORD, entity.getPassword()))
                .thenReturn(false);
        //Act
        var excecao = assertThrows(LoginSenhaException.class,
                () -> loginService.logar(EMAIL, PASSWORD));
        //Assert
        assertThat(excecao)
                .hasMessage("Usuário ou senha inválidos.");

        verify(loginRepository).findByEmail(EMAIL);
        verify(encoder).matches(PASSWORD, entity.getPassword());

        verifyNoMoreInteractions(loginRepository, encoder);
        verifyNoInteractions(usuarioMapper);
    }
}
