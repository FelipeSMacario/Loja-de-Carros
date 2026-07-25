package com.javacar.lojadecarro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaResponseFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaTestContext;
import com.javacar.lojadecarro.service.CarroceriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.CARROCERIA;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.INATIVAS;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertStatus400;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertStatus404;
import static com.javacar.lojadecarro.factory.helper.CarroceriaHelper.assertResultadoCarroceria;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarroceriaController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller da carroceria")
class CarroceriaControllerTest {
    private static final String URL = "/carrocerias";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarroceriaService carroceriaService;

    @Nested
    @DisplayName("Testes da criação da carroceria")
    class Criar {
        @Test
        @DisplayName("Deve criar uma carroceria")
        void deveCriarCarroceria() throws Exception {
            //Arrange
            var carroceriacx = new CarroceriaTestContext();

            when(carroceriaService.criar(carroceriacx.carroceriaRequest))
                    .thenReturn(carroceriacx.carroceriaResponse);

            // Act + Assert
            mockMvc.perform(
                            post(URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(carroceriacx.carroceriaRequest))
                    ).andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").value(ID_VALIDO))
                    .andExpect(jsonPath("$.nome").value("Hatch"))
                    .andExpect(jsonPath("$.ativo").value(true));

            verify(carroceriaService).criar(carroceriacx.carroceriaRequest);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao criar uma carroceria sem nome")
        void deveLancar400aoCriarCarroceriaSemNome() throws Exception {
            //Arrange
            var carroceriacx = new CarroceriaTestContext();

            // Act + Assert
            var resultado = mockMvc.perform(
                    post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper
                                    .writeValueAsString(carroceriacx.carroceriaRequestIncompleta))
            );
            assertStatus400(resultado);

            verifyNoInteractions(carroceriaService);

        }
    }

    @Nested
    @DisplayName("Testes da listagem de carrocerias")
    class Listar {

        @Test
        @DisplayName("Deve utilizar ATIVAS como status padrão")
        void deveListarAsCarroceriasAtivas() throws Exception {
            //Arrange
            var carroceriacx = new CarroceriaTestContext();

            var response = List.of(carroceriacx.carroceriaResponse,
                    carroceriacx.carroceriaResponse2);

            when(carroceriaService.listar(ATIVAS))
                    .thenReturn(response);

            // Act + Assert
            mockMvc.perform(
                            get(URL)
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$.[0].id").value(ID_VALIDO))
                    .andExpect(jsonPath("$.[0].nome").value("Hatch"))
                    .andExpect(jsonPath("$.[0].ativo").value(true))
                    .andExpect(jsonPath("$.[1].id").value(2L))
                    .andExpect(jsonPath("$.[1].nome").value("Sedan"))
                    .andExpect(jsonPath("$.[1].ativo").value(true));

            verify(carroceriaService).listar(ATIVAS);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve encaminhar o status informado para a service")
        void deveListarAsCarroceriasInativas() throws Exception {
            //Arrange
            var response1 = CarroceriaResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comAtivo(false)
                    .build();

            var response2 = CarroceriaResponseFactory
                    .criarResponse()
                    .comId(2L)
                    .comNome("Sedan")
                    .comAtivo(false)
                    .build();

            var response = List.of(response1, response2);

            when(carroceriaService.listar(INATIVAS))
                    .thenReturn(response);

            // Act + Assert
            mockMvc.perform(
                            get(URL)
                                    .param("status", INATIVAS.toString())
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$.[0].id").value(ID_VALIDO))
                    .andExpect(jsonPath("$.[0].nome").value("Hatch"))
                    .andExpect(jsonPath("$.[0].ativo").value(false))
                    .andExpect(jsonPath("$.[1].id").value(2L))
                    .andExpect(jsonPath("$.[1].nome").value("Sedan"))
                    .andExpect(jsonPath("$.[1].ativo").value(false));

            verify(carroceriaService).listar(INATIVAS);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao informar um status invalido")
        void deveLancar400aoInformarStatusInvalido() throws Exception {
            //ACT + Assert
            var resultado = mockMvc.perform(
                    get(URL)
                            .param("status", "123")
            );
            assertStatus400(resultado);
            verifyNoInteractions(carroceriaService);
        }
    }

    @Nested
    @DisplayName("Testes da busca da carroceria")
    class Buscar {
        @Test
        @DisplayName("Deve buscar uma carroceria por ID")
        void deveBuscarCarroceriaPorId() throws Exception {
            //Arrange

            var carroceriacx = new CarroceriaTestContext();

            when(carroceriaService.buscaPorId(ID_VALIDO))
                    .thenReturn(carroceriacx.carroceriaResponse);

            // Act + Assert
            var resultado = mockMvc.perform(
                    get(URL + "/" + ID_VALIDO)
            );

            assertResultadoCarroceria(resultado);

            verify(carroceriaService).buscaPorId(ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }


        @Test
        @DisplayName("Deve lançar 404 ao buscar uma carroceria por ID")
        void deveLancar404aoBuscarCarroceriaPorId() throws Exception {
            //Arrange
            when(carroceriaService.buscaPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(CARROCERIA, ID_VALIDO));

            //ACT + Assert
            var resultado = mockMvc.perform(
                    get(URL + "/" + ID_VALIDO)
            );

            assertStatus404(resultado, CARROCERIA, ID_VALIDO);

            verify(carroceriaService).buscaPorId(ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }

    }

    @Nested
    @DisplayName("Testes da atualização da carroceria")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar a carroceria")
        void deveAtualizaraCarroceria() throws Exception {
            //Arrange
            var carroceriacx = new CarroceriaTestContext();

            when(carroceriaService.atualizar(carroceriacx.carroceriaRequest, ID_VALIDO))
                    .thenReturn(carroceriacx.carroceriaResponse);

            //Act + Assert

            var resultado = mockMvc.perform(
                    put(URL + "/" + ID_VALIDO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(carroceriacx.carroceriaRequest))
            );
            assertResultadoCarroceria(resultado);

            verify(carroceriaService).atualizar(carroceriacx.carroceriaRequest, ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao atualizar uma carroceria sem nome")
        void deveLancar400aoAtualizarCarroceriaSemNome() throws Exception {
            //Arrange
            var carroceriacx = new CarroceriaTestContext();

            // Act + Assert
            var resultado = mockMvc.perform(
                    put(URL + "/" + ID_VALIDO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(carroceriacx.carroceriaRequestIncompleta))
            );
            assertStatus400(resultado);

            verifyNoInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao atulizar uma carroceria")
        void deveLancar404aoAtualizarCarroceria() throws Exception {
            //Arrange
            var carroceriacx = new CarroceriaTestContext();

            when(carroceriaService.atualizar(carroceriacx.carroceriaRequest, ID_VALIDO))
                    .thenThrow(new NotFoundException(CARROCERIA, ID_VALIDO));

            // Act + Assert
            var resultado = mockMvc.perform(
                    put(URL + "/" + ID_VALIDO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(carroceriacx.carroceriaRequest))
            );

            assertStatus404(resultado, CARROCERIA, ID_VALIDO);

            verify(carroceriaService).atualizar(carroceriacx.carroceriaRequest, ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }

    }

    @Nested
    @DisplayName("Testes da alteração do status da carroceria")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status da carroceria")
        void deveAlterarStatusDaCarroceria() throws Exception {
            //Arrange
            var carroceriacx = new CarroceriaTestContext();
            var status = new StatusRequest(true);

            when(carroceriaService.alterarStatus(ID_VALIDO, status))
                    .thenReturn(carroceriacx.carroceriaResponse);
            //ACT + assert
            var resultado = mockMvc.perform(
                    patch(URL + "/" + ID_VALIDO + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(status))
            );
            assertResultadoCarroceria(resultado);

            verify(carroceriaService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao alterar status")
        void deveLancar400aoAlterarStatusDaCarroceria() throws Exception {
            //Arrange
            var status = new StatusRequest(null);
            //ACT + assert
            var resultado = mockMvc.perform(
                    patch(URL + "/" + ID_VALIDO + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(status))
            );

            assertStatus400(resultado);
            verifyNoInteractions(carroceriaService);

        }

        @Test
        @DisplayName("Deve lançar 404 ao alterar status")
        void deveLancar404aoAlterarStatusDaCarroceria() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            when(carroceriaService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new NotFoundException(CARROCERIA, ID_VALIDO));
            //ACT + assert
            var resultado = mockMvc.perform(
                    patch(URL + "/" + ID_VALIDO + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(status))
            );
            assertStatus404(resultado,
                    CARROCERIA,
                    ID_VALIDO);

            verify(carroceriaService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(carroceriaService);
        }
    }

}
