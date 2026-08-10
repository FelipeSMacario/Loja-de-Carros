package com.javacar.lojadecarro.controller.publica;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacar.lojadecarro.controller.publico.CarroceriaController;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaResponseFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaTestContext;
import com.javacar.lojadecarro.service.CarroceriaService;
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

import static com.javacar.lojadecarro.enums.Entidade.CARROCERIA;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.INATIVAS;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertStatus400;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertStatus404;
import static com.javacar.lojadecarro.factory.helper.CarroceriaHelper.assertResultadoCarroceria;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarroceriaController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller da carroceria")
class CarroceriaControllerTest {
    private static final String URL = "/carrocerias";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarroceriaService carroceriaService;


    @Nested
    @DisplayName("Testes da listagem de carrocerias")
    class Listar {

        @Test
        @DisplayName("Deve utilizar ATIVAS como status padrão")
        void deveListarAsCarroceriasAtivas() throws Exception {
            //Arrange
            var carroceriacx = new CarroceriaTestContext();

            var response = List.of(carroceriacx.carroceriaResponse,
                    carroceriacx.carroceriaResponse2);

            when(carroceriaService.listarCarroceriasAtivas())
                    .thenReturn(response);

            // Act + Assert
            mockMvc.perform(
                            get(URL)
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$.[0].id").value(ID_VALIDO))
                    .andExpect(jsonPath("$.[0].nome").value("Hatch"))
                    .andExpect(jsonPath("$.[0].ativo").value(true))
                    .andExpect(jsonPath("$.[1].id").value(2L))
                    .andExpect(jsonPath("$.[1].nome").value("Sedan"))
                    .andExpect(jsonPath("$.[1].ativo").value(true));

            verify(carroceriaService).listarCarroceriasAtivas();
            verifyNoMoreInteractions(carroceriaService);
        }

    }

    @Nested
    @DisplayName("Testes da busca da carroceria")
    class Buscar {
        @Test
        @DisplayName("Deve buscar uma carroceria por ID")
        void deveBuscarCarroceriaPorId() throws Exception {
            //Arrange

            var carroceriacx = new CarroceriaTestContext();

            when(carroceriaService.buscarCarroceriaAtivaPorId(ID_VALIDO))
                    .thenReturn(carroceriacx.carroceriaResponse);

            // Act + Assert
            var resultado = mockMvc.perform(
                    get(URL + "/" + ID_VALIDO)
            );

            assertResultadoCarroceria(resultado);

            verify(carroceriaService).buscarCarroceriaAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }


        @Test
        @DisplayName("Deve lançar 404 ao buscar uma carroceria por ID")
        void deveLancar404aoBuscarCarroceriaPorId() throws Exception {
            //Arrange
            when(carroceriaService.buscarCarroceriaAtivaPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(CARROCERIA, ID_VALIDO));

            //ACT + Assert
            var resultado = mockMvc.perform(
                    get(URL + "/" + ID_VALIDO)
            );

            assertStatus404(resultado, CARROCERIA, ID_VALIDO);

            verify(carroceriaService).buscarCarroceriaAtivaPorId(ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }

    }


}
