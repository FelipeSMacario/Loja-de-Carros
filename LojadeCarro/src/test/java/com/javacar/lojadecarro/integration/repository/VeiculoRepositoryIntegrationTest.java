package com.javacar.lojadecarro.integration.repository;

import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.integration.fixture.VendaIntegrationFixture;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.enums.StatusVeiculo.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.data.domain.Pageable.unpaged;

@Transactional
@Import(VendaIntegrationFixture.class)
@DisplayName("Testes da repository do veículo")
class VeiculoRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    protected VendaIntegrationFixture vendaIntegrationFixture;
    @PersistenceContext
    protected EntityManager entityManager;

    @Test
    @DisplayName("Deve listar os veículos disponíveis")
    void deveBuscarVeiculosDisponiveis() {
        //Arrange
        var veiculos = veiculoPersistidoPorStatus(null, null, null);
        //ACT
        var response = veiculoRepository.findByStatusVeiculo(DISPONIVEL, unpaged());
        //Assert
        assertThat(response)
                .isNotEmpty()
                .allMatch(v -> v.getStatusVeiculo() == DISPONIVEL)
                .contains(veiculos.getFirst(),
                        veiculos.get(1))
                .doesNotContain(
                        veiculos.get(2),
                        veiculos.get(3),
                        veiculos.getLast());
    }

    @Test
    @DisplayName("Deve buscar o veículo pela placa")
    void deveBuscarVeiculoPlaca() {
        //Arrange
        var veiculo = criarVeiculoPersistido("Z7Y46L7", DISPONIVEL, null);
        entityManager.flush();
        entityManager.clear();
        //ACT
        var response = veiculoRepository.findByPlaca(veiculo.getPlaca()).orElseThrow();
        var response2 = veiculoRepository.findByPlaca("Z7Y4JT1");

        //Assert
        assertThat(response)
                .extracting(
                        Veiculo::getId,
                        Veiculo::getPlaca
                ).containsExactly(
                        veiculo.getId(),
                        veiculo.getPlaca()
                );
        assertThat(response2)
                .isEmpty();
    }

    @Test
    @DisplayName("Deve validar a existência do veículo pela placa")
    void deveValidarExistenciaVeiculoPorPlaca() {
        //Arrange
        var veiculo = criarVeiculoPersistido("Z7Y46Q8", DISPONIVEL, null);
        entityManager.flush();
        entityManager.clear();
        //ACT
        var veiculoExistente = veiculoRepository.existsByPlaca(veiculo.getPlaca());
        var veiculoInexistente = veiculoRepository.existsByPlaca("Z7Y4XT1");
        //Assert
        assertThat(veiculoExistente).isTrue();
        assertThat(veiculoInexistente).isFalse();
    }

    @Test
    @DisplayName("Deve validar a existência do veículo por Id e pelo vendedor")
    void deveValidarExistenciaVeiculoIdEPorVendedor() {
        //Arrange
        var outroVendedor = criarVendedorPersistido("OUTRO VENDEDOR", "15926347977", "outrovendedor.com");
        var veiculos = veiculoPersistidoPorStatus(null, null, outroVendedor);
        //ACT
        entityManager.flush();
        entityManager.clear();

        var resultado = veiculoRepository.existsByIdAndVendedor_Id(veiculos.getFirst().getId(), outroVendedor.getId());
        var resultadoFalso = veiculoRepository.existsByIdAndVendedor_Id(veiculos.getLast().getId(), outroVendedor.getId());
        //Assert
        assertThat(resultado).isTrue();
        assertThat(resultadoFalso).isFalse();
    }

    @Test
    @DisplayName("Deve buscar veículos por vendedor e status")
    void deveBuscarVeiculoPorVendedorEStatus() {
        //Arrange
        var outroVendedor = criarVendedorPersistido("OUTRO VENDEDOR", "15926347977", "outrovendedor.com");
        var veiculos = veiculoPersistidoPorStatus(null, null, outroVendedor);
        var vendedor = veiculos.get(1).getVendedor();
        //ACT
        entityManager.flush();
        entityManager.clear();

        var response = veiculoRepository.findByVendedor_IdAndStatusVeiculo(vendedor.getId(), DISPONIVEL);
        //Assert
        assertThat(response)
                .isNotEmpty()
                .allMatch(v -> v.getStatusVeiculo() == DISPONIVEL)
                .extracting(Veiculo::getId)
                .contains(veiculos.get(1).getId())
                .doesNotContain(veiculos.getFirst().getId(),
                        veiculos.get(2).getId(),
                        veiculos.get(3).getId(),
                        veiculos.getLast().getId());
    }

    @Test
    @DisplayName("Deve validar a existência do veículo por vendedor e status")
    void deveValidarExistenciaVeiculoPorVendedorEStatus() {
        //Arrange
        var outroVendedor = criarVendedorPersistido("OUTRO VENDEDOR", "15926347977", "outrovendedor.com");
        veiculoPersistidoPorStatus(null, null, outroVendedor);
        //ACT
        entityManager.flush();
        entityManager.clear();

        var responseTrue = veiculoRepository.existsByVendedor_IdAndStatusVeiculo(outroVendedor.getId(), DISPONIVEL);
        var responseFalse = veiculoRepository.existsByVendedor_IdAndStatusVeiculo(outroVendedor.getId(), VENDIDO);
        //Assert
        assertThat(responseTrue).isTrue();
        assertThat(responseFalse).isFalse();
    }

    @Test
    @DisplayName("Deve paginar veículos por vendedor e status")
    void devePaginarVeiculosPorVendedorEStatus() {
        // Arrange
        var veiculos = veiculoPersistidoPorStatus(
                DISPONIVEL,
                null,
                null
        );

        var vendedorId = veiculos.getFirst()
                .getVendedor()
                .getId();

        entityManager.flush();
        entityManager.clear();

        var pageable = PageRequest.of(
                0,
                2,
                Sort.by("id")
        );

        // Act
        var response =
                veiculoRepository.findByVendedor_IdAndStatusVeiculo(
                        vendedorId,
                        DISPONIVEL,
                        pageable
                );

        // Assert
        assertThat(response.getContent())
                .hasSize(2)
                .allMatch(veiculo ->
                        veiculo.getVendedor().getId().equals(vendedorId)
                                && veiculo.getStatusVeiculo() == DISPONIVEL
                );

        assertThat(response.getTotalElements())
                .isEqualTo(5);

        assertThat(response.getTotalPages())
                .isEqualTo(3);
    }

    @Test
    @DisplayName("Deve listas os veículos por vendedor")
    void deveListarVeiculoPorVendedor() {
        //Arrange
        var outroVendedor = criarVendedorPersistido("OUTRO VENDEDOR", "15926347977", "outrovendedor.com");
        var veiculos = veiculoPersistidoPorStatus(null, null, outroVendedor);
        var vendedor = veiculos.get(1).getVendedor();
        //ACT
        entityManager.flush();
        entityManager.clear();

        var response = veiculoRepository.findByVendedor_Id(vendedor.getId(), unpaged());
        //Assert
        assertThat(response)
                .isNotEmpty()
                .extracting(Veiculo::getId)
                .contains(veiculos.get(1).getId(),
                        veiculos.get(2).getId(),
                        veiculos.get(3).getId(),
                        veiculos.getLast().getId())
                .doesNotContain(veiculos.getFirst().getId());
    }

    @Test
    @DisplayName("Deve buscar o veículo por Id e status")
    void deveBuscarVeiculoPorIdEStatus() {
        //Arrange
        var veiculo = criarVeiculoPersistido("Z7Y46T0", DISPONIVEL, null);
        //ACT
        entityManager.flush();
        entityManager.clear();

        var response = veiculoRepository.findByIdAndStatusVeiculo(veiculo.getId(), DISPONIVEL).orElseThrow();
        var response2 = veiculoRepository.findByIdAndStatusVeiculo(veiculo.getId(), VENDIDO);
        var response3 = veiculoRepository.findByIdAndStatusVeiculo(-1L, DISPONIVEL);
        //Assert
        assertThat(response)
                .isNotNull()
                .extracting(Veiculo::getId, Veiculo::getStatusVeiculo)
                .containsExactly(veiculo.getId(), DISPONIVEL);

        assertThat(response2)
                .isEmpty();

        assertThat(response3)
                .isEmpty();
    }

    private Veiculo criarVeiculoPersistido(String placa, StatusVeiculo status, Usuario usuario) {
        var vendedor = (usuario == null) ? criarVendedorPersistido() : usuario;
        return vendaIntegrationFixture
                .criarVeiculoPersistido(placa,
                        BigDecimal.valueOf(200000),
                        vendaIntegrationFixture.criarCarroceriaPersistida(),
                        vendaIntegrationFixture.criarCorPersistida(),
                        vendaIntegrationFixture.criarModeloPersistido(),
                        vendaIntegrationFixture.criarCombustivelPersistido(),
                        vendedor,
                        status);
    }

    private Usuario criarVendedorPersistido() {
        return vendaIntegrationFixture
                .criarUsuarioPersistido("USUARIO 1", "85296374165", "usuario1@gmail.com");
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
