package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.entity.VeiculoOpcional;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.OpcionalHelper;
import com.javacar.lojadecarro.factory.helper.VeiculoTestContext;
import com.javacar.lojadecarro.factory.opcional.OpcionalEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;
import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("Testes dos opcionais do veículo")
public class VeiculoServiceOpcionalTest extends AbstractVeiculoServiceTest{
    @Nested
    @DisplayName("Testes da desvinculação de opcionais ao veículo")
    class DesvincularOpcionais {
        @Test
        @DisplayName("Deve desvincular um opcional")
        void deveDesvincularUmOpcional() {
            //Arrange
            var cx = new VeiculoTestContext();
            cx.opcionais.forEach(opcion -> {
                var veiculoOpcional = new VeiculoOpcional(cx.entity, opcion);
                cx.entity.getOpcionais().add(veiculoOpcional);
            });
            var listOpcionais = List.of(1L);
            var opcionais = List.of(OpcionalHelper.criarOpcionalEntity());

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionais(listOpcionais))
                    .thenReturn(opcionais);
            //ACT

            veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais);
            //Assert

            assertThat(cx.entity.getOpcionais())
                    .isNotNull()
                    .hasSize(1);

            assertThat(cx.entity.getOpcionais().getFirst().getOpcional())
                    .isNotNull()
                    .extracting(
                            Opcional::getId,
                            Opcional::getNome
                    ).containsExactly(
                            2L,
                            "Automatico"
                    );

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionais(listOpcionais);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );

        }

        @Test
        @DisplayName("Deve lançar exceção ao inserir opcionais duplicados")
        void deveLancarExcecaoAoInserirOpcionaisDuplicados() {
            //Arrange
            var listOpcionais = List.of(1L, 2L, 2L);

            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(exception, "A requisição possui opcionais duplicadas.");

            verify(veiculoRepository, never()).findById(anyLong());
            verify(opcionalService, never()).buscarOpcionais(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoAoNaoEncontrarVeiculo() {
            //Arrange
            var listOpcionais = List.of(1L, 2L);
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_VALIDO);
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService, never()).buscarOpcionais(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        @DisplayName("Deve lançar exceção ao desvincular opcionais para veículo com status proibido")
        void deveLancarExcecaoAoDesvincularOpcionaisEmVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var listOpcionais = List.of(1L);
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
                    () -> veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(exception, "Somente anúncios disponíveis ou pausados podem ser editados.");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService, never()).buscarOpcionais(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lançar exceçao de opcionais não existem")
        void deveLancarExcecaoOpcionaisNaoExistem() {
            //Arrange
            var cx = new VeiculoTestContext();
            var listOpcionais = List.of(1L, 2L, 3L);

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionais(listOpcionais))
                    .thenReturn(cx.opcionais);

            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(excecao, "Um ou mais opcionais não foram encontrados.");
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionais(listOpcionais);
            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService);
        }

        @Test
        @DisplayName("Deve lançar exceção não possui opcional")
        void deveLancarExcecaoNaoPossuiOpcional() {
            //Arrange
            var cx = new VeiculoTestContext();
            cx.opcionais.forEach(opcion -> {
                var veiculoOpcional = new VeiculoOpcional(cx.entity, opcion);
                cx.entity.getOpcionais().add(veiculoOpcional);
            });
            var listOpcionais = List.of(3L);
            var opcionais = List.of(OpcionalEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(3L)
                    .comNome("Teto solar")
                    .build());

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionais(listOpcionais))
                    .thenReturn(opcionais);
            //ACT

            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(exception, "O Veiculo informado não possui esse opcional");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionais(listOpcionais);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );

        }
    }

    @Nested
    @DisplayName("Testes da vinculação de opcionais ao veículo")
    class VincularOpcionais {
        @Test
        @DisplayName("Deve vincular um opcional")
        void deveVincularUmOpcional() {
            //Arrange
            var cx = new VeiculoTestContext();
            cx.opcionais.forEach(opcion -> {
                var veiculoOpcional = new VeiculoOpcional(cx.entity, opcion);
                cx.entity.getOpcionais().add(veiculoOpcional);
            });
            var listOpcionais = List.of(3L);
            var opcionais = List.of(OpcionalEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(3L)
                    .comNome("Teto solar")
                    .build());

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionaisAtivos(listOpcionais))
                    .thenReturn(opcionais);

            //ACT
            veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais);
            //Assert
            assertThat(cx.entity.getOpcionais())
                    .isNotNull()
                    .hasSize(3);

            assertThat(cx.entity.getOpcionais())
                    .extracting(vo -> vo.getOpcional().getId())
                    .containsExactlyInAnyOrder(1L, 2L, 3L);

            assertThat(cx.entity.getOpcionais())
                    .allMatch(vo -> vo.getVeiculo() == cx.entity);
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionaisAtivos(listOpcionais);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lancar exceção de opcionais duplicados")
        void deveLancarExcecaoDuplicados() {
            //Arrange
            var lista = List.of(1L, 1L);
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(ID_VALIDO, lista));
            //Assert
            assertBusinessResponseError(excecao, "A requisição possui opcionais duplicadas.");

            verify(veiculoRepository, never()).findById(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoAoNaoEncontrarVeiculo() {
            //Arrange
            var listOpcionais = List.of(1L, 2L);
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_VALIDO);
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        @DisplayName("Deve lançar exceção ao vincular opcionais para veículo com status proibido")
        void deveLancarExcecaoAoVincularOpcionaisEmVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var listOpcionais = List.of(1L);
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
                    () -> veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(exception, "Somente anúncios disponíveis ou pausados podem ser editados.");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lançar exceção de opcionais não existentes")
        void deveLancarExcecaoOpcionaisNaoExistente() {
            //Arrange
            var cx = new VeiculoTestContext();
            var listOpcionais = List.of(1L);
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionaisAtivos(listOpcionais))
                    .thenReturn(Collections.emptyList());
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(excecao, "Um ou mais opcionais não foram encontrados.");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionaisAtivos(listOpcionais);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lançar exceção de opcional já existente")
        void deveLancarExcecaoOpcionaisExistente() {
            //Arrange
            var cx = new VeiculoTestContext();
            cx.opcionais.forEach(opcion -> {
                var veiculoOpcional = new VeiculoOpcional(cx.entity, opcion);
                cx.entity.getOpcionais().add(veiculoOpcional);
            });
            var listOpcionais = List.of(1L);
            var opcionais = List.of(OpcionalHelper.criarOpcionalEntity());

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionaisAtivos(listOpcionais))
                    .thenReturn(opcionais);

            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert

            assertBusinessResponseError(excecao, OPCIONAL.jaAtiva());

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionaisAtivos(listOpcionais);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }
    }
}
