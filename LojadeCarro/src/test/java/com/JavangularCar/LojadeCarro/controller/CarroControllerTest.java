package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.dto.request.CarroRequest;
import com.JavangularCar.LojadeCarro.dto.request.FiltrarCamposCarroRequest;
import com.JavangularCar.LojadeCarro.dto.response.CarroResponse;
import com.JavangularCar.LojadeCarro.exception.CarroException;
import com.JavangularCar.LojadeCarro.factory.carro.CarroRequestFactory;
import com.JavangularCar.LojadeCarro.factory.carro.CarroResponseFactory;
import com.JavangularCar.LojadeCarro.service.CarroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.JavangularCar.LojadeCarro.support.ErrorMessages.CARRO;
import static com.JavangularCar.LojadeCarro.support.ErrorMessages.ID_NOT_FOUND;
import static com.JavangularCar.LojadeCarro.support.TestConstants.ID_INVALIDO;
import static com.JavangularCar.LojadeCarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarroController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CarroControllerTest {
    private static final String URL = "/carro";
    public static final String VENDIDO = "/vendido/";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarroService carroService;

    @Test
    @DisplayName("Deve cadastrar uma compra")
    void deveCadastrarCompra() throws Exception {
        //Arrange
        var request = criarCarroRequest();
        var response = criarCarroResponse();

        when(carroService.createCarro(request))
                .thenReturn(response);

        //Act + Assert

        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(carroService).createCarro(request);
        verifyNoMoreInteractions(carroService);
    }


    @Test
    @DisplayName("Deve lançar 400 ao cadastrar um carro")
    void deveLancar400aoCadastrarCompra() throws Exception {
        //Arrange
        var request = CarroRequestFactory
                .criarRequest()
                .comCores(3L)
                .build();

        //Act + Assert
        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(carroService);
    }

    @Test
    @DisplayName("Deve listar os carros")
    void deveListarOsCarros() throws Exception {
        //Arrange
        var response = CarroResponseFactory.criarResponse()
                .comTodosOsCampos()
                .build();

        Page<CarroResponse> page =
                new PageImpl<>(List.of(response));

        when(carroService.listarCarros(any(Pageable.class)))
                .thenReturn(page);
        //Act + Assert

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(ID_VALIDO))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(carroService).listarCarros(any(Pageable.class));

        verifyNoMoreInteractions(carroService);
    }

    @Test
    @DisplayName("Deve filtrar um carro por ID")
    void deveFiltrarCarroPorID() throws Exception {
        //Arrange
        var response = criarCarroResponse();

        when(carroService.findCarroById(ID_VALIDO))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        get(URL + "/" + ID_VALIDO)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_VALIDO));

        verify(carroService).findCarroById(ID_VALIDO);
        verifyNoMoreInteractions(carroService);
    }

    @Test
    @DisplayName("Deve lançar 404 ao buscar um carro por ID")
    void deveLancar404aoBuscarCarroPorID() throws Exception {
        //Arrange
        when(carroService.findCarroById(ID_INVALIDO))
                .thenThrow(new CarroException(ID_INVALIDO));
        //Act + Assert
        mockMvc.perform(
                        get(URL + "/" + ID_INVALIDO)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, CARRO, ID_INVALIDO)));

        verify(carroService).findCarroById(ID_INVALIDO);
        verifyNoMoreInteractions(carroService);
    }

    @Test
    @DisplayName("Deve atualizar o carro pod ID")
    void deveAtualizarCarroPorID() throws Exception {
        //Arrange
        var request = criarCarroRequest();
        var response = criarCarroResponse();

        when(carroService.updateCarro(request, ID_VALIDO))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        put(URL + "/" + ID_VALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_VALIDO));

        verify(carroService).updateCarro(request, ID_VALIDO);
        verifyNoMoreInteractions(carroService);
    }

    @Test
    @DisplayName("Deve lançar 400 ao atualizar um carro")
    void deveLancar400aoAtualizarCarroPorID() throws Exception {
        //Arrange
        var request = CarroRequestFactory
                .criarRequest()
                .comCores(4L)
                .build();
        //Act + Assert
        mockMvc.perform(
                        put(URL + "/" + ID_VALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(carroService);
    }

    @Test
    @DisplayName("Deve lançar 404 ao atualizar um carro")
    void deveLancar404aoAtualizarCarroPorID() throws Exception {
        //Arrange
        var request = criarCarroRequest();

        when(carroService.updateCarro(request, ID_INVALIDO))
                .thenThrow(new CarroException(ID_INVALIDO));
        //Act + Assert
        mockMvc.perform(
                        put(URL + "/" + ID_INVALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, CARRO, ID_INVALIDO)));

        verify(carroService).updateCarro(request, ID_INVALIDO);
    }

    @Test
    @DisplayName("Deve marcar o carro como vendido")
    void deveMarcarCarroComoVendido() throws Exception {
        //Arrange
        var request = criarCarroRequest();
        var response = criarCarroResponse();

        when(carroService.marcarVendido(request, ID_VALIDO))
                .thenReturn(response);
        //Act + Assert
        mockMvc.perform(
                        put(URL + VENDIDO + ID_VALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_VALIDO));

        verify(carroService).marcarVendido(request, ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar 400 o carro como vendido")
    void deveLancar400aoMarcarCarroComoVendido() throws Exception {
        //Arrange
        var request = CarroRequestFactory
                .criarRequest()
                .comCores(6L)
                .build();

        //Act + Assert
        mockMvc.perform(
                        put(URL + VENDIDO + ID_INVALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(carroService);
    }

    @Test
    @DisplayName("Deve lançar 404 o carro como vendido")
    void deveLancar404aoMarcarCarroComoVendido() throws Exception {
        //Arrange
        var request = CarroRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        when(carroService.marcarVendido(request, ID_INVALIDO))
                .thenThrow(new CarroException(ID_INVALIDO));
        //Act + Assert
        mockMvc.perform(
                        put(URL + VENDIDO + ID_INVALIDO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, CARRO, ID_INVALIDO)));
        verify(carroService).marcarVendido(request, ID_INVALIDO);
    }

    @Test
    @DisplayName("Deve deletar o carro por ID")
    void deveDeletarCarroPorID() throws Exception {
        //Arrange
        doNothing().when(carroService).deleteCarro(ID_VALIDO);
        //Act + Assert
        mockMvc.perform(delete(URL + "/" + ID_VALIDO)
        ).andExpect(status().isNoContent());

        verify(carroService).deleteCarro(ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar 404 ao deletar o carro por ID")
    void deveLancar404aoDeletarCarroPorId() throws Exception {
        //Arrange
        doThrow(new CarroException(ID_INVALIDO))
                .when(carroService).deleteCarro(ID_INVALIDO);
        //Act + Assert
        mockMvc.perform(
                        delete(URL + "/" + ID_INVALIDO)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(String.format(ID_NOT_FOUND, CARRO, ID_INVALIDO)));
    }

    @Test
    @DisplayName("Deve buscar os carros via search")
    void deveBuscarOsCarrosViaSearch() throws Exception {
        //Arrange
        var campos = new FiltrarCamposCarroRequest(null, null, 2000, null, null, null, null);
        var response = CarroResponseFactory.criarResponse()
                .comTodosOsCampos()
                .build();

        Page<CarroResponse> page =
                new PageImpl<>(List.of(response));

        when(carroService.filtrarCampos(any(), any()))
                .thenReturn(page);
        //Act + Assert
        mockMvc.perform(get(URL + "/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(ID_VALIDO))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(carroService).filtrarCampos(any(), any());
    }

    private CarroRequest criarCarroRequest() {
        return CarroRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    private CarroResponse criarCarroResponse() {
        return CarroResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }
}
