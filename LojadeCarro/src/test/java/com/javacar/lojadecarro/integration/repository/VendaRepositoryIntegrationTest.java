package com.javacar.lojadecarro.integration.repository;

import com.javacar.lojadecarro.entity.*;
import com.javacar.lojadecarro.enums.StatusVenda;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.integration.fixture.VendaIntegrationFixture;
import com.javacar.lojadecarro.repository.VendasRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.javacar.lojadecarro.enums.StatusVeiculo.RESERVADO;
import static com.javacar.lojadecarro.enums.StatusVenda.CANCELADA;
import static com.javacar.lojadecarro.enums.StatusVenda.EM_ANDAMENTO;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.data.domain.Pageable.unpaged;

@Transactional
@Import(VendaIntegrationFixture.class)
class VendaRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private VendasRepository vendasRepository;

    @Autowired
    private VendaIntegrationFixture vendaIntegrationFixture;

    private Usuario comprador;
    private Usuario vendedor;
    private Carroceria carroceria;
    private Cor cor;
    private Modelo modelo;
    private Combustivel combustivel;

    @BeforeEach
    void prepararRelacionamentos() {
        vendedor = vendaIntegrationFixture
                .criarUsuarioPersistido("USUARIO 1", "85296374155", "usuario1@gmail.com");

        comprador = vendaIntegrationFixture
                .criarUsuarioPersistido("USUARIO 2", "85296374152", "usuario2@gmail.com");

        carroceria = vendaIntegrationFixture.criarCarroceriaPersistida();
        cor = vendaIntegrationFixture.criarCorPersistida();
        modelo = vendaIntegrationFixture.criarModeloPersistido();
        combustivel = vendaIntegrationFixture.criarCombustivelPersistido();

    }

    @Test
    @DisplayName("Deve validar a existencia da venda com id do veiculo e status da venda")
    void deveValidarAExistenciaDaVendaComVeiculoIdEStatusVenda() {
        //Arrange
        var venda1 = criarVenda(BigDecimal.valueOf(40000), EM_ANDAMENTO, "placa1");
        //ACT
        var resultadoAndamento = vendasRepository
                .existsByVeiculoIdAndStatusVenda(venda1.getVeiculo().getId(), EM_ANDAMENTO);
        var resultadoCancelado = vendasRepository
                .existsByVeiculoIdAndStatusVenda(venda1.getVeiculo().getId(), CANCELADA);
        //Assert
        assertThat(resultadoAndamento).isTrue();
        assertThat(resultadoCancelado).isFalse();
    }

    @Test
    @DisplayName("Deve listar a venda por status")
    void deveListarVendaPorStatus() {
        // Arrange
        var venda1 = criarVenda(BigDecimal.valueOf(40000), EM_ANDAMENTO, "placa1");
        var venda2 = criarVenda(BigDecimal.valueOf(20000), EM_ANDAMENTO, "placa2");
        var vendaCancelada = criarVenda(BigDecimal.valueOf(10000), CANCELADA, "placa3");

        // Act
        var resultado = vendasRepository.findByStatusVenda(EM_ANDAMENTO, unpaged());

        // Assert
        assertThat(resultado.getContent())
                .extracting(Venda::getId)
                .contains(venda1.getId(), venda2.getId())
                .doesNotContain(vendaCancelada.getId());

        assertThat(resultado.getContent())
                .extracting(Venda::getStatusVenda)
                .containsOnly(EM_ANDAMENTO);

    }

    @Test
    @DisplayName("Deve validar se o usuário faz parte da venda")
    void deveValidarSeUsuarioFazParteDaVenda() {
        //Arrange
        var venda1 = criarVenda(BigDecimal.valueOf(40000), EM_ANDAMENTO, "placa1");
        var usuarioForaVenda = vendaIntegrationFixture
                .criarUsuarioPersistido("USUARIO 3", "85296374150", "usuario3@gmail.com");
        //ACT
        var resultadoVendedor = vendasRepository.usuarioRelacionadoAVenda(venda1.getId(), vendedor.getId());
        var resultadoComprador = vendasRepository.usuarioRelacionadoAVenda(venda1.getId(), comprador.getId());
        var resultadoForaVenda = vendasRepository.usuarioRelacionadoAVenda(venda1.getId(), usuarioForaVenda.getId());
        var resultadoVendaInexistente =
                vendasRepository.usuarioRelacionadoAVenda(
                        999_999L,
                        comprador.getId()
                );
        //Assert
        assertThat(resultadoVendedor).isTrue();
        assertThat(resultadoComprador).isTrue();
        assertThat(resultadoForaVenda).isFalse();
        assertThat(resultadoVendaInexistente).isFalse();
    }

    @Test
    @DisplayName("Deve validar se existe uma venda com o usuário como vendedor e status da venda")
    void deveValidarSeExisteUmaVendaComUsuarioComoVendedorEstatusVenda() {
        //Arrange
        criarVenda(BigDecimal.valueOf(40000), EM_ANDAMENTO, "placa1");
        //ACT
        var resultadoVendedorAndamento = vendasRepository
                .existsByVendedor_IdAndStatusVenda(vendedor.getId(), EM_ANDAMENTO);
        var resultadoVendedorCancelado = vendasRepository
                .existsByVendedor_IdAndStatusVenda(vendedor.getId(), CANCELADA);
        var resultadoCompradorAndamento = vendasRepository
                .existsByVendedor_IdAndStatusVenda(comprador.getId(), EM_ANDAMENTO);
        var resultadoCompradorCancelado = vendasRepository
                .existsByVendedor_IdAndStatusVenda(comprador.getId(), CANCELADA);
        //Assert
        assertThat(resultadoVendedorAndamento).isTrue();
        assertThat(resultadoVendedorCancelado).isFalse();
        assertThat(resultadoCompradorAndamento).isFalse();
        assertThat(resultadoCompradorCancelado).isFalse();
    }

    @Test
    @DisplayName("Deve validar se existe uma venda com o usuário como comprador e status da venda")
    void deveValidarSeExisteUmaVendaComUsuarioComoCompradorEstatusVenda() {
        //Arrange
        criarVenda(BigDecimal.valueOf(40000), EM_ANDAMENTO, "placa1");
        //ACT
        var resultadoCompradorAndamento = vendasRepository
                .existsByComprador_IdAndStatusVenda(comprador.getId(), EM_ANDAMENTO);
        var resultadoCompradorCancelado = vendasRepository
                .existsByComprador_IdAndStatusVenda(comprador.getId(), CANCELADA);
        var resultadoVendedorAndamento = vendasRepository
                .existsByComprador_IdAndStatusVenda(vendedor.getId(), EM_ANDAMENTO);
        var resultadoVendedorCancelado = vendasRepository
                .existsByComprador_IdAndStatusVenda(vendedor.getId(), CANCELADA);

        //Assert
        assertThat(resultadoCompradorAndamento).isTrue();
        assertThat(resultadoCompradorCancelado).isFalse();
        assertThat(resultadoVendedorAndamento).isFalse();
        assertThat(resultadoVendedorCancelado).isFalse();

    }

    @Test
    @DisplayName("Deve buscar as vendas do usuário por Id")
    void deveBuscarVendasPorVendedor() {
        //Arrange
        var venda1 = criarVenda(BigDecimal.valueOf(40000), EM_ANDAMENTO, "placa1");
        var venda2 = criarVenda(BigDecimal.valueOf(20000), EM_ANDAMENTO, "placa2");
        var vendaCancelada = criarVenda(BigDecimal.valueOf(10000), CANCELADA, "placa3");
        var outroVendedor = vendaIntegrationFixture
                .criarUsuarioPersistido(
                        "OUTRO VENDEDOR",
                        "85296374149",
                        "outrovendedor@gmail.com"
                );
        var vendaOutroVendedor = criarVenda(
                BigDecimal.valueOf(30_000),
                EM_ANDAMENTO,
                "placa4",
                comprador,
                outroVendedor
        );
        //ACT
        var resultado = vendasRepository.findByVendedor_Id(vendedor.getId(), unpaged());
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(Venda::getId)
                .contains(venda1.getId(), venda2.getId(), vendaCancelada.getId());

        assertThat(resultado.getContent())
                .extracting(Venda::getId)
                .doesNotContain(vendaOutroVendedor.getId());
    }

    @Test
    @DisplayName("Deve buscar as compras do usuário por Id")
    void deveBuscarVendasPorComprador() {
        //Arrange
        var venda1 = criarVenda(BigDecimal.valueOf(40000), EM_ANDAMENTO, "placa1");
        var venda2 = criarVenda(BigDecimal.valueOf(20000), EM_ANDAMENTO, "placa2");
        var vendaCancelada = criarVenda(BigDecimal.valueOf(10000), CANCELADA, "placa3");
        var outroComprador = vendaIntegrationFixture
                .criarUsuarioPersistido(
                        "OUTRO VENDEDOR",
                        "85296374149",
                        "outrovendedor@gmail.com"
                );
        var vendaOutroComprador = criarVenda(
                BigDecimal.valueOf(30_000),
                EM_ANDAMENTO,
                "placa4",
                outroComprador,
                vendedor
        );

        //ACT
        var resultado = vendasRepository.findByComprador_Id(comprador.getId(), unpaged());
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(Venda::getId)
                .contains(venda1.getId(), venda2.getId(), vendaCancelada.getId());

        assertThat(resultado.getContent())
                .extracting(Venda::getId)
                .doesNotContain(vendaOutroComprador.getId());
    }

    @Test
    @DisplayName("Deve buscar as vendas do usuário por Id e status da venda")
    void deveBuscarVendaPorIdEStatusVenda() {
        //Arrange
        var venda1 = criarVenda(BigDecimal.valueOf(40000), EM_ANDAMENTO, "placa1");
        var venda2 = criarVenda(BigDecimal.valueOf(20000), EM_ANDAMENTO, "placa2");
        var vendaCancelada = criarVenda(BigDecimal.valueOf(10000), CANCELADA, "placa3");

        //ACT
        var resultado = vendasRepository.findByVendedor_IdAndStatusVenda(vendedor.getId(), unpaged(), EM_ANDAMENTO);
        //Assert
        assertThat(resultado.getContent())
                .extracting(Venda::getId)
                .contains(venda1.getId(), venda2.getId())
                .doesNotContain(vendaCancelada.getId());

        assertThat(resultado.getContent())
                .extracting(Venda::getStatusVenda)
                .containsOnly(EM_ANDAMENTO);
    }

    @Test
    @DisplayName("Deve buscar as compras do usuário por Id e status da venda")
    void deveBuscarComprasPorIdEStatusVenda() {
        //Arrange
        var venda1 = criarVenda(BigDecimal.valueOf(40000), EM_ANDAMENTO, "placa1");
        var venda2 = criarVenda(BigDecimal.valueOf(20000), EM_ANDAMENTO, "placa2");
        var vendaCancelada = criarVenda(BigDecimal.valueOf(10000), CANCELADA, "placa3");

        //ACT
        var resultado = vendasRepository.findByComprador_IdAndStatusVenda(comprador.getId(), unpaged(), CANCELADA);
        //Assert
        assertThat(resultado.getContent())
                .extracting(Venda::getId)
                .contains(vendaCancelada.getId())
                .doesNotContain(venda1.getId(), venda2.getId());

        assertThat(resultado.getContent())
                .extracting(Venda::getStatusVenda)
                .containsOnly(CANCELADA);
    }

    @Test
    @DisplayName("Deve validar a existencia da venda com id")
    void deveValidarExistenciaDaVendaPorIdEVendedor() {
        //Arrange
        var venda = criarVenda(BigDecimal.valueOf(40000), EM_ANDAMENTO, "placa1");
        //ACT
        var resultadoVendedor = vendasRepository
                .existsByIdAndVendedor_Id(venda.getId(), vendedor.getId());
        var resultadoComprador = vendasRepository
                .existsByIdAndVendedor_Id(venda.getId(), comprador.getId());
        //Assert
        assertThat(resultadoVendedor).isTrue();
        assertThat(resultadoComprador).isFalse();
    }

    private Venda criarVenda(BigDecimal valorVenda,
                             StatusVenda statusVenda,
                             String placa) {

        var venda = new Venda();
        venda.setValorVenda(valorVenda);
        venda.setDataVenda(LocalDateTime.now());
        venda.setStatusVenda(statusVenda);
        venda.setComprador(comprador);
        venda.setVendedor(vendedor);
        venda.setVeiculo(vendaIntegrationFixture.criarVeiculoPersistido(
                placa,
                valorVenda,
                carroceria,
                cor,
                modelo,
                combustivel,
                vendedor,
                RESERVADO
        ));

        return vendasRepository.saveAndFlush(venda);
    }

    private Venda criarVenda(
            BigDecimal valorVenda,
            StatusVenda statusVenda,
            String placa,
            Usuario compradorDaVenda,
            Usuario vendedorDaVenda) {
        var venda = new Venda();
        venda.setValorVenda(valorVenda);
        venda.setDataVenda(LocalDateTime.now());
        venda.setStatusVenda(statusVenda);
        venda.setComprador(compradorDaVenda);
        venda.setVendedor(vendedorDaVenda);

        venda.setVeiculo(
                vendaIntegrationFixture.criarVeiculoPersistido(
                        placa,
                        valorVenda,
                        carroceria,
                        cor,
                        modelo,
                        combustivel,
                        vendedorDaVenda,
                        RESERVADO
                )
        );

        return vendasRepository.saveAndFlush(venda);
    }
}
