//package com.javacar.lojadecarro.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.javacar.lojadecarro.dto.request.OpcionalRequest;
//import com.javacar.lojadecarro.dto.response.OpcionalResponse;
//import com.javacar.lojadecarro.exception.OpcionalException;
//import com.javacar.lojadecarro.factory.opcional.KitRequestFactory;
//import com.javacar.lojadecarro.factory.opcional.KitResponseFactory;
//import com.javacar.lojadecarro.service.OpcionalService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
//import static com.javacar.lojadecarro.support.ErrorMessages.KIT;
//import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
//import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(OpcionalController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class OpcionalControllerTest {
//    private static final String URL = "/kits";
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private OpcionalService opcionalService;
//
//    @Test
//    @DisplayName("Deve cadastrar um opcional")
//    void deveCadastrarUmKit() throws Exception {
//        //Arrange
//        var request = criarKitRequest();
//        var response = criarKitResponse();
//
//        when(opcionalService.createKit(request))
//                .thenReturn(response);
//        //Act + Assert
//
//        mockMvc.perform(
//                        post(URL)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                ).andExpect(status().isCreated())
//                .andExpect(header().exists("Location"))
//                .andExpect(jsonPath("$.id").value(ID_VALIDO));
//
//        verify(opcionalService).createKit(request);
//        verifyNoMoreInteractions(opcionalService);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 400 ao cadastrar um opcional sem id do veiculo")
//    void deveRetornar400aoCadastrarUmKitSemCarroId() throws Exception {
//        //Arrange
//        var request = KitRequestFactory.criarRequest().comTodosOsCampos().comId(null).build();
//
//        //Act + Assert
//        mockMvc.perform(
//                        post(URL)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                ).andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status").value(400));
//
//        verifyNoInteractions(opcionalService);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 ao buscar opcional com ID do veiculo invalido")
//    void deveRetornar404aoCadastrarUmKitComIDInvalido() throws Exception {
//        //Arrange
//        var request = criarKitRequest();
//
//        when(opcionalService.createKit(request))
//                .thenThrow(new OpcionalException(ID_INVALIDO));
//        //Act + Assert
//        mockMvc.perform(
//                        post(URL)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                ).andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404))
//                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, KIT, ID_INVALIDO)));
//
//        verify(opcionalService).createKit(request);
//        verifyNoMoreInteractions(opcionalService);
//    }
//
//    @Test
//    @DisplayName("Deve listar os kits")
//    void deveListarOsKits() throws Exception {
//        //Arrange
//        var response = List.of(criarKitResponse(), KitResponseFactory
//                .criarResponse()
//                .comTodosOsCampos()
//                .comId(2L)
//                .comFreio(false)
//                .build());
//
//        when(opcionalService.listarKit())
//                .thenReturn(response);
//        //Act + Assert
//        mockMvc.perform(
//                        get(URL)
//                ).andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$.[0].id").value(ID_VALIDO))
//                .andExpect(jsonPath("$.[1].id").value(2L));
//
//        verify(opcionalService).listarKit();
//        verifyNoMoreInteractions(opcionalService);
//    }
//
//    @Test
//    @DisplayName("Deve buscar um opcional por ID")
//    void deveBuscarUmKitPorID() throws Exception {
//        //Arrange
//        var response = criarKitResponse();
//
//        when(opcionalService.filtrarKit(ID_VALIDO))
//                .thenReturn(response);
//        //Act + Assert
//        mockMvc.perform(
//                        get(URL + "/" + ID_VALIDO)
//                ).andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(ID_VALIDO));
//
//        verify(opcionalService).filtrarKit(ID_VALIDO);
//        verifyNoMoreInteractions(opcionalService);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 ao buscar um opcional por ID")
//    void deveRetornar404aoBuscarUmKitPorID() throws Exception {
//        //Arrange
//
//        when(opcionalService.filtrarKit(ID_INVALIDO))
//                .thenThrow(new OpcionalException(ID_INVALIDO));
//        //Act + Assert
//        mockMvc.perform(
//                        get(URL + "/" + ID_INVALIDO)
//                ).andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404))
//                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, KIT, ID_INVALIDO)));
//    }
//
//    @Test
//    @DisplayName("Deve atualizar o opcional")
//    void deveAtualizarUmKit() throws Exception {
//        //Arrange
//        var request = criarKitRequest();
//        var response = criarKitResponse();
//
//        when(opcionalService.updateKit(request, ID_VALIDO))
//                .thenReturn(response);
//
//        //Act + Assert
//        mockMvc.perform(
//                        put(URL + "/" + ID_VALIDO)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                ).andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(ID_VALIDO));
//
//        verify(opcionalService).updateKit(request, ID_VALIDO);
//        verifyNoMoreInteractions(opcionalService);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 400 na atualização do opcional ao buscar o opcional sem id do veiculo")
//    void deveRetornar400aoCadastrarUmKitSemId() throws Exception {
//        //Arrange
//        var request = KitRequestFactory.criarRequest().comTodosOsCampos().comId(null).build();
//        //Act + Assert
//        mockMvc.perform(
//                        put(URL + "/" + ID_VALIDO)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                ).andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status").value(400));
//
//        verifyNoInteractions(opcionalService);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 na atualização do opcional ao buscar o opcional com o id do veiculo invalido")
//    void deveRetornar404aoCadastrarUmKitComIdInvalido() throws Exception {
//        //Arrange
//        var request = criarKitRequest();
//
//        when(opcionalService.updateKit(request, ID_INVALIDO))
//                .thenThrow(new OpcionalException(ID_INVALIDO));
//
//        //Act + Assert
//        mockMvc.perform(
//                        put(URL + "/" + ID_INVALIDO)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                ).andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404))
//                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, KIT, ID_INVALIDO)));
//
//        verify(opcionalService).updateKit(request, ID_INVALIDO);
//        verifyNoMoreInteractions(opcionalService);
//
//    }
//
//    @Test
//    @DisplayName("Deve deletar um opcional")
//    void deveDeletarUmKit() throws Exception {
//        //Arrange
//        //Act + Assert
//        mockMvc.perform(
//                delete(URL + "/" + ID_VALIDO)
//        ).andExpect(status().isNoContent());
//
//        verify(opcionalService).deleteKit(ID_VALIDO);
//        verifyNoMoreInteractions(opcionalService);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 ao buscar um opcional com id do veiculo invalido")
//    void deveRetornar404aoCadastrarUmKitSemIdComIdInvalido() throws Exception {
//        //Arrange
//        doThrow(new OpcionalException(ID_INVALIDO))
//                .when(opcionalService).deleteKit(ID_INVALIDO);
//        //Act + Assert
//        mockMvc.perform(
//                        delete(URL + "/" + ID_INVALIDO)
//                ).andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404))
//                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, KIT, ID_INVALIDO)));
//
//        verify(opcionalService).deleteKit(ID_INVALIDO);
//        verifyNoMoreInteractions(opcionalService);
//    }
//
//    private OpcionalRequest criarKitRequest() {
//        return KitRequestFactory
//                .criarRequest()
//                .comTodosOsCampos()
//                .build();
//    }
//
//
//    private OpcionalResponse criarKitResponse() {
//        return KitResponseFactory
//                .criarResponse()
//                .comTodosOsCampos()
//                .build();
//    }
//}
