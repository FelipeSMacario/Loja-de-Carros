package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.exception.UsuarioException;
import com.javacar.lojadecarro.factory.usuario.UsuarioRequestFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioResponseFactory;
import com.javacar.lojadecarro.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
import static com.javacar.lojadecarro.support.ErrorMessages.USUARIO;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {
    private static final String URL = "/usuario";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve buscar um usuário")
    public void deveBuscarUmUsuario() throws Exception {
        //Arrange
        var request = criarUsuarioRequest();
        var response = criarUsuarioResponse();

        when(usuarioService.createUsuario(request))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(ID_VALIDO));

        verify(usuarioService).createUsuario(request);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar um usuário")
    void deveRetornar400aoCriarUmUsuario() throws Exception {
        //Arrange
        var request = UsuarioRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .comNome(null)
                .build();
        //Act + Assert
        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao inserir um CPF invalido")
    void deveRetornar400aoInserirCPFInvalido() throws Exception {
        //Arrange
        var request = UsuarioRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .comCPF("15000000000")
                .build();
        //Act + Assert
        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve retornar 500 ao acontecer um erro inesperado")
    void deveRetornar500aoAcontecerUmErro() throws Exception {
        //Arrange
        var request = criarUsuarioRequest();

        when(usuarioService.createUsuario(request))
                .thenThrow(new RuntimeException("Erro inesperado"));

        //Act + Assert
        mockMvc.perform(
                post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));

        verify(usuarioService).createUsuario(request);
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosOsUsuarios() throws Exception {
        //Arrange
        var response = List.of(criarUsuarioResponse(), UsuarioResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comNome("Goku")
                .build());

        when(usuarioService.listarUsuario())
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        get(URL)
                ).andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(ID_VALIDO))
                .andExpect(jsonPath("$.[0].nome").value("Felipe"))
                .andExpect(jsonPath("$.[0].email").value("felipesmacario@gmail.com"))
                .andExpect(jsonPath("$.[1].nome").value("Goku"));

        verify(usuarioService).listarUsuario();
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve buscar um usuário por ID")
    void deveBuscarUmUsuarioPorID() throws Exception {
        //Arrange
        var response = criarUsuarioResponse();

        when(usuarioService.findUsuarioBId(ID_VALIDO))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        get(URL + "/" + ID_VALIDO)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.nome").value("Felipe"))
                .andExpect(jsonPath("$.email").value("felipesmacario@gmail.com"));

        verify(usuarioService).findUsuarioBId(ID_VALIDO);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar um usuário por ID")
    void deveRetornar404aoBuscarUmUsuarioPorID() throws Exception {
        //Arrange
        when(usuarioService.findUsuarioBId(ID_INVALIDO))
                .thenThrow(new UsuarioException(ID_INVALIDO));
        //Act + Assert
        mockMvc.perform(
                        get(URL + "/" + ID_INVALIDO)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, USUARIO, ID_INVALIDO)));
        verify(usuarioService).findUsuarioBId(ID_INVALIDO);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve atualizar um usuário")
    void deveAtualizarUmUsuario() throws Exception {
        //Arrange
        var request = criarUsuarioRequest();
        var response = criarUsuarioResponse();

        when(usuarioService.updateUsuario(request, ID_VALIDO))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        put("/usuario/" + ID_VALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.nome").value("Felipe"))
                .andExpect(jsonPath("$.email").value("felipesmacario@gmail.com"));

        verify(usuarioService).updateUsuario(request, ID_VALIDO);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar um usuário sem senha")
    void deveRetornar400aoAtualizarUmUsuarioSemSenha() throws Exception {
        //Arrange
        var request = UsuarioRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .comSenha(null)
                .build();
        //Act + Assert
        mockMvc.perform(
                        put(URL + "/" + ID_VALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar um usuário com ID errado")
    void deveRetornar404aoAtualizarUmUsuarioComIDErrado() throws Exception {
        //Arrange
        var request = criarUsuarioRequest();

        when(usuarioService.updateUsuario(request, ID_INVALIDO))
                .thenThrow(new UsuarioException(ID_INVALIDO));
        //Act + Assert
        mockMvc.perform(
                        put(URL + "/" + ID_INVALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, USUARIO, ID_INVALIDO)));

        verify(usuarioService).updateUsuario(request, ID_INVALIDO);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve deletar um usuário")
    void deveDeletarUmUsuario() throws Exception {
        //Arrange

        //Act + Assert
        mockMvc.perform(
                delete("/usuario/" + ID_VALIDO)
        ).andExpect(status().isNoContent());

        verify(usuarioService).deleteUsuario(ID_VALIDO);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Deve retornar 404 ao deletar um usuário")
    void deveRetornar404aoDeletarUmUsuario() throws Exception {
        //Arrange
        doThrow(new UsuarioException(ID_INVALIDO))
                .when(usuarioService).deleteUsuario(ID_INVALIDO);
        //Act + Assert
        mockMvc.perform(
                        delete(URL + "/" + ID_INVALIDO)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, USUARIO, ID_INVALIDO)));

        verify(usuarioService).deleteUsuario(ID_INVALIDO);
        verifyNoMoreInteractions(usuarioService);
    }

    private UsuarioRequest criarUsuarioRequest() {
        return UsuarioRequestFactory.criarRequest().comTodosOsCampos().build();
    }

    private UsuarioResponse criarUsuarioResponse() {
        return UsuarioResponseFactory.criarResponse().comTodosOsCampos().build();
    }

}
