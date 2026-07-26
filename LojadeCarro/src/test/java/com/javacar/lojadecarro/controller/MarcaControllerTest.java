package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.marca.MarcaTestContext;
import com.javacar.lojadecarro.service.MarcaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.MARCA;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.helper.MarcaHelper.*;
import static com.javacar.lojadecarro.factory.marca.MarcaTestContext.criaMarcaResponse;
import static com.javacar.lojadecarro.factory.marca.MarcaTestContext.criaMarcaResponse2;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarcaController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller da marca")
class MarcaControllerTest extends BaseControllerTest {
    private static final String URL = "/marcas";

    @MockitoBean
    private MarcaService marcaService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {

        @Test
        @DisplayName("Deve criar uma marca")
        void deveCriarMarca() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaService.criar(cx.request))
                    .thenReturn(cx.response);
            // Act + Assert
            var resultado = performPost(URL, cx.request);
            resultado.andExpect(header().exists("Location"));
            resultado.andExpect(jsonPath("$.url").value("https://www.google.com"));
            assertResult(resultado, status().isCreated(), ID_VALIDO, "Ford", true);

            verify(marcaService).criar(cx.request);
            verifyNoMoreInteractions(marcaService);

        }

        @Test
        @DisplayName("Deve retornar 400 quando o nome não for informado")
        void deveRetornarBadRequestQuandoNomeNaoForInformado() throws Exception {
            // Arrange
            var cx = new MarcaTestContext();
            // Act + Assert
            var resultado = performPost(URL, cx.requestIncompleta);
            assertStatus400(resultado);

            verifyNoInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve lançar 500 ao criar uma marca")
        void deveLancar500AoCriarMarca() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaService.criar(cx.request))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPost(URL, cx.request);
            assertStatus500(resultado);

            verify(marcaService).criar(cx.request);
            verifyNoMoreInteractions(marcaService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve utilizar ATIVAS como status padrão")
        void deveUtilizarAtivasComoStatusPadrao() throws Exception {
            //Arrange
            var response1 = criaMarcaResponse(true);
            var response2 = criaMarcaResponse2(true);

            var response = List.of(response1, response2);

            when(marcaService.listar(ATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Ford",
                    "Fiat",
                    true,
                    true
            );
            verify(marcaService).listar(ATIVAS);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve encaminhar o status informado para a service")
        void deveEncaminharStatusTodas() throws Exception {
            //Arrange
            var response1 = criaMarcaResponse(true);
            var response2 = criaMarcaResponse2(false);

            var response = List.of(response1, response2);

            when(marcaService.listar(TODAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL, "status", TODAS.toString());
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Ford",
                    "Fiat",
                    true,
                    false
            );
            verify(marcaService).listar(TODAS);
            verifyNoMoreInteractions(marcaService);
        }


    }

    @Nested
    @DisplayName("Testes da busca por ID")
    class Buscar {
        @Test
        @DisplayName("Deve buscar marca por ID")
        void deveBuscarMarcaPorId() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaService.buscarPorId(ID_VALIDO))
                    .thenReturn(cx.response);

            // Act + Assert
            var resultado = performGet(URL + "/" + ID_VALIDO);
            resultado.andExpect(jsonPath("$.url").value("https://www.google.com"));
            assertResult(resultado, status().isOk(), ID_VALIDO, "Ford", true);

            verify(marcaService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao buscar uma marca por ID")
        void deveLancar404AoBuscarMarcaPorId() throws Exception {
            //Arrange
            when(marcaService.buscarPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(MARCA, ID_VALIDO));

            // Act + Assert
            var resultado = performGet(URL + "/" + ID_VALIDO);
            assertStatus404(resultado, MARCA, ID_VALIDO);

            verify(marcaService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }
    }

    @Nested
    @DisplayName("Testes da atualização")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar a marca por ID")
        void deveAtualizarMarcaPorId() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaService.atualizar(cx.request, ID_VALIDO))
                    .thenReturn(cx.response);

            // Act + Assert
            var resultado = performPut(URL + "/" + ID_VALIDO, cx.request);
            resultado.andExpect(jsonPath("$.url").value("https://www.google.com"));
            assertResult(resultado, status().isOk(), ID_VALIDO, "Ford", true);

            verify(marcaService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao atualizar uma merca")
        void deveLancar404AoAtualizarMarcaPorId() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaService.atualizar(cx.request, ID_VALIDO))
                    .thenThrow(new NotFoundException(MARCA, ID_VALIDO));
            // Act + Assert
            var resultado = performPut(URL + "/" + ID_VALIDO, cx.request);
            assertStatus404(resultado, MARCA, ID_VALIDO);

            verify(marcaService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve lançar 400 quando o nome nao for preenchido na atualização da marca")
        void deveLancar400NaAtualizarMarcaPorId() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            // Act + Assert
            var resultado = performPut(URL + "/" + ID_VALIDO, cx.requestIncompleta);
            assertStatus400(resultado);

            verifyNoInteractions(marcaService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();
            var status = new StatusRequest(true);

            when(marcaService.alterarStatus(ID_VALIDO, status))
                    .thenReturn(cx.response);
            //ACT + assert
            var resultado = performPatch(URL + "/" + ID_VALIDO + "/status", status);
            resultado.andExpect(jsonPath("$.url").value("https://www.google.com"));
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Ford",
                    true);

            verify(marcaService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao alterar status")
        void deveLancar400AoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(null);
            //ACT + assert
            var resultado = performPatch(URL + "/" + ID_VALIDO + "/status", status);

            assertStatus400(resultado);
            verifyNoInteractions(marcaService);

        }

        @Test
        @DisplayName("Deve lançar 404 ao alterar status")
        void deveLancar404aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            when(marcaService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new NotFoundException(MARCA, ID_VALIDO));
            //ACT + assert
            var resultado = performPatch(URL + "/" + ID_VALIDO + "/status", status);
            assertStatus404(resultado,
                    MARCA,
                    ID_VALIDO);

            verify(marcaService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(marcaService);
        }
    }

}
