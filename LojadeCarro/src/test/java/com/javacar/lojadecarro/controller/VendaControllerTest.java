package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.venda.VendaTestContext;
import com.javacar.lojadecarro.service.VendasService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.enums.Entidade.VENDA;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.enums.StatusVeiculo.VENDIDO;
import static com.javacar.lojadecarro.enums.StatusVenda.*;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.factory.helper.VendaHelper.assertPage;
import static com.javacar.lojadecarro.factory.helper.VendaHelper.assertVendaResponse;
import static com.javacar.lojadecarro.factory.venda.VendaTestContext.criarVeiculoResponse;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VendaController.class)
@DisplayName("Testes da controller de venda")
public class VendaControllerTest extends BaseControllerTest {
    private static final String URL = "/vendas";
    private static final String URL_ID = URL + "/" + ID_VALIDO;
    private static final String URL_ID_INVALIDO = URL + "/A";
    private static final String URL_CANCELAR = URL + "/" + ID_VALIDO + "/cancelar";
    private static final String URL_CONCLUIR = URL + "/" + ID_VALIDO + "/concluir";
    private static final String URL_COMPRAS = URL + "/minhas-compras";
    private static final String URL_VENDAS = URL + "/minhas-vendas";

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

            when(vendasService.criar(cx.vendaRequest, ID_VALIDO))
                    .thenReturn(cx.vendaResponse);
            // Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.vendaRequest, ID_JWT, ROLE_USUARIO);
            assertVendaResponse(resultado, status().isCreated());
            resultado.andExpect(header().string("Location", "http://localhost/vendas/" + ID_VALIDO));

            verify(vendasService).criar(cx.vendaRequest, ID_VALIDO);
            verifyNoMoreInteractions(vendasService);

        }

        @Test
        @DisplayName("Deve retornar 400 ao criar venda")
        void deveRetornar400AoCriarVenda() throws Exception {
            // Arrange
            var cx = new VendaTestContext();
            // Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.vendaRequestIncompleta, ID_JWT, ROLE_USUARIO);
            assertStatus400(resultado);

            verifyNoInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao criar venda")
        void deveRetornar404AoCriarVenda() throws Exception {
            // Arrange
            var cx = new VendaTestContext();
            when(vendasService.criar(cx.vendaRequest, ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            // Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.vendaRequest, ID_JWT, ROLE_USUARIO);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(vendasService).criar(cx.vendaRequest, ID_VALIDO);
            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao criar uma venda")
        void deveRetornar500AoCriarVenda() throws Exception {
            //Arrange
            var cx = new VendaTestContext();

            when(vendasService.criar(cx.vendaRequest, ID_VALIDO))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.vendaRequest, ID_JWT, ROLE_USUARIO);
            assertStatus500(resultado);

            verify(vendasService).criar(cx.vendaRequest, ID_VALIDO);
            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao criar uma venda")
        void deveRetornar401AoCriarVenda() throws Exception {
            //Arrange
            var cx = new VendaTestContext();
            //Act + Assert
            var resultado = performPost(URL, cx.vendaRequest);

            assertStatus401(resultado);
            verifyNoInteractions(vendasService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar as vendas")
        void deveListarAsVendas() throws Exception {
            // Arrange
            var cx = new VendaTestContext();

            var page = new PageImpl<>(
                    List.of(
                            cx.vendaResponse,
                            cx.vendaResponse2
                    ),
                    PageRequest.of(1, 5),
                    7
            );

            when(vendasService.listar(
                    any(Pageable.class),
                    eq(EM_ANDAMENTO)
            )).thenReturn(page);

            // Act
            var resultado = performGetComAutenticacao(
                    URL + "?page=1&size=5",
                    "status",
                    EM_ANDAMENTO.name(),
                    ID_JWT,
                    ROLE_ADM
            );

            // Assert
            resultado
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].id").value(ID_VALIDO))
                    .andExpect(jsonPath("$.content[1].id").value(2L))
                    .andExpect(jsonPath("$.content[0].statusVenda").value(EM_ANDAMENTO.name()))
                    .andExpect(jsonPath("$.content[1].statusVenda").value(EM_ANDAMENTO.name()))
                    .andExpect(jsonPath("$.content[0].valorVenda").value(200000))
                    .andExpect(jsonPath("$.content[1].valorVenda").value(300000));

            var pageableCaptor =
                    ArgumentCaptor.forClass(Pageable.class);

            verify(vendasService).listar(
                    pageableCaptor.capture(),
                    eq(EM_ANDAMENTO)
            );

            assertThat(pageableCaptor.getValue().getPageNumber())
                    .isEqualTo(1);

            assertThat(pageableCaptor.getValue().getPageSize())
                    .isEqualTo(5);

            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao listar vendas com status inválido")
        void deveRetornar400AoListarVendasComStatusInvalido() throws Exception {
            //Arrange

            //Act + Assert
            var resultado = performGetComAutenticacao(
                    URL, "status", "FINALIZADISSIMA",
                    ID_JWT,
                    ROLE_ADM
            );

            assertStatus400(resultado);
            verifyNoInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao listar as vendas")
        void deveRetornar401AoListarAsVendas() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performGet(URL);

            assertStatus401(resultado);
            verifyNoInteractions(vendasService);
        }

    }

    @Nested
    @DisplayName("Testes da busca da venda")
    class Buscar {
        @Test
        @DisplayName("Deve buscar uma venda")
        void deveBuscarUmaVenda() throws Exception {
            //Arrange
            var cx = new VendaTestContext();
            when(vendasService.buscarPorId(ID_VALIDO))
                    .thenReturn(cx.vendaResponse);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_USUARIO);
            assertVendaResponse(resultado, status().isOk());

            verify(vendasService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao buscar venda com ID inválido")
        void deveRetornar400AoBuscarUmaVendaComIdInvalido() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performGetComAutenticacao(URL_ID_INVALIDO, ID_JWT, ROLE_USUARIO);
            assertStatus400(exception);
            verifyNoInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar venda")
        void deveRetornar404AoBuscarVenda() throws Exception {
            //Arrange
            when(vendasService.buscarPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(VENDA, ID_VALIDO));
            //Act + Assert
            var exception = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_USUARIO);
            assertStatus404(exception, VENDA, ID_VALIDO);

            verify(vendasService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao buscar venda")
        void deveRetornar401AoBuscarVenda() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performGet(URL_ID);
            assertStatus401(exception);

            verifyNoInteractions(vendasService);
        }

    }

    @Nested
    @DisplayName("Testes da busca das compras do usuário autenticado")
    class BuscarComprasUsuario {
        @Test
        @DisplayName("Deve buscar as compras do usuário autenticado")
        void deveBuscarAsComprasUsuarioAutenticado() throws Exception {
            //Arrange
            var listaVendasResponse = VendaTestContext
                    .criarListaVendasResponse();
            Page<VendaResponse> page =
                    new PageImpl<>(listaVendasResponse);

            when(vendasService.buscarMinhasCompras(eq(ID_VALIDO), any(Pageable.class), eq(EM_ANDAMENTO)))
                    .thenReturn(page);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_COMPRAS, "status", EM_ANDAMENTO.name(), ID_JWT, ROLE_USUARIO);
            assertPage(resultado, status().isOk());

            var pageable =
                    ArgumentCaptor.forClass(Pageable.class);
            verify(vendasService).buscarMinhasCompras(
                    eq(ID_VALIDO),
                    pageable.capture(),
                    eq(EM_ANDAMENTO)
            );

            assertThat(pageable.getValue().getPageNumber()).isZero();
            assertThat(pageable.getValue().getPageSize()).isEqualTo(9);

            assertThat(pageable.getValue().getSort().getOrderFor("dataVenda"))
                    .isNotNull()
                    .extracting(Sort.Order::getDirection)
                    .isEqualTo(Sort.Direction.DESC);
            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando o usuário não possuir compras")
        void deveRetornarPaginaVaziaQuandoUsuarioNaoPossuirCompras() throws Exception {
            // Arrange
            var pagina = Page.<VendaResponse>empty();

            when(vendasService.buscarMinhasCompras(
                    eq(ID_VALIDO),
                    any(Pageable.class),
                    isNull()
            )).thenReturn(pagina);

            // Act + Assert
            var resultado = performGetComAutenticacao(
                    URL_COMPRAS,
                    ID_JWT,
                    ROLE_USUARIO
            );

            resultado
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(vendasService).buscarMinhasCompras(
                    eq(ID_VALIDO),
                    any(Pageable.class),
                    isNull()
            );

            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao buscar as compras do usuário autenticado")
        void deveRetornar401AoBuscarAsComprasUsuarioAutenticado() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performGet(URL_COMPRAS);
            assertStatus401(exception);

            verifyNoInteractions(vendasService);
        }
    }

    @Nested
    @DisplayName("Testes da busca das vendas do usuário autenticado")
    class BuscarVendasUsuario {
        @Test
        @DisplayName("Deve buscar as vendas do usuário autenticado")
        void deveBuscarAsVendasUsuarioAutenticado() throws Exception {
            //Arrange
            var listaVendasResponse = VendaTestContext
                    .criarListaVendasResponse();
            Page<VendaResponse> page =
                    new PageImpl<>(listaVendasResponse);

            when(vendasService.buscarMinhasVendas(eq(ID_VALIDO), any(Pageable.class), eq(EM_ANDAMENTO)))
                    .thenReturn(page);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_VENDAS, "status", EM_ANDAMENTO.name(), ID_JWT, ROLE_USUARIO);
            assertPage(resultado, status().isOk());

            var pageable =
                    ArgumentCaptor.forClass(Pageable.class);
            verify(vendasService).buscarMinhasVendas(
                    eq(ID_VALIDO),
                    pageable.capture(),
                    eq(EM_ANDAMENTO));

            assertThat(pageable.getValue().getPageNumber()).isZero();
            assertThat(pageable.getValue().getPageSize()).isEqualTo(9);

            assertThat(pageable.getValue().getSort().getOrderFor("dataVenda"))
                    .isNotNull()
                    .extracting(Sort.Order::getDirection)
                    .isEqualTo(Sort.Direction.DESC);

            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando o usuário não possuir vendas")
        void deveRetornarPaginaVaziaQuandoUsuarioNaoPossuirVendas() throws Exception {
            // Arrange
            var pagina = Page.<VendaResponse>empty();

            when(vendasService.buscarMinhasVendas(
                    eq(ID_VALIDO),
                    any(Pageable.class),
                    isNull()
            )).thenReturn(pagina);

            // Act + Assert
            var resultado = performGetComAutenticacao(
                    URL_VENDAS,
                    ID_JWT,
                    ROLE_USUARIO
            );

            resultado
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(vendasService).buscarMinhasVendas(
                    eq(ID_VALIDO),
                    any(Pageable.class),
                    isNull()
            );

            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao buscar as vendas do usuário autenticado")
        void deveRetornar401AoBuscarAsVendasUsuarioAutenticado() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performGet(URL_VENDAS);
            assertStatus401(exception);

            verifyNoInteractions(vendasService);
        }
    }

    @Nested
    @DisplayName("Testes do cancelamento da venda")
    class Cancelar {
        @Test
        @DisplayName("Deve cancelar a venda")
        void deveCancelarAVenda() throws Exception {
            //Arrange
            var response = criarVeiculoResponse(DISPONIVEL, CANCELADA);
            when(vendasService.cancelarVenda(ID_VALIDO))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_CANCELAR, ID_JWT, ROLE_ADM);

            resultado.andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusVenda").value(CANCELADA.name()))
                    .andExpect(jsonPath("$.veiculo.status").value(DISPONIVEL.name()));

            verify(vendasService).cancelarVenda(ID_VALIDO);
            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao cancelar venda")
        void deveRetornar404AoCancelarVenda() throws Exception {
            //Arrange
            when(vendasService.cancelarVenda(ID_VALIDO))
                    .thenThrow(new NotFoundException(VENDA, ID_VALIDO));
            //Act + Assert
            var exception = performPatchComAutenticacao(URL_CANCELAR, ID_JWT, ROLE_ADM);
            assertStatus404(exception, VENDA, ID_VALIDO);

            verify(vendasService).cancelarVenda(ID_VALIDO);
            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao cancelar venda")
        void deveRetornar401AoCancelarVenda() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performPatch(URL_CANCELAR);
            assertStatus401(exception);

            verifyNoInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao cancelar venda com ID inválido")
        void deveRetornar400AoCancelarVenda() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performPatchComAutenticacao(URL_ID_INVALIDO + "/cancelar", ID_JWT, ROLE_ADM);
            assertStatus400(exception);
            verifyNoInteractions(vendasService);
        }
    }

    @Nested
    @DisplayName("Testes da conclusão da venda")
    class Concluir {
        @Test
        @DisplayName("Deve concluir a venda")
        void deveConcluirAVenda() throws Exception {
            //Arrange
            var response = criarVeiculoResponse(VENDIDO, CONCLUIDA);
            when(vendasService.concluirVenda(ID_VALIDO))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_CONCLUIR, ID_JWT, ROLE_ADM);

            resultado.andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusVenda").value(CONCLUIDA.name()))
                    .andExpect(jsonPath("$.veiculo.status").value(VENDIDO.name()));

            verify(vendasService).concluirVenda(ID_VALIDO);
            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao concluir a venda")
        void deveRetornar404AoConcluirVenda() throws Exception {
            //Arrange
            when(vendasService.concluirVenda(ID_VALIDO))
                    .thenThrow(new NotFoundException(VENDA, ID_VALIDO));
            //Act + Assert
            var exception = performPatchComAutenticacao(URL_CONCLUIR, ID_JWT, ROLE_ADM);
            assertStatus404(exception, VENDA, ID_VALIDO);

            verify(vendasService).concluirVenda(ID_VALIDO);
            verifyNoMoreInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao concluir a venda")
        void deveRetornar401AoConcluirVenda() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performPatch(URL_CONCLUIR);
            assertStatus401(exception);

            verifyNoInteractions(vendasService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao concluir venda com ID inválido")
        void deveRetornar400AoConcluirVenda() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performPatchComAutenticacao(URL_ID_INVALIDO + "/concluir", ID_JWT, ROLE_ADM);
            assertStatus400(exception);
            verifyNoInteractions(vendasService);
        }
    }
}
