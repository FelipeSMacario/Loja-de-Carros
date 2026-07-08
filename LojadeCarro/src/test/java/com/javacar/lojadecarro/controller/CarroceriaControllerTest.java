package com.javacar.lojadecarro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacar.lojadecarro.exception.CarroceriaException;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaRequestFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaResponseFactory;
import com.javacar.lojadecarro.service.CarroceriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.javacar.lojadecarro.support.ErrorMessages.CARROCERIA;
import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarroceriaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CarroceriaControllerTest {
    private static final String URL = "/carrocerias";
    private static final Long ID_CARROCERIA = 1L;
    private static final Long ID_CARROCERIA_EXCEPTION = 99L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarroceriaService carroceriaService;

    @Test
    @DisplayName("Deve criar uma carroceria")
    void deveCriarCarroceria() throws Exception {
        //Arrange
        var request = CarroceriaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        var response = CarroceriaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(carroceriaService.createCarroceria(request))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"))

                .andExpect(jsonPath("$.id").value(ID_CARROCERIA))

                .andExpect(jsonPath("$.nome").value("Hatch"));

        verify(carroceriaService).createCarroceria(request);
    }

    @Test
    @DisplayName("Deve lançar 400 ao criar uma carroceria sem nome")
    void deveLancar400aoCriarCarroceriaSemNome() throws Exception {
        //Arrange
        var request = CarroceriaRequestFactory
                .criarRequest()
                .build();

        // Act + Assert
        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(carroceriaService);

    }

    @Test
    @DisplayName("Deve listar as carrocerias")
    void deveListarAsCarrocerias() throws Exception {
        //Arrange
        var hatch = CarroceriaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        var sedan = CarroceriaResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Sedan")
                .build();

        var response = List.of(hatch, sedan);

        when(carroceriaService.listarCarroceria())
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        get(URL)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(ID_CARROCERIA))
                .andExpect(jsonPath("$.[0].nome").value("Hatch"))
                .andExpect(jsonPath("$.[1].id").value(2L))
                .andExpect(jsonPath("$.[1].nome").value("Sedan"));

        verify(carroceriaService).listarCarroceria();
    }

    @Test
    @DisplayName("Deve buscar uma carroceria por ID")
    void deveBuscarCarroceriaPorId() throws Exception {
        //Arrange
        var id = ID_CARROCERIA;

        var response = CarroceriaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(carroceriaService.findCarroceriaById(id))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        get(URL + "/" + id)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_CARROCERIA))
                .andExpect(jsonPath("$.nome").value("Hatch"));

        verify(carroceriaService).findCarroceriaById(id);
    }

    @Test
    @DisplayName("Deve lançar 404 ao buscar uma carroceria por ID")
    void deveLancar404aoBuscarCarroceriaPorId() throws Exception {
        //Arrange
        var id = ID_CARROCERIA_EXCEPTION;

        when(carroceriaService.findCarroceriaById(id))
                .thenThrow(new CarroceriaException(id));

        //ACT + Assert
        mockMvc.perform(
                        get(URL + "/" + id)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(String.format(ID_NOT_FOUND, CARROCERIA, ID_INVALIDO)));

        verify(carroceriaService).findCarroceriaById(id);
    }

    @Test
    @DisplayName("Deve atualizar a carroceria")
    void deveAtualizaraCarroceria() throws Exception {
        //Arrange
        var id = ID_CARROCERIA;
        var request = CarroceriaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        var response = CarroceriaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(carroceriaService.updateCarroceria(request, id))
                .thenReturn(response);

        //Act + Assert

        mockMvc.perform(
                        put(URL + "/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_CARROCERIA))
                .andExpect(jsonPath("$.nome").value("Hatch"));

        verify(carroceriaService).updateCarroceria(request, id);
    }

    @Test
    @DisplayName("Deve lançar 400 ao atualizar uma carroceria sem nome")
    void deveLancar400aoAtualizarCarroceriaSemNome() throws Exception {
        //Arrange
        var id = ID_CARROCERIA;
        var request = CarroceriaRequestFactory
                .criarRequest()
                .build();

        when(carroceriaService.updateCarroceria(request, id))
                .thenThrow(new CarroceriaException(id));

        // Act + Assert
        mockMvc.perform(
                        put(URL + "/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(carroceriaService);
    }

    @Test
    @DisplayName("Deve lançar 404 ao atulizar uma carroceria")
    void deveLancar404aoAtualizarCarroceria() throws Exception {
        //Arrange
        var id = ID_CARROCERIA_EXCEPTION;
        var request = CarroceriaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        when(carroceriaService.updateCarroceria(request, id))
                .thenThrow(new CarroceriaException(id));

        // Act + Assert
        mockMvc.perform(
                        put(URL + "/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(String.format(ID_NOT_FOUND, CARROCERIA, ID_INVALIDO)));

        verify(carroceriaService).updateCarroceria(request, id);
    }

    @Test
    @DisplayName("Deve deletar a carroceria")
    void deveDeletarCarroceria() throws Exception {
        //Arrange
        var id = ID_CARROCERIA;
        // Act + Assert
        mockMvc.perform(
                delete(URL + "/" + id)
        ).andExpect(status().isNoContent());

        verify(carroceriaService).deleteCarroceria(id);
    }

    @Test
    @DisplayName("Deve lançar 404 ao deletar a carroceria")
    void deveLancar404aoDeletarCarroceria() throws Exception {
        //Arrange
        var id = ID_CARROCERIA_EXCEPTION;
        doThrow(new CarroceriaException(id))
                .when(carroceriaService).deleteCarroceria(id);
        // Act + Assert
        mockMvc.perform(
                        delete(URL + "/" + id)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(String.format(ID_NOT_FOUND, CARROCERIA, ID_INVALIDO)));
        verify(carroceriaService).deleteCarroceria(id);
    }
}
