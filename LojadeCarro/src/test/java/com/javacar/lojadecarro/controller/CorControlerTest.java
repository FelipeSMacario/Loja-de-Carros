//package com.javacar.lojadecarro.controller;
//
//import com.javacar.lojadecarro.factory.cores.CoresRequestFactory;
//import com.javacar.lojadecarro.factory.cores.CoresResponseFactory;
//import com.javacar.lojadecarro.service.CoresService;
//import com.fasterxml.jackson.databind.ObjectMapper;
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
//import static com.javacar.lojadecarro.support.ErrorMessages.CORES;
//import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
//import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
//import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CoresController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class CorControlerTest {
//    private static final String URL = "/cores";
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private CoresService coresService;
//
//    @Test
//    @DisplayName("Deve criar uma cor")
//    void deveCriarUmaCor() throws Exception {
//        //Arrange
//        var request = CoresRequestFactory
//                .criarRequest()
//                .comTodosOsCampos()
//                .build();
//
//
//        var response = CoresResponseFactory
//                .criarResponse()
//                .comTodosOsCampos()
//                .build();
//
//        when(coresService.createCores(request))
//                .thenReturn(response);
//        //Act + Assert
//        mockMvc.perform(
//                        post(URL)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                ).andExpect(status().isCreated())
//                .andExpect(header().exists("Location"))
//                .andExpect(jsonPath("$.id").value(ID_VALIDO))
//                .andExpect(jsonPath("$.nome").value("Branco"));
//
//        verify(coresService).createCores(request);
//    }
//
//    @Test
//    @DisplayName("Deve lançar 400 ao cadastrar uma cor sem nome")
//    void deveLancar400aoCadastrarUmaCorSemNome() throws Exception {
//        //Arrange
//        var request = CoresRequestFactory
//                .criarRequest()
//                .build();
//
//        //Act + Assert
//        mockMvc.perform(
//                        post(URL)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                ).andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status").value(400));
//
//        verifyNoInteractions(coresService);
//    }
//
//    @Test
//    @DisplayName("Deve listar as cores")
//    void deveListarAsCores() throws Exception {
//        //Arrange
//        var brancoResponse = CoresResponseFactory
//                .criarResponse()
//                .comTodosOsCampos()
//                .build();
//        var vermelhoResponse = CoresResponseFactory
//                .criarResponse()
//                .comId(2L)
//                .comNome("Vermelho")
//                .build();
//        var response = List.of(brancoResponse, vermelhoResponse);
//
//        when(coresService.listarCores())
//                .thenReturn(response);
//        //Act + Assert
//        mockMvc.perform(
//                        get(URL)
//                ).andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].id").value(ID_VALIDO))
//                .andExpect(jsonPath("$[0].nome").value("Branco"))
//                .andExpect(jsonPath("$[1].id").value(2L))
//                .andExpect(jsonPath("$[1].nome").value("Vermelho"));
//
//        verify(coresService).listarCores();
//    }
//
//    @Test
//    @DisplayName("Deve buscar uma cor por ID")
//    void deveBuscarUmaCorPorID() throws Exception {
//        //Arrange
//        var response = CoresResponseFactory
//                .criarResponse()
//                .comTodosOsCampos()
//                .build();
//
//        when(coresService.findCoresById(ID_VALIDO))
//                .thenReturn(response);
//        //Act + Assert
//        mockMvc.perform(
//                        get(URL + "/" + ID_VALIDO)
//                ).andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(ID_VALIDO))
//                .andExpect(jsonPath("$.nome").value("Branco"));
//
//        verify(coresService).findCoresById(ID_VALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve lançar 404 ao buscar uma cor por ID")
//    void deveLancar404aoBuscarUmaCorPorID() throws Exception {
//        //Arrange
//        when(coresService.findCoresById(ID_INVALIDO))
//                .thenThrow(new CorNotFoundException(ID_INVALIDO));
//        //Act + Assert
//        mockMvc.perform(
//                        get(URL + "/" + ID_INVALIDO)
//                ).andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404))
//                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, CORES, ID_INVALIDO)));
//        verify(coresService).findCoresById(ID_INVALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve atualizar uma cor")
//    void deveAtualizarUmaCor() throws Exception {
//        //Arrange
//        var request = CoresRequestFactory
//                .criarRequest()
//                .comTodosOsCampos()
//                .build();
//
//        var response = CoresResponseFactory
//                .criarResponse()
//                .comTodosOsCampos()
//                .build();
//
//        when(coresService.updateCores(request, ID_VALIDO))
//                .thenReturn(response);
//        //Act + Assert
//        mockMvc.perform(
//                        put(URL + "/" + ID_VALIDO)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                ).andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(ID_VALIDO))
//                .andExpect(jsonPath("$.nome").value("Branco"));
//
//        verify(coresService).updateCores(request, ID_VALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve lançar 400 ao atualizar uma cor")
//    void deveLancar400aoAtualizarUmaCor() throws Exception {
//        //Arrange
//        var request = CoresRequestFactory
//                .criarRequest()
//                .build();
//
//
//        //Act + Assert
//        mockMvc.perform(
//                        put(URL + "/" + ID_VALIDO)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                ).andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status").value(400));
//        verifyNoInteractions(coresService);
//    }
//
//    @Test
//    @DisplayName("Deve lançar 404 ao atualizar uma cor")
//    void deveLancar404aoBuscarUmaCor() throws Exception {
//        //Arrange
//        var request = CoresRequestFactory
//                .criarRequest()
//                .comTodosOsCampos()
//                .build();
//
//        when(coresService.updateCores(request, ID_INVALIDO))
//                .thenThrow(new CorNotFoundException(ID_INVALIDO));
//        //Act + Assert
//        mockMvc.perform(
//                        put(URL + "/" + ID_INVALIDO)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                ).andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404))
//                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, CORES, ID_INVALIDO)));
//        verify(coresService).updateCores(request, ID_INVALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve deletar uma cor")
//    void deveDeletarUmaCor() throws Exception {
//        //Act + Assert
//        mockMvc.perform(
//                delete(URL + "/" + ID_VALIDO)
//        ).andExpect(status().isNoContent());
//        verify(coresService).deleteCores(ID_VALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve lançar 404 ao deletar uma cor")
//    void deveLancar404aoDeletarUmaCor() throws Exception {
//        //Arrange
//
//        doThrow(new CorNotFoundException(ID_INVALIDO))
//                .when(coresService).deleteCores(ID_INVALIDO);
//        //Act + Assert
//        mockMvc.perform(
//                        delete(URL + "/" + ID_INVALIDO)
//                )
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404))
//                .andExpect(jsonPath("$.message")
//                        .value(String.format(ID_NOT_FOUND, CORES, ID_INVALIDO)));
//
//        verify(coresService).deleteCores(ID_INVALIDO);
//    }
//}
