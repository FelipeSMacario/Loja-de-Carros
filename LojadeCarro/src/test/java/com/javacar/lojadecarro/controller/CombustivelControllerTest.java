package com.javacar.lojadecarro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.combustivel.CombustivelTestContext;
import com.javacar.lojadecarro.service.CombustivelService;
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

import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CombustivelController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller do combustível")
class CombustivelControllerTest {
    private static final String URL = "/combustiveis";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CombustivelService combustivelService;

    @Nested
    @DisplayName("Testes da criação do combustível")
    class Criar {
        @Test
        @DisplayName("Deve criar um combustível")
        void deveCriarUmCombustivel() throws Exception {
            //Arrange
            var combustivelcx = new CombustivelTestContext();

            when(combustivelService.criar(combustivelcx.combustivelRequest))
                    .thenReturn(combustivelcx.combustivelResponse);
            //Act + Assert
            var resultado = mockMvc.perform(
                    post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(combustivelcx.combustivelRequest))
            );
            resultado.andExpect(header().exists("Location"));
            assertResult(resultado,
                    status().isCreated(),
                    ID_VALIDO,
                    "Gasolina",
                    true);

            verify(combustivelService).criar(combustivelcx.combustivelRequest);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao cadastrar um combustível ao cadastrar sem nome")
        void deveLancarErroCadastroCombustivel() throws Exception {
            //Arrange
            var combustivelcx = new CombustivelTestContext();

            //Act + Assert
            var resultado = mockMvc.perform(
                    post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(combustivelcx.combustivelRequestIncompleto))
            );
            assertStatus400(resultado);

            verifyNoInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve lançar 500 ao cadastrar o combustível vazio")
        void deveRetornar500QuandoOcorrerErroInternoAoCriarCombustivel() throws Exception {
            //Arrange
            var combustivelcx = new CombustivelTestContext();
            when(combustivelService.criar(combustivelcx.combustivelRequest))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = mockMvc.perform(
                    post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(combustivelcx.combustivelRequest))
            );
            assertStatus500(resultado);
            verify(combustivelService).criar(combustivelcx.combustivelRequest);
            verifyNoMoreInteractions(combustivelService);
        }
    }

    @Nested
    @DisplayName("Testes da listagem de combustiveis")
    class Listar {
        @Test
        @DisplayName("Deve utilizar ATIVAS como status padrão")
        void deveUtilizarAtivasComStatusPadrao() throws Exception {
            //Arrange
            var combustivelResponse1 = CombustivelTestContext
                    .criaCombustivelResponse(true);
            var combustivelResponse2 = CombustivelTestContext
                    .criaCombustivelResponse2(true);

            var response = List.of(combustivelResponse1, combustivelResponse2);
            when(combustivelService.listar(ATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = mockMvc.perform(
                    get(URL)
            );

            assertList(resultado,
                    ID_VALIDO,
                    2L,
                    "Gasolina",
                    "Eletrico",
                    true,
                    true);

            verify(combustivelService).listar(ATIVAS);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve encaminhar o status informado para a service")
        void deveEncaminharStatusInativo() throws Exception {
            //Arrange
            var carroceriaResponse1 = CombustivelTestContext
                    .criaCombustivelResponse(true);
            var carroceriaResponse2 = CombustivelTestContext
                    .criaCombustivelResponse2(false);

            var response = List.of(carroceriaResponse1, carroceriaResponse2);
            when(combustivelService.listar(TODAS))
                    .thenReturn(response);
            //Act + Assert

            var resultado = mockMvc.perform(
                    get(URL)
                            .param("status", TODAS.toString())
            );

            assertList(resultado,
                    ID_VALIDO,
                    2L,
                    "Gasolina",
                    "Eletrico",
                    true,
                    false);

            verify(combustivelService).listar(TODAS);
            verifyNoMoreInteractions(combustivelService);
        }
    }

    @Nested
    @DisplayName("Testes para buscar combustível")
    class Buscar {
        @Test
        @DisplayName("Deve buscar combustivel por ID")
        void deveBuscarCombustivelPorId() throws Exception {
            //Arrange
            var combustivelcx = new CombustivelTestContext();

            when(combustivelService.buscaPorId(ID_VALIDO))
                    .thenReturn(combustivelcx.combustivelResponse);
            //Act + Assert
            var resultado = mockMvc.perform(
                    get(URL + "/" + ID_VALIDO)
            );

            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Gasolina",
                    true
            );

            verify(combustivelService).buscaPorId(ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao buscar o combustível")
        void deveRetornar404AoBuscarCombustivelPorId() throws Exception {
            //Arrange
            when(combustivelService.buscaPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, ID_VALIDO));

            //Act + Assert
            var resultado = mockMvc.perform(
                    get(URL + "/" + ID_VALIDO)
            );
            assertStatus404(
                    resultado,
                    COMBUSTIVEL,
                    ID_VALIDO
            );
            verify(combustivelService).buscaPorId(ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }
    }

    @Nested
    @DisplayName("Testes para atualizar combustível")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o combustível")
        void deveAtualizarCombustivel() throws Exception {
            //Arrange
            var combustivelcx = new CombustivelTestContext();

            when(combustivelService.atualizar(combustivelcx.combustivelRequest, ID_VALIDO))
                    .thenReturn(combustivelcx.combustivelResponse);
            //Act + Assert
            var resultado = mockMvc.perform(
                    put(URL + "/" + ID_VALIDO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(combustivelcx.combustivelRequest))
            );

            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Gasolina",
                    true
            );

            verify(combustivelService).atualizar(combustivelcx.combustivelRequest, ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao atualizar o combustivel sem nome")
        void deveAtualizarCombustivelSemNome() throws Exception {
            //Arrange
            var combustivelcx = new CombustivelTestContext();

            //Act + Assert
            var resultado = mockMvc.perform(
                    put(URL + "/" + ID_VALIDO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(combustivelcx
                                    .combustivelRequestIncompleto))
            );
            assertStatus400(resultado);

            verifyNoInteractions(combustivelService);
        }


        @Test
        @DisplayName("Deve lançar 404 ao atualizar o combustível")
        void deveLancar404NaoAtualizarCombustivel() throws Exception {
            //Arrange
            var combustivelcx = new CombustivelTestContext();

            when(combustivelService.atualizar(combustivelcx.combustivelRequest, ID_VALIDO))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, ID_VALIDO));
            //Act + Assert
            var resultado = mockMvc.perform(
                    put(URL + "/" + ID_VALIDO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(combustivelcx.combustivelRequest))
            );

            assertStatus404(resultado, COMBUSTIVEL, ID_VALIDO);

            verify(combustivelService).atualizar(combustivelcx.combustivelRequest, ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }

    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            //Arrange
            var combustivelcx = new CombustivelTestContext();
            var status = new StatusRequest(true);

            when(combustivelService.alterarStatus(ID_VALIDO, status))
                    .thenReturn(combustivelcx.combustivelResponse);
            //ACT + assert
            var resultado = mockMvc.perform(
                    patch(URL + "/" + ID_VALIDO + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(status))
            );
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Gasolina",
                    true);

            verify(combustivelService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao alterar status")
        void deveLancar400aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(null);
            //ACT + assert
            var resultado = mockMvc.perform(
                    patch(URL + "/" + ID_VALIDO + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(status))
            );

            assertStatus400(resultado);
            verifyNoInteractions(combustivelService);

        }

        @Test
        @DisplayName("Deve lançar 404 ao alterar status")
        void deveLancar404aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            when(combustivelService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, ID_VALIDO));
            //ACT + assert
            var resultado = mockMvc.perform(
                    patch(URL + "/" + ID_VALIDO + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(status))
            );
            assertStatus404(resultado,
                    COMBUSTIVEL,
                    ID_VALIDO);

            verify(combustivelService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(combustivelService);
        }
    }
}
