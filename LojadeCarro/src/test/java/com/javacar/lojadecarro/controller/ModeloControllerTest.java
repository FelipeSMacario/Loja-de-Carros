package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.exception.ModeloException;
import com.javacar.lojadecarro.factory.modelo.ModeloRequestFactory;
import com.javacar.lojadecarro.factory.modelo.ModeloResponseFactory;
import com.javacar.lojadecarro.service.ModeloService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
import static com.javacar.lojadecarro.support.ErrorMessages.MODELO;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModeloController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ModeloControllerTest {
    private static final String URl = "/modelos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ModeloService modeloService;

    @Test
    @DisplayName("Deve criar um modelo")
    public void createModelo() throws Exception {
        //Arrange
        var request = ModeloRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        var response = ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(modeloService.createModelo(request))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        post(URl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.nome").value("Onix"))
                .andExpect(jsonPath("$.marcaResponse.id").value(3L))
                .andExpect(jsonPath("$.marcaResponse.nome").value("Chevrolet"))
                .andExpect(jsonPath("$.marcaResponse.url").value("https://www.chevrolet.com"))
        ;

        verify(modeloService).createModelo(request);
        verifyNoMoreInteractions(modeloService);
    }

    @Test
    @DisplayName("Deve lançar 400 ao criar um modelo")
    public void createModeloNotFound() throws Exception {
        //Arrange
        var request = ModeloRequestFactory
                .criarRequest()
                .build();

        //Act + Assert
        mockMvc.perform(
                        post(URl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(modeloService);
    }

    @Test
    @DisplayName("Deve lançar 500 ao criar um modelo")
    public void createModeloException() throws Exception {
        //Arrange
        var request = ModeloRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        var response = ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        when(modeloService.createModelo(request))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                post(URl + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));

        verifyNoInteractions(modeloService);
    }

    @Test
    @DisplayName("Deve listar os modelos")
    public void listModelos() throws Exception {
        //Arrange
        var onixResponse = ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        var celtaResponse = ModeloResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Celta")
                .build();

        var response = List.of(onixResponse, celtaResponse);

        when(modeloService.listarModelo())
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        get(URl)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(ID_VALIDO))
                .andExpect(jsonPath("$[0].nome").value("Onix"))
                .andExpect(jsonPath("$[0].marcaResponse.id").value(3L))
                .andExpect(jsonPath("$[0].marcaResponse.nome").value("Chevrolet"))
                .andExpect(jsonPath("$[0].marcaResponse.url").value("https://www.chevrolet.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nome").value("Celta"))
                .andExpect(jsonPath("$[1].marcaResponse.id").value(3L))
                .andExpect(jsonPath("$[1].marcaResponse.nome").value("Chevrolet"))
                .andExpect(jsonPath("$[1].marcaResponse.url").value("https://www.chevrolet.com"));
        verify(modeloService).listarModelo();
        verifyNoMoreInteractions(modeloService);
    }

    @Test
    @DisplayName("Deve buscar um modelo por ID")
    void findModeloById() throws Exception {
        //Arrange
        var response = ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(modeloService.findModeloById(ID_VALIDO))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        get(URl + "/" + ID_VALIDO)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.nome").value("Onix"))
                .andExpect(jsonPath("$.marcaResponse.id").value(3L))
                .andExpect(jsonPath("$.marcaResponse.nome").value("Chevrolet"))
                .andExpect(jsonPath("$.marcaResponse.url").value("https://www.chevrolet.com"));

        verify(modeloService).findModeloById(ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar 404 ao buscar o modelo")
    void findModeloNotFound() throws Exception {
        //Arrange
        when(modeloService.findModeloById(ID_INVALIDO))
                .thenThrow(new ModeloException(ID_INVALIDO));
        //Act + Assert
        mockMvc.perform(
                        get(URl + "/" + ID_INVALIDO)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, MODELO, ID_INVALIDO)));
        verify(modeloService).findModeloById(ID_INVALIDO);
    }

    @Test
    @DisplayName("Deve atualizar o modelo")
    public void updateModelo() throws Exception {
        //Arrange
        var request = ModeloRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        var response = ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        when(modeloService.updateModelo(request, ID_VALIDO))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        put(URl + "/" + ID_VALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.nome").value("Onix"))
                .andExpect(jsonPath("$.marcaResponse.id").value(3L))
                .andExpect(jsonPath("$.marcaResponse.nome").value("Chevrolet"))
                .andExpect(jsonPath("$.marcaResponse.url").value("https://www.chevrolet.com"));

        verify(modeloService).updateModelo(request, ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar 400 ao atualizar o modelo sem nome")
    public void updateModeloNotFound() throws Exception {
        //Arrange
        var request = ModeloRequestFactory
                .criarRequest()
                .build();

        //Act + Assert
        mockMvc.perform(
                        put(URl + "/" + ID_INVALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(modeloService);
    }

    @Test
    @DisplayName("Deve lançar 404 ao atualizar o modelo")
    public void updateNotFound() throws Exception {
        //Arrange
        var request = ModeloRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        when(modeloService.updateModelo(request,ID_INVALIDO))
                .thenThrow(new ModeloException(ID_INVALIDO));
        //Act + Assert
        mockMvc.perform(
                        put(URl + "/" + ID_INVALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, MODELO, ID_INVALIDO)));

        verify(modeloService).updateModelo(request,ID_INVALIDO);
    }

    @Test
    @DisplayName("Deve deletar o modelo")
    public void deleteModelo() throws Exception {
        //Act + Assert
        mockMvc.perform(
                delete(URl + "/" + ID_VALIDO)
        ).andExpect(status().isNoContent());
        verify(modeloService).deleteModelo(ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar 404 ao deletar o modelo")
    public void deleteNotFound() throws Exception {
        //Act + Assert
        doThrow(new ModeloException(ID_INVALIDO))
                .when(modeloService).deleteModelo(ID_INVALIDO);

        mockMvc.perform(
                        delete(URl + "/" + ID_INVALIDO)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, MODELO, ID_INVALIDO)));
        verify(modeloService).deleteModelo(ID_INVALIDO);
    }

    private ResultActions validarModelo(ResultActions result) throws Exception {

        return result
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.nome").value("Onix"))
                .andExpect(jsonPath("$.marcaResponse.id").value(3L))
                .andExpect(jsonPath("$.marcaResponse.nome").value("Chevrolet"))
                .andExpect(jsonPath("$.marcaResponse.url").value("https://www.chevrolet.com"));
    }
}
