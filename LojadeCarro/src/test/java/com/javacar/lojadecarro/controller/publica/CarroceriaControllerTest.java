package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.CarroceriaController;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaTestContext;
import com.javacar.lojadecarro.service.CarroceriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.CARROCERIA;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarroceriaController.class)
@DisplayName("Testes da controller da carroceria")
class CarroceriaControllerTest extends BaseControllerTest {
    private static final String URL = "/carrocerias";
    private static final String URL_ID = URL + "/" + ID_VALIDO;

    @MockitoBean
    private CarroceriaService carroceriaService;

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar somente as carrocerias ativas sem autenticação")
        void deveListarSomenteCarroceriasAtivasSemAutenticacao() throws Exception {
            // Arrange
            var cx = new CarroceriaTestContext();

            when(carroceriaService.listarCarroceriasAtivas())
                    .thenReturn(List.of(cx.carroceriaResponse, cx.carroceriaResponse2));

            // Act + Assert
            var resultado = performGet(URL);
            assertList(resultado, ID_VALIDO, 2L, "Hatch", "Sedan", true, true);

            verify(carroceriaService).listarCarroceriasAtivas();
            verifyNoMoreInteractions(carroceriaService);
        }
    }

    @Nested
    @DisplayName("Testes da busca da carroceria")
    class Buscar {
        @Test
        @DisplayName("Deve buscar carroceria ativa sem autenticação")
        void deveBuscarCarroceriaAtivaSemAutenticacao() throws Exception {
            // Arrange
            var cx = new CarroceriaTestContext();

            when(carroceriaService.buscarCarroceriaAtivaPorId(ID_VALIDO))
                    .thenReturn(cx.carroceriaResponse);

            // Act + Assert
            var resultado = performGet(URL_ID);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Hatch", true);

            verify(carroceriaService).buscarCarroceriaAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar uma carroceria ativa não encontrada")
        void deveRetornar404AoBuscarCarroceriaAtivaNaoEncontrada() throws Exception {
            // Arrange
            when(carroceriaService.buscarCarroceriaAtivaPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(CARROCERIA, ID_VALIDO));

            // Act + Assert
            var resultado = performGet(URL_ID);
            assertStatus404(resultado, CARROCERIA, ID_VALIDO);

            verify(carroceriaService).buscarCarroceriaAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }
    }
}
