package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.OpcionalController;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.opcional.OpcionalTestContext;
import com.javacar.lojadecarro.service.OpcionalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;
import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.*;
import static com.javacar.lojadecarro.factory.opcional.OpcionalTestContext.criaOpcionalResponse;
import static com.javacar.lojadecarro.factory.opcional.OpcionalTestContext.criaOpcionalResponse2;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpcionalController.class)
@DisplayName("Testes da controller do opcional")
class OpcionalControllerTest extends BaseControllerTest {
    private static final String URL = "/opcionais";
    private static final String URL_ID = URL + "/" + ID_VALIDO;

    @MockitoBean
    private OpcionalService opcionalService;

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {

        @Test
        @DisplayName("Deve listar somente os opcionais ativos")
        void deveListarSomenteOpcionaisAtivos() throws Exception {
            //Arrange
            var response1 = criaOpcionalResponse(true);
            var response2 = criaOpcionalResponse2(true);

            var response = List.of(response1, response2);

            when(opcionalService.listarOpcionaisAtivas())
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Freio Abs",
                    "Automatico",
                    true,
                    true
            );
            verify(opcionalService).listarOpcionaisAtivas();
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

            when(opcionalService.buscarOpcionalAtivoPorId(ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performGet(URL_ID);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Freio Abs", true);

            verify(opcionalService).buscarOpcionalAtivoPorId(ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar um opcional por ID")
        void deveRetornar404aoBuscarUmOpcionalPorID() throws Exception {
            //Arrange

            when(opcionalService.buscarOpcionalAtivoPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(OPCIONAL, ID_VALIDO));
            //Act + Assert
            var resultado = performGet(URL_ID);
            assertStatus404(resultado, OPCIONAL, ID_VALIDO);

            verify(opcionalService).buscarOpcionalAtivoPorId(ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);
        }
    }

}
