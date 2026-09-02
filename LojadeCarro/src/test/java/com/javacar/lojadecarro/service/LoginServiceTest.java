package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.LoginRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import com.javacar.lojadecarro.repository.LoginRepository;
import com.javacar.lojadecarro.security.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioEntity;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioResponse;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private LoginRepository loginRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
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
        var authentication = mock(Authentication.class);
        var response = criarUsuarioResponse();
        var usernamePassword = new UsernamePasswordAuthenticationToken(
                loginRequest.login(),
                loginRequest.senha()
        );


        when(authenticationManager.authenticate(usernamePassword)).thenReturn(authentication);

        when(jwtService.gerarToken(authentication))
                .thenReturn("token-jwt-gerado");

        when(authentication.getPrincipal())
                .thenReturn(entity);

        when(usuarioMapper.toResponse(entity)).thenReturn(response);

        //Act
        var resultado = loginService.autenticar(loginRequest);
        //Assert

        assertThat(resultado)
                .isNotNull();

        assertThat(resultado.usuario())
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
        verify(authenticationManager).authenticate(usernamePassword);
        verify(jwtService).gerarToken(authentication);
        verify(usuarioMapper).toResponse(entity);

        verifyNoMoreInteractions(authenticationManager, jwtService, usuarioMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção quando as credenciais forem inválidas")
    void deveLancarExcecaoQuandoCredenciaisInvalidas() {
        // Arrange
        var request = new LoginRequest(
                "usuario@email.com",
                "senha-incorreta"
        );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new BadCredentialsException("Usuário ou senha inválidos")
        );

        // Act e Assert
        assertThatThrownBy(() -> loginService.autenticar(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Usuário ou senha inválidos");

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verifyNoInteractions(jwtService, usuarioMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário estiver desativado")
    void deveLancarExcecaoQuandoUsuarioEstiverDesativado() {
        // Arrange
        var request = new LoginRequest(
                "usuario@email.com",
                "senha"
        );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new DisabledException("Usuário desativado")
        );

        // Act e Assert
        assertThatThrownBy(() -> loginService.autenticar(request))
                .isInstanceOf(DisabledException.class);

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verifyNoInteractions(jwtService, usuarioMapper);
    }

    @Test
    @DisplayName("Deve propagar exceção quando não for possível gerar o JWT")
    void devePropagarExcecaoQuandoFalharGeracaoDoToken() {
        // Arrange
        var request = new LoginRequest(
                "usuario@email.com",
                "senha"
        );

        var authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(jwtService.gerarToken(authentication))
                .thenThrow(new RuntimeException("Erro ao gerar JWT"));

        // Act e Assert
        assertThatThrownBy(() -> loginService.autenticar(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro ao gerar JWT");

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
        verify(jwtService).gerarToken(authentication);

        verifyNoInteractions(usuarioMapper);
    }

}
