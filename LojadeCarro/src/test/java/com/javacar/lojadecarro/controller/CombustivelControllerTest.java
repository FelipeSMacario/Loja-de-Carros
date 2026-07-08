package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.exception.CombustivelException;
import com.javacar.lojadecarro.factory.combustivel.CombustivelRequestFactory;
import com.javacar.lojadecarro.factory.combustivel.CombustivelResponseFactory;
import com.javacar.lojadecarro.service.CombustivelService;
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

import static com.javacar.lojadecarro.support.ErrorMessages.COMBUSTIVEL;
import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CombustivelController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CombustivelControllerTest {
    private static final String URL = "/combustiveis";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CombustivelService combustivelService;

    @Test
    @DisplayName("Deve criar um combustível")
    void deveCriarUmCombustivel() throws Exception {
        //Arrange
        var request = CombustivelRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        var response = CombustivelResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        when(combustivelService.createCombustivel(request))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.nome").value("Gasolina"));

        verify(combustivelService).createCombustivel(request);
    }

    @Test
    @DisplayName("Deve lançar 500 ao cadastrar o combustível vazio")
    void deveRetornar500QuandoOcorrerErroInternoAoCriarCombustivel() throws Exception {
        //Arrange
        var request = CombustivelRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        when(combustivelService.createCombustivel(request))
                .thenThrow(new RuntimeException("Erro inesperado"));
        //Act + Assert
        mockMvc.perform(
                post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
        verify(combustivelService).createCombustivel(request);
    }

    @Test
    @DisplayName("Deve lançar 400 ao cadastrar um combustível ao cadastrar sem nome")
    void deveLancarErroCadastroCombustivel() throws Exception {
        //Arrange
        var request = CombustivelRequestFactory
                .criarRequest()
                .build();

        //Act + Assert
        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(combustivelService);
    }

    @Test
    @DisplayName("Deve listar os combustíveis")
    void deveListarOsCombustivel() throws Exception {
        //Arrange
        var gasolinaResponse = CombustivelResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        var eletrioResponse = CombustivelResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Eletrico")
                .build();
        var response = List.of(gasolinaResponse, eletrioResponse);
        when(combustivelService.listarCombustivel())
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        get(URL)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(ID_VALIDO))
                .andExpect(jsonPath("$[0].nome").value("Gasolina"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nome").value("Eletrico"));
        verify(combustivelService).listarCombustivel();

    }

    @Test
    @DisplayName("Deve buscar o combustível por ID")
    void deveBuscarCombustivelPorId() throws Exception {
        //Arrange
        var response = CombustivelResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(combustivelService.findCombustivelById(ID_VALIDO))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        get(URL + "/" + ID_VALIDO)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.nome").value("Gasolina"));

        verify(combustivelService).findCombustivelById(ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar 404 ao buscar o combustível")
    void deveRetornar404AoBuscarCombustivelPorId() throws Exception {
        //Arrange
        when(combustivelService.findCombustivelById(ID_INVALIDO))
                .thenThrow(new CombustivelException(ID_INVALIDO));

        //Act + Assert
        mockMvc.perform(
                        get(URL + "/" + ID_INVALIDO)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(String.format(ID_NOT_FOUND, COMBUSTIVEL, ID_INVALIDO)));
        verify(combustivelService).findCombustivelById(ID_INVALIDO);
    }

    @Test
    @DisplayName("Deve atualizar o combustível")
    void deveAtualizarCombustivel() throws Exception {
        //Arrange
        var request = CombustivelRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        var response = CombustivelResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        when(combustivelService.updateCombustivel(request, ID_VALIDO))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        put(URL + "/" + ID_VALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.nome").value("Gasolina"));

        verify(combustivelService).updateCombustivel(request, ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar 400 ao atualizar o combustivel sem nome")
    void deveAtualizarCombustivelSemNome() throws Exception {
        //Arrange
        var request = CombustivelRequestFactory
                .criarRequest()
                .build();

        //Act + Assert
        mockMvc.perform(
                put(URL + "/" + ID_VALIDO)
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(combustivelService);
    }


    @Test
    @DisplayName("Deve lançar 404 ao atualizar o combustível")
    void deveLancar404NaoAtualizarCombustivel() throws Exception {
        //Arrange
        var request = CombustivelRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        when(combustivelService.updateCombustivel(request, ID_INVALIDO))
                .thenThrow(new CombustivelException(ID_INVALIDO));
        //Act + Assert
        mockMvc.perform(
                        put(URL + "/" + ID_INVALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, COMBUSTIVEL, ID_INVALIDO)));

        verify(combustivelService).updateCombustivel(request, ID_INVALIDO);
    }

    @Test
    @DisplayName("Deve deletar o combustível")
    void deveDeletarOCombustivel() throws Exception {
        //Act + Assert
        mockMvc.perform(
                delete(URL + "/" + ID_VALIDO)
        ).andExpect(status().isNoContent());
        verify(combustivelService).deleteCombustivel( ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar 404 ao deletar um combustível")
    void deveLancar404NaoDeletarCombustivel() throws Exception {
        //Arrange
        doThrow(new CombustivelException(ID_INVALIDO))
            .when(combustivelService).deleteCombustivel( ID_VALIDO);
        //Act + Assert
        mockMvc.perform(
                delete(URL + "/" + ID_VALIDO)
        ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(combustivelService).deleteCombustivel( ID_VALIDO);
    }
}
