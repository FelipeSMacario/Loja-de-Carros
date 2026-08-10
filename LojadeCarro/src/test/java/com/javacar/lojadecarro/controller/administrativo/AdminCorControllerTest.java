package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.cor.CorTestContext;
import com.javacar.lojadecarro.service.CoresService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.COR;
import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.cor.CorTestContext.criaCorResponse;
import static com.javacar.lojadecarro.factory.cor.CorTestContext.criaCorResponse2;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCorController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller da cor")
class AdminCorControllerTest extends BaseControllerTest {
    private static final String URL = "/admin/cores";

    @MockitoBean
    private CoresService coresService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {
        @Test
        @DisplayName("Deve criar uma cor")
        void deveCriarUmaCor() throws Exception {
            //Arrange
            var cx = new CorTestContext();

            when(coresService.criar(cx.corRequest))
                    .thenReturn(cx.corResponse);
            //Act + Assert
            var resultado = performPost(URL, cx.corRequest);
            resultado.andExpect(header().exists("Location"));

            assertResult(
                    resultado,
                    status().isCreated(),
                    ID_VALIDO,
                    "Branco",
                    true);

            verify(coresService).criar(cx.corRequest);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao cadastrar uma cor sem nome")
        void deveLancar400aoCadastrarUmaCorSemNome() throws Exception {
            //Arrange
            var cx = new CorTestContext();

            //Act + Assert
            var resultado = performPost(URL, cx.corRequestIncompleto);
            assertStatus400(resultado);

            verifyNoInteractions(coresService);
        }

        @Test
        @DisplayName("Deve lançar 500 ao cadastrar a cor vazio")
        void deveLancar500AoCriarUmaCor() throws Exception {
            //Arrange
            var cx = new CorTestContext();
            when(coresService.criar(cx.corRequest))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPost(URL, cx.corRequest);
            assertStatus500(resultado);
            verify(coresService).criar(cx.corRequest);
            verifyNoMoreInteractions(coresService);
        }

    }

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve utilizar ATIVAS como status padrão")
        void deveListarAsCores() throws Exception {
            //Arrange
            var corResponse1 = criaCorResponse(true);
            var corResponse2 = criaCorResponse2(true);

            var response = List.of(corResponse1, corResponse2);

            when(coresService.listarAdministracao(ATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL, "status", ATIVAS.toString());
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Branco",
                    "Vermelho",
                    true,
                    true
            );

            verify(coresService).listarAdministracao(ATIVAS);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve encaminhar o status informado para a service")
        void deveEncaminharStatusTodas() throws Exception {
            var corResponse1 = criaCorResponse(true);
            var corResponse2 = criaCorResponse2(false);

            var response = List.of(corResponse1, corResponse2);

            when(coresService.listarAdministracao(TODAS))
                    .thenReturn(response);

            //Act + Assert
            var resultado = performGet(URL, "status", TODAS.toString());
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "Branco",
                    "Vermelho",
                    true,
                    false
            );

            verify(coresService).listarAdministracao(TODAS);
            verifyNoMoreInteractions(coresService);
        }
    }

    @Nested
    @DisplayName("Testes da busca por ID")
    class Buscar {

        @Test
        @DisplayName("Deve buscar uma cor por ID")
        void deveBuscarUmaCorPorID() throws Exception {
            //Arrange
            var cx = new CorTestContext();

            when(coresService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenReturn(cx.corResponse);
            //Act + Assert
            var resultado = performGet(URL + "/" + ID_VALIDO);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Branco",
                    true
            );

            verify(coresService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao buscar uma cor por ID")
        void deveLancar404aoBuscarUmaCorPorID() throws Exception {
            //Arrange
            when(coresService.buscarPorIdAdministracao(ID_VALIDO))
                    .thenThrow(new NotFoundException(COR, ID_VALIDO));
            //Act + Assert
            var resultado = performGet(URL + "/" + ID_VALIDO);
            assertStatus404(resultado, COR, ID_VALIDO);

            verify(coresService).buscarPorIdAdministracao(ID_VALIDO);
            verifyNoMoreInteractions(coresService);
        }
    }

    @Nested
    @DisplayName("Testes da atualização")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar uma cor")
        void deveAtualizarUmaCor() throws Exception {
            //Arrange
            var cx = new CorTestContext();

            when(coresService.atualizar(cx.corRequest, ID_VALIDO))
                    .thenReturn(cx.corResponse);
            //Act + Assert
            var resultado = performPut(URL + "/" + ID_VALIDO, cx.corRequest);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Branco",
                    true
            );
            verify(coresService).atualizar(cx.corRequest, ID_VALIDO);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao atualizar uma cor")
        void deveLancar400aoAtualizarUmaCor() throws Exception {
            //Arrange
            var cx = new CorTestContext();

            //Act + Assert
            var resultado = performPut(URL + "/" + ID_VALIDO, cx.corRequestIncompleto);
            assertStatus400(resultado);

            verifyNoInteractions(coresService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao atualizar uma cor")
        void deveLancar404aoBuscarUmaCor() throws Exception {
            //Arrange
            var cx = new CorTestContext();

            when(coresService.atualizar(cx.corRequest, ID_VALIDO))
                    .thenThrow(new NotFoundException(COR, ID_VALIDO));
            //Act + Assert
            var resultado = performPut(URL + "/" + ID_VALIDO, cx.corRequest);
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
            //Arrange
            var cx = new CorTestContext();
            var status = new StatusRequest(true);

            when(coresService.alterarStatus(ID_VALIDO, status))
                    .thenReturn(cx.corResponse);
            //ACT + assert
            var resultado = performPatch(URL + "/" + ID_VALIDO + "/status", status);
            assertResult(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Branco",
                    true);

            verify(coresService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(coresService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao alterar status")
        void deveLancar400aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(null);
            //ACT + assert
            var resultado = performPatch(URL + "/" + ID_VALIDO + "/status", status);

            assertStatus400(resultado);
            verifyNoInteractions(coresService);

        }

        @Test
        @DisplayName("Deve lançar 404 ao alterar status")
        void deveLancar404aoAlterarStatus() throws Exception {
            //Arrange
            var status = new StatusRequest(true);

            when(coresService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new NotFoundException(COR, ID_VALIDO));
            //ACT + assert
            var resultado = performPatch(URL + "/" + ID_VALIDO + "/status", status);
            assertStatus404(resultado,
                    COR,
                    ID_VALIDO);

            verify(coresService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(coresService);
        }
    }

}
