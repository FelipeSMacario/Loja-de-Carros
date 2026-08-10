package com.javacar.lojadecarro.controller.publica;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacar.lojadecarro.controller.publico.CombustivelController;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.combustivel.CombustivelTestContext;
import com.javacar.lojadecarro.service.CombustivelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CombustivelController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller do combustível")
class CombustivelControllerTest {
    private static final String URL = "/combustiveis";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CombustivelService combustivelService;


    @Nested
    @DisplayName("Testes da listagem de combustiveis")
    class Listar {
        @Test
        @DisplayName("Deve utilizar ATIVAS")
        void deveUtilizarAtivasComStatusPadrao() throws Exception {
            //Arrange
            var combustivelResponse1 = CombustivelTestContext
                    .criaCombustivelResponse(true);
            var combustivelResponse2 = CombustivelTestContext
                    .criaCombustivelResponse2(true);

            var response = List.of(combustivelResponse1, combustivelResponse2);
            when(combustivelService.listarCombustiveisAtivas())
                    .thenReturn(response);
            //Act + Assert
            var resultado = mockMvc.perform(
                    get(URL)
            );

            assertList(resultado,
                    ID_VALIDO,
                    2L,
                    "Gasolina",
                    "Eletrico",
                    true,
                    true);

            verify(combustivelService).listarCombustiveisAtivas();
            verifyNoMoreInteractions(combustivelService);
        }


    }

    @Nested
    @DisplayName("Testes para buscar combustível")
    class Buscar {
        @Test
        @DisplayName("Deve buscar combustivel por ID")
        void deveBuscarCombustivelPorId() throws Exception {
            //Arrange
            var combustivelcx = new CombustivelTestContext();

            when(combustivelService.buscarCombustivelAtivaPorId(ID_VALIDO))
                    .thenReturn(combustivelcx.combustivelResponse);
            //Act + Assert
            var resultado = mockMvc.perform(
                    get(URL + "/" + ID_VALIDO)
            );

            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Gasolina",
                    true
            );

            verify(combustivelService).buscarCombustivelAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao buscar o combustível")
        void deveRetornar404AoBuscarCombustivelPorId() throws Exception {
            //Arrange
            when(combustivelService.buscarCombustivelAtivaPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, ID_VALIDO));

            //Act + Assert
            var resultado = mockMvc.perform(
                    get(URL + "/" + ID_VALIDO)
            );
            assertStatus404(
                    resultado,
                    COMBUSTIVEL,
                    ID_VALIDO
            );
            verify(combustivelService).buscarCombustivelAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }
    }
}
