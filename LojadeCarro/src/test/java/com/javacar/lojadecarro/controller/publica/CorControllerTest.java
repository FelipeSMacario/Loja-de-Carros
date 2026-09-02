package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.CorController;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.cor.CorTestContext;
import com.javacar.lojadecarro.service.CoresService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.COR;
import static com.javacar.lojadecarro.factory.cor.CorTestContext.criaCorResponse;
import static com.javacar.lojadecarro.factory.cor.CorTestContext.criaCorResponse2;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CorController.class)
@DisplayName("Testes da controller da cor")
class CorControllerTest extends BaseControllerTest {
    private static final String URL = "/cores";
    private static final String URL_ID = URL + "/" + ID_VALIDO;

    @MockitoBean
    private CoresService coresService;

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar somente as cores ativas sem autenticação")
        void deveListarSomenteCoresAtivasSemAutenticacao() throws Exception {
            // Arrange
            var response1 = criaCorResponse(true);
            var response2 = criaCorResponse2(true);

            when(coresService.listarCoresAtivas())
                    .thenReturn(List.of(response1, response2));

            // Act + Assert
            var resultado = performGet(URL);
            assertList(resultado, ID_VALIDO, 2L, "Branco", "Vermelho", true, true);

            verify(coresService).listarCoresAtivas();
            verifyNoMoreInteractions(coresService);
        }
    }

    @Nested
    @DisplayName("Testes da busca da cor")
    class Buscar {
        @Test
        @DisplayName("Deve buscar cor ativa sem autenticação")
        void deveBuscarCorAtivaSemAutenticacao() throws Exception {
            // Arrange
            var cx = new CorTestContext();

            when(coresService.buscarCorAtivaPorId(ID_VALIDO))
                    .thenReturn(cx.corResponse);

            // Act + Assert
            var resultado = performGet(URL_ID);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Branco", true);

            verify(coresService).buscarCorAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar uma cor ativa não encontrada")
        void deveRetornar404AoBuscarCorAtivaNaoEncontrada() throws Exception {
            // Arrange
            when(coresService.buscarCorAtivaPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(COR, ID_VALIDO));

            // Act + Assert
            var resultado = performGet(URL_ID);
            assertStatus404(resultado, COR, ID_VALIDO);

            verify(coresService).buscarCorAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(coresService);
        }
    }
}
