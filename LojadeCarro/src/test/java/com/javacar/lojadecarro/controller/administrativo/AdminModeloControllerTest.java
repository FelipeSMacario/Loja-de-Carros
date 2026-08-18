package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
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
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.factory.modelo.ModeloTestContext.criaModeloResponse;
import static com.javacar.lojadecarro.factory.modelo.ModeloTestContext.criaModeloResponse2;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminModeloController.class)
@DisplayName("Testes da controller do modelo")
class AdminModeloControllerTest extends BaseControllerTest {
    private static final String URL = "/admin/modelos";
    private static final String URL_ID = URL + "/" + ID_VALIDO;

    @MockitoBean
    private ModeloService modeloService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {
        @Test
        @DisplayName("Deve criar um modelo")
        void deveCriarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloService.criar(cx.request))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.request, ID_JWT, ROLE_ADM);
            resultado.andExpect(
                    header().string(
                            "Location",
                            "http://localhost/admin/modelos/" + ID_VALIDO
                    )
            );
            assertResult(
                    resultado,
                    status().isCreated(),
                    ID_VALIDO,
                    "Onix",
                    true
            );

            resultado
                    .andExpect(jsonPath("$.marca").exists())
                    .andExpect(jsonPath("$.marca.id").value(3L))
                    .andExpect(jsonPath("$.marca.nome").value("Chevrolet"))
                    .andExpect(jsonPath("$.marca.url").value("https://www.chevrolet.com"));

            verify(modeloService).criar(cx.request);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao criar um modelo")
        void deveRetornar400AoCriarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            //Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.requestIncompleto, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao criar um modelo")
        void deveRetornar500AoCriarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloService.criar(cx.request))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPostComAutenticacao(URL, cx.request, ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(modeloService).criar(cx.request);
            verifyNoMoreInteractions(modeloService);
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve listar modelos ativos")
        void deveListarModelosAtivos() throws Exception {
            //Arrange
            var response1 = criaModeloResponse(true);
            var response2 = criaModeloResponse2(true);

            var response = List.of(response1, response2);

            when(modeloService.listarAdministracao(ATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, "status", ATIVAS.toString(), ID_JWT, ROLE_ADM);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Onix",
                    "Celta",
                    true,
                    true
            );
            verify(modeloService).listarAdministracao(ATIVAS);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve utilizar TODAS como status padrão")
        void deveUtilizarTodasComoStatusPadrao() throws Exception {
            //Arrange
            var response1 = criaModeloResponse(true);
            var response2 = criaModeloResponse2(false);

            var response = List.of(response1, response2);

            when(modeloService.listarAdministracao(TODAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_ADM);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Onix",
                    "Celta",
                    true,
                    false
            );
            verify(modeloService).listarAdministracao(TODAS);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao listar modelos")
        void deveRetornar401AoListarModelos() throws Exception {
            //Arrange
            //ACT
            var resultado = performGet(URL);
            //Assert
            assertStatus401(resultado);

            verifyNoInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve retornar 403 ao listar modelos")
        void deveRetornar403AoListarModelos() throws Exception {
            //Arrange
            //ACT
            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_USUARIO);
            //Assert
            assertStatus403(resultado);

            verifyNoInteractions(modeloService);
        }
    }

    @Nested
    @DisplayName("Testes da busca por ID")
    class Buscar {

        @Test
        @DisplayName("Deve buscar um modelo")
        void deveBuscarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Onix",
                    true
            );

            verify(modeloService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar o modelo")
        void deveRetornar404BuscarModelo() throws Exception {
            //Arrange
            when(modeloService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenThrow(new NotFoundException(MODELO, ID_VALIDO));
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ID, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, MODELO, ID_VALIDO);

            verify(modeloService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(modeloService);
        }
    }

    @Nested
    @DisplayName("Testes da atualização")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o modelo")
        void deveAtualizarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloService.atualizar(cx.request, ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.request, ID_JWT, ROLE_ADM);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Onix",
                    true
            );

            verify(modeloService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao atualizar o modelo sem nome")
        void deveRetornar400AtualizarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.requestIncompleto, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar o modelo")
        void deveRetornar404AtualizarModelo() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloService.atualizar(cx.request, ID_VALIDO))
                    .thenThrow(new NotFoundException(MODELO, ID_VALIDO));
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.request, ID_JWT, ROLE_ADM);
            assertStatus404(
                    resultado,
                    MODELO,
                    ID_VALIDO
            );

            verify(modeloService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(modeloService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            //Arrange
            var cx = new ModeloTestContext();
            var status = new StatusRequest(true);

            when(modeloService.alterarStatus(ID_VALIDO, status))
                    .thenReturn(cx.response);
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_ID + "/status", status, ID_JWT, ROLE_ADM);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Onix",
                    true);

            verify(modeloService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(modeloService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao alterar status")
        void deveRetornar400AoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(null);
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_ID + "/status", status, ID_JWT, ROLE_ADM);

            assertStatus400(resultado);
            verifyNoInteractions(modeloService);

        }

        @Test
        @DisplayName("Deve retornar 404 ao alterar status")
        void deveRetornar404aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            when(modeloService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new NotFoundException(MODELO, ID_VALIDO));
            //ACT + assert
            var resultado = performPatchComAutenticacao(URL_ID + "/status", status, ID_JWT, ROLE_ADM);
            assertStatus404(resultado,
                    MODELO,
                    ID_VALIDO);

            verify(modeloService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(modeloService);
        }
    }

}
