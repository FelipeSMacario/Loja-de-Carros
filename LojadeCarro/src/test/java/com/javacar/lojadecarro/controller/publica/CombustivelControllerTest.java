package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.CombustivelController;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.combustivel.CombustivelTestContext;
import com.javacar.lojadecarro.service.CombustivelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;
import static com.javacar.lojadecarro.factory.combustivel.CombustivelTestContext.criaCombustivelResponse;
import static com.javacar.lojadecarro.factory.combustivel.CombustivelTestContext.criaCombustivelResponse2;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CombustivelController.class)
@DisplayName("Testes da controller do combustível")
class CombustivelControllerTest extends BaseControllerTest {
    private static final String URL = "/combustiveis";
    private static final String URL_ID = URL + "/" + ID_VALIDO;

    @MockitoBean
    private CombustivelService combustivelService;

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar somente os combustíveis ativos sem autenticação")
        void deveListarSomenteCombustiveisAtivosSemAutenticacao() throws Exception {
            // Arrange
            var response1 = criaCombustivelResponse(true);
            var response2 = criaCombustivelResponse2(true);

            when(combustivelService.listarCombustiveisAtivas())
                    .thenReturn(List.of(response1, response2));

            // Act + Assert
            var resultado = performGet(URL);
            assertList(resultado, ID_VALIDO, 2L, "Gasolina", "Eletrico", true, true);

            verify(combustivelService).listarCombustiveisAtivas();
            verifyNoMoreInteractions(combustivelService);
        }
    }

    @Nested
    @DisplayName("Testes da busca do combustível")
    class Buscar {
        @Test
        @DisplayName("Deve buscar combustível ativo sem autenticação")
        void deveBuscarCombustivelAtivoSemAutenticacao() throws Exception {
            // Arrange
            var cx = new CombustivelTestContext();

            when(combustivelService.buscarCombustivelAtivaPorId(ID_VALIDO))
                    .thenReturn(cx.combustivelResponse);

            // Act + Assert
            var resultado = performGet(URL_ID);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Gasolina", true);

            verify(combustivelService).buscarCombustivelAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar um combustível ativo não encontrado")
        void deveRetornar404AoBuscarCombustivelAtivoNaoEncontrado() throws Exception {
            // Arrange
            when(combustivelService.buscarCombustivelAtivaPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, ID_VALIDO));

            // Act + Assert
            var resultado = performGet(URL_ID);
            assertStatus404(resultado, COMBUSTIVEL, ID_VALIDO);

            verify(combustivelService).buscarCombustivelAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }
    }
}
