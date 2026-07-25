package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.modelo.ModeloTestContext;
import com.javacar.lojadecarro.service.ModeloService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.MODELO;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.factory.modelo.ModeloTestContext.criaModeloResponse;
import static com.javacar.lojadecarro.factory.modelo.ModeloTestContext.criaModeloResponse2;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModeloController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller do modelo")
class ModeloControllerTest extends BaseControllerTest {
    private static final String URL = "/modelos";

    @MockitoBean
    private ModeloService modeloService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {
        @Test
        @DisplayName("Deve criar um modelo")
        void deveCriarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloService.criar(cx.request))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPost(URL, cx.request);
            resultado.andExpect(header().exists("Location"));
            assertResult(
                    resultado,
                    status().isCreated(),
                    ID_VALIDO,
                    "Onix",
                    true
            );

            verify(modeloService).criar(cx.request);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao criar um modelo")
        void deveLancar400AoCriarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            //Act + Assert
            var resultado = performPost(URL, cx.requestIncompleto);
            assertStatus400(resultado);

            verifyNoInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve lançar 500 ao criar um modelo")
        void deveLancar500AoCriarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloService.criar(cx.request))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPost(URL, cx.request);
            assertStatus500(resultado);

            verify(modeloService).criar(cx.request);
            verifyNoMoreInteractions(modeloService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve utilizar ATIVAS como status padrão")
        void deveUtilizarAtivasComoStatusPadrao() throws Exception {
            //Arrange
            var response1 = criaModeloResponse(true);
            var response2 = criaModeloResponse2(true);

            var response = List.of(response1, response2);

            when(modeloService.listar(ATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Onix",
                    "Celta",
                    true,
                    true
            );
            verify(modeloService).listar(ATIVAS);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve encaminhar o status informado para a service")
        void deveEncaminharStatusTodas() throws Exception {
            //Arrange
            var response1 = criaModeloResponse(true);
            var response2 = criaModeloResponse2(false);

            var response = List.of(response1, response2);

            when(modeloService.listar(TODAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL, "status", TODAS.toString());
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Onix",
                    "Celta",
                    true,
                    false
            );
            verify(modeloService).listar(TODAS);
            verifyNoMoreInteractions(modeloService);
        }
    }

    @Nested
    @DisplayName("Testes da busca por ID")
    class Buscar {

        @Test
        @DisplayName("Deve buscar um modelo por ID")
        void deveBuscarModeloPorId() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloService.buscarPorId(ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performGet(URL + "/" + ID_VALIDO);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Onix",
                    true
            );

            verify(modeloService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao buscar o modelo")
        void deveLancar404BuscarModelo() throws Exception {
            //Arrange
            when(modeloService.buscarPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(MODELO, ID_VALIDO));
            //Act + Assert
            var resultado = performGet(URL + "/" + ID_VALIDO);
            assertStatus404(resultado, MODELO, ID_VALIDO);

            verify(modeloService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(modeloService);
        }
    }

    @Nested
    @DisplayName("Testes da atualização")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o modelo")
        void deveAtualizarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloService.atualizar(cx.request, ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPut(URL + "/" + ID_VALIDO, cx.request);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Onix",
                    true
            );

            verify(modeloService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao atualizar o modelo sem nome")
        void deveLancar400AtualizarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            //Act + Assert
            var resultado = performPut(URL + "/" + ID_VALIDO, cx.requestIncompleto);
            assertStatus400(resultado);

            verifyNoInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao atualizar o modelo")
        void deveLancar404AtualizarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloService.atualizar(cx.request, ID_VALIDO))
                    .thenThrow(new NotFoundException(MODELO, ID_VALIDO));
            //Act + Assert
            var resultado = performPut(URL + "/" + ID_VALIDO, cx.request);
            assertStatus404(
                    resultado,
                    MODELO,
                    ID_VALIDO
            );

            verify(modeloService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(modeloService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();
            var status = new StatusRequest(true);

            when(modeloService.alterarStatus(ID_VALIDO, status))
                    .thenReturn(cx.response);
            //ACT + assert
            var resultado = performPatch(URL + "/" + ID_VALIDO + "/status", status);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Onix",
                    true);

            verify(modeloService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao alterar status")
        void deveLancar400AoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(null);
            //ACT + assert
            var resultado = performPatch(URL + "/" + ID_VALIDO + "/status", status);

            assertStatus400(resultado);
            verifyNoInteractions(modeloService);

        }

        @Test
        @DisplayName("Deve lançar 404 ao alterar status")
        void deveLancar404aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            when(modeloService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new NotFoundException(MODELO, ID_VALIDO));
            //ACT + assert
            var resultado = performPatch(URL + "/" + ID_VALIDO + "/status", status);
            assertStatus404(resultado,
                    MODELO,
                    ID_VALIDO);

            verify(modeloService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(modeloService);
        }
    }

}
