package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.request.VendaRequest;
import com.javacar.lojadecarro.dto.response.VeiculoVendaResponse;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.entity.*;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.enums.StatusVenda;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.integration.fixture.VendaIntegrationFixture;
import com.javacar.lojadecarro.repository.VendasRepository;
import com.javacar.lojadecarro.service.VendasService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.javacar.lojadecarro.enums.Entidade.*;
import static com.javacar.lojadecarro.enums.StatusVeiculo.*;
import static com.javacar.lojadecarro.enums.StatusVenda.*;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@DisplayName("Testes da service de vendas")
@Import(VendaIntegrationFixture.class)
class VendaServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private VendasService vendasService;
    @Autowired
    private VendasRepository vendasRepository;
    @Autowired
    private VendaIntegrationFixture vendaIntegrationFixture;
    @PersistenceContext
    private EntityManager entityManager;

    private Usuario comprador;
    private Usuario vendedor;
    private Carroceria carroceria;
    private Cor cor;
    private Modelo modelo;
    private Combustivel combustivel;


    @BeforeEach
    void prepararRelacionamentos() {
        vendedor = vendaIntegrationFixture
                .criarUsuarioPersistido("USUARIO 1", "85296374165", "usuario1@gmail.com");

        comprador = vendaIntegrationFixture
                .criarUsuarioPersistido("USUARIO 2", "85296374172", "usuario2@gmail.com");

        carroceria = vendaIntegrationFixture.criarCarroceriaPersistida();
        cor = vendaIntegrationFixture.criarCorPersistida();
        modelo = vendaIntegrationFixture.criarModeloPersistido();
        combustivel = vendaIntegrationFixture.criarCombustivelPersistido();

    }

    @Nested
    @WithMockUser(roles = "USUARIO")
    @DisplayName("Testes para criar vendas")
    class Criar {
        @Test
        @DisplayName("Deve criar uma venda")
        void deveCriarUmaVenda() {
            //Arrange
            var veiculo = criarVeiculoPersistido("PLACA1", BigDecimal.valueOf(300000), vendedor);
            var request = new VendaRequest(veiculo.getId());
            //ACT
            var response = vendasService.criar(request, comprador.getId());

            entityManager.flush();
            entityManager.clear();

            var vendaPersistida = buscaVendaPorId(response.id());

            //Assert
            assertThat(response)
                    .isNotNull();

            assertThat(response)
                    .extracting(
                            VendaResponse::id,
                            VendaResponse::valorVenda,
                            VendaResponse::dataVenda
                    ).doesNotContainNull();

            assertThat(vendaPersistida.getValorVenda())
                    .isEqualByComparingTo("300000");

            assertThat(response.valorVenda())
                    .isEqualByComparingTo("300000");

            assertThat(vendaPersistida.getId())
                    .isEqualTo(response.id());

            assertThat(vendaPersistida.getValorVenda())
                    .isEqualByComparingTo(response.valorVenda());

            assertThat(vendaPersistida.getDataVenda())
                    .isCloseTo(
                            response.dataVenda(),
                            within(1, ChronoUnit.MICROS)
                    );

            assertThat(vendaPersistida.getVeiculo().getStatusVeiculo())
                    .isEqualTo(RESERVADO);

            assertThat(vendaPersistida.getStatusVenda())
                    .isEqualTo(EM_ANDAMENTO);

            assertThat(vendaPersistida)
                    .extracting(v -> v.getComprador().getId())
                    .isEqualTo(comprador.getId());
            assertThat(vendaPersistida)
                    .extracting(v -> v.getVeiculo().getId())
                    .isEqualTo(request.veiculoId());

            assertThat(response.statusVenda())
                    .isEqualTo(EM_ANDAMENTO);

            assertThat(response.veiculo().status())
                    .isEqualTo(RESERVADO);

            assertThat(response.comprador().id())
                    .isEqualTo(comprador.getId());

            assertThat(response.vendedor().id())
                    .isEqualTo(vendedor.getId());

            assertThat(vendaPersistida.getVendedor().getId())
                    .isEqualTo(vendedor.getId());
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar veiculo")
        void deveLancarExcecaoBuscarVeiculo() {
            //Arrange
            var request = new VendaRequest(ID_INVALIDO);
            var compradorId = comprador.getId();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.criar(request, compradorId));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_INVALIDO);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar comprador invalido")
        void deveLancarExcecaoBuscarCompradorInvalido() {
            //Arrange
            var veiculo = criarVeiculoPersistido("PLACA2", BigDecimal.valueOf(300000), vendedor);
            var request = new VendaRequest(veiculo.getId());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.criar(request, ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(exception, USUARIO, ID_INVALIDO);
        }


        @Test
        @DisplayName("Deve lançar exceção quando o comprador for o vendedor")
        void deveLancarExcecaoQuandoOCompradorForVendedor() {
            //Arrange
            var veiculo = criarVeiculoPersistido("PLACA3", BigDecimal.valueOf(300000), vendedor);
            var vendedorId = vendedor.getId();
            var request = new VendaRequest(veiculo.getId());
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.criar(request, vendedorId));
            //Assert
            assertThat(exception)
                    .hasMessage("O comprador não pode ser o próprio vendedor.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando veiculo já estiver a venda")
        void deveLancarExcecaoQuandoVeiculoJaPossuiVenda() {
            //Arrange
            var venda = criarVenda(BigDecimal.valueOf(300000), EM_ANDAMENTO, "PLACA5", PAUSADO);
            var request = new VendaRequest(venda.getVeiculo().getId());
            var compradorId = comprador.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.criar(request, compradorId));
            //Assert
            assertThat(exception)
                    .hasMessage("O veículo já possui uma venda cadastrada.");
        }

    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da listagem de vendas")
    class Listar {
        @Test
        @DisplayName("Deve listar todas as vendas")
        void deveListarTodasAsVendas() {
            //Arrange
            var vendaAndamento = criarVenda(BigDecimal.valueOf(300000), EM_ANDAMENTO, "PLACA5", RESERVADO);
            var vendaPausada = criarVenda(BigDecimal.valueOf(400000), PAUSADA, "PLACA6", PAUSADO);
            var vendaCancelada = criarVenda(BigDecimal.valueOf(500000), CANCELADA, "PLACA7", DISPONIVEL);
            //ACT
            var response = vendasService.listar(Pageable.unpaged(), null);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .anyMatch(v -> v.statusVenda() == PAUSADA)
                    .anyMatch(v -> v.statusVenda() == CANCELADA)
                    .anyMatch(v -> v.statusVenda() == EM_ANDAMENTO)
                    .extracting(VendaResponse::id)
                    .contains(vendaAndamento.getId(), vendaPausada.getId(), vendaCancelada.getId());
        }

        @ParameterizedTest
        @EnumSource(StatusVenda.class)
        @DisplayName("Deve listar vendas")
        void deveListarVendas(StatusVenda status) {
            //Arrange
            var venda1 = criarVenda(BigDecimal.valueOf(300000), status, "PLACA5", DISPONIVEL);
            var venda2 = criarVenda(BigDecimal.valueOf(400000), status, "PLACA6", DISPONIVEL);
            var venda3 = criarVenda(BigDecimal.valueOf(500000), status, "PLACA7", DISPONIVEL);
            //ACT
            var response = vendasService.listar(Pageable.unpaged(), status);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(v -> v.statusVenda() == status)
                    .extracting(VendaResponse::id)
                    .contains(venda1.getId(), venda2.getId(), venda3.getId());
        }

    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da busca da venda")
    class Buscar {
        @Test
        @DisplayName("Deve buscar a venda por ID")
        void deveBuscarAVendaPorId() {
            //Arrange
            var vendaAndamento = criarVenda(BigDecimal.valueOf(300000), EM_ANDAMENTO, "PLACA5", RESERVADO);
            //ACT
            var response = vendasService.buscarPorId(vendaAndamento.getId());
            //Assert
            assertThat(response)
                    .isNotNull()
                    .extracting(
                            VendaResponse::id,
                            VendaResponse::statusVenda
                    ).containsExactly(
                            vendaAndamento.getId(),
                            vendaAndamento.getStatusVenda()
                    );
        }

        @Test
        @DisplayName("Deve lançar exceção de venda não encontrada")
        void deveLancarExcecaoVendaNaoEncontrada() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.buscarPorId(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(exception, VENDA, ID_INVALIDO);
        }
    }

    @Nested
    @WithMockUser(roles = "USUARIO")
    @DisplayName("Testes da listagem das compras do usuário autenticado")
    class ListarMinhasCompras {
        @Test
        @DisplayName("Deve listar todas as compras do usuário autenticado")
        void deveListarTodasAsComprasDoUsuarioAutenticado() {
            //Arrange
            var vendaAndamento = criarVenda(BigDecimal.valueOf(300000), EM_ANDAMENTO, "PLACA5", RESERVADO);
            var vendaPausada = criarVenda(BigDecimal.valueOf(400000), PAUSADA, "PLACA6", PAUSADO);
            var vendaCancelada = criarVenda(BigDecimal.valueOf(500000), CANCELADA, "PLACA7", DISPONIVEL);
            var outroComprador = vendaIntegrationFixture
                    .criarUsuarioPersistido(
                            "OUTRO COMPRADOR",
                            "85296374149",
                            "outrocomprador@gmail.com"
                    );
            var vendaOutroComprador = criarVenda(
                    BigDecimal.valueOf(30_000),
                    EM_ANDAMENTO,
                    "placa4",
                    outroComprador,
                    vendedor);
            //ACT
            var response = vendasService.buscarMinhasCompras(comprador.getId(), Pageable.unpaged(), null);
            //Assert
            assertThat(response)
                    .allMatch(venda ->
                            venda.comprador().id().equals(comprador.getId())
                    )
                    .extracting(VendaResponse::id)
                    .contains(
                            vendaAndamento.getId(),
                            vendaPausada.getId(),
                            vendaCancelada.getId()
                    )
                    .doesNotContain(vendaOutroComprador.getId());
        }

        @ParameterizedTest
        @EnumSource(StatusVenda.class)
        @DisplayName("Deve listar compras filtradas por status do usuário autenticado")
        void deveListarComprasFiltradasPorStatusDoUsuarioAutenticado(StatusVenda status) {
            //Arrange
            var venda1 = criarVenda(BigDecimal.valueOf(300000), status, "PLACA5", DISPONIVEL);
            var venda2 = criarVenda(BigDecimal.valueOf(400000), status, "PLACA6", DISPONIVEL);
            var venda3 = criarVenda(BigDecimal.valueOf(500000), status, "PLACA7", DISPONIVEL);
            //ACT
            var response = vendasService.buscarMinhasCompras(comprador.getId(), Pageable.unpaged(), status);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(v -> v.statusVenda() == status)
                    .extracting(VendaResponse::id)
                    .contains(venda1.getId(), venda2.getId(), venda3.getId());
        }

    }

    @Nested
    @WithMockUser(roles = "USUARIO")
    @DisplayName("Testes da listagem das vendas do usuário autenticado")
    class ListarMinhasVendas {
        @Test
        @DisplayName("Deve listar todas as vendas do usuário autenticado")
        void deveListarTodasAsVendasDoUsuarioAutenticado() {
            //Arrange
            var vendaAndamento = criarVenda(BigDecimal.valueOf(300000), EM_ANDAMENTO, "PLACA5", RESERVADO);
            var vendaPausada = criarVenda(BigDecimal.valueOf(400000), PAUSADA, "PLACA6", PAUSADO);
            var vendaCancelada = criarVenda(BigDecimal.valueOf(500000), CANCELADA, "PLACA7", DISPONIVEL);
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
                    outroVendedor);
            //ACT
            var response = vendasService.buscarMinhasVendas(vendedor.getId(), Pageable.unpaged(), null);
            //Assert
            assertThat(response)
                    .allMatch(venda ->
                            venda.vendedor().id().equals(vendedor.getId())
                    )
                    .extracting(VendaResponse::id)
                    .contains(
                            vendaAndamento.getId(),
                            vendaPausada.getId(),
                            vendaCancelada.getId()
                    )
                    .doesNotContain(vendaOutroVendedor.getId());
        }

        @ParameterizedTest
        @EnumSource(StatusVenda.class)
        @DisplayName("Deve listar vendas filtradas por status do usuário autenticado")
        void deveListarVendasFiltradasPorStatusDoUsuarioAutenticado(StatusVenda status) {
            //Arrange
            var venda1 = criarVenda(BigDecimal.valueOf(300000), status, "PLACA5", DISPONIVEL);
            var venda2 = criarVenda(BigDecimal.valueOf(400000), status, "PLACA6", DISPONIVEL);
            var venda3 = criarVenda(BigDecimal.valueOf(500000), status, "PLACA7", DISPONIVEL);
            //ACT
            var response = vendasService.buscarMinhasVendas(vendedor.getId(), Pageable.unpaged(), status);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(v -> v.statusVenda() == status)
                    .extracting(VendaResponse::id)
                    .contains(venda1.getId(), venda2.getId(), venda3.getId());
        }

    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes do cancelamento da venda")
    class Cancelar {
        @Test
        @DisplayName("Deve cancelar a venda")
        void deveCancelarAVenda() {
            //Arrange
            var vendaAndamento = criarVenda(BigDecimal.valueOf(300000), EM_ANDAMENTO, "PLACA5", RESERVADO);
            assertThat(vendaAndamento)
                    .extracting(Venda::getStatusVenda)
                    .isEqualTo(EM_ANDAMENTO);
            assertThat(vendaAndamento.getVeiculo())
                    .extracting(Veiculo::getStatusVeiculo)
                    .isEqualTo(RESERVADO);
            //ACT
            var response = vendasService.cancelarVenda(vendaAndamento.getId());
            var vendaId = vendaAndamento.getId();
            entityManager.flush();
            entityManager.clear();

            var vendaPersistida = buscaVendaPorId(vendaId);
            //Assert
            assertThat(vendaPersistida)
                    .extracting(Venda::getStatusVenda)
                    .isEqualTo(CANCELADA);
            assertThat(vendaPersistida.getVeiculo())
                    .extracting(Veiculo::getStatusVeiculo)
                    .isEqualTo(DISPONIVEL);

            assertThat(response)
                    .isNotNull()
                    .extracting(
                            VendaResponse::id,
                            VendaResponse::statusVenda)
                    .containsExactly(
                            vendaAndamento.getId(),
                            CANCELADA);
            assertThat(response.veiculo())
                    .isNotNull()
                    .extracting(VeiculoVendaResponse::status)
                    .isEqualTo(DISPONIVEL);
        }

        @Test
        @DisplayName("Deve lançar exceção de venda não encontrada")
        void deveLancarExcecaoVendaNaoEncontrada() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.cancelarVenda(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(exception, VENDA, ID_INVALIDO);
        }

        @Test
        @DisplayName("Deve lançar quando a venda não está em andamento")
        void deveLancarQuandoAVendaNaoEmAndamento() {
            //Arrange
            var venda = criarVenda(BigDecimal.valueOf(300000), PAUSADA, "PLACA5", DISPONIVEL);
            var idVenda = venda.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.cancelarVenda(idVenda));
            //Assert
            assertThat(exception)
                    .hasMessage("Somente uma venda em andamento pode ser cancelada.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o veículo não está reservado")
        void deveLancarExcecaoQuandoVeiculoNaoEstaReservado() {
            //Arrange
            var venda = criarVenda(BigDecimal.valueOf(300000), EM_ANDAMENTO, "PLACA5", DISPONIVEL);
            var idVenda = venda.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.cancelarVenda(idVenda));
            //Assert
            assertThat(exception)
                    .hasMessage("Somente um veículo reservado pode ser disponibilizado.");
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da conclusão da venda")
    class Concluir {
        @Test
        @DisplayName("Deve concluir uma venda")
        void deveConcluirVenda() {
            //Arrange
            var vendaAndamento = criarVenda(BigDecimal.valueOf(300000), EM_ANDAMENTO, "PLACA5", RESERVADO);
            assertThat(vendaAndamento)
                    .extracting(Venda::getStatusVenda)
                    .isEqualTo(EM_ANDAMENTO);
            assertThat(vendaAndamento.getVeiculo())
                    .extracting(Veiculo::getStatusVeiculo)
                    .isEqualTo(RESERVADO);

            //ACT
            var response = vendasService.concluirVenda(vendaAndamento.getId());
            var vendaId = vendaAndamento.getId();
            entityManager.flush();
            entityManager.clear();

            var vendaPersistida = buscaVendaPorId(vendaId);
            //Assert
            assertThat(vendaPersistida)
                    .extracting(Venda::getStatusVenda)
                    .isEqualTo(CONCLUIDA);
            assertThat(vendaPersistida.getVeiculo())
                    .extracting(Veiculo::getStatusVeiculo)
                    .isEqualTo(VENDIDO);

            assertThat(response)
                    .isNotNull()
                    .extracting(
                            VendaResponse::id,
                            VendaResponse::statusVenda)
                    .containsExactly(
                            vendaPersistida.getId(),
                            CONCLUIDA);
            assertThat(response.veiculo())
                    .isNotNull()
                    .extracting(VeiculoVendaResponse::status)
                    .isEqualTo(VENDIDO);
        }

        @Test
        @DisplayName("Deve lançar exceção de venda não encontrada")
        void deveLancarExcecaoVendaNaoEncontrada() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.concluirVenda(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(exception, VENDA, ID_INVALIDO);
        }

        @Test
        @DisplayName("Deve lançar quando a venda não está em andamento")
        void deveLancarQuandoAVendaNaoEmAndamento() {
            //Arrange
            var venda = criarVenda(BigDecimal.valueOf(300000), PAUSADA, "PLACA5", RESERVADO);
            var idVenda = venda.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.concluirVenda(idVenda));
            //Assert
            assertThat(exception)
                    .hasMessage("Somente uma venda em andamento pode ser concluída.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o veículo não está reservado")
        void deveLancarExcecaoQuandoVeiculoNaoEstaReservado() {
            //Arrange
            var venda = criarVenda(BigDecimal.valueOf(300000), EM_ANDAMENTO, "PLACA5", PAUSADO);
            var idVenda = venda.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.concluirVenda(idVenda));
            //Assert
            assertThat(exception)
                    .hasMessage("Somente um veículo reservado pode ser vendido.");
        }
    }


    private Venda criarVenda(BigDecimal valorVenda,
                             StatusVenda statusVenda,
                             String placa,
                             StatusVeiculo statusVeiculo) {

        var venda = new Venda();
        venda.setValorVenda(valorVenda);
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
                statusVeiculo
        ));

        return vendasRepository.saveAndFlush(venda);
    }


    private Veiculo criarVeiculoPersistido(String placa, BigDecimal valorVenda, Usuario vendedorDaVenda) {
        return vendaIntegrationFixture.criarVeiculoPersistido(
                placa,
                valorVenda,
                carroceria,
                cor,
                modelo,
                combustivel,
                vendedorDaVenda,
                DISPONIVEL
        );
    }

    private Venda buscaVendaPorId(Long id) {
        return vendasRepository.findById(id).orElseThrow();
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
