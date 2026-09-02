package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.combustivel.CombustivelTestContext;
import com.javacar.lojadecarro.service.CombustivelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.combustivel.CombustivelTestContext.criaCombustivelResponse;
import static com.javacar.lojadecarro.factory.combustivel.CombustivelTestContext.criaCombustivelResponse2;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCombustivelController.class)
@DisplayName("Testes da controller administrativa do combustível")
class AdminCombustivelControllerTest extends BaseControllerTest {
    private static final String URL = "/admin/combustiveis";
    private static final String URL_ID = URL + "/" + ID_VALIDO;
    private static final String URL_STATUS = URL_ID + "/status";

    @MockitoBean
    private CombustivelService combustivelService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {
        @Test
        @DisplayName("Deve criar um combustível")
        void deveCriarCombustivel() throws Exception {
            // Arrange
            var cx = new CombustivelTestContext();
            when(combustivelService.criar(cx.combustivelRequest)).thenReturn(cx.combustivelResponse);

            // Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.combustivelRequest, ID_JWT, ROLE_ADM);
            resultado.andExpect(header().string(
                    "Location",
                    "http://localhost/admin/combustiveis/" + ID_VALIDO
            ));
            assertResult(resultado, status().isCreated(), ID_VALIDO, "Gasolina", true);

            verify(combustivelService).criar(cx.combustivelRequest);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve retornar 400 quando o nome não for informado")
        void deveRetornar400QuandoNomeNaoForInformado() throws Exception {
            var cx = new CombustivelTestContext();

            var resultado = performPostComAutenticacao(
                    URL,
                    cx.combustivelRequestIncompleto,
                    ID_JWT,
                    ROLE_ADM
            );
            assertStatus400(resultado);

            verifyNoInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao criar um combustível")
        void deveRetornar500AoCriarCombustivel() throws Exception {
            var cx = new CombustivelTestContext();
            when(combustivelService.criar(cx.combustivelRequest))
                    .thenThrow(new RuntimeException("Erro inesperado"));

            var resultado = performPostComAutenticacao(URL, cx.combustivelRequest, ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(combustivelService).criar(cx.combustivelRequest);
            verifyNoMoreInteractions(combustivelService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar combustíveis ativos")
        void deveListarCombustiveisAtivos() throws Exception {
            var response = List.of(
                    criaCombustivelResponse(true),
                    criaCombustivelResponse2(true)
            );
            when(combustivelService.listarAdministracao(ATIVAS)).thenReturn(response);

            var resultado = performGetComAutenticacao(
                    URL,
                    "status",
                    ATIVAS.toString(),
                    ID_JWT,
                    ROLE_ADM
            );
            assertList(resultado, ID_VALIDO, 2L, "Gasolina", "Eletrico", true, true);

            verify(combustivelService).listarAdministracao(ATIVAS);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve utilizar TODAS como status padrão")
        void deveUtilizarTodasComoStatusPadrao() throws Exception {
            var response = List.of(
                    criaCombustivelResponse(true),
                    criaCombustivelResponse2(false)
            );
            when(combustivelService.listarAdministracao(TODAS)).thenReturn(response);

            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_ADM);
            assertList(resultado, ID_VALIDO, 2L, "Gasolina", "Eletrico", true, false);

            verify(combustivelService).listarAdministracao(TODAS);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve retornar 401 na listagem")
        void deveRetornar401NaListagem() throws Exception {
            var resultado = performGet(URL);
            assertStatus401(resultado);

            verifyNoInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve retornar 403 na listagem")
        void deveRetornar403NaListagem() throws Exception {
            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_USUARIO);
            assertStatus403(resultado);

            verifyNoInteractions(combustivelService);
        }
    }

    @Nested
    @DisplayName("Testes da busca")
    class Buscar {
        @Test
        @DisplayName("Deve buscar combustível")
        void deveBuscarCombustivel() throws Exception {
            var cx = new CombustivelTestContext();
            when(combustivelService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenReturn(cx.combustivelResponse);

            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Gasolina", true);

            verify(combustivelService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar um combustível")
        void deveRetornar404AoBuscarCombustivel() throws Exception {
            when(combustivelService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, ID_VALIDO));

            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, COMBUSTIVEL, ID_VALIDO);

            verify(combustivelService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }
    }

    @Nested
    @DisplayName("Testes de atualização")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o combustível")
        void deveAtualizarCombustivel() throws Exception {
            var cx = new CombustivelTestContext();
            when(combustivelService.atualizar(cx.combustivelRequest, ID_VALIDO))
                    .thenReturn(cx.combustivelResponse);

            var resultado = performPutComAutenticacao(URL_ID, cx.combustivelRequest, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Gasolina", true);

            verify(combustivelService).atualizar(cx.combustivelRequest, ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao atualizar um combustível sem nome")
        void deveRetornar400AoAtualizarCombustivelSemNome() throws Exception {
            var cx = new CombustivelTestContext();

            var resultado = performPutComAutenticacao(
                    URL_ID,
                    cx.combustivelRequestIncompleto,
                    ID_JWT,
                    ROLE_ADM
            );
            assertStatus400(resultado);

            verifyNoInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar um combustível")
        void deveRetornar404AoAtualizarCombustivel() throws Exception {
            var cx = new CombustivelTestContext();
            when(combustivelService.atualizar(cx.combustivelRequest, ID_VALIDO))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, ID_VALIDO));

            var resultado = performPutComAutenticacao(URL_ID, cx.combustivelRequest, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, COMBUSTIVEL, ID_VALIDO);

            verify(combustivelService).atualizar(cx.combustivelRequest, ID_VALIDO);
            verifyNoMoreInteractions(combustivelService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            var cx = new CombustivelTestContext();
            var request = new StatusRequest(true);
            when(combustivelService.alterarStatus(ID_VALIDO, request))
                    .thenReturn(cx.combustivelResponse);

            var resultado = performPatchComAutenticacao(URL_STATUS, request, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Gasolina", true);

            verify(combustivelService).alterarStatus(ID_VALIDO, request);
            verifyNoMoreInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao alterar o status sem valor")
        void deveRetornar400AoAlterarStatusSemValor() throws Exception {
            var request = new StatusRequest(null);

            var resultado = performPatchComAutenticacao(URL_STATUS, request, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(combustivelService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao alterar o status")
        void deveRetornar404AoAlterarStatus() throws Exception {
            var request = new StatusRequest(true);
            when(combustivelService.alterarStatus(ID_VALIDO, request))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, ID_VALIDO));

            var resultado = performPatchComAutenticacao(URL_STATUS, request, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, COMBUSTIVEL, ID_VALIDO);

            verify(combustivelService).alterarStatus(ID_VALIDO, request);
            verifyNoMoreInteractions(combustivelService);
        }
    }
}
