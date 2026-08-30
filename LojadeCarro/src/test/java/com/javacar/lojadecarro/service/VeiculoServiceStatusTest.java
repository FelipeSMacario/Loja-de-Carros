package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.VeiculoTestContext;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoResponseFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.enums.StatusVeiculo.PAUSADO;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@DisplayName("Testes das alterações de status do veículo")
public class VeiculoServiceStatusTest extends AbstractVeiculoServiceTest{
    @Nested
    @DisplayName("Testes para pausar o veículo")
    class Pausar {
        @Test
        @DisplayName("Deve pausar o veículo")
        void devePausarOVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comStatus(PAUSADO)
                    .build();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(response);
            //ACT
            var resultado = veiculoService.pausarVeiculo(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo,
                            VeiculoResponse::placa
                    ).containsExactly(cx.entity.getId(), PAUSADO, cx.entity.getPlaca());

            assertThat(cx.entity)
                    .extracting(Veiculo::getStatusVeiculo)
                    .isEqualTo(PAUSADO);

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper).toResponse(cx.entity);
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoNaoEncontrarVeiculo() {
            //Arrange
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.pausarVeiculo(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_VALIDO);

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                mode = EnumSource.Mode.EXCLUDE,
                names = "DISPONIVEL"
        )
        @DisplayName("Deve lançar exceção ao pausar veículo com status proibido")
        void deveLancarExcecaoPausarVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(ID_VALIDO)
                    .comStatus(status)
                    .build();
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(veiculo));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.pausarVeiculo(ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "Somente um veículo disponível pode ser pausado");
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }
    }

    @Nested
    @DisplayName("Testes para reativar o veículo")
    class Reativar {
        @Test
        @DisplayName("Deve reativar o veículo")
        void deveReativarOVeiculo() {
            //Arrange
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comStatus(PAUSADO)
                    .build();
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comStatus(DISPONIVEL)
                    .build();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(veiculo));

            when(veiculoMapper.toResponse(veiculo))
                    .thenReturn(response);
            //ACT
            var resultado = veiculoService.reativarVeiculo(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo,
                            VeiculoResponse::placa
                    ).containsExactly(veiculo.getId(), DISPONIVEL, veiculo.getPlaca());

            assertThat(veiculo)
                    .extracting(Veiculo::getStatusVeiculo)
                    .isEqualTo(DISPONIVEL);

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper).toResponse(veiculo);
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoNaoEncontrarVeiculo() {
            //Arrange
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.reativarVeiculo(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_VALIDO);

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                mode = EnumSource.Mode.EXCLUDE,
                names = "PAUSADO"
        )
        @DisplayName("Deve lançar exceção ao reativar veículo com status proibido")
        void deveLancarExcecaoReativarVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(ID_VALIDO)
                    .comStatus(status)
                    .build();
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(veiculo));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.reativarVeiculo(ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "Somente um veículo pausado pode ser reativado");
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }
    }
}
