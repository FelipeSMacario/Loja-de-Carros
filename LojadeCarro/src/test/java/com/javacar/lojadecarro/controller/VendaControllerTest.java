package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.venda.VendaTestContext;
import com.javacar.lojadecarro.service.VendasService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VendaController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller de venda")
public class VendaControllerTest extends BaseControllerTest {
    private static final String URL = "/vendas";

    @MockitoBean
    private VendasService vendasService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {

        @Test
        @DisplayName("Deve criar uma venda")
        void deveCriarVenda() throws Exception {
            //Arrange
            var cx = new VendaTestContext();

            when(vendasService.criar(cx.vendaRequest))
                    .thenReturn(cx.vendaResponse);
            // Act + Assert
            var resultado = performPost(URL, cx.vendaRequest);
            resultado
                    .andExpect(header().exists("Location"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(ID_VALIDO))
                    .andExpect(jsonPath("$.valorVenda").value(BigDecimal.valueOf(200000)));

            verify(vendasService).criar(cx.vendaRequest);
            verifyNoMoreInteractions(vendasService);

        }

        @Test
        @DisplayName("Deve lançar 400 quando não passar o ID do usuário")
        void deveLancar400QUandoNaoPassarIdVendedor() throws Exception {
            // Arrange
            var cx = new VendaTestContext();
            // Act + Assert
            var resultado = performPost(URL, cx.vendaRequestIncompleta);
            assertStatus400(resultado);

            verifyNoInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao informar um vendedor invalido")
        void deveLancar404aoInformarUmVendedorInvalido() throws Exception {
            // Arrange
            var cx = new VendaTestContext();
            when(vendasService.criar(cx.vendaRequest))
                    .thenThrow(new NotFoundException(USUARIO, cx.vendaRequest.vendedorId()));
            // Act + Assert
            var resultado = performPost(URL, cx.vendaRequest);
            assertStatus404(resultado, USUARIO, cx.vendaRequest.vendedorId());

            verify(vendasService).criar(cx.vendaRequest);
            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve lançar 500 ao criar uma venda")
        void deveLancar500AoCriarVenda() throws Exception {
            //Arrange
            var cx = new VendaTestContext();

            when(vendasService.criar(cx.vendaRequest))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPost(URL, cx.vendaRequest);
            assertStatus500(resultado);

            verify(vendasService).criar(cx.vendaRequest);
            verifyNoMoreInteractions(vendasService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar as vendas")
        void deveListarAsVendas() throws Exception {
            //Arrange
            var cx = new VendaTestContext();

            Page<VendaResponse> page =
                    new PageImpl<>(List.of(cx.vendaResponse, cx.vendaResponse2));

            when(vendasService.listar(any(Pageable.class), isNull()))
                    .thenReturn(page);
            //Act + Assert
            var resultado = performGet(URL);

            resultado.andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].id").value(ID_VALIDO))
                    .andExpect(jsonPath("$.content[1].id").value(2L))
                    .andExpect(jsonPath("$.content[0].valorVenda").value(200000))
                    .andExpect(jsonPath("$.content[1].valorVenda").value(200000));


            verify(vendasService).listar(any(Pageable.class), isNull());
            verifyNoMoreInteractions(vendasService);
        }

    }


}
