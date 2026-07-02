package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.exception.MarcaException;
import com.JavangularCar.LojadeCarro.factory.marca.MarcaRequestFactory;
import com.JavangularCar.LojadeCarro.factory.marca.MarcaResponseFactory;
import com.JavangularCar.LojadeCarro.service.MarcaService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarcaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MarcaControllerTest {
    private static final String MARCAS_URL = "/marcas";
    private static final Long ID_MARCA = 1L;
    private static final Long ID_MARCA_EXCECAO = 99L;
    public static final String MSG_MARCA_EXCEPTION = "Marca não encontrada com o id: ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MarcaService marcaService;

    @Test
    @DisplayName("Deve criar uma marca")
    void deveCriarMarca() throws Exception {
        //Arrange
        var request = MarcaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        var response = MarcaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(marcaService.createMarca(request))
                .thenReturn(response);
        // Act + Assert
        mockMvc.perform(
                        post("/marcas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())

                .andExpect(header().exists("Location"))

                .andExpect(jsonPath("$.id").value(ID_MARCA))

                .andExpect(jsonPath("$.nome").value("Ford"))

                .andExpect(jsonPath("$.url").value("https://www.google.com"));

        verify(marcaService).createMarca(request);

    }

    @Test
    @DisplayName("Deve retornar 400 quando o nome não for informado")
    void deveRetornarBadRequestQuandoNomeNaoForInformado() throws Exception {
        // Arrange
        var request = MarcaRequestFactory.criarRequest()
                .comTodosOsCamposExcetoNome()
                .build();
        // Act + Assert
        mockMvc.perform(post(MARCAS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(marcaService);
    }

    @Test
    @DisplayName("Deve listar as marcas")
    void deveListarTodasAsMarcas() throws Exception {
        // Arrange
        var fordResponse = MarcaResponseFactory
                .criarResponse()
                .comId(1L)
                .comNome("Ford")
                .comURL("https://www.google.com")
                .build();

        var fiatResponse = MarcaResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Fiat")
                .comURL("https://www.google.com")
                .build();

        var response = List.of(fordResponse, fiatResponse);

        when(marcaService.listarMarcas())
                .thenReturn(response);
        // Act + Assert
        mockMvc.perform(
                        get(MARCAS_URL)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Ford"))
                .andExpect(jsonPath("$[1].nome").value("Fiat"));

        verify(marcaService).listarMarcas();

    }

    @Test
    @DisplayName("Deve buscar a marca por ID")
    void deveBuscarMarcaPorId() throws Exception {
        //Arrange
        var id = ID_MARCA;
        var response = MarcaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(marcaService.findMarcaById(id))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        get(MARCAS_URL + "/" + id)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ford"))
                .andExpect(jsonPath("$.url").value("https://www.google.com"));

        verify(marcaService).findMarcaById(id);
    }

    @Test
    @DisplayName("Deve exibir 404 ao buscar uma marca por ID")
    void deveExibir404AoBuscarMarcaPorId() throws Exception {
        //Arrange
        var id = ID_MARCA_EXCECAO;

        when(marcaService.findMarcaById(id))
                .thenThrow(new MarcaException(id));

        // Act + Assert
        mockMvc.perform(
                        get(MARCAS_URL + "/" + id)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(MSG_MARCA_EXCEPTION + id));

        verify(marcaService).findMarcaById(id);
    }

    @Test
    @DisplayName("Deve atualizar a marca por ID")
    void deveAtualizarMarcaPorId() throws Exception {
        //Arrange
        var id = ID_MARCA;
        var request = MarcaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        var response = MarcaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(marcaService.updateMarca(request, id))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        put(MARCAS_URL + "/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ford"))
                .andExpect(jsonPath("$.url").value("https://www.google.com"));

        verify(marcaService).updateMarca(request, id);
    }

    @Test
    @DisplayName("Deve lançar 404 ao atualizar uma merca")
    void deveLancar404AoAtualizarMarcaPorId() throws Exception {
        //Arrange
        var id = ID_MARCA_EXCECAO;

        var request = MarcaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        when(marcaService.updateMarca(request, id))
                .thenThrow(new MarcaException(id));
        // Act + Assert
        mockMvc.perform(
                        put(MARCAS_URL + "/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(MSG_MARCA_EXCEPTION + id));

        verify(marcaService).updateMarca(request, id);
    }

    @Test
    @DisplayName("Deve lançar 400 quando o nome nao for preenchido na atualização da marca")
    void deveLancar400NaAtualizarMarcaPorId() throws Exception {
        //Arrange
        var id = ID_MARCA;
        var request = MarcaRequestFactory
                .criarRequest()
                .comTodosOsCamposExcetoNome()
                .build();

        // Act + Assert
        mockMvc.perform(
                        put(MARCAS_URL + "/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(marcaService);
    }

    @Test
    @DisplayName("Deve deletar uma marca")
    void deveDeletarMarcaPorId() throws Exception {
        //Arrange
        var id = ID_MARCA;

        // Act + Assert
        mockMvc.perform(
                delete(MARCAS_URL + "/" + id)
        ).andExpect(status().isNoContent());

        verify(marcaService).deleteMarca(id);
    }

    @Test
    @DisplayName("Deve lançar 404 ao deletar uma marca")
    void deveLancar404AoDeletarMarcaPorId() throws Exception {
        //Arrange
        var id = ID_MARCA_EXCECAO;

        doThrow(new MarcaException(id))
                .when(marcaService).deleteMarca(id);

        // Act + Assert
        mockMvc.perform(delete(MARCAS_URL + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(MSG_MARCA_EXCEPTION + id));

        verify(marcaService).deleteMarca(id);
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar inserir um caracteres como ID")
    void deveRetornar400aoTentarInserirCaracteresComoID() throws Exception {
        //Arrange
        var id = "ABC";

        // Act + Assert
        mockMvc.perform(
                        delete(MARCAS_URL + "/" + id)
                ).andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(marcaService);

    }

}
