package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.ModeloController;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.modelo.ModeloTestContext;
import com.javacar.lojadecarro.service.ModeloService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.MODELO;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.factory.modelo.ModeloTestContext.criaModeloResponse;
import static com.javacar.lojadecarro.factory.modelo.ModeloTestContext.criaModeloResponse2;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModeloController.class)
@DisplayName("Testes da controller do modelo")
class ModeloControllerTest extends BaseControllerTest {
    private static final String URL = "/modelos";

    @MockitoBean
    private ModeloService modeloService;


    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar somente os modelos ativos")
        void deveListarSomenteModelosAtivos() throws Exception {
            //Arrange
            var response1 = criaModeloResponse(true);
            var response2 = criaModeloResponse2(true);

            var response = List.of(response1, response2);

            when(modeloService.listarModelosAtivos())
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
            verify(modeloService).listarModelosAtivos();
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

            when(modeloService.buscarModeloAtivoPorId(ID_VALIDO))
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

            verify(modeloService).buscarModeloAtivoPorId(ID_VALIDO);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao buscar o modelo")
        void deveLancar404BuscarModelo() throws Exception {
            //Arrange
            when(modeloService.buscarModeloAtivoPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(MODELO, ID_VALIDO));
            //Act + Assert
            var resultado = performGet(URL + "/" + ID_VALIDO);
            assertStatus404(resultado, MODELO, ID_VALIDO);

            verify(modeloService).buscarModeloAtivoPorId(ID_VALIDO);
            verifyNoMoreInteractions(modeloService);
        }
    }

}
