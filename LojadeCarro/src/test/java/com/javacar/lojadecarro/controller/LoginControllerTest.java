package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.exception.LoginSenhaException;
import com.javacar.lojadecarro.factory.usuario.UsuarioResponseFactory;
import com.javacar.lojadecarro.service.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {
    private static final String URL = "/login";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoginService loginService;

    @Test
    @DisplayName("Deve logar com sucesso")
    void logarSucesso() throws Exception {
        //Arrange
        var login = "felespe";
        var password = "12345";
        var response = criarUsuarioResponse();

        when(loginService.logar(login, password))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        get(URL)
                                .param("login", login)
                                .param("password", password)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.nome").value("Felipe"))
                .andExpect(jsonPath("$.email").value("felipesmacario@gmail.com"));

        verify(loginService).logar(login, password);
        verifyNoMoreInteractions(loginService);
    }

    @Test
    @DisplayName("Deve retornar 401 quando as credenciais forem inválidas")
    void retornar401aoInserirLogin() throws Exception {
        //Arrange
        var login = "felespeErrado";
        var password = "12345";

        when(loginService.logar(login, password))
                .thenThrow(new LoginSenhaException());
        //Act + Assert
        mockMvc.perform(
                        get(URL)
                                .param("login", login)
                                .param("password", password)
                ).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Usuário ou senha inválidos."));

        verify(loginService).logar(login, password);

        verifyNoMoreInteractions(loginService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao nao informar a senha")
    void retorna400aoNaoInformarSenha() throws Exception {
        //Arrange
        var login = "felespe";
        //Act + Assert
        mockMvc.perform(
                        get(URL)
                                .param("login", login)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(loginService);
    }

    private UsuarioResponse criarUsuarioResponse() {
        return UsuarioResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }
}
