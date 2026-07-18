//package com.javacar.lojadecarro.controller;
//
//import com.javacar.lojadecarro.dto.response.ImagensResponse;
//import com.javacar.lojadecarro.factory.imagens.ImagensRequestFactory;
//import com.javacar.lojadecarro.factory.imagens.ImagensResponseFactory;
//import com.javacar.lojadecarro.service.ImagensService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
//import static com.javacar.lojadecarro.support.ErrorMessages.IMAGENS;
//import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
//import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ImagensController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class ImagensControllerTest {
//    private static final String URL = "/imagens";
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private ImagensService imagensService;
//
//    @Test
//    @DisplayName("Deve cadastrar uma imagem")
//    void deveCadastrarImagem() throws Exception {
//        // Arrange
//        MockMultipartFile file1 = new MockMultipartFile(
//                "files",
//                "foto1.jpg",
//                MediaType.IMAGE_JPEG_VALUE,
//                "imagem1".getBytes()
//        );
//
//        MockMultipartFile file2 = new MockMultipartFile(
//                "files",
//                "foto2.jpg",
//                MediaType.IMAGE_JPEG_VALUE,
//                "imagem2".getBytes()
//        );
//
//        var response = List.of(
//                criarImagemResponse(),
//                ImagensResponseFactory.criarResponse()
//                        .comTodosOsCampos()
//                        .comId(2L)
//                        .build()
//        );
//
//        when(imagensService.create(any(MultipartFile[].class), eq(ID_VALIDO)))
//                .thenReturn(response);
//
//        // Act + Assert
//        mockMvc.perform(
//                        multipart(URL + "/{idVeiculo}", ID_VALIDO)
//                                .file(file1)
//                                .file(file2)
//                )
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].id").value(ID_VALIDO))
//                .andExpect(jsonPath("$[1].id").value(2L));
//
//        verify(imagensService)
//                .create(any(MultipartFile[].class), eq(ID_VALIDO));
//
//        verifyNoMoreInteractions(imagensService);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 500 ao cadastrar uma imagem")
//    void deveRetornar500aoCadastrarImagem() throws Exception {
//        //Arrange
//        MockMultipartFile file1 = new MockMultipartFile(
//                "files",
//                "foto1.jpg",
//                MediaType.IMAGE_JPEG_VALUE,
//                "imagem1".getBytes()
//        );
//
//        MockMultipartFile file2 = new MockMultipartFile(
//                "files",
//                "foto2.jpg",
//                MediaType.IMAGE_JPEG_VALUE,
//                "imagem2".getBytes()
//        );
//
//        when(imagensService.create(any(MultipartFile[].class), eq(ID_VALIDO)))
//        .thenThrow(new RuntimeException("Erro inesperado"));
//        //Act + Assert
//        mockMvc.perform(
//                        multipart(URL + "/{idVeiculo}", ID_VALIDO)
//                                .file(file1)
//                                .file(file2)
//        ).andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.status").value(500));
//
//        verify(imagensService).create(any(MultipartFile[].class), eq(ID_VALIDO));
//
//        verifyNoMoreInteractions(imagensService);
//    }
//
//    @Test
//    @DisplayName("Deve listar as imagens pelo ID do carro")
//    void deveListarImagemPorID() throws Exception {
//        //Arrange
//        var response = List.of(criarImagemResponse(), ImagensResponseFactory
//                .criarResponse()
//                .comTodosOsCampos()
//                .comURL("url")
//                .build());
//
//        when(imagensService.listarImagens(ID_VALIDO))
//                .thenReturn(response);
//        //Act + Assert
//        mockMvc.perform(
//                        get(URL + "/carro/{idVeiculo}", ID_VALIDO)
//                ).andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].id").value(ID_VALIDO))
//                .andExpect(jsonPath("$[0].url").value("https://bucket/imagens/onix.jpg"))
//                .andExpect(jsonPath("$[1].id").value(ID_VALIDO))
//                .andExpect(jsonPath("$[1].url").value("url"));
//
//        verify(imagensService).listarImagens(ID_VALIDO);
//        verifyNoMoreInteractions(imagensService);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 ao buscar imagens do carro por ID")
//    void deveRetornar404AoBuscarImagemPorID() throws Exception {
//        //Arrange
//        when(imagensService.listarImagens(ID_INVALIDO))
//                .thenThrow(new ImagemNotFoundException(ID_INVALIDO));
//        //Act + Assert
//        mockMvc.perform(
//                get(URL + "/carro/{idVeiculo}", ID_INVALIDO)
//        ).andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value("404"))
//                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, IMAGENS, ID_INVALIDO)));
//
//        verify(imagensService).listarImagens(ID_INVALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve buscar uma imagem por ID")
//    void  deveBuscarImagemPorID() throws Exception {
//        //Arrange
//        var response = criarImagemResponse();
//        when(imagensService.findImagensById(ID_VALIDO))
//        .thenReturn(response);
//        //Act + Assert
//        mockMvc.perform(
//                get(URL + "/{idImagem}", ID_VALIDO)
//        ).andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(ID_VALIDO))
//                .andExpect(jsonPath("$.url").value("https://bucket/imagens/onix.jpg"))
//                .andExpect(jsonPath("$.idVeiculo").value(ID_VALIDO));
//
//        verify(imagensService).findImagensById(ID_VALIDO);
//        verifyNoMoreInteractions(imagensService);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 ao buscar uma imagem por ID")
//    void deveRetornar404AoBuscarImagenPorID() throws Exception {
//        //Arrange
//        when(imagensService.findImagensById(ID_INVALIDO))
//        .thenThrow(new ImagemNotFoundException(ID_INVALIDO));
//        //Act + Assert
//        mockMvc.perform(
//                get(URL + "/{idImagem}", ID_INVALIDO)
//        ).andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value("404"))
//                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, IMAGENS, ID_INVALIDO)));
//
//        verify(imagensService).findImagensById(ID_INVALIDO);
//        verifyNoMoreInteractions(imagensService);
//    }
//
//    @Test
//    @DisplayName("Deve atualizar uma imagem")
//    void deveAtualizarImagemPorID() throws Exception {
//        //Arrange
//        var request = ImagensRequestFactory.criarRequest().comTodosOsCampos().build();
//        var response = criarImagemResponse();
//
//        when(imagensService.updateImagens(request, ID_VALIDO))
//        .thenReturn(response);
//        //Act + Assert
//        mockMvc.perform(
//                put(URL + "/{idImagem}", ID_VALIDO)
//                .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//        ).andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(ID_VALIDO))
//                .andExpect(jsonPath("$.url").value("https://bucket/imagens/onix.jpg"))
//                .andExpect(jsonPath("$.idVeiculo").value(ID_VALIDO));
//
//        verify(imagensService).updateImagens(request, ID_VALIDO);
//        verifyNoMoreInteractions(imagensService);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 400 ao atualizar a imagem")
//    void deveRetornar400AoAtualizarImagemPorID() throws Exception {
//        //Arrange
//        var request = ImagensRequestFactory.criarRequest().build();
//        //Act + Assert
//        mockMvc.perform(
//                put(URL + "/{idImagem}", ID_VALIDO)
//                .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//        ).andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status").value("400"));
//
//        verifyNoInteractions(imagensService);
//    }
//    @Test
//    @DisplayName("Deve retornar 404 ao atualizar a imagem")
//    void deveRetornar404AoAtualizarImagemPorID() throws Exception {
//        //Arrange
//        var  request = ImagensRequestFactory.criarRequest().comTodosOsCampos().build();
//
//        when(imagensService.updateImagens(request, ID_INVALIDO))
//        .thenThrow(new ImagemNotFoundException(ID_INVALIDO));
//        //Act + Assert
//        mockMvc.perform(
//                put(URL + "/{idImagem}", ID_INVALIDO)
//                .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//        ).andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value("404"))
//                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, IMAGENS, ID_INVALIDO)));
//
//        verify(imagensService).updateImagens(request, ID_INVALIDO);
//        verifyNoMoreInteractions(imagensService);
//    }
//
//    @Test
//    @DisplayName("Deve deletar uma imagem por ID")
//    void deveDeletarImagemPorID() throws Exception {
//        //Arrange
//        //Act + Assert
//        mockMvc.perform(
//                delete(URL +  "/{idImagem}", ID_VALIDO)
//        ).andExpect(status().isNoContent());
//
//        verify(imagensService).deleteImagens(ID_VALIDO);
//        verifyNoMoreInteractions(imagensService);
//    }
//
//    @Test
//    @DisplayName("Deve retornar 404 ao deletar uma imagem por ID")
//    void deveDeletarImagenPorID() throws Exception {
//        //Arrange
//        doThrow(new ImagemNotFoundException(ID_INVALIDO))
//                .when(imagensService).deleteImagens(ID_INVALIDO);
//        //Act + Assert
//        mockMvc.perform(
//                delete(URL +  "/{idImagem}", ID_INVALIDO)
//        ).andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value("404"))
//                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, IMAGENS, ID_INVALIDO)));
//
//        verify(imagensService).deleteImagens(ID_INVALIDO);
//        verifyNoMoreInteractions(imagensService);
//
//    }
//
//    private static ImagensResponse criarImagemResponse() {
//        return ImagensResponseFactory.criarResponse()
//                .comTodosOsCampos()
//                .build();
//    }
//
//
//}
