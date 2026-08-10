package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.CorController;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.cor.CorTestContext;
import com.javacar.lojadecarro.service.CoresService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.COR;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.factory.cor.CorTestContext.criaCorResponse;
import static com.javacar.lojadecarro.factory.cor.CorTestContext.criaCorResponse2;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CorController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller da cor")
class CorControllerTest extends BaseControllerTest {
    private static final String URL = "/cores";

    @MockitoBean
    private CoresService coresService;


    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve utilizar ATIVAS como status padrão")
        void deveListarAsCores() throws Exception {
            //Arrange
            var corResponse1 = criaCorResponse(true);
            var corResponse2 = criaCorResponse2(true);

            var response = List.of(corResponse1, corResponse2);

            when(coresService.listarCoresAtivas())
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Branco",
                    "Vermelho",
                    true,
                    true
            );

            verify(coresService).listarCoresAtivas();
            verifyNoMoreInteractions(coresService);
        }

    }

    @Nested
    @DisplayName("Testes da busca por ID")
    class Buscar {

        @Test
        @DisplayName("Deve buscar uma cor por ID")
        void deveBuscarUmaCorPorID() throws Exception {
            //Arrange
            var cx = new CorTestContext();

            when(coresService.buscarCorAtivaPorId(ID_VALIDO))
                    .thenReturn(cx.corResponse);
            //Act + Assert
            var resultado = performGet(URL + "/" + ID_VALIDO);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Branco",
                    true
            );

            verify(coresService).buscarCorAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao buscar uma cor por ID")
        void deveLancar404aoBuscarUmaCorPorID() throws Exception {
            //Arrange
            when(coresService.buscarCorAtivaPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(COR, ID_VALIDO));
            //Act + Assert
            var resultado = performGet(URL + "/" + ID_VALIDO);
            assertStatus404(resultado, COR, ID_VALIDO);

            verify(coresService).buscarCorAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(coresService);
        }
    }

}
