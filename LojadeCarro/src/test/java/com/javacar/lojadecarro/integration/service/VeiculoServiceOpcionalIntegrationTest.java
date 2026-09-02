package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;
import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes dos opcionais do veículo")
public class VeiculoServiceOpcionalIntegrationTest extends AbstractVeiculoServiceIntegrationTest {
    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes para desvincular os opcionais")
    class DesvinculaOpcionais {
        @Test
        @DisplayName("Deve desvincular opcionais")
        void deveDesvincularOsOpcionais() {
            //Arrange
            var veiculo = criarVeiculoPersistidoComOpcionais("ZX5AS7Q", DISPONIVEL, null);

            var idVeiculo = veiculo.getId();


            entityManager.flush();
            entityManager.clear();

            var veiculoPersistido = veiculoRepository
                    .findById(idVeiculo)
                    .orElseThrow();

            var opcionalRemovido = veiculoPersistido
                    .getOpcionais()
                    .getFirst()
                    .getOpcional()
                    .getId();

            var idsEsperados = veiculoPersistido.getOpcionais()
                    .stream()
                    .map(vo -> vo.getOpcional().getId())
                    .filter(id -> !id.equals(opcionalRemovido))
                    .toList();

            assertThat(veiculoPersistido.getOpcionais())
                    .hasSize(3);
            //ACT
            veiculoService.desvincularOpcionais(
                    idVeiculo,
                    List.of(opcionalRemovido)
            );

            entityManager.flush();
            entityManager.clear();

            var veiculoAtualizado = veiculoRepository
                    .findById(idVeiculo)
                    .orElseThrow();

            assertThat(veiculoAtualizado.getOpcionais())
                    .hasSize(2)
                    .extracting(vo -> vo.getOpcional().getId())
                    .containsExactlyInAnyOrderElementsOf(idsEsperados)
                    .doesNotContain(opcionalRemovido);
        }

        @Test
        @DisplayName("Deve lançar exceção inserir opcionais duplicados")
        void deveLancarExcecaoAoInformarOpcionaisDuplicados() {
            //Arrange
            var listaOpcionais = List.of(1L, 1L, 3L);
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", DISPONIVEL, null);
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertBusinessResponseError(exception, "A requisição possui opcionais duplicadas.");
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar o veiculo")
        void deveLancarExcecaoQuandoNaoEncontrarVeiculo() {
            //Arrange
            var listaOpcionais = List.of(1L, 3L);
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.desvincularOpcionais(ID_INVALIDO, listaOpcionais));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_INVALIDO);

        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar um opcional inexistente")
        void deveLancarExcecaoQuandoNaoExistente() {
            //Arrange
            var listaOpcionais = List.of(-1L, 1L, 3L);
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", DISPONIVEL, null);
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertBusinessResponseError(exception, "Um ou mais opcionais não foram encontrados.");
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar desvincular um opcional que o veiculo não possui")
        void deveLancarExcecaoAoDesvinvincularOpcionalQueVeiculoNaoPossui() {
            //Arrange
            var opcional = vendaIntegrationFixture.criarOpcional("NOVO OPCIONAL", true);
            entityManager.flush();
            entityManager.clear();

            var listaOpcionais = List.of(opcional.getId());
            var veiculo = criarVeiculoPersistidoComOpcionais("ZX5AS7Q", DISPONIVEL, null);
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertBusinessResponseError(exception, "O Veiculo informado não possui esse opcional");
        }

    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes para vincular os opcionais")
    class VincularOpcionais {
        @Test
        @DisplayName("Deve vincular opcionais")
        void deveVincularOpcionais() {
            //Arrange
            var opcional = vendaIntegrationFixture.criarOpcional("NOVO OPCIONAL", true);
            entityManager.flush();
            entityManager.clear();

            var veiculo = criarVeiculoPersistidoComOpcionais(
                    "ZX5AS7Q",
                    DISPONIVEL,
                    null
            );

            entityManager.flush();
            entityManager.clear();

            var veiculoPersistido = veiculoRepository
                    .findById(veiculo.getId())
                    .orElseThrow();

            var idsEsperados = veiculoPersistido.getOpcionais()
                    .stream()
                    .map(vo -> vo.getOpcional().getId())
                    .collect(Collectors.toCollection(ArrayList::new));

            idsEsperados.add(opcional.getId());

// Act
            veiculoService.vincularOpcionais(
                    veiculoPersistido.getId(),
                    List.of(opcional.getId())
            );

            entityManager.flush();
            entityManager.clear();

            var veiculoAtualizado = veiculoRepository
                    .findById(veiculoPersistido.getId())
                    .orElseThrow();

// Assert
            assertThat(veiculoAtualizado.getOpcionais())
                    .extracting(vo -> vo.getOpcional().getId())
                    .containsExactlyInAnyOrderElementsOf(idsEsperados);
        }

        @Test
        @DisplayName("Deve lançar exceção inserir opcionais duplicados")
        void deveLancarExcecaoInserirOpcionaisDuplicados() {
            //Arrange
            var listaOpcionais = List.of(1L, 1L, 3L);
            var veiculo = criarVeiculoPersistidoComOpcionais("ZX5AS7Q", DISPONIVEL, null);
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertBusinessResponseError(exception, "A requisição possui opcionais duplicadas.");
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar o veiculo")
        void deveLancarExcecaoQuandoNaoEncontrarVeiculo() {
            //Arrange
            var listaOpcionais = List.of(1L, 3L);
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.vincularOpcionais(ID_INVALIDO, listaOpcionais));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_INVALIDO);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar um opcional inexistente")
        void deveLancarExcecaoQuandoNaoExistente() {
            //Arrange
            var listaOpcionais = List.of(-1L, 1L, 3L);
            var veiculo = criarVeiculoPersistidoComOpcionais("ZX5AS7Q", DISPONIVEL, null);
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertBusinessResponseError(exception, "Um ou mais opcionais não foram encontrados.");
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar vincular um opcional que o veiculo já possui")
        void deveLancarExcecaoAoVincularOpcionalQueVeiculoJaPossui() {
            //Arrange
            var veiculo = criarVeiculoPersistidoComOpcionais("ZX5AS7Q", DISPONIVEL, null);
            var listaOpcionais = List.of(veiculo.getOpcionais().getFirst().getOpcional().getId());
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertBusinessResponseError(exception, OPCIONAL.jaAtiva());
        }
    }

    private Veiculo criarVeiculoPersistidoComOpcionais(String placa, StatusVeiculo status, Usuario usuario) {
        var vendedor = (usuario == null) ? criarVendedorPersistido() : usuario;
        return vendaIntegrationFixture
                .criarVeiculoPersistidoComOpcionais(placa,
                        BigDecimal.valueOf(200000),
                        vendaIntegrationFixture.criarCarroceriaPersistida(),
                        vendaIntegrationFixture.criarCorPersistida(),
                        vendaIntegrationFixture.criarModeloPersistido(),
                        vendaIntegrationFixture.criarCombustivelPersistido(),
                        vendedor,
                        status);
    }


}
