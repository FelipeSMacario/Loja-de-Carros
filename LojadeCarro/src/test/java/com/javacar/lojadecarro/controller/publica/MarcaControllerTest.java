package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.MarcaController;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.marca.MarcaTestContext;
import com.javacar.lojadecarro.service.MarcaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.MARCA;
import static com.javacar.lojadecarro.factory.helper.MarcaHelper.*;
import static com.javacar.lojadecarro.factory.marca.MarcaTestContext.criaMarcaResponse;
import static com.javacar.lojadecarro.factory.marca.MarcaTestContext.criaMarcaResponse2;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarcaController.class)
@DisplayName("Testes da controller da marca")
class MarcaControllerTest extends BaseControllerTest {
    private static final String URL = "/marcas";
    private static final String URL_ID = URL + "/" + ID_VALIDO;

    @MockitoBean
    private MarcaService marcaService;

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar somente as marcas ativas")
        void deveListarSomenteMarcasAtivas() throws Exception {
            //Arrange
            var response1 = criaMarcaResponse(true);
            var response2 = criaMarcaResponse2(true);

            var response = List.of(response1, response2);

            when(marcaService.listarMarcasAtivas())
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Ford",
                    "Fiat",
                    "https://www.ford.com",
                    "https://www.fiat.com",
                    true,
                    true
            );
            verify(marcaService).listarMarcasAtivas();
            verifyNoMoreInteractions(marcaService);
        }

    }

    @Nested
    @DisplayName("Testes da busca da marca")
    class Buscar {
        @Test
        @DisplayName("Deve buscar marca ativa")
        void deveBuscarMarcaAtiva() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaService.buscarMarcaAtivaPorId(ID_VALIDO))
                    .thenReturn(cx.response);

            // Act + Assert
            var resultado = performGet(URL_ID);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Ford", "https://www.ford.com", true);

            verify(marcaService).buscarMarcaAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar uma marca não encontrada")
        void deveRetornar404AoBuscarMarcaNaoEncontrada() throws Exception {
            //Arrange
            when(marcaService.buscarMarcaAtivaPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(MARCA, ID_VALIDO));

            // Act + Assert
            var resultado = performGet(URL_ID);
            assertStatus404(resultado, MARCA, ID_VALIDO);

            verify(marcaService).buscarMarcaAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }
    }

}
