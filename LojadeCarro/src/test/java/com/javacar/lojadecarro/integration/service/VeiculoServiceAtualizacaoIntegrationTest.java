package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.enums.StatusVeiculo.*;
import static com.javacar.lojadecarro.enums.StatusVeiculo.VENDIDO;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static com.javacar.lojadecarro.enums.Entidade.*;
import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;
import static com.javacar.lojadecarro.enums.Entidade.MODELO;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes da atualização de veículos")
public class VeiculoServiceAtualizacaoIntegrationTest extends AbstractVeiculoServiceIntegrationTest{
    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da atualização do veiculo")
    class Atualizar {
        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"DISPONIVEL", "PAUSADO"}
        )
        @DisplayName("Deve atualizar a placa do veículo com status permitido")
        void deveAtualizarPlacaDeVeiculoComStatusPermitido(StatusVeiculo status) {
            //Arrange
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", status, null);
            var request = criaRequestVeiculo(veiculo, "ZX5AS8Q", null, null, null, null);

            //ACT
            var response = veiculoService.atualizar(request, veiculo.getId());
            entityManager.flush();
            entityManager.clear();
            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            //Assert
            assertThat(response)
                    .isNotNull();

            assertThat(response.placa())
                    .isEqualTo("ZX5AS8Q")
                    .isEqualTo(veiculoAtualizado.getPlaca())
                    .isEqualTo(request.placa());

            assertThat(response.statusVeiculo())
                    .isEqualTo(veiculoAtualizado.getStatusVeiculo());
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"DISPONIVEL", "PAUSADO"}
        )
        @DisplayName("Deve atualizar a carroceria do veículo com status permitido")
        void deveAtualizarCarroceriaDeVeiculoComStatusPermitido(StatusVeiculo status) {
            //Arrange
            var carroceria = vendaIntegrationFixture.criarCarroceriaPersistida("CARROCERIA TESTE 2", true);
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", status, null);
            var request = criaRequestVeiculo(veiculo, null, carroceria.getId(), null, null, null);

            //ACT
            var response = veiculoService.atualizar(request, veiculo.getId());
            entityManager.flush();
            entityManager.clear();
            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            //Assert
            assertThat(response)
                    .isNotNull();

            assertThat(carroceria.getId())
                    .isEqualTo(veiculoAtualizado.getCarroceria().getId());

            assertThat(response.placa())
                    .isEqualTo(veiculoAtualizado.getPlaca())
                    .isEqualTo(request.placa());

            assertThat(response.statusVeiculo())
                    .isEqualTo(veiculoAtualizado.getStatusVeiculo());
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"DISPONIVEL", "PAUSADO"}
        )
        @DisplayName("Deve atualizar a cor do veículo com status permitido")
        void deveAtualizarCorDeVeiculoComStatusPermitido(StatusVeiculo status) {
            //Arrange
            var cor = vendaIntegrationFixture.criarCorPersistida("COR TESTE 2", true);
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", status, null);
            var request = criaRequestVeiculo(veiculo, null, null, cor.getId(), null, null);

            //ACT
            var response = veiculoService.atualizar(request, veiculo.getId());
            entityManager.flush();
            entityManager.clear();
            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            //Assert
            assertThat(response)
                    .isNotNull();

            assertThat(cor.getId())
                    .isEqualTo(veiculoAtualizado.getCor().getId());

            assertThat(response.placa())
                    .isEqualTo(veiculoAtualizado.getPlaca())
                    .isEqualTo(request.placa());

            assertThat(response.statusVeiculo())
                    .isEqualTo(veiculoAtualizado.getStatusVeiculo());
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"DISPONIVEL", "PAUSADO"}
        )
        @DisplayName("Deve atualizar o modelo do veículo com status permitido")
        void deveAtualizarModeloDeVeiculoComStatusPermitido(StatusVeiculo status) {
            //Arrange
            var modelo = vendaIntegrationFixture.criarModeloPersistido("MODELO TESTE 2", true);
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", status, null);
            var request = criaRequestVeiculo(veiculo, null, null, null, modelo.getId(), null);

            //ACT
            var response = veiculoService.atualizar(request, veiculo.getId());
            entityManager.flush();
            entityManager.clear();
            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            //Assert
            assertThat(response)
                    .isNotNull();

            assertThat(modelo.getId())
                    .isEqualTo(veiculoAtualizado.getModelo().getId());

            assertThat(response.placa())
                    .isEqualTo(veiculoAtualizado.getPlaca())
                    .isEqualTo(request.placa());

            assertThat(response.statusVeiculo())
                    .isEqualTo(veiculoAtualizado.getStatusVeiculo());
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"DISPONIVEL", "PAUSADO"}
        )
        @DisplayName("Deve atualizar o combustível do veículo com status permitido")
        void deveAtualizarCombustivelDeVeiculoComStatusPermitido(StatusVeiculo status) {
            //Arrange
            var combustivel = vendaIntegrationFixture.criarCombustivelPersistido("COMBUSTIVEL TESTE 2", true);
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", status, null);
            var request = criaRequestVeiculo(veiculo, null, null, null, null, combustivel.getId());

            //ACT
            var response = veiculoService.atualizar(request, veiculo.getId());
            entityManager.flush();
            entityManager.clear();
            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            //Assert
            assertThat(response)
                    .isNotNull();

            assertThat(combustivel.getId())
                    .isEqualTo(veiculoAtualizado.getCombustivel().getId());

            assertThat(response.placa())
                    .isEqualTo(veiculoAtualizado.getPlaca())
                    .isEqualTo(request.placa());

            assertThat(response.statusVeiculo())
                    .isEqualTo(veiculoAtualizado.getStatusVeiculo());
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        @DisplayName("Deve lançar exceção quando atualizar veículo com status proíbido")
        void deveLancarExcecaoQuandoAtualizarVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", status, null);
            var request = criaRequestVeiculo(veiculo, "ZX5AS8Q", null, null, null, null);
            var veiculoId = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.atualizar(request, veiculoId));
            //Assert
            assertBusinessResponseError(exception, "Somente anúncios disponíveis ou pausados podem ser editados.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar o veículo")
        void deveLancarExcecaoQuandoNaoEncotrarVeiculo() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .build();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_INVALIDO);
        }

        @Test
        @DisplayName("Deve validar o placa unica")
        void deveLancarExcecaoQuandoPlacaJaExistir() {
            //Arrange
            var veiculos = veiculoPersistidoPorStatus(null, null, null);
            var request = criaRequestVeiculo(veiculos.getFirst(), "1234565", null, null, null, null);

            var idVeiculo = veiculos.getFirst().getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.atualizar(request, idVeiculo));
            //Assert
            assertBusinessResponseError(exception, "A placa informada já possui um cadastro.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar a carroceria")
        void deveLancarExcecaoQuandoNaoEncontrarNenhumaCarroceria() {
            //Arrange
            var veiculo = criarVeiculoPersistido("ABC89ZY", DISPONIVEL, null);
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("ABC89ZY")
                    .comIdCarroceria(ID_INVALIDO)
                    .build();
            var veiculoId = veiculo.getId();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, veiculoId));

            //Assert
            assertNotFoundResponseError(exception, CARROCERIA, request.idCarroceria());
        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar a cor")
        void deveLancarExcecaoQuandoNaoEncontrarNenhumaCor() {
            //Arrange
            var veiculo = criarVeiculoPersistido("ABC89ZY", DISPONIVEL, null);
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("ABC89ZY")
                    .comIdCores(ID_INVALIDO)
                    .build();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, idVeiculo));

            //Assert
            assertNotFoundResponseError(exception, COR, request.idCores());

        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar o modelo")
        void deveLancarExcecaoQuandoNaoEncontrarNenhumModelo() {
            //Arrange
            var veiculo = criarVeiculoPersistido("ABC89ZY", DISPONIVEL, null);
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("ABC89ZY")
                    .comIdModelo(ID_INVALIDO)
                    .build();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, idVeiculo));

            //Assert
            assertNotFoundResponseError(exception, MODELO, request.idModelo());

        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar o combustivel")
        void deveLancarExcecaoQuandoNaoEncontrarNenhumCombustivel() {
            //Arrange
            var veiculo = criarVeiculoPersistido("ABC89ZY", DISPONIVEL, null);
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("ABC89ZY")
                    .comIdCombustivel(ID_INVALIDO)
                    .build();

            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, idVeiculo));

            //Assert
            assertNotFoundResponseError(exception, COMBUSTIVEL, request.idCombustivel());

        }
    }

    private VeiculoRequest criaRequestVeiculo(Veiculo veiculo,
                                              String placa,
                                              Long idCarroceria,
                                              Long idCor,
                                              Long idModelo,
                                              Long combustivel) {
        var placaRequest = placa == null ? veiculo.getPlaca() : placa;
        var carroceriaRequest = idCarroceria == null ? veiculo.getCarroceria().getId() : idCarroceria;
        var corRequest = idCor == null ? veiculo.getCor().getId() : idCor;
        var modeloRequest = idModelo == null ? veiculo.getModelo().getId() : idModelo;
        var combustivelRequest = combustivel == null ? veiculo.getCombustivel().getId() : combustivel;

        return new VeiculoRequest(
                veiculo.getQuilometragem(),
                veiculo.getValor(),
                placaRequest,
                veiculo.getMotor(),
                veiculo.getDescricao(),
                veiculo.getAnoFabricacao(),
                null,
                carroceriaRequest,
                corRequest,
                modeloRequest,
                combustivelRequest
        );
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
}
