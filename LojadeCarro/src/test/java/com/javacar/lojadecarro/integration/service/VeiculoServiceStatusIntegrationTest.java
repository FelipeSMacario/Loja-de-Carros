package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.security.test.context.support.WithMockUser;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.enums.StatusVeiculo.PAUSADO;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes das alterações de status do veículo")
public class VeiculoServiceStatusIntegrationTest extends AbstractVeiculoServiceIntegrationTest{
    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes para pausar o anúncio do veiculo")
    class Pausar {
        @Test
        @DisplayName("Deve pausar o veículo")
        void devePausarVeiculo() {
            //Arrange
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", DISPONIVEL, null);
            //ACT
            var response = veiculoService.pausarVeiculo(veiculo.getId());
            entityManager.flush();
            entityManager.clear();
            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            //Assert
            assertThat(response)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa
                    ).containsExactly(
                            veiculoAtualizado.getId(),
                            veiculoAtualizado.getPlaca()
                    );

            assertThat(response.statusVeiculo())
                    .isEqualTo(veiculoAtualizado.getStatusVeiculo())
                    .isEqualTo(PAUSADO);
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoNaoEncontrarVeiculo() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.pausarVeiculo(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_INVALIDO);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                mode = EnumSource.Mode.EXCLUDE,
                names = "DISPONIVEL"
        )
        @DisplayName("Deve lançar exceção ao pausar veículo com status proibido")
        void deveLancarExcecaoPausarVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", status, null);
            var veiculoId = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.pausarVeiculo(veiculoId));
            //Assert
            assertBusinessResponseError(exception, "Somente um veículo disponível pode ser pausado");
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes para reativar o veículo")
    class Reativar {
        @Test
        @DisplayName("Deve reativar o veículo")
        void deveReativarOVeiculo() {
            //Arrange
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", PAUSADO, null);
            //ACT
            var response = veiculoService.reativarVeiculo(veiculo.getId());
            entityManager.flush();
            entityManager.clear();
            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            //Assert
            AssertionsForClassTypes.assertThat(response)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa
                    ).containsExactly(
                            veiculoAtualizado.getId(),
                            veiculoAtualizado.getPlaca()
                    );

            assertThat(response.statusVeiculo())
                    .isEqualTo(veiculoAtualizado.getStatusVeiculo())
                    .isEqualTo(DISPONIVEL);
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoNaoEncontrarVeiculo() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.reativarVeiculo(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_INVALIDO);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                mode = EnumSource.Mode.EXCLUDE,
                names = "PAUSADO"
        )
        @DisplayName("Deve lançar exceção ao reativar veículo com status proibido")
        void deveLancarExcecaoReativarVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", status, null);
            var veiculoId = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.reativarVeiculo(veiculoId));
            //Assert
            assertBusinessResponseError(exception, "Somente um veículo pausado pode ser reativado");
        }
    }
}
