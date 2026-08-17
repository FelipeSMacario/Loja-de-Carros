package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.MarcaController;
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
import static com.javacar.lojadecarro.factory.helper.MarcaHelper.*;
import static com.javacar.lojadecarro.factory.marca.MarcaTestContext.criaMarcaResponse;
import static com.javacar.lojadecarro.factory.marca.MarcaTestContext.criaMarcaResponse2;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarcaController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller da marca")
class MarcaControllerTest extends BaseControllerTest {
    private static final String URL = "/marcas";

    @MockitoBean
    private MarcaService marcaService;

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
                    true,
                    true
            );
            verify(marcaService).listarMarcasAtivas();
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

            when(marcaService.buscarMarcaAtivaPorId(ID_VALIDO))
                    .thenReturn(cx.response);

            // Act + Assert
            var resultado = performGet(URL + "/" + ID_VALIDO);
            resultado.andExpect(jsonPath("$.url").value("https://www.ford.com"));
            assertResult(resultado, status().isOk(), ID_VALIDO, "Ford", true);

            verify(marcaService).buscarMarcaAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao buscar uma marca por ID")
        void deveLancar404AoBuscarMarcaPorId() throws Exception {
            //Arrange
            when(marcaService.buscarMarcaAtivaPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(MARCA, ID_VALIDO));

            // Act + Assert
            var resultado = performGet(URL + "/" + ID_VALIDO);
            assertStatus404(resultado, MARCA, ID_VALIDO);

            verify(marcaService).buscarMarcaAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }
    }

}
