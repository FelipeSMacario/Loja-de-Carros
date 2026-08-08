package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.controller.publico.OpcionalController;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.opcional.OpcionalTestContext;
import com.javacar.lojadecarro.service.OpcionalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.*;
import static com.javacar.lojadecarro.factory.opcional.OpcionalTestContext.criaOpcionalResponse;
import static com.javacar.lojadecarro.factory.opcional.OpcionalTestContext.criaOpcionalResponse2;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpcionalController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller do opcional")
class OpcionalControllerTest extends BaseControllerTest {
    private static final String URL = "/opcionais";
    private static final String URL_ID = URL + "/" + ID_VALIDO;

    @MockitoBean
    private OpcionalService opcionalService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {
        @Test
        @DisplayName("Deve cadastrar um opcional")
        void deveCadastrarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalService.criar(cx.request))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPost(URL, cx.request);
            resultado.andExpect(header().exists("Location"));
            assertResult(resultado, status().isCreated(), ID_VALIDO, "Freio ABS", true);

            verify(opcionalService).criar(cx.request);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao criar um opcional")
        void deveLancar400AoCriarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            //Act + Assert
            var resultado = performPost(URL, cx.requestIncompleto);
            assertStatus400(resultado);

            verifyNoInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve lançar 500 ao criar um opcional")
        void deveLancar500AoCriarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalService.criar(cx.request))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPost(URL, cx.request);
            assertStatus500(resultado);

            verify(opcionalService).criar(cx.request);
            verifyNoMoreInteractions(opcionalService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {

        @Test
        @DisplayName("Deve utilizar ATIVAS como status padrão")
        void deveUtilizarAtivasComoStatusPadrao() throws Exception {
            //Arrange
            var response1 = criaOpcionalResponse(true);
            var response2 = criaOpcionalResponse2(true);

            var response = List.of(response1, response2);

            when(opcionalService.listar(ATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Freio ABS",
                    "Automatico",
                    true,
                    true
            );
            verify(opcionalService).listar(ATIVAS);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve encaminhar o status informado para a service")
        void deveEncaminharStatusTodas() throws Exception {
            //Arrange
            var response1 = criaOpcionalResponse(true);
            var response2 = criaOpcionalResponse2(false);

            var response = List.of(response1, response2);

            when(opcionalService.listar(TODAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL, "status", TODAS.toString());
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Freio ABS",
                    "Automatico",
                    true,
                    false
            );
            verify(opcionalService).listar(TODAS);
            verifyNoMoreInteractions(opcionalService);
        }
    }

    @Nested
    @DisplayName("Testes da busca por ID")
    class Buscar {

        @Test
        @DisplayName("Deve buscar um opcional por ID")
        void deveBuscarUmOpcionalPorID() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalService.buscarPorId(ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performGet(URL_ID);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Freio ABS", true);

            verify(opcionalService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar um opcional por ID")
        void deveRetornar404aoBuscarUmOpcionalPorID() throws Exception {
            //Arrange

            when(opcionalService.buscarPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(OPCIONAL, ID_VALIDO));
            //Act + Assert
            var resultado = performGet(URL_ID);
            assertStatus404(resultado, OPCIONAL, ID_VALIDO);

            verify(opcionalService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);
        }
    }

    @Nested
    @DisplayName("Testes da atualização")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar o opcional")
        void deveAtualizarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalService.atualizar(cx.request, ID_VALIDO))
                    .thenReturn(cx.response);

            //Act + Assert
            var resultado = performPut(URL_ID, cx.request);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Freio ABS", true);

            verify(opcionalService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);
        }


        @Test
        @DisplayName("Deve lançar 404 ao atualizar opcional")
        void deveLancar404AoAtualizarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalService.atualizar(cx.request, ID_VALIDO))
                    .thenThrow(new NotFoundException(OPCIONAL, ID_VALIDO));

            //Act + Assert
            var resultado = performPut(URL_ID, cx.request);
            assertStatus404(resultado, OPCIONAL, ID_VALIDO);

            verify(opcionalService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);

        }

        @Test
        @DisplayName("Deve lançar 400 ao atualizar opcional")
        void deveLancar400aoAtualizarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();
            //Act + Assert
            var resultado = performPut(URL_ID, cx.requestIncompleto);
            assertStatus400(resultado);
            verifyNoInteractions(opcionalService);
        }
    }


    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();
            var status = new StatusRequest(true);

            when(opcionalService.alterarStatus(ID_VALIDO, status))
                    .thenReturn(cx.response);
            //ACT + assert
            var resultado = performPatch(URL_ID + "/status", status);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Freio ABS",
                    true);

            verify(opcionalService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao alterar status")
        void deveLancar400AoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(null);
            //ACT + assert
            var resultado = performPatch(URL_ID + "/status", status);

            assertStatus400(resultado);
            verifyNoInteractions(opcionalService);

        }

        @Test
        @DisplayName("Deve lançar 404 ao alterar status")
        void deveLancar404aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            when(opcionalService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new NotFoundException(OPCIONAL, ID_VALIDO));
            //ACT + assert
            var resultado = performPatch(URL_ID + "/status", status);
            assertStatus404(resultado,
                    OPCIONAL,
                    ID_VALIDO);

            verify(opcionalService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(opcionalService);
        }
    }

}
