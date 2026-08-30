package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.factory.veiculo.VeiculoResponseFactory;
import com.javacar.lojadecarro.service.VeiculoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.StatusVeiculo.*;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminVeiculoController.class)
@DisplayName("Testes da controller ADM do veiculo")
public class AdminVeiculoControllerTest extends BaseControllerTest {
    private static final String URL = "/admin/veiculos";
    @MockitoBean
    private VeiculoService veiculoService;

    private final PageRequest pageable =
            PageRequest.of(0, 9);

    @Nested
    @DisplayName("Testes da listagem de veículos")
    class Listar {

        @ParameterizedTest
        @EnumSource(value = StatusVeiculo.class)
        @DisplayName("Deve listar os veículos por status")
        void deveListarVeiculosPorStatus(StatusVeiculo statusVeiculo) throws Exception {
            //Arrange
            var veiculos = veiculosResponseList(statusVeiculo);

            //Act + Assert
            when(veiculoService.listarAdministrativo(any(), eq(statusVeiculo)))
                    .thenReturn(veiculos);

            var resultado = performGetComAutenticacao(URL, "status", statusVeiculo.toString(), ID_JWT, ROLE_ADM);
            resultado
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(4))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(4))
                    .andExpect(jsonPath(
                            "$.content[*].statusVeiculo",
                            everyItem(is(statusVeiculo.name()))
                    ));

            var captor = ArgumentCaptor.forClass(Pageable.class);

            verify(veiculoService).listarAdministrativo(
                    captor.capture(),
                    eq(statusVeiculo)
            );

            assertThat(captor.getValue())
                    .extracting(
                            Pageable::getPageNumber,
                            Pageable::getPageSize
                    )
                    .containsExactly(0, 9);

            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve listar todos os veículos")
        void deveListarTodosOsVeiculos() throws Exception {
            //Arrange
            var veiculos = veiculosResponseList(null);

            //Act + Assert
            when(veiculoService.listarAdministrativo(any(), isNull()))
                    .thenReturn(veiculos);

            var resultado = performGetComAutenticacao(URL, ID_JWT, ROLE_ADM);
            resultado
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].statusVeiculo").value(DISPONIVEL.name()))
                    .andExpect(jsonPath("$.content[1].statusVeiculo").value(PAUSADO.name()))
                    .andExpect(jsonPath("$.content[2].statusVeiculo").value(RESERVADO.name()))
                    .andExpect(jsonPath("$.content[3].statusVeiculo").value(VENDIDO.name()));

            var captor = ArgumentCaptor.forClass(Pageable.class);

            verify(veiculoService).listarAdministrativo(
                    captor.capture(),
                    isNull()
            );

            assertThat(captor.getValue())
                    .extracting(
                            Pageable::getPageNumber,
                            Pageable::getPageSize
                    )
                    .containsExactly(0, 9);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não houver veículos")
        void deveRetornarPaginaVaziaQuandoNaoHouverVeiculos() throws Exception {
            // Arrange

            when(veiculoService.listarAdministrativo(
                    any(Pageable.class),
                    eq(DISPONIVEL)
            )).thenReturn(Page.empty());

            // Act + Assert
            var resultado = performGetComAutenticacao(URL, "status", DISPONIVEL.toString(), ID_JWT, ROLE_ADM);

            resultado
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(veiculoService).listarAdministrativo(
                    any(Pageable.class),
                    eq(DISPONIVEL)
            );

            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao informar status invalído")
        void deveRetornar400AoInformarStatusInvalido() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performGetComAutenticacao(URL, "status", "ALUGADO", ID_JWT, ROLE_ADM);
            assertStatus400(exception);

            verifyNoInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao listar veículos")
        void deveRetornar401aoListarVeiculos() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performGet(URL, "status", DISPONIVEL.toString());
            assertStatus401(exception);

            verifyNoInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 403 ao listar veículos")
        void deveRetornar403aoListarVeiculos() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performGetComAutenticacao(URL, "status", DISPONIVEL.toString(), ID_JWT, ROLE_USUARIO);
            assertStatus403(exception);

            verifyNoInteractions(veiculoService);
        }

    }

    private Page<VeiculoResponse> veiculosResponseList(StatusVeiculo status) {
        var response1 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comStatus(status == null ? DISPONIVEL : status)
                .build();

        var response2 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(2L)
                .comStatus(status == null ? PAUSADO : status)
                .build();

        var response3 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(3L)
                .comStatus(status == null ? RESERVADO : status)
                .build();

        var response4 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(4L)
                .comStatus(status == null ? VENDIDO : status)
                .build();

        return new PageImpl<>(
                List.of(response1, response2, response3, response4),
                pageable,
                4);
    }

}
