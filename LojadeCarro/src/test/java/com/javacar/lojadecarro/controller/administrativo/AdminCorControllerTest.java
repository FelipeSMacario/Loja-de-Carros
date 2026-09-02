package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.cor.CorTestContext;
import com.javacar.lojadecarro.service.CoresService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.COR;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.cor.CorTestContext.criaCorResponse;
import static com.javacar.lojadecarro.factory.cor.CorTestContext.criaCorResponse2;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCorController.class)
@DisplayName("Testes da controller administrativa da cor")
class AdminCorControllerTest extends BaseControllerTest {
    private static final String URL = "/admin/cores";
    private static final String URL_ID = URL + "/" + ID_VALIDO;
    private static final String URL_STATUS = URL_ID + "/status";

    @MockitoBean
    private CoresService coresService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {
        @Test
        @DisplayName("Deve criar uma cor")
        void deveCriarCor() throws Exception {
            // Arrange
            var cx = new CorTestContext();

            when(coresService.criar(cx.corRequest)).thenReturn(cx.corResponse);

            // Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.corRequest, ID_JWT, ROLE_ADM);
            resultado.andExpect(header().string("Location", "http://localhost/admin/cores/" + ID_VALIDO));
            assertResult(resultado, status().isCreated(), ID_VALIDO, "Branco", true);

            verify(coresService).criar(cx.corRequest);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve retornar 400 quando o nome não for informado")
        void deveRetornar400QuandoNomeNaoForInformado() throws Exception {
            var cx = new CorTestContext();

            var resultado = performPostComAutenticacao(URL, cx.corRequestIncompleto, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(coresService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao criar uma cor")
        void deveRetornar500AoCriarCor() throws Exception {
            var cx = new CorTestContext();
            when(coresService.criar(cx.corRequest)).thenThrow(new RuntimeException("Erro inesperado"));

            var resultado = performPostComAutenticacao(URL, cx.corRequest, ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(coresService).criar(cx.corRequest);
            verifyNoMoreInteractions(coresService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar cores ativas")
        void deveListarCoresAtivas() throws Exception {
            var response = List.of(criaCorResponse(true), criaCorResponse2(true));
            when(coresService.listarAdministracao(ATIVAS)).thenReturn(response);

            var resultado = performGetComAutenticacao(URL, "status", ATIVAS.toString(), ID_JWT, ROLE_ADM);
            assertList(resultado, ID_VALIDO, 2L, "Branco", "Vermelho", true, true);

            verify(coresService).listarAdministracao(ATIVAS);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve utilizar TODAS como status padrão")
        void deveUtilizarTodasComoStatusPadrao() throws Exception {
            var response = List.of(criaCorResponse(true), criaCorResponse2(false));
            when(coresService.listarAdministracao(TODAS)).thenReturn(response);

            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_ADM);
            assertList(resultado, ID_VALIDO, 2L, "Branco", "Vermelho", true, false);

            verify(coresService).listarAdministracao(TODAS);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve retornar 401 na listagem")
        void deveRetornar401NaListagem() throws Exception {
            var resultado = performGet(URL);
            assertStatus401(resultado);

            verifyNoInteractions(coresService);
        }

        @Test
        @DisplayName("Deve retornar 403 na listagem")
        void deveRetornar403NaListagem() throws Exception {
            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_USUARIO);
            assertStatus403(resultado);

            verifyNoInteractions(coresService);
        }
    }

    @Nested
    @DisplayName("Testes da busca")
    class Buscar {
        @Test
        @DisplayName("Deve buscar cor")
        void deveBuscarCor() throws Exception {
            var cx = new CorTestContext();
            when(coresService.buscarPorIdAdministracao(ID_VALIDO)).thenReturn(cx.corResponse);

            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Branco", true);

            verify(coresService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar uma cor")
        void deveRetornar404AoBuscarCor() throws Exception {
            when(coresService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenThrow(new NotFoundException(COR, ID_VALIDO));

            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, COR, ID_VALIDO);

            verify(coresService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(coresService);
        }
    }

    @Nested
    @DisplayName("Testes de atualização")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar a cor")
        void deveAtualizarCor() throws Exception {
            var cx = new CorTestContext();
            when(coresService.atualizar(cx.corRequest, ID_VALIDO)).thenReturn(cx.corResponse);

            var resultado = performPutComAutenticacao(URL_ID, cx.corRequest, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Branco", true);

            verify(coresService).atualizar(cx.corRequest, ID_VALIDO);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao atualizar uma cor sem nome")
        void deveRetornar400AoAtualizarCorSemNome() throws Exception {
            var cx = new CorTestContext();

            var resultado = performPutComAutenticacao(URL_ID, cx.corRequestIncompleto, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(coresService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar uma cor")
        void deveRetornar404AoAtualizarCor() throws Exception {
            var cx = new CorTestContext();
            when(coresService.atualizar(cx.corRequest, ID_VALIDO))
                    .thenThrow(new NotFoundException(COR, ID_VALIDO));

            var resultado = performPutComAutenticacao(URL_ID, cx.corRequest, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, COR, ID_VALIDO);

            verify(coresService).atualizar(cx.corRequest, ID_VALIDO);
            verifyNoMoreInteractions(coresService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            var cx = new CorTestContext();
            var request = new StatusRequest(true);
            when(coresService.alterarStatus(ID_VALIDO, request)).thenReturn(cx.corResponse);

            var resultado = performPatchComAutenticacao(URL_STATUS, request, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Branco", true);

            verify(coresService).alterarStatus(ID_VALIDO, request);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao alterar o status sem valor")
        void deveRetornar400AoAlterarStatusSemValor() throws Exception {
            var request = new StatusRequest(null);

            var resultado = performPatchComAutenticacao(URL_STATUS, request, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(coresService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao alterar o status")
        void deveRetornar404AoAlterarStatus() throws Exception {
            var request = new StatusRequest(true);
            when(coresService.alterarStatus(ID_VALIDO, request))
                    .thenThrow(new NotFoundException(COR, ID_VALIDO));

            var resultado = performPatchComAutenticacao(URL_STATUS, request, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, COR, ID_VALIDO);

            verify(coresService).alterarStatus(ID_VALIDO, request);
            verifyNoMoreInteractions(coresService);
        }
    }
}
