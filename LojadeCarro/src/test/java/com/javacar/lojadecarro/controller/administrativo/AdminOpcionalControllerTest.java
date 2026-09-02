package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.opcional.OpcionalTestContext;
import com.javacar.lojadecarro.service.OpcionalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;
import static com.javacar.lojadecarro.enums.StatusFiltro.*;
import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.*;
import static com.javacar.lojadecarro.factory.opcional.OpcionalTestContext.criaOpcionalResponse;
import static com.javacar.lojadecarro.factory.opcional.OpcionalTestContext.criaOpcionalResponse2;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminOpcionalController.class)
@DisplayName("Testes da controller do opcional")
class AdminOpcionalControllerTest extends BaseControllerTest {
    private static final String URL = "/admin/opcionais";
    private static final String URL_ID = URL + "/" + ID_VALIDO;

    @MockitoBean
    private OpcionalService opcionalService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {
        @Test
        @DisplayName("Deve cadastrar um opcional")
        void deveCadastrarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalService.criar(cx.request))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.request, ID_JWT, ROLE_ADM);
            resultado.andExpect(
                    header().string(
                            "Location",
                            "http://localhost/admin/opcionais/" + ID_VALIDO
                    )
            );
            assertResult(resultado, status().isCreated(), ID_VALIDO, "Freio Abs", true);

            verify(opcionalService).criar(cx.request);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao criar um opcional")
        void deveRetornar400AoCriarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            //Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.requestIncompleto, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao criar um opcional")
        void deveRetornar500AoCriarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalService.criar(cx.request))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.request, ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(opcionalService).criar(cx.request);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 403 ao cadastrar um opcional")
        void deveRetornar403AoCadastrarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();
            //Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.request, ID_JWT, ROLE_USUARIO);
            assertStatus403(resultado);

            verifyNoInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao cadastrar um opcional")
        void deveRetornar401AoCadastrarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();
            //Act + Assert
            var resultado = performPost(URL, cx.request);
            assertStatus401(resultado);

            verifyNoInteractions(opcionalService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {

        @Test
        @DisplayName("Deve listar opcionais ativos")
        void deveListarOpcionaisAtivos() throws Exception {
            //Arrange
            var response1 = criaOpcionalResponse(true);
            var response2 = criaOpcionalResponse2(true);

            var response = List.of(response1, response2);

            when(opcionalService.listarAdministracao(ATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, "status", ATIVAS.toString(), ID_JWT, ROLE_ADM);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Freio Abs",
                    "Automatico",
                    true,
                    true
            );
            verify(opcionalService).listarAdministracao(ATIVAS);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve encaminhar o status informado para a service")
        void deveEncaminharStatusTodas() throws Exception {
            //Arrange
            var response1 = criaOpcionalResponse(true);
            var response2 = criaOpcionalResponse2(false);

            var response = List.of(response1, response2);

            when(opcionalService.listarAdministracao(TODAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_ADM);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Freio Abs",
                    "Automatico",
                    true,
                    false
            );
            verify(opcionalService).listarAdministracao(TODAS);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 401 na listagem")
        void deveRetornar401NaListagem() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performGet(URL);
            assertStatus401(resultado);
            verifyNoInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 403 na listagem")
        void deveRetornar403NaListagem() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_USUARIO);
            assertStatus403(resultado);
            verifyNoInteractions(opcionalService);
        }
    }

    @Nested
    @DisplayName("Testes da busca de opcional")
    class Buscar {

        @Test
        @DisplayName("Deve buscar um opcional")
        void deveBuscarUmOpcionalPorID() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Freio Abs", true);

            verify(opcionalService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar um opcional")
        void deveRetornar404aoBuscarUmOpcionalPorID() throws Exception {
            //Arrange

            when(opcionalService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenThrow(new NotFoundException(OPCIONAL, ID_VALIDO));
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, OPCIONAL, ID_VALIDO);

            verify(opcionalService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao buscar um opcional")
        void deveRetornar500AoBuscarOpcional() throws Exception {
            //Arrange
            when(opcionalService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(opcionalService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao buscar um opcional")
        void deveRetornar401AoBuscarOpcional() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performGet(URL_ID);
            assertStatus401(resultado);

            verifyNoInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 403 ao buscar um opcional")
        void deveRetornar403AoBuscarOpcional() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_USUARIO);
            assertStatus403(resultado);

            verifyNoInteractions(opcionalService);
        }
    }

    @Nested
    @DisplayName("Testes da atualização")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar o opcional")
        void deveAtualizarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalService.atualizar(cx.request, ID_VALIDO))
                    .thenReturn(cx.response);

            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.request, ID_JWT, ROLE_ADM);
            assertResult(resultado, status().isOk(), ID_VALIDO, "Freio Abs", true);

            verify(opcionalService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);
        }


        @Test
        @DisplayName("Deve retornar 404 ao atualizar opcional")
        void deveRetornar404AoAtualizarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalService.atualizar(cx.request, ID_VALIDO))
                    .thenThrow(new NotFoundException(OPCIONAL, ID_VALIDO));

            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.request, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, OPCIONAL, ID_VALIDO);

            verify(opcionalService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);

        }

        @Test
        @DisplayName("Deve retornar 400 ao atualizar opcional")
        void deveRetornar400aoAtualizarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.requestIncompleto, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);
            verifyNoInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao atualizar opcional")
        void deveRetornar500aoAtualizarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();
            when(opcionalService.atualizar(cx.request, ID_VALIDO))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.request, ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(opcionalService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao atualizar opcional")
        void deveRetornar401aoAtualizarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();
            //Act + Assert
            var resultado = performPut(URL_ID, cx.request);
            assertStatus401(resultado);
            verifyNoInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 403 ao atualizar opcional")
        void deveRetornar403aoAtualizarOpcional() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.request, ID_JWT, ROLE_USUARIO);
            assertStatus403(resultado);
            verifyNoInteractions(opcionalService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            //Arrange
            var cx = new OpcionalTestContext();
            var status = new StatusRequest(true);

            when(opcionalService.alterarStatus(ID_VALIDO, status))
                    .thenReturn(cx.response);
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_ID + "/status", status, ID_JWT, ROLE_ADM);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Freio Abs",
                    true);

            verify(opcionalService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao alterar status")
        void deveRetornar400AoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(null);
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_ID + "/status", status, ID_JWT, ROLE_ADM);

            assertStatus400(resultado);
            verifyNoInteractions(opcionalService);

        }

        @Test
        @DisplayName("Deve retornar 404 ao alterar status")
        void deveRetornar404aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            when(opcionalService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new NotFoundException(OPCIONAL, ID_VALIDO));
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_ID + "/status", status, ID_JWT, ROLE_ADM);
            assertStatus404(resultado,
                    OPCIONAL,
                    ID_VALIDO);

            verify(opcionalService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao alterar status")
        void deveRetornar500aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            when(opcionalService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_ID + "/status", status, ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(opcionalService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao alterar status")
        void deveRetornar401aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            //ACT + assert
            var resultado = performPatch(URL_ID + "/status", status);
            assertStatus401(resultado);

            verifyNoInteractions(opcionalService);
        }

        @Test
        @DisplayName("Deve retornar 403 ao alterar status")
        void deveRetornar403aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_ID + "/status", status, ID_JWT, ROLE_USUARIO);
            assertStatus403(resultado);

            verifyNoInteractions(opcionalService);
        }
    }

}
