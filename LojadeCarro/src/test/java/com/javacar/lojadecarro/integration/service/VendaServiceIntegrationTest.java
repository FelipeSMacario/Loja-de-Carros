package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.entity.Venda;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.enums.StatusVenda;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.VendaHelper;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import com.javacar.lojadecarro.repository.VendasRepository;
import com.javacar.lojadecarro.service.VendasService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes da service de vendas")
public class VendaServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private VendasService vendasService;
    @Autowired
    private VendasRepository vendasRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Nested
    @DisplayName("Testes para criar vendas")
    class Criar {
        @Test
        @DisplayName("Deve criar uma venda")
        @Transactional
        void deveCriarUmaVenda() {
            //Arrange
            var veiculo = buscarVeiculoPorPlaca("KPB8712");
            var comprador = buscarUsuarioPorEmail("german@gmail.com");
            var request = VendaHelper.criarVendasComCampos(
                    veiculo.getId(),
                    comprador.getId(),
                    veiculo.getVendedor().getId(),
                    new BigDecimal("150000")
            );
            //ACT
            var response = vendasService.criar(request);
            var veiculoAtualizado = buscarVeiculoPorPlaca("KPB8712");

            //Assert
            assertThat(response)
                    .isNotNull()
                    .extracting(
                            VendaResponse::id,
                            VendaResponse::valorVenda,
                            VendaResponse::dataVenda
                    ).doesNotContainNull();

            var venda = buscaVendaPorId(response.id());

            assertThat(venda)
                    .extracting(
                            Venda::getId,
                            Venda::getValorVenda,
                            Venda::getDataVenda
                    ).containsExactly(
                            response.id(),
                            response.valorVenda(),
                            response.dataVenda()
                    );
            assertThat(veiculoAtualizado.getStatusVeiculo())
                    .isEqualTo(StatusVeiculo.VENDIDO);
            assertThat(venda)
                    .extracting(v -> v.getVendedor().getId())
                    .isEqualTo(request.vendedorId());
            assertThat(venda)
                    .extracting(v -> v.getComprador().getId())
                    .isEqualTo(request.compradorId());
            assertThat(venda)
                    .extracting(v -> v.getVeiculo().getId())
                    .isEqualTo(request.veiculoId());
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar veiculo")
        void deveLancarExcecaoBuscarVeiculo() {
            //Arrange
            var request = VendaHelper.criarVendasComCampos(
                    -1L,
                    1L,
                    1L,
                    new BigDecimal("150000"));
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage(VEICULO.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar vendedor inválido")
        void deveLancarExcecaoBuscarVendedorInvalido() {
            //Arrange
            var veiculo = buscarVeiculoPorPlaca("KPB8712");
            var request = VendaHelper.criarVendasComCampos(
                    veiculo.getId(),
                    1L,
                    -1L,
                    new BigDecimal("150000"));
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage(USUARIO.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar comprador invalido")
        void deveLancarExcecaoBuscarCompradorInvalido() {
            //Arrange
            var veiculo = buscarVeiculoPorPlaca("KPB8712");
            var request = VendaHelper.criarVendasComCampos(
                    veiculo.getId(),
                    -1L,
                    veiculo.getVendedor().getId(),
                    new BigDecimal("150000"));
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage(USUARIO.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando vendedor não for proprietario")
        @Transactional
        void deveLancarExcecaoQuandoVendedorNaoForProprietario() {
            //Arrange
            var veiculo = buscarVeiculoPorPlaca("KPB8712");
            var comprador = buscarUsuarioPorEmail("german@gmail.com");
            var request = VendaHelper.criarVendasComCampos(
                    veiculo.getId(),
                    comprador.getId(),
                    comprador.getId(),
                    new BigDecimal("150000"));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage("O vendedor informado não é o proprietário do veículo.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o comprador for o vendedor")
        @Transactional
        void deveLancarExcecaoQuandoOCompradorForVendedor() {
            //Arrange
            var veiculo = buscarVeiculoPorPlaca("KPB8712");
            var request = VendaHelper.criarVendasComCampos(
                    veiculo.getId(),
                    veiculo.getVendedor().getId(),
                    veiculo.getVendedor().getId(),
                    new BigDecimal("150000"));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage("O comprador não pode ser o próprio vendedor.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando veiculo já estiver a venda")
        @Transactional
        void deveLancarExcecaoQuandoVeiculoJaPossuiVenda() {
            //Arrange
            var veiculo = buscarVeiculoPorPlaca("ABC1D23");
            var request = VendaHelper.criarVendasComCampos(veiculo.getId(), 1L, 1L, BigDecimal.ZERO);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage("O veículo já possui uma venda cadastrada.");
        }

    }

    @Nested
    @DisplayName("Testes da listagem de vendas")
    class Listar {
        @Test
        @DisplayName("Deve listar todas as vendas")
        void deveListarTodasAsVendas() {
            //Arrange
            //ACT
            var response = vendasService.listar(Pageable.unpaged(), null);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .anyMatch(v -> v.statusVenda() == StatusVenda.CONCLUIDA)
                    .anyMatch(v -> v.statusVenda() == StatusVenda.CANCELADA)
                    .anyMatch(v -> v.statusVenda() == StatusVenda.EM_ANDAMENTO);
        }

        @Test
        @DisplayName("Deve listar vendas concluidas")
        void deveListarVendasConcluidas() {
            //Arrange
            //ACT
            var response = vendasService.listar(Pageable.unpaged(), StatusVenda.CONCLUIDA);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(v -> v.statusVenda() == StatusVenda.CONCLUIDA);
        }
        @Test
        @DisplayName("Deve listar vendas em andamento")
        void deveListarVendasEmAndamento() {
            //Arrange
            //ACT
            var response = vendasService.listar(Pageable.unpaged(), StatusVenda.EM_ANDAMENTO);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(v -> v.statusVenda() == StatusVenda.EM_ANDAMENTO);
        }
    }


    private Veiculo buscarVeiculoPorPlaca(String nome) {
        return veiculoRepository.findByPlaca(nome).orElseThrow();
    }

    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow();
    }

    private Venda buscaVendaPorId(Long id) {
        return vendasRepository.findById(id).orElseThrow();
    }
}
