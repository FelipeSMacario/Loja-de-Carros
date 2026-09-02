package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.enums.StatusVenda;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.venda.VendaTestContext;
import com.javacar.lojadecarro.mapper.VendasMapper;
import com.javacar.lojadecarro.repository.VendasRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static com.javacar.lojadecarro.enums.Entidade.VENDA;
import static com.javacar.lojadecarro.enums.StatusVeiculo.*;
import static com.javacar.lojadecarro.enums.StatusVenda.*;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.factory.helper.VendaHelper.*;
import static com.javacar.lojadecarro.factory.venda.VendaTestContext.criarVeiculo;
import static com.javacar.lojadecarro.factory.venda.VendaTestContext.criarVeiculoResponse;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Testes do serviço de vendas")
public class VendaServiceTest extends BaseServiceTest {
    @Mock
    private VendasRepository vendasRepository;
    @Mock
    private VendasMapper vendasMapper;
    @Mock
    private VeiculoService veiculoService;
    @Mock
    private UsuarioService usuarioService;
    @InjectMocks
    private VendasService vendasService;

    @Nested
    @DisplayName("Testes da criação da venda")
    class Criar {
        @Test
        @DisplayName("Deve criar uma venda")
        void deveCriarUmaVenda() {
            //Arrange
            var cx = new VendaTestContext();

            assertThat(cx.veiculo.getStatusVeiculo())
                    .isEqualTo(DISPONIVEL);

            assertThat(cx.vendaEntity.getComprador()).isNull();
            assertThat(cx.vendaEntity.getVendedor()).isNull();
            assertThat(cx.vendaEntity.getVeiculo()).isNull();

            when(vendasRepository.existsByVeiculoIdAndStatusVenda(cx.vendaRequest.veiculoId(), EM_ANDAMENTO))
                    .thenReturn(false);

            when(veiculoService.buscaVeiculo(cx.vendaRequest.veiculoId()))
                    .thenReturn(cx.veiculo);

            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.comprador);

            when(vendasMapper.toEntity(cx.vendaRequest))
                    .thenReturn(cx.vendaEntity);

            when(vendasRepository.save(cx.vendaEntity))
                    .thenReturn(cx.vendaEntity);

            when(vendasMapper.toResponse(cx.vendaEntity))
                    .thenReturn(cx.vendaResponse);

            //ACT
            var resultado = vendasService.criar(cx.vendaRequest, ID_VALIDO);
            //Assert
            assertVendaResponse(resultado);

            assertThat(cx.vendaEntity.getComprador())
                    .isSameAs(cx.comprador);

            assertThat(cx.vendaEntity.getVendedor())
                    .isSameAs(cx.veiculo.getVendedor());


            assertThat(cx.vendaEntity.getVeiculo())
                    .isSameAs(cx.veiculo);

            assertThat(cx.veiculo.getStatusVeiculo())
                    .isEqualTo(RESERVADO);

            verify(vendasRepository).existsByVeiculoIdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(veiculoService).buscaVeiculo(cx.vendaRequest.veiculoId());
            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(vendasMapper).toEntity(cx.vendaRequest);
            verify(vendasRepository).save(cx.vendaEntity);
            verify(vendasMapper).toResponse(cx.vendaEntity);

            verifyNoMoreInteractions(
                    veiculoService,
                    usuarioService,
                    vendasRepository,
                    vendasMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção quando veiculo já tiver em venda")
        void deveLancarExcecaoQuandoVendaJaEstiverEmVenda() {
            //Arrange
            var cx = new VendaTestContext();

            when(vendasRepository.existsByVeiculoIdAndStatusVenda(cx.vendaRequest.veiculoId(), EM_ANDAMENTO))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.criar(cx.vendaRequest, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("O veículo já possui uma venda cadastrada.");

            verify(vendasRepository).existsByVeiculoIdAndStatusVenda(cx.vendaRequest.veiculoId(), EM_ANDAMENTO);
            verifyNoMoreInteractions(vendasRepository);

            verifyNoInteractions(usuarioService, vendasMapper, veiculoService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar veiculo com dados incorretos")
        void deveLançarExcecaoAoBuscarVeiculoComDadosIncorretos() {
            //Arrange
            var cx = new VendaTestContext();

            when(vendasRepository.existsByVeiculoIdAndStatusVenda(cx.vendaRequest.veiculoId(), EM_ANDAMENTO))
                    .thenReturn(false);

            when(veiculoService.buscaVeiculo(cx.vendaRequest.veiculoId()))
                    .thenThrow(new NotFoundException(VEICULO, ID_VALIDO));
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.criar(cx.vendaRequest, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_VALIDO);
            verify(vendasRepository).existsByVeiculoIdAndStatusVenda(cx.vendaRequest.veiculoId(), EM_ANDAMENTO);
            verify(veiculoService).buscaVeiculo(cx.vendaRequest.veiculoId());

            verifyNoMoreInteractions(vendasRepository, veiculoService);

            verifyNoInteractions(usuarioService, vendasMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção de vendedor ser o mesmo que comprador")
        void deveLancarExcecaoQuandoVendedorForComprador() {
            //Arrange
            var cx = new VendaTestContext();

            when(vendasRepository.existsByVeiculoIdAndStatusVenda(cx.vendaRequest.veiculoId(), EM_ANDAMENTO))
                    .thenReturn(false);

            when(veiculoService.buscaVeiculo(cx.vendaRequest.veiculoId()))
                    .thenReturn(cx.veiculo);

            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.veiculo.getVendedor());
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> vendasService.criar(cx.vendaRequest, ID_VALIDO));
            //Assert
            assertBusinessResponseError(excecao, "O comprador não pode ser o próprio vendedor.");
            assertThat(cx.veiculo.getStatusVeiculo())
                    .isEqualTo(DISPONIVEL);

            verify(vendasRepository).existsByVeiculoIdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(veiculoService).buscaVeiculo(cx.vendaRequest.veiculoId());
            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);

            verifyNoMoreInteractions(veiculoService, usuarioService, vendasRepository);

            verifyNoInteractions(vendasMapper);
        }
    }

    @Nested
    @DisplayName("Testes para listar vendas")
    class Listar {
        @Test
        @DisplayName("Deve listar todas as vendas")
        void deveListarAsVendasPaginadas() {
            //Arrange
            var listaVendas = VendaTestContext
                    .criarListaVendas();
            var listaVendasResponse = VendaTestContext
                    .criarListaVendasResponse();
            var pageable = PageRequest.of(0, 10);
            var pagina = new PageImpl<>(
                    listaVendas,
                    pageable,
                    4);

            when(vendasRepository.findAll(pageable))
                    .thenReturn(pagina);

            when(vendasMapper.toResponse(listaVendas.getFirst()))
                    .thenReturn(listaVendasResponse.getFirst());
            when(vendasMapper.toResponse(listaVendas.get(1)))
                    .thenReturn(listaVendasResponse.get(1));
            when(vendasMapper.toResponse(listaVendas.get(2)))
                    .thenReturn(listaVendasResponse.get(2));
            when(vendasMapper.toResponse(listaVendas.getLast()))
                    .thenReturn(listaVendasResponse.getLast());
            //ACT
            var resultado = vendasService.listar(pageable, null);
            //Assert
            assertListVenda(resultado);

            assertThat(resultado.getNumber()).isZero();
            assertThat(resultado.getSize()).isEqualTo(10);
            assertThat(resultado.getTotalElements()).isEqualTo(4);
            assertThat(resultado.getTotalPages()).isEqualTo(1);


            verify(vendasRepository).findAll(pageable);
            verify(vendasMapper).toResponse(listaVendas.getFirst());
            verify(vendasMapper).toResponse(listaVendas.get(1));
            verify(vendasMapper).toResponse(listaVendas.get(2));
            verify(vendasMapper).toResponse(listaVendas.getLast());
            verifyNoMoreInteractions(vendasRepository, vendasMapper);
        }

        @ParameterizedTest
        @EnumSource(StatusVenda.class)
        @DisplayName("Deve listar vendas filtradas por status")
        void deveListarVendasFiltradasPorStatus(StatusVenda status) {
            // Arrange
            var pageable = PageRequest.of(0, 10);
            var venda = VendaTestContext.criarListaVendas(status);
            var response = VendaTestContext.criarListaVendasResponse(status);
            var pagina = new PageImpl<>(
                    venda,
                    pageable,
                    2
            );

            when(vendasRepository.findByStatusVenda(status, pageable))
                    .thenReturn(pagina);

            when(vendasMapper.toResponse(venda.getFirst()))
                    .thenReturn(response.getFirst());

            when(vendasMapper.toResponse(venda.getLast()))
                    .thenReturn(response.getLast());

            // Act
            var resultado = vendasService.listar(pageable, status);

            // Assert
            assertListVendaComStatus(resultado, status);
            assertThat(resultado.getNumber()).isZero();
            assertThat(resultado.getSize()).isEqualTo(10);
            assertThat(resultado.getTotalElements()).isEqualTo(2);
            assertThat(resultado.getTotalPages()).isEqualTo(1);

            verify(vendasRepository)
                    .findByStatusVenda(status, pageable);

            verify(vendasMapper).toResponse(venda.getFirst());
            verify(vendasMapper).toResponse(venda.getLast());

            verifyNoMoreInteractions(vendasRepository, vendasMapper);
        }
    }

    @Nested
    @DisplayName("Testes da busca da venda")
    class BuscarVenda {
        @Test
        @DisplayName("Deve buscar uma venda")
        void deveBuscarVenda() {
            //Arrange
            var cx = new VendaTestContext();

            when(vendasRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.vendaEntity));

            when(vendasMapper.toResponse(cx.vendaEntity))
                    .thenReturn(cx.vendaResponse);
            //ACT
            var resultado = vendasService.buscarPorId(ID_VALIDO);
            //Assert
            assertVendaResponse(resultado);

            verify(vendasRepository).findById(ID_VALIDO);
            verify(vendasMapper).toResponse(cx.vendaEntity);
            verifyNoMoreInteractions(vendasRepository, vendasMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção de venda não encontrada")
        void deveLancarExcecaoVendaNaoEncontrada() {
            //Arrange
            when(vendasRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.buscarPorId(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, VENDA, ID_VALIDO);

            verify(vendasRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(vendasRepository);

            verifyNoInteractions(vendasMapper);
        }
    }

    @Nested
    @DisplayName("Testes da busca das compras do usuário autenticado")
    class BuscarCompras {
        @Test
        @DisplayName("Deve buscar todas as compras do usuário autenticado")
        void deveBuscarAsComprasDoUsuarioAutenticado() {
            //Arrange
            var listaVendas = VendaTestContext
                    .criarListaVendas();
            var listaVendasResponse = VendaTestContext
                    .criarListaVendasResponse();
            var pageable = PageRequest.of(0, 10);
            var pagina = new PageImpl<>(
                    listaVendas,
                    pageable,
                    4);

            when(vendasRepository.findByComprador_Id(ID_VALIDO, pageable))
                    .thenReturn(pagina);

            when(vendasMapper.toResponse(listaVendas.getFirst()))
                    .thenReturn(listaVendasResponse.getFirst());
            when(vendasMapper.toResponse(listaVendas.get(1)))
                    .thenReturn(listaVendasResponse.get(1));
            when(vendasMapper.toResponse(listaVendas.get(2)))
                    .thenReturn(listaVendasResponse.get(2));
            when(vendasMapper.toResponse(listaVendas.getLast()))
                    .thenReturn(listaVendasResponse.getLast());
            //ACT
            var resultado = vendasService.buscarMinhasCompras(ID_VALIDO, pageable, null);
            //Assert
            assertListVenda(resultado);
            verify(vendasRepository).findByComprador_Id(ID_VALIDO, pageable);
            verify(vendasMapper).toResponse(listaVendas.getFirst());
            verify(vendasMapper).toResponse(listaVendas.get(1));
            verify(vendasMapper).toResponse(listaVendas.get(2));
            verify(vendasMapper).toResponse(listaVendas.getLast());
            verifyNoMoreInteractions(vendasRepository, vendasMapper);
        }

        @ParameterizedTest
        @EnumSource(StatusVenda.class)
        @DisplayName("Deve listar compras filtradas do usuário autenticado")
        void deveListarComprasFiltradasDoUsuarioAutenticado(StatusVenda status) {
            // Arrange
            var pageable = PageRequest.of(0, 10);
            var venda = VendaTestContext.criarListaVendas(status);
            var response = VendaTestContext.criarListaVendasResponse(status);
            var pagina = new PageImpl<>(
                    venda,
                    pageable,
                    2
            );

            when(vendasRepository.findByComprador_IdAndStatusVenda(ID_VALIDO, pageable, status))
                    .thenReturn(pagina);

            when(vendasMapper.toResponse(venda.getFirst()))
                    .thenReturn(response.getFirst());

            when(vendasMapper.toResponse(venda.getLast()))
                    .thenReturn(response.getLast());

            // Act
            var resultado = vendasService.buscarMinhasCompras(ID_VALIDO, pageable, status);

            // Assert
            assertListVendaComStatus(resultado, status);

            verify(vendasRepository)
                    .findByComprador_IdAndStatusVenda(ID_VALIDO, pageable, status);

            verify(vendasMapper).toResponse(venda.getFirst());
            verify(vendasMapper).toResponse(venda.getLast());

            verifyNoMoreInteractions(vendasRepository, vendasMapper);
        }
    }

    @Nested
    @DisplayName("Testes da busca das vendas do usuário autenticado")
    class BuscarVendas {
        @Test
        @DisplayName("Deve buscar todas as vendas do usuário autenticado")
        void deveBuscarAsVendasDoUsuarioAutenticado() {
            //Arrange
            var listaVendas = VendaTestContext
                    .criarListaVendas();
            var listaVendasResponse = VendaTestContext
                    .criarListaVendasResponse();
            var pageable = PageRequest.of(0, 10);
            var pagina = new PageImpl<>(
                    listaVendas,
                    pageable,
                    4);

            when(vendasRepository.findByVendedor_Id(ID_VALIDO, pageable))
                    .thenReturn(pagina);

            when(vendasMapper.toResponse(listaVendas.getFirst()))
                    .thenReturn(listaVendasResponse.getFirst());
            when(vendasMapper.toResponse(listaVendas.get(1)))
                    .thenReturn(listaVendasResponse.get(1));
            when(vendasMapper.toResponse(listaVendas.get(2)))
                    .thenReturn(listaVendasResponse.get(2));
            when(vendasMapper.toResponse(listaVendas.getLast()))
                    .thenReturn(listaVendasResponse.getLast());
            //ACT
            var resultado = vendasService.buscarMinhasVendas(ID_VALIDO, pageable, null);
            //Assert
            assertListVenda(resultado);
            verify(vendasRepository).findByVendedor_Id(ID_VALIDO, pageable);
            verify(vendasMapper).toResponse(listaVendas.getFirst());
            verify(vendasMapper).toResponse(listaVendas.get(1));
            verify(vendasMapper).toResponse(listaVendas.get(2));
            verify(vendasMapper).toResponse(listaVendas.getLast());
            verifyNoMoreInteractions(vendasRepository, vendasMapper);
        }

        @ParameterizedTest
        @EnumSource(StatusVenda.class)
        @DisplayName("Deve listar vendas filtradas do usuário autenticado")
        void deveListarComprasFiltradasDoUsuarioAutenticado(StatusVenda status) {
            // Arrange
            var pageable = PageRequest.of(0, 10);
            var venda = VendaTestContext.criarListaVendas(status);
            var response = VendaTestContext.criarListaVendasResponse(status);
            var pagina = new PageImpl<>(
                    venda,
                    pageable,
                    2
            );

            when(vendasRepository.findByVendedor_IdAndStatusVenda(ID_VALIDO, pageable, status))
                    .thenReturn(pagina);

            when(vendasMapper.toResponse(venda.getFirst()))
                    .thenReturn(response.getFirst());

            when(vendasMapper.toResponse(venda.getLast()))
                    .thenReturn(response.getLast());

            // Act
            var resultado = vendasService.buscarMinhasVendas(ID_VALIDO, pageable, status);

            // Assert
            assertListVendaComStatus(resultado, status);

            verify(vendasRepository)
                    .findByVendedor_IdAndStatusVenda(ID_VALIDO, pageable, status);

            verify(vendasMapper).toResponse(venda.getFirst());
            verify(vendasMapper).toResponse(venda.getLast());

            verifyNoMoreInteractions(vendasRepository, vendasMapper);
        }
    }

    @Nested
    @DisplayName("Testes do cancelamento da venda")
    class Cancelar {
        @Test
        @DisplayName("Deve cancelar a venda")
        void deveCancelarAVenda() {
            //Arrange
            var entity = criarVeiculo(RESERVADO, EM_ANDAMENTO);
            var response = criarVeiculoResponse(DISPONIVEL, CANCELADA);

            assertThat(entity.getStatusVenda()).isEqualTo(EM_ANDAMENTO);
            assertThat(entity.getVeiculo().getStatusVeiculo()).isEqualTo(RESERVADO);

            when(vendasRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(vendasMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = vendasService.cancelarVenda(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.statusVenda()).isEqualTo(CANCELADA);
            assertThat(resultado.veiculo().status()).isEqualTo(DISPONIVEL);

            assertThat(entity.getStatusVenda()).isEqualTo(CANCELADA);
            assertThat(entity.getVeiculo().getStatusVeiculo()).isEqualTo(DISPONIVEL);

            verify(vendasRepository).findById(ID_VALIDO);
            verify(vendasMapper).toResponse(entity);

            verifyNoMoreInteractions(vendasRepository, vendasMapper);
        }
        @Test
        @DisplayName("Deve lançar exceção de venda não encontrada")
        void deveLancarExcecaoVendaNaoEncontrada() {
            //Arrange
            when(vendasRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.cancelarVenda(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, VENDA, ID_VALIDO);
            verify(vendasRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(vendasRepository);
            verifyNoInteractions(vendasMapper);
        }

        @Test
        @DisplayName("Deve lançar quando a venda não está em andamento")
        void deveLancarQuandoAVendaNaoEmAndamento() {
            //Arrange
            var entity = criarVeiculo(RESERVADO, PAUSADA);
            when(vendasRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.cancelarVenda(ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("Somente uma venda em andamento pode ser cancelada.");

            verify(vendasRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(vendasRepository);

            verifyNoInteractions(vendasMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção quando o veículo não está reservado")
        void deveLancarExcecaoQuandoVeiculoNaoEstaReservado() {
            //Arrange
            var entity = criarVeiculo(PAUSADO, EM_ANDAMENTO);
            when(vendasRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.cancelarVenda(ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("Somente um veículo reservado pode ser disponibilizado.");

            verify(vendasRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(vendasRepository);

            verifyNoInteractions(vendasMapper);
        }
    }

    @Nested
    @DisplayName("Testes da conclusão da venda")
    class Concluir {
        @Test
        @DisplayName("Deve concluir uma venda")
        void deveConcluirVenda() {
            //Arrange
            var entity = criarVeiculo(RESERVADO, EM_ANDAMENTO);
            var response = criarVeiculoResponse(VENDIDO, CONCLUIDA);

            assertThat(entity.getStatusVenda()).isEqualTo(EM_ANDAMENTO);
            assertThat(entity.getVeiculo().getStatusVeiculo()).isEqualTo(RESERVADO);

            when(vendasRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(vendasMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = vendasService.concluirVenda(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.statusVenda()).isEqualTo(CONCLUIDA);
            assertThat(resultado.veiculo().status()).isEqualTo(VENDIDO);

            assertThat(entity.getStatusVenda()).isEqualTo(CONCLUIDA);
            assertThat(entity.getVeiculo().getStatusVeiculo()).isEqualTo(VENDIDO);

            verify(vendasRepository).findById(ID_VALIDO);
            verify(vendasMapper).toResponse(entity);

            verifyNoMoreInteractions(vendasRepository, vendasMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção de venda não encontrada")
        void deveLancarExcecaoVendaNaoEncontrada() {
            //Arrange
            when(vendasRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> vendasService.concluirVenda(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, VENDA, ID_VALIDO);
            verify(vendasRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(vendasRepository);
            verifyNoInteractions(vendasMapper);
        }

        @Test
        @DisplayName("Deve lançar quando a venda não está em andamento")
        void deveLancarQuandoAVendaNaoEmAndamento() {
            //Arrange
            var entity = criarVeiculo(RESERVADO, PAUSADA);
            when(vendasRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.concluirVenda(ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("Somente uma venda em andamento pode ser concluída.");

            verify(vendasRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(vendasRepository);

            verifyNoInteractions(vendasMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção quando o veículo não está reservado")
        void deveLancarExcecaoQuandoVeiculoNaoEstaReservado() {
            //Arrange
            var entity = criarVeiculo(PAUSADO, EM_ANDAMENTO);
            when(vendasRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> vendasService.concluirVenda(ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("Somente um veículo reservado pode ser vendido.");

            verify(vendasRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(vendasRepository);

            verifyNoInteractions(vendasMapper);
        }
    }
}
