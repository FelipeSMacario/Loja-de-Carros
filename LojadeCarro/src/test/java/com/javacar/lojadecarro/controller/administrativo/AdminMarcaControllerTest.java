package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
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
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.helper.MarcaHelper.*;
import static com.javacar.lojadecarro.factory.marca.MarcaTestContext.criaMarcaResponse;
import static com.javacar.lojadecarro.factory.marca.MarcaTestContext.criaMarcaResponse2;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminMarcaController.class)
@DisplayName("Testes da controller da marca")
class AdminMarcaControllerTest extends BaseControllerTest {
    private static final String URL = "/admin/marcas";
    private static final String URL_ID = URL + "/" + ID_VALIDO;
    private static final String URL_STATUS = URL + "/" + ID_VALIDO + "/status";

    @MockitoBean
    private MarcaService marcaService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {

        @Test
        @DisplayName("Deve criar uma marca")
        void deveCriarMarca() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaService.criar(cx.request))
                    .thenReturn(cx.response);
            // Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.request, ID_JWT, ROLE_ADM);
            resultado.andExpect(
                    header().string(
                            "Location",
                            "http://localhost/admin/marcas/" + ID_VALIDO
                    )
            );
            assertResult(resultado, status().isCreated(), ID_VALIDO, "Ford", "https://www.ford.com", true);
            verify(marcaService).criar(cx.request);
            verifyNoMoreInteractions(marcaService);

        }

        @Test
        @DisplayName("Deve retornar 400 quando o nome não for informado")
        void deveRetornarBadRequestQuandoNomeNaoForInformado() throws Exception {
            // Arrange
            var cx = new MarcaTestContext();
            // Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.requestIncompleta, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao criar uma marca")
        void deveRetornar500AoCriarMarca() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaService.criar(cx.request))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.request, ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(marcaService).criar(cx.request);
            verifyNoMoreInteractions(marcaService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar marcas ativas")
        void deveListarMarcasAtivas() throws Exception {
            //Arrange
            var response1 = criaMarcaResponse(true);
            var response2 = criaMarcaResponse2(true);

            var response = List.of(response1, response2);

            when(marcaService.listarAdministracao(ATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, "status", ATIVAS.toString(), ID_JWT, ROLE_ADM);
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
            verify(marcaService).listarAdministracao(ATIVAS);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve utilizar TODAS como status padrão")
        void deveUtilizarTodasComoStatusPadrao() throws Exception {
            //Arrange
            var response1 = criaMarcaResponse(true);
            var response2 = criaMarcaResponse2(false);

            var response = List.of(response1, response2);

            when(marcaService.listarAdministracao(TODAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_ADM);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Ford",
                    "Fiat",
                    "https://www.ford.com",
                    "https://www.fiat.com",
                    true,
                    false
            );
            verify(marcaService).listarAdministracao(TODAS);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve retornar 401 na listagem")
        void deveRetornar401NaListagem() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performGet(URL);
            assertStatus401(resultado);
            verifyNoInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve retornar 403 na listagem")
        void deveRetornar403NaListagem() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_USUARIO);
            assertStatus403(resultado);
            verifyNoInteractions(marcaService);
        }

    }

    @Nested
    @DisplayName("Testes da busca")
    class Buscar {
        @Test
        @DisplayName("Deve buscar marca")
        void deveBuscarMarcaPorId() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenReturn(cx.response);

            // Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Ford", "https://www.ford.com", true);

            verify(marcaService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar uma marca")
        void deveRetornar404AoBuscarMarca() throws Exception {
            //Arrange
            when(marcaService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenThrow(new NotFoundException(MARCA, ID_VALIDO));

            // Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, MARCA, ID_VALIDO);

            verify(marcaService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }
    }

    @Nested
    @DisplayName("Testes da atualização")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar a marca")
        void deveAtualizarMarca() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaService.atualizar(cx.request, ID_VALIDO))
                    .thenReturn(cx.response);

            // Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.request, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Ford", "https://www.ford.com", true);

            verify(marcaService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar uma marca")
        void deveRetornar404AoAtualizarMarca() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaService.atualizar(cx.request, ID_VALIDO))
                    .thenThrow(new NotFoundException(MARCA, ID_VALIDO));
            // Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.request, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, MARCA, ID_VALIDO);

            verify(marcaService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao atualizar marca")
        void deveRetornar400AoAtualizarMarca() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();

            // Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.requestIncompleta, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(marcaService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            //Arrange
            var cx = new MarcaTestContext();
            var status = new StatusRequest(true);

            when(marcaService.alterarStatus(ID_VALIDO, status))
                    .thenReturn(cx.response);
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_STATUS, status, ID_JWT, ROLE_ADM);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Ford",
                    "https://www.ford.com",
                    true);

            verify(marcaService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(marcaService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao alterar status")
        void deveRetornar400AoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(null);
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_STATUS, status, ID_JWT, ROLE_ADM);

            assertStatus400(resultado);
            verifyNoInteractions(marcaService);

        }

        @Test
        @DisplayName("Deve retornar 404 ao alterar status")
        void deveRetornar404aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            when(marcaService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new NotFoundException(MARCA, ID_VALIDO));
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_STATUS, status, ID_JWT, ROLE_ADM);
            assertStatus404(resultado,
                    MARCA,
                    ID_VALIDO);

            verify(marcaService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(marcaService);
        }
    }

}
