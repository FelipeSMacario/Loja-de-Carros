package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.factory.venda.VendaTestContext;
import com.javacar.lojadecarro.mapper.VendasMapper;
import com.javacar.lojadecarro.repository.VendasRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static com.javacar.lojadecarro.enums.StatusVeiculo.*;
import static com.javacar.lojadecarro.enums.StatusVenda.EM_ANDAMENTO;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do serviço de vendas")
public class VendaServiceTest {
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
            cx.veiculo.setVendedor(cx.vendedor);

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
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VendaResponse::id,
                            VendaResponse::valorVenda
                    ).containsExactly(
                            ID_VALIDO,
                            BigDecimal.valueOf(200000)
                    );
            assertThat(cx.vendaEntity.getComprador())
                    .isSameAs(cx.comprador);

            assertThat(cx.vendaEntity.getVendedor())
                    .isSameAs(cx.vendedor);

            assertThat(cx.vendaEntity.getVeiculo())
                    .isSameAs(cx.veiculo);

            assertThat(cx.veiculo.getStatusVeiculo())
                    .isEqualTo(RESERVADO);

            assertThat(resultado.statusVenda())
                    .isEqualTo(EM_ANDAMENTO);

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
        @DisplayName("Deve lançar exceção de vendedor ser o mesmo que comprador")
        void deveLancarExcecaoQuandoVendedorForComprador() {
            //Arrange
            var cx = new VendaTestContext();
            cx.veiculo.setVendedor(cx.vendedor);

            when(vendasRepository.existsByVeiculoIdAndStatusVenda(cx.vendaRequest.veiculoId(), EM_ANDAMENTO))
                    .thenReturn(false);

            when(veiculoService.buscaVeiculo(cx.vendaRequest.veiculoId()))
                    .thenReturn(cx.veiculo);

            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.vendedor);
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> vendasService.criar(cx.vendaRequest, ID_VALIDO));
            //Assert
            assertBusinessResponseError(excecao, "O comprador não pode ser o próprio vendedor.");
            assertThat(cx.veiculo.getStatusVeiculo())
                    .isNotEqualTo(VENDIDO);

            verify(vendasRepository).existsByVeiculoIdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(veiculoService).buscaVeiculo(cx.vendaRequest.veiculoId());
            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);

            verifyNoMoreInteractions(veiculoService, usuarioService, vendasRepository);

            verifyNoInteractions(vendasMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao persistir a venda no banco")
        void deveLancarExcecaoPersistirAVendaNoBanco() {
            //Arrange
            var cx = new VendaTestContext();
            cx.veiculo.setVendedor(cx.vendedor);

            when(vendasRepository.existsByVeiculoIdAndStatusVenda(cx.vendaRequest.veiculoId(), EM_ANDAMENTO))
                    .thenReturn(false);

            when(veiculoService.buscaVeiculo(cx.vendaRequest.veiculoId()))
                    .thenReturn(cx.veiculo);

            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.comprador);

            when(vendasMapper.toEntity(cx.vendaRequest))
                    .thenReturn(cx.vendaEntity);

            when(vendasRepository.save(cx.vendaEntity))
                    .thenThrow(new RuntimeException("Erro ao persistir a venda no banco"));

            //ACT
            var excecao = assertThrows(RuntimeException.class,
                    () -> vendasService.criar(cx.vendaRequest, ID_VALIDO));
            //Assert
            assertThat(excecao)
                    .hasMessage("Erro ao persistir a venda no banco");

            assertThat(cx.veiculo.getStatusVeiculo())
                    .isEqualTo(RESERVADO);

            verify(vendasRepository).existsByVeiculoIdAndStatusVenda(ID_VALIDO, EM_ANDAMENTO);
            verify(veiculoService).buscaVeiculo(cx.vendaRequest.veiculoId());
            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(vendasMapper).toEntity(cx.vendaRequest);
            verify(vendasRepository).save(cx.vendaEntity);
            verify(vendasMapper, never()).toResponse(cx.vendaEntity);

            verifyNoMoreInteractions(
                    veiculoService,
                    usuarioService,
                    vendasRepository,
                    vendasMapper);
        }
    }


    @Nested
    @DisplayName("Testes para listar vendas")
    class Listar {
        @Test
        @DisplayName("Deve listar as vendas paginadas")
        void deveListarAsVendasPaginadas() {
            //Arrange
            var cx = new VendaTestContext();
            var pageable = PageRequest.of(0, 10);
            var pagina = new PageImpl<>(
                    cx.vendaEntityList,
                    pageable,
                    2);

            when(vendasRepository.findAll(pageable))
                    .thenReturn(pagina);

            when(vendasMapper.toResponse(cx.vendaEntity))
                    .thenReturn(cx.vendaResponse);
            when(vendasMapper.toResponse(cx.vendaEntity2))
                    .thenReturn(cx.vendaResponse2);
            //ACT
            var resultado = vendasService.listar(pageable, null);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VendaResponse::id,
                            VendaResponse::valorVenda
                    ).containsExactly(
                            tuple(1L, BigDecimal.valueOf(200000)),
                            tuple(2L, BigDecimal.valueOf(200000))
                    );
            verify(vendasRepository).findAll(pageable);
            verify(vendasMapper).toResponse(cx.vendaEntity);
            verify(vendasMapper).toResponse(cx.vendaEntity2);
            verifyNoMoreInteractions(vendasRepository, vendasMapper);
        }
    }
}
