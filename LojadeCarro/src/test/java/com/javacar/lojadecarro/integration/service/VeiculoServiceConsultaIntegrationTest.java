package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.*;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes das consultas de veículos")
public class VeiculoServiceConsultaIntegrationTest extends AbstractVeiculoServiceIntegrationTest {
    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da listagem de veiculos")
    class Listar {
        @Test
        @DisplayName("Deve listar todos os veiculos")
        void deveListarTodosOsVeiculos() {
            //Act
            var veiculosPorStatus = veiculoPersistidoPorStatus(null, null, null);

            var veiculos = veiculoService.listarAdministrativo(Pageable.unpaged(), null);
            //Assert
            assertThat(veiculos)
                    .isNotEmpty()
                    .anyMatch(veiculo -> veiculo.statusVeiculo() == DISPONIVEL)
                    .anyMatch(veiculo -> veiculo.statusVeiculo() == RESERVADO)
                    .anyMatch(veiculo -> veiculo.statusVeiculo() == PAUSADO)
                    .anyMatch(veiculo -> veiculo.statusVeiculo() == VENDIDO)
                    .extracting(VeiculoResponse::id)
                    .contains(veiculosPorStatus.getFirst().getId(),
                            veiculosPorStatus.get(1).getId(),
                            veiculosPorStatus.get(2).getId(),
                            veiculosPorStatus.get(3).getId(),
                            veiculosPorStatus.getLast().getId());

        }

        @ParameterizedTest
        @EnumSource(StatusVeiculo.class)
        @DisplayName("Deve listar todos os veiculos por status")
        void deveListarOsVeiculosPorStatus(StatusVeiculo status) {
            //Arrange
            var veiculosPorStatus = veiculoPersistidoPorStatus(status, null, null);
            //Act
            var veiculos = veiculoService.listarAdministrativo(Pageable.unpaged(), status);
            //Assert
            assertThat(veiculos)
                    .isNotEmpty()
                    .allMatch(veiculo -> veiculo.statusVeiculo() == status)
                    .extracting(VeiculoResponse::id)
                    .contains(veiculosPorStatus.getFirst().getId(),
                            veiculosPorStatus.get(1).getId(),
                            veiculosPorStatus.get(2).getId(),
                            veiculosPorStatus.get(3).getId(),
                            veiculosPorStatus.getLast().getId());

        }
    }

    @Nested
    @DisplayName("Testes da listagem de veiculos ativos")
    class ListarAtivos {
        @Test
        @DisplayName("Deve listar todos os veiculos ativos")
        void deveListarOsVeiculosAtivos() {
            //Arrange
            var veiculosPorStatus = veiculoPersistidoPorStatus(null, null, null);
            //Act
            var veiculos = veiculoService.listarAtivos(Pageable.unpaged());
            //Assert
            assertThat(veiculos)
                    .isNotEmpty()
                    .allMatch(veiculo -> veiculo.statusVeiculo() == DISPONIVEL)
                    .extracting(VeiculoResponse::id)
                    .contains(veiculosPorStatus.getFirst().getId(),
                            veiculosPorStatus.get(1).getId())
                    .doesNotContain(
                            veiculosPorStatus.get(2).getId(),
                            veiculosPorStatus.get(3).getId(),
                            veiculosPorStatus.getLast().getId());

        }
    }

    @Nested
    @WithMockUser(roles = "USUARIO")
    @DisplayName("Testes da listagem de veiculos do usuário autenticado")
    class ListarMeusAnuncios {
        @Test
        @DisplayName("Deve listar todos os veiculos do usuário autenticado")
        void deveListarTodosOsVeiculosUsuarioAutenticado() {
            //Act
            var vendedor = criarVendedorPersistido();
            var outroVendedor = criarVendedorPersistido("OUTRO VENDEDOR", "15926347977", "outrovendedor.com");
            var veiculosPorStatus = veiculoPersistidoPorStatus(null, vendedor, outroVendedor);

            var veiculos = veiculoService.listarMeusAnuncios(Pageable.unpaged(), vendedor.getId(), null);
            //Assert
            assertThat(veiculos)
                    .isNotEmpty()
                    .anyMatch(veiculo -> veiculo.statusVeiculo() == DISPONIVEL)
                    .anyMatch(veiculo -> veiculo.statusVeiculo() == RESERVADO)
                    .anyMatch(veiculo -> veiculo.statusVeiculo() == PAUSADO)
                    .anyMatch(veiculo -> veiculo.statusVeiculo() == VENDIDO)
                    .extracting(VeiculoResponse::id)
                    .contains(
                            veiculosPorStatus.get(1).getId(),
                            veiculosPorStatus.get(2).getId(),
                            veiculosPorStatus.get(3).getId(),
                            veiculosPorStatus.getLast().getId())
                    .doesNotContain(veiculosPorStatus.getFirst().getId());

        }

        @ParameterizedTest
        @EnumSource(StatusVeiculo.class)
        @DisplayName("Deve listar todos os veiculos por status")
        void deveListarOsVeiculosPorStatus(StatusVeiculo status) {
            //Arrange
            var vendedor = criarVendedorPersistido();
            var outroVendedor = criarVendedorPersistido("OUTRO VENDEDOR", "15926347977", "outrovendedor.com");
            var veiculosPorStatus = veiculoPersistidoPorStatus(status, vendedor, outroVendedor);
            //Act
            var veiculos = veiculoService.listarMeusAnuncios(Pageable.unpaged(), vendedor.getId(), status);
            //Assert
            assertThat(veiculos)
                    .isNotEmpty()
                    .allMatch(veiculo -> veiculo.statusVeiculo() == status)
                    .extracting(VeiculoResponse::id)
                    .contains(
                            veiculosPorStatus.get(1).getId(),
                            veiculosPorStatus.get(2).getId(),
                            veiculosPorStatus.get(3).getId(),
                            veiculosPorStatus.getLast().getId())
                    .doesNotContain(veiculosPorStatus.getFirst().getId());

        }
    }

    @Nested
    @DisplayName("Testes de busca do veiculo")
    class Buscar {
        @Test
        @DisplayName("Deve buscar o veiculo")
        void deveBuscarVeiculo() {
            //Arrange
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", DISPONIVEL, null);
            //Act
            var response = veiculoService.buscarPorId(veiculo.getId());
            entityManager.flush();
            entityManager.clear();
            var veiculoPersistido = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            //Assert
            assertThat(response)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::valor,
                            VeiculoResponse::statusVeiculo
                    ).doesNotContainNull();

            assertThat(veiculoPersistido)
                    .extracting(Veiculo::getStatusVeiculo)
                    .isEqualTo(DISPONIVEL);

            assertThat(veiculoPersistido)
                    .extracting(
                            Veiculo::getId,
                            Veiculo::getStatusVeiculo
                    ).containsExactly(response.id(), response.statusVeiculo());
        }

        @Test
        @DisplayName("Deve lançar exceção quando veiculo não existir")
        void deveLancarExcecaoQuandoVeiculoNaoExistir() {
            //Act
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.buscarPorId(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_INVALIDO);
        }
    }

    private List<Veiculo> veiculoPersistidoPorStatus(StatusVeiculo status,
                                                     Usuario usuario,
                                                     Usuario outroVendedor) {
        var carroceria = vendaIntegrationFixture.criarCarroceriaPersistida();
        var cor = vendaIntegrationFixture.criarCorPersistida();
        var modelo = vendaIntegrationFixture.criarModeloPersistido();
        var combustivel = vendaIntegrationFixture.criarCombustivelPersistido();
        var vendedor = (usuario == null) ? criarVendedorPersistido() : usuario;
        var outroUsuario = (outroVendedor == null) ? vendedor : outroVendedor;

        var veiculoDisponivel = vendaIntegrationFixture
                .criarVeiculoPersistido("1234561",
                        BigDecimal.valueOf(100000),
                        carroceria,
                        cor,
                        modelo,
                        combustivel,
                        outroUsuario,
                        status == null ? DISPONIVEL : status);
        var veiculoDisponivel2 = vendaIntegrationFixture
                .criarVeiculoPersistido("1234565",
                        BigDecimal.valueOf(500000),
                        carroceria,
                        cor,
                        modelo,
                        combustivel,
                        vendedor,
                        status == null ? DISPONIVEL : status);
        var veiculoReservado = vendaIntegrationFixture
                .criarVeiculoPersistido("1234562",
                        BigDecimal.valueOf(200000),
                        carroceria,
                        cor,
                        modelo,
                        combustivel,
                        vendedor,
                        status == null ? RESERVADO : status);
        var veiculoPausado = vendaIntegrationFixture
                .criarVeiculoPersistido("1234563",
                        BigDecimal.valueOf(300000),
                        carroceria,
                        cor,
                        modelo,
                        combustivel,
                        vendedor,
                        status == null ? PAUSADO : status);
        var veiculoVendido = vendaIntegrationFixture
                .criarVeiculoPersistido("1234564",
                        BigDecimal.valueOf(400000),
                        carroceria,
                        cor,
                        modelo,
                        combustivel,
                        vendedor,
                        status == null ? VENDIDO : status);

        return List.of(veiculoDisponivel, veiculoDisponivel2, veiculoReservado, veiculoPausado, veiculoVendido);
    }

    private Usuario criarVendedorPersistido(String nome, String cpf, String email) {
        return vendaIntegrationFixture
                .criarUsuarioPersistido(nome, cpf, email);
    }

}
