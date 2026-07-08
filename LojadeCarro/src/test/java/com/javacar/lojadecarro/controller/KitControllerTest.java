package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.KitRequest;
import com.javacar.lojadecarro.dto.response.KitResponse;
import com.javacar.lojadecarro.entity.Kit;
import com.javacar.lojadecarro.exception.KitException;
import com.javacar.lojadecarro.factory.kit.KitEntityFactory;
import com.javacar.lojadecarro.factory.kit.KitRequestFactory;
import com.javacar.lojadecarro.factory.kit.KitResponseFactory;
import com.javacar.lojadecarro.service.KitService;
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
import static com.javacar.lojadecarro.support.ErrorMessages.KIT;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KitController.class)
@AutoConfigureMockMvc(addFilters = false)
public class KitControllerTest {
    private static final String URL = "/kits";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KitService kitService;

    @Test
    @DisplayName("Deve cadastrar um kit")
    public void deveCadastrarUmKit() throws Exception {
        //Arrange
        var request = criarKitRequest();
        var response = criarKitResponse();

        when(kitService.createKit(request))
                .thenReturn(response);
        //Act + Assert

        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(ID_VALIDO));

        verify(kitService).createKit(request);
        verifyNoMoreInteractions(kitService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao cadastrar um kit sem id do carro")
    void deveRetornar400aoCadastrarUmKitSemCarroId() throws Exception {
        //Arrange
        var request = KitRequestFactory.criarRequest().comTodosOsCampos().comId(null).build();

        //Act + Assert
        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(kitService);
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar kit com ID do carro invalido")
    void deveRetornar404aoCadastrarUmKitComIDInvalido() throws Exception {
        //Arrange
        var request = criarKitRequest();

        when(kitService.createKit(request))
                .thenThrow(new KitException(ID_INVALIDO));
        //Act + Assert
        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, KIT, ID_INVALIDO)));

        verify(kitService).createKit(request);
        verifyNoMoreInteractions(kitService);
    }

    @Test
    @DisplayName("Deve listar os kits")
    void deveListarOsKits() throws Exception {
        //Arrange
        var response = List.of(criarKitResponse(), KitResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(2L)
                .comFreio(false)
                .build());

        when(kitService.listarKit())
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        get(URL)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(ID_VALIDO))
                .andExpect(jsonPath("$.[1].id").value(2L));

        verify(kitService).listarKit();
        verifyNoMoreInteractions(kitService);
    }

    @Test
    @DisplayName("Deve buscar um kit por ID")
    void deveBuscarUmKitPorID() throws Exception {
        //Arrange
        var response = criarKitResponse();

        when(kitService.filtrarKit(ID_VALIDO))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        get(URL + "/" + ID_VALIDO)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_VALIDO));

        verify(kitService).filtrarKit(ID_VALIDO);
        verifyNoMoreInteractions(kitService);
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar um kit por ID")
    void deveRetornar404aoBuscarUmKitPorID() throws Exception {
        //Arrange

        when(kitService.filtrarKit(ID_INVALIDO))
                .thenThrow(new KitException(ID_INVALIDO));
        //Act + Assert
        mockMvc.perform(
                        get(URL + "/" + ID_INVALIDO)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, KIT, ID_INVALIDO)));
    }

    @Test
    @DisplayName("Deve atualizar o kit")
    void deveAtualizarUmKit() throws Exception {
        //Arrange
        var request = criarKitRequest();
        var response = criarKitResponse();

        when(kitService.updateKit(request, ID_VALIDO))
        .thenReturn(response);

        //Act + Assert
        mockMvc.perform(
                put(URL + "/" + ID_VALIDO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_VALIDO));

        verify(kitService).updateKit(request, ID_VALIDO);
        verifyNoMoreInteractions(kitService);
    }

    @Test
    @DisplayName("Deve retornar 400 na atualização do kit ao buscar o kit sem id do carro")
    void deveRetornar400aoCadastrarUmKitSemId() throws Exception {
        //Arrange
        var request = KitRequestFactory.criarRequest().comTodosOsCampos().comId(null).build();
        //Act + Assert
        mockMvc.perform(
                        put(URL + "/" + ID_VALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(kitService);
    }

    @Test
    @DisplayName("Deve retornar 404 na atualização do kit ao buscar o kit com o id do carro invalido")
    void deveRetornar404aoCadastrarUmKitComIdInvalido() throws Exception {
        //Arrange
        var request = criarKitRequest();

        when(kitService.updateKit(request, ID_INVALIDO))
                .thenThrow(new KitException(ID_INVALIDO));

        //Act + Assert
        mockMvc.perform(
                        put(URL + "/" + ID_INVALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, KIT, ID_INVALIDO)));

        verify(kitService).updateKit(request, ID_INVALIDO);
        verifyNoMoreInteractions(kitService);

    }

    @Test
    @DisplayName("Deve deletar um kit")
    void deveDeletarUmKit() throws Exception {
        //Arrange
        //Act + Assert
        mockMvc.perform(
                delete(URL + "/" + ID_VALIDO)
        ).andExpect(status().isNoContent());

        verify(kitService).deleteKit(ID_VALIDO);
        verifyNoMoreInteractions(kitService);
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar um kit com id do carro invalido")
    void deveRetornar404aoCadastrarUmKitSemIdComIdInvalido() throws Exception {
        //Arrange
        doThrow(new KitException(ID_INVALIDO))
                .when(kitService).deleteKit(ID_INVALIDO);
        //Act + Assert
        mockMvc.perform(
                        delete(URL + "/" + ID_INVALIDO)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, KIT, ID_INVALIDO)));

        verify(kitService).deleteKit(ID_INVALIDO);
        verifyNoMoreInteractions(kitService);
    }

    private KitRequest criarKitRequest() {
        return KitRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    private Kit criarKitEntity() {
        return KitEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    private KitResponse criarKitResponse() {
        return KitResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }
}
