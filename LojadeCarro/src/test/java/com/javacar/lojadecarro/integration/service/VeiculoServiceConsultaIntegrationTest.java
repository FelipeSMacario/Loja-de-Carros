package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.response.VeiculoDetalheResponse;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.*;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.veiculo.VeiculoFiltroFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.shaded.org.apache.commons.lang3.ObjectUtils;

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
            var vendedor = criarVendedorPersistido();
            var veiculos = criarVeiculosPersistidos("123Q5Q5",
                    "123Q5Q6",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    vendedor,
                    (short) 0,
                    (short) 0,
                    null,
                    null,
                    null,
                    null,
                    DISPONIVEL,
                    RESERVADO);

            entityManager.flush();
            entityManager.clear();
            var filtro = VeiculoFiltroFactory.criarFiltro().build();
            //Act
            var resultado = veiculoService.listarAtivos(filtro, Pageable.unpaged());

            var veiculo = veiculoRepository.findById(veiculos.getFirst().getId()).orElseThrow();
            var veiculo2 = veiculoRepository.findById(veiculos.getLast().getId()).orElseThrow();
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .extracting(VeiculoResponse::id)
                    .anyMatch(c -> c.equals(veiculo.getId()))
                    .noneMatch(c -> c.equals(veiculo2.getId()));

        }

        @Test
        @DisplayName("Deve filtrar o veículo pela carroceria")
        void deveFiltrarOsVeiculosCarroceria() {
            //Arrange
            var carroceria = vendaIntegrationFixture.criarCarroceriaPersistida("CARROCERIA 4", true);
            var carroceriaNova = vendaIntegrationFixture.criarCarroceriaPersistida("CARROCERIA 5", true);
            var vendedor = criarVendedorPersistido();

            criarVeiculosPersistidos("123Q5Q5",
                    "123Q5Q6",
                    carroceria,
                    carroceriaNova,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    vendedor,
                    (short) 0,
                    (short) 0,
                    null,
                    null,
                    null,
                    null,
                    DISPONIVEL,
                    DISPONIVEL);

            entityManager.flush();
            entityManager.clear();

            var filtro = VeiculoFiltroFactory.criarFiltro().comCarroceria(carroceria.getId()).build();
            //Act
            var resultado = veiculoService.listarAtivos(filtro, Pageable.unpaged());
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .extracting(VeiculoResponse::carroceria)
                    .anyMatch(c -> c.equals(carroceria.getNome()))
                    .noneMatch(c -> c.equals(carroceriaNova.getNome()));

        }

        @Test
        @DisplayName("Deve filtrar o veículo pela cor")
        void deveFiltrarOsVeiculosCor() {
            //Arrange
            var cor = vendaIntegrationFixture.criarCorPersistida("COR 44", true);
            var corNova = vendaIntegrationFixture.criarCorPersistida("COR 45", true);
            var vendedor = criarVendedorPersistido();

            criarVeiculosPersistidos("123Q5Q5",
                    "123Q5Q6",
                    null,
                    null,
                    cor,
                    corNova,
                    null,
                    null,
                    null,
                    null,
                    vendedor,
                    (short) 0,
                    (short) 0,
                    null,
                    null,
                    null,
                    null,
                    DISPONIVEL,
                    DISPONIVEL);

            entityManager.flush();
            entityManager.clear();

            var filtro = VeiculoFiltroFactory.criarFiltro().comCor(cor.getId()).build();
            //Act
            var resultado = veiculoService.listarAtivos(filtro, Pageable.unpaged());
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .extracting(VeiculoResponse::cor)
                    .anyMatch(c -> c.equals(cor.getNome()))
                    .noneMatch(c -> c.equals(corNova.getNome()));

        }

        @Test
        @DisplayName("Deve filtrar o veículo pela marca")
        void deveFiltrarOsVeiculosPelaMarca() {
            //Arrange
            var marca = vendaIntegrationFixture.criarMarcaPersistida("MARCA 44","URL MARCA 44", true);
            var marcaNova = vendaIntegrationFixture.criarMarcaPersistida("MARCA 45", "URL MARCA 45", true);
            entityManager.flush();
            entityManager.clear();

            var modelo = vendaIntegrationFixture.criarModeloPersistido("MODELO 44", marca, true);
            var modeloNovo = vendaIntegrationFixture.criarModeloPersistido("MODELO 45", marcaNova, true);
            entityManager.flush();
            entityManager.clear();

            var vendedor = criarVendedorPersistido();

            criarVeiculosPersistidos("123Q5Q5",
                    "123Q5Q6",
                    null,
                    null,
                    null,
                    null,
                    modelo,
                    modeloNovo,
                    null,
                    null,
                    vendedor,
                    (short) 0,
                    (short) 0,
                    null,
                    null,
                    null,
                    null,
                    DISPONIVEL,
                    DISPONIVEL);

            entityManager.flush();
            entityManager.clear();

            var filtro = VeiculoFiltroFactory.criarFiltro().comMarca(marca.getId()).build();
            //Act
            var resultado = veiculoService.listarAtivos(filtro, Pageable.unpaged());
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .extracting(VeiculoResponse::marca)
                    .anyMatch(c -> c.equals(marca.getNome()))
                    .noneMatch(c -> c.equals(marcaNova.getNome()));

        }

        @Test
        @DisplayName("Deve filtrar o veículo pelo modelo")
        void deveFiltrarOsVeiculosPeloModelo() {
            //Arrange
            var marcaNova = vendaIntegrationFixture.criarMarcaPersistida("MARCA 45", "URL MARCA 45", true);
            entityManager.flush();

            var modelo = vendaIntegrationFixture.criarModeloPersistido("MODELO 44", marcaNova, true);
            var modeloNovo = vendaIntegrationFixture.criarModeloPersistido("MODELO 45", marcaNova, true);
            entityManager.flush();
            entityManager.clear();

            var vendedor = criarVendedorPersistido();

            criarVeiculosPersistidos("123Q5Q5",
                    "123Q5Q6",
                    null,
                    null,
                    null,
                    null,
                    modelo,
                    modeloNovo,
                    null,
                    null,
                    vendedor,
                    (short) 0,
                    (short) 0,
                    null,
                    null,
                    null,
                    null,
                    DISPONIVEL,
                    DISPONIVEL);

            entityManager.flush();
            entityManager.clear();

            var filtro = VeiculoFiltroFactory.criarFiltro().comModelo(modelo.getId()).build();
            //Act
            var resultado = veiculoService.listarAtivos(filtro, Pageable.unpaged());
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .extracting(VeiculoResponse::modelo)
                    .anyMatch(c -> c.equals(modelo.getNome()))
                    .noneMatch(c -> c.equals(modeloNovo.getNome()));

        }

        @Test
        @DisplayName("Deve filtrar o veículo pelo combustível")
        void deveFiltrarOsVeiculosPeloCombustivel() {
            //Arrange
            var combustivel = vendaIntegrationFixture.criarCombustivelPersistido("COMBUSTIVEL 44", true);
            var combustivelNovo = vendaIntegrationFixture.criarCombustivelPersistido("COMBUSTIVEL 45", true);
            entityManager.flush();
            entityManager.clear();

            var vendedor = criarVendedorPersistido();

            criarVeiculosPersistidos("123Q5Q5",
                    "123Q5Q6",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    combustivel,
                    combustivelNovo,
                    vendedor,
                    (short) 0,
                    (short) 0,
                    null,
                    null,
                    null,
                    null,
                    DISPONIVEL,
                    DISPONIVEL);

            entityManager.flush();
            entityManager.clear();

            var filtro = VeiculoFiltroFactory.criarFiltro().comCombustivel(combustivel.getId()).build();
            //Act
            var resultado = veiculoService.listarAtivos(filtro, Pageable.unpaged());
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .extracting(VeiculoResponse::combustivel)
                    .anyMatch(c -> c.equals(combustivel.getNome()))
                    .noneMatch(c -> c.equals(combustivelNovo.getNome()));

        }

        @Test
        @DisplayName("Deve filtrar pelo ano mínimo incluindo o limite")
        void deveFiltrarPeloAnoMinimo() {
            //Arrange
            var vendedor = criarVendedorPersistido();

            var veiculos = criarVeiculosPersistidos("123Q5Q5",
                    "123Q5Q6",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    vendedor,
                    (short) 2010,
                    (short) 2009,
                    null,
                    null,
                    null,
                    null,
                    DISPONIVEL,
                    DISPONIVEL);

            entityManager.flush();
            entityManager.clear();

            var filtro = VeiculoFiltroFactory
                    .criarFiltro()
                    .comAnoMinimo((short) 2010)
                    .comAnoMaximo((short) 2020)
                    .build();
            //Act
            var resultado = veiculoService.listarAtivos(filtro, Pageable.unpaged());
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .extracting(VeiculoResponse::id)
                    .anyMatch(c -> c.equals(veiculos.getFirst().getId()))
                    .noneMatch(c -> c.equals(veiculos.getLast().getId()));

        }

        @Test
        @DisplayName("Deve filtrar pelo ano máximo incluindo o limite")
        void deveFiltrarPeloAnoMaximo() {
            //Arrange
            var vendedor = criarVendedorPersistido();

            var veiculos = criarVeiculosPersistidos("123Q5Q5",
                    "123Q5Q6",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    vendedor,
                    (short) 2010,
                    (short) 2020,
                    null,
                    null,
                    null,
                    null,
                    DISPONIVEL,
                    DISPONIVEL);

            entityManager.flush();
            entityManager.clear();

            var filtro = VeiculoFiltroFactory
                    .criarFiltro()
                    .comAnoMinimo((short) 2000)
                    .comAnoMaximo((short) 2010)
                    .build();
            //Act
            var resultado = veiculoService.listarAtivos(filtro, Pageable.unpaged());
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .extracting(VeiculoResponse::id)
                    .anyMatch(c -> c.equals(veiculos.getFirst().getId()))
                    .noneMatch(c -> c.equals(veiculos.getLast().getId()));

        }

        @Test
        @DisplayName("Deve filtrar pelo valor mínimo incluindo o limite")
        void deveFiltrarPeloValorMinimo() {
            //Arrange
            var vendedor = criarVendedorPersistido();

            var veiculos = criarVeiculosPersistidos("123Q5Q5",
                    "123Q5Q6",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    vendedor,
                    (short) 0,
                    (short) 0,
                    null,
                    null,
                    BigDecimal.valueOf(100000),
                    BigDecimal.valueOf(90000),
                    DISPONIVEL,
                    DISPONIVEL);

            entityManager.flush();
            entityManager.clear();

            var filtro = VeiculoFiltroFactory
                    .criarFiltro()
                    .comValorMinimo(BigDecimal.valueOf(100000))
                    .comValorMaximo(BigDecimal.valueOf(150000))
                    .build();
            //Act
            var resultado = veiculoService.listarAtivos(filtro, Pageable.unpaged());
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .extracting(VeiculoResponse::id)
                    .anyMatch(c -> c.equals(veiculos.getFirst().getId()))
                    .noneMatch(c -> c.equals(veiculos.getLast().getId()));

        }

        @Test
        @DisplayName("Deve filtrar pelo ano máximo incluindo o limite")
        void deveFiltrarPeloValorMaximo() {
            //Arrange
            var vendedor = criarVendedorPersistido();

            var veiculos = criarVeiculosPersistidos("123Q5Q5",
                    "123Q5Q6",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    vendedor,
                    (short) 0,
                    (short) 0,
                    null,
                    null,
                    BigDecimal.valueOf(100000),
                    BigDecimal.valueOf(200000),
                    DISPONIVEL,
                    DISPONIVEL);

            entityManager.flush();
            entityManager.clear();

            var filtro = VeiculoFiltroFactory
                    .criarFiltro()
                    .comValorMinimo(BigDecimal.valueOf(50000))
                    .comValorMaximo(BigDecimal.valueOf(100000))
                    .build();
            //Act
            var resultado = veiculoService.listarAtivos(filtro, Pageable.unpaged());
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .extracting(VeiculoResponse::id)
                    .anyMatch(c -> c.equals(veiculos.getFirst().getId()))
                    .noneMatch(c -> c.equals(veiculos.getLast().getId()));

        }

        @Test
        @DisplayName("Deve filtrar o veículo pela quilometragem")
        void deveFiltrarOsVeiculosPelaQuilometragem() {
            //Arrange
            var vendedor = criarVendedorPersistido();

            var veiculos = criarVeiculosPersistidos("123Q5Q5",
                    "123Q5Q6",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    vendedor,
                    (short) 0,
                    (short) 0,
                    45000,
                    110000,
                    null,
                    null,
                    DISPONIVEL,
                    DISPONIVEL);

            entityManager.flush();
            entityManager.clear();

            var filtro = VeiculoFiltroFactory
                    .criarFiltro()
                    .comValorQuilometragem(70000)
                    .build();
            //Act
            var resultado = veiculoService.listarAtivos(filtro, Pageable.unpaged());

            var veiculo = veiculoRepository.findById(veiculos.getFirst().getId()).orElseThrow();
            var veiculo2 = veiculoRepository.findById(veiculos.getLast().getId()).orElseThrow();
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .extracting(VeiculoResponse::id)
                    .anyMatch(c -> c.equals(veiculo.getId()))
                    .noneMatch(c -> c.equals(veiculo2.getId()));

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
                            VeiculoDetalheResponse::id,
                            VeiculoDetalheResponse::marca,
                            VeiculoDetalheResponse::modelo,
                            VeiculoDetalheResponse::valor,
                            VeiculoDetalheResponse::statusVeiculo
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

    private List<Veiculo> criarVeiculosPersistidos(String placa,
                                           String placa2,
                                           Carroceria carroceria,
                                           Carroceria carroceria2,
                                           Cor cor,
                                           Cor cor2,
                                           Modelo modelo,
                                           Modelo modelo2,
                                           Combustivel combustivel,
                                           Combustivel combustivel2,
                                           Usuario outroUsuario,
                                           short anoFabricacao,
                                           short anoFabricacao2,
                                           Integer quilometragem,
                                           Integer quilometragem2,
                                           BigDecimal valor,
                                           BigDecimal valor2,
                                           StatusVeiculo status,
                                           StatusVeiculo status2) {
        if (ObjectUtils.isEmpty(carroceria) && ObjectUtils.isEmpty(carroceria2)) {
            var carroceriaNova = vendaIntegrationFixture.criarCarroceriaPersistida();
            carroceria = carroceriaNova;
            carroceria2 = carroceriaNova;
        }

        if (ObjectUtils.isEmpty(cor) && ObjectUtils.isEmpty(cor2)) {
            var corNova = vendaIntegrationFixture.criarCorPersistida();
            cor = corNova;
            cor2 = corNova;
        }

        if (ObjectUtils.isEmpty(modelo) && ObjectUtils.isEmpty(modelo2)) {
            var modeloNovo = vendaIntegrationFixture.criarModeloPersistido();
            modelo = modeloNovo;
            modelo2 = modeloNovo;
        }

        if (ObjectUtils.isEmpty(combustivel) && ObjectUtils.isEmpty(combustivel2)) {
            var combustivelNovo = vendaIntegrationFixture.criarCombustivelPersistido();
            combustivel = combustivelNovo;
            combustivel2 = combustivelNovo;
        }

        if (anoFabricacao == 0 && anoFabricacao2 == 0) {
            var anoFabricacaoFiltrado = (short) 2014;
            anoFabricacao = anoFabricacaoFiltrado;
            anoFabricacao2 = anoFabricacaoFiltrado;
        }

        if (ObjectUtils.isEmpty(quilometragem) && ObjectUtils.isEmpty(quilometragem2)) {
            var quilometragemNova = 40000;
            quilometragem = quilometragemNova;
            quilometragem2 = quilometragemNova;
        }

        if (ObjectUtils.isEmpty(valor) && ObjectUtils.isEmpty(valor2)) {
            var valorNovo = BigDecimal.valueOf(100000);
            valor = valorNovo;
            valor2 = valorNovo;
        }


        var veiculo1 = vendaIntegrationFixture
                .criarVeiculoPersistido(placa,
                        valor,
                        carroceria,
                        cor,
                        modelo,
                        combustivel,
                        outroUsuario,
                        anoFabricacao,
                        quilometragem,
                        status == null ? DISPONIVEL : status);

        var veiculo2 = vendaIntegrationFixture
                .criarVeiculoPersistido(placa2,
                        valor2,
                        carroceria2,
                        cor2,
                        modelo2,
                        combustivel2,
                        outroUsuario,
                        anoFabricacao2,
                        quilometragem2,
                        status == null ? DISPONIVEL : status2);

        return List.of(veiculo1, veiculo2);
    }

}
