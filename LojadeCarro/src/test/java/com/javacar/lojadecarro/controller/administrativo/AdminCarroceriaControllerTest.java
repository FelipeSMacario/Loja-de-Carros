package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaResponseFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaTestContext;
import com.javacar.lojadecarro.service.CarroceriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.CARROCERIA;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCarroceriaController.class)
@DisplayName("Testes da controller administrativa da carroceria")
class AdminCarroceriaControllerTest extends BaseControllerTest {
    private static final String URL = "/admin/carrocerias";
    private static final String URL_ID = URL + "/" + ID_VALIDO;
    private static final String URL_STATUS = URL_ID + "/status";

    @MockitoBean
    private CarroceriaService carroceriaService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {
        @Test
        @DisplayName("Deve criar uma carroceria")
        void deveCriarCarroceria() throws Exception {
            var cx = new CarroceriaTestContext();
            when(carroceriaService.criar(cx.carroceriaRequest)).thenReturn(cx.carroceriaResponse);

            var resultado = performPostComAutenticacao(URL, cx.carroceriaRequest, ID_JWT, ROLE_ADM);
            resultado.andExpect(header().string("Location", "http://localhost/admin/carrocerias/" + ID_VALIDO));
            assertResult(resultado, status().isCreated(), ID_VALIDO, "Hatch", true);

            verify(carroceriaService).criar(cx.carroceriaRequest);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve retornar 400 quando o nome não for informado")
        void deveRetornar400QuandoNomeNaoForInformado() throws Exception {
            var cx = new CarroceriaTestContext();

            var resultado = performPostComAutenticacao(URL, cx.carroceriaRequestIncompleta, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao criar uma carroceria")
        void deveRetornar500AoCriarCarroceria() throws Exception {
            var cx = new CarroceriaTestContext();
            when(carroceriaService.criar(cx.carroceriaRequest))
                    .thenThrow(new RuntimeException("Erro inesperado"));

            var resultado = performPostComAutenticacao(URL, cx.carroceriaRequest, ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(carroceriaService).criar(cx.carroceriaRequest);
            verifyNoMoreInteractions(carroceriaService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar carrocerias ativas")
        void deveListarCarroceriasAtivas() throws Exception {
            var cx = new CarroceriaTestContext();
            when(carroceriaService.listarAdministracao(ATIVAS))
                    .thenReturn(List.of(cx.carroceriaResponse, cx.carroceriaResponse2));

            var resultado = performGetComAutenticacao(URL, "status", ATIVAS.toString(), ID_JWT, ROLE_ADM);
            assertList(resultado, ID_VALIDO, 2L, "Hatch", "Sedan", true, true);

            verify(carroceriaService).listarAdministracao(ATIVAS);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve utilizar TODAS como status padrão")
        void deveUtilizarTodasComoStatusPadrao() throws Exception {
            var cx = new CarroceriaTestContext();
            var response2 = CarroceriaResponseFactory.criarResponse()
                    .comId(2L)
                    .comNome("Sedan")
                    .comAtivo(false)
                    .build();
            when(carroceriaService.listarAdministracao(TODAS))
                    .thenReturn(List.of(cx.carroceriaResponse, response2));

            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_ADM);
            assertList(resultado, ID_VALIDO, 2L, "Hatch", "Sedan", true, false);

            verify(carroceriaService).listarAdministracao(TODAS);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao informar um status inválido")
        void deveRetornar400AoInformarStatusInvalido() throws Exception {
            var resultado = performGetComAutenticacao(URL, "status", "123", ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve retornar 401 na listagem")
        void deveRetornar401NaListagem() throws Exception {
            var resultado = performGet(URL);
            assertStatus401(resultado);

            verifyNoInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve retornar 403 na listagem")
        void deveRetornar403NaListagem() throws Exception {
            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_USUARIO);
            assertStatus403(resultado);

            verifyNoInteractions(carroceriaService);
        }
    }

    @Nested
    @DisplayName("Testes da busca")
    class Buscar {
        @Test
        @DisplayName("Deve buscar carroceria")
        void deveBuscarCarroceria() throws Exception {
            var cx = new CarroceriaTestContext();
            when(carroceriaService.buscarPorIdAdministracao(ID_VALIDO)).thenReturn(cx.carroceriaResponse);

            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Hatch", true);

            verify(carroceriaService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar uma carroceria")
        void deveRetornar404AoBuscarCarroceria() throws Exception {
            when(carroceriaService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenThrow(new NotFoundException(CARROCERIA, ID_VALIDO));

            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, CARROCERIA, ID_VALIDO);

            verify(carroceriaService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }
    }

    @Nested
    @DisplayName("Testes de atualização")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar a carroceria")
        void deveAtualizarCarroceria() throws Exception {
            var cx = new CarroceriaTestContext();
            when(carroceriaService.atualizar(cx.carroceriaRequest, ID_VALIDO))
                    .thenReturn(cx.carroceriaResponse);

            var resultado = performPutComAutenticacao(URL_ID, cx.carroceriaRequest, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Hatch", true);

            verify(carroceriaService).atualizar(cx.carroceriaRequest, ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao atualizar uma carroceria sem nome")
        void deveRetornar400AoAtualizarCarroceriaSemNome() throws Exception {
            var cx = new CarroceriaTestContext();

            var resultado = performPutComAutenticacao(URL_ID, cx.carroceriaRequestIncompleta, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar uma carroceria")
        void deveRetornar404AoAtualizarCarroceria() throws Exception {
            var cx = new CarroceriaTestContext();
            when(carroceriaService.atualizar(cx.carroceriaRequest, ID_VALIDO))
                    .thenThrow(new NotFoundException(CARROCERIA, ID_VALIDO));

            var resultado = performPutComAutenticacao(URL_ID, cx.carroceriaRequest, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, CARROCERIA, ID_VALIDO);

            verify(carroceriaService).atualizar(cx.carroceriaRequest, ID_VALIDO);
            verifyNoMoreInteractions(carroceriaService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            var cx = new CarroceriaTestContext();
            var request = new StatusRequest(true);
            when(carroceriaService.alterarStatus(ID_VALIDO, request)).thenReturn(cx.carroceriaResponse);

            var resultado = performPatchComAutenticacao(URL_STATUS, request, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Hatch", true);

            verify(carroceriaService).alterarStatus(ID_VALIDO, request);
            verifyNoMoreInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao alterar o status sem valor")
        void deveRetornar400AoAlterarStatusSemValor() throws Exception {
            var request = new StatusRequest(null);

            var resultado = performPatchComAutenticacao(URL_STATUS, request, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(carroceriaService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao alterar o status")
        void deveRetornar404AoAlterarStatus() throws Exception {
            var request = new StatusRequest(true);
            when(carroceriaService.alterarStatus(ID_VALIDO, request))
                    .thenThrow(new NotFoundException(CARROCERIA, ID_VALIDO));

            var resultado = performPatchComAutenticacao(URL_STATUS, request, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, CARROCERIA, ID_VALIDO);

            verify(carroceriaService).alterarStatus(ID_VALIDO, request);
            verifyNoMoreInteractions(carroceriaService);
        }
    }
}
