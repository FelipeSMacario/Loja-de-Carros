package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaTestContext;
import com.javacar.lojadecarro.factory.combustivel.CombustivelTestContext;
import com.javacar.lojadecarro.factory.cor.CorTestContext;
import com.javacar.lojadecarro.factory.helper.VeiculoTestContext;
import com.javacar.lojadecarro.factory.marca.MarcaTestContext;
import com.javacar.lojadecarro.factory.modelo.ModeloTestContext;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoResponseFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.*;
import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;
import static com.javacar.lojadecarro.enums.Entidade.COR;
import static com.javacar.lojadecarro.enums.Entidade.MODELO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@DisplayName("Testes da atualização de veículos")
public class VeiculoServiceAtualizarTest extends AbstractVeiculoServiceTest{
    @Nested
    @DisplayName("Testes da atualização do veículo")
    class Atualizar {
        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"DISPONIVEL", "PAUSADO"}
        )
        @DisplayName("Deve atualizar a placa do veículo com status permitido")
        void deveAtualizarPlacaDeVeiculoComStatusPermitido(StatusVeiculo status) {
            //Arrange
            var entity = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comStatus(status)
                    .build();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("123FEP")
                    .build();
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comPlaca("123FEP")
                    .comStatus(status)
                    .build();

            when(veiculoRepository.findById(entity.getId()))
                    .thenReturn(Optional.of(entity));

            when(veiculoRepository.existsByPlaca(request.placa()))
                    .thenReturn(false);

            when(veiculoMapper.toResponse(entity))
                    .thenReturn(response);

            //ACT
            var resultado = veiculoService.atualizar(request, entity.getId());
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::valor,
                            VeiculoResponse::quilometragem,
                            VeiculoResponse::anoFabricacao,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            1L,
                            request.placa(),
                            "Chevrolet",
                            "Onix",
                            new BigDecimal(58000),
                            67000,
                            (short) 2020,
                            status
                    );

            assertThat(entity.getStatusVeiculo())
                    .isEqualTo(status);

            verify(veiculoRepository).findById(entity.getId());
            verify(veiculoRepository).existsByPlaca(request.placa());
            verify(veiculoMapper).toUpdate(request, entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper).toResponse(entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve atualizar a carroceria do veículo")
        void deveAtualizarACarroceriaDoVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCarroceria(2L)
                    .build();
            var carroceria = CarroceriaTestContext.carroceriaEntity(2L, "Conversivel", true);
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comCarroceria("Conversivel")
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(carroceriaService.buscaCarroceriaAtiva(request.idCarroceria()))
                    .thenReturn(carroceria);

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(response);

            //ACT
            var resultado = veiculoService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::carroceria,
                            VeiculoResponse::cor,
                            VeiculoResponse::combustivel,
                            VeiculoResponse::valor,
                            VeiculoResponse::quilometragem,
                            VeiculoResponse::anoFabricacao,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            1L,
                            "QUV1F83",
                            "Chevrolet",
                            "Onix",
                            "Conversivel",
                            "Branco",
                            "Etanol",
                            new BigDecimal(58000),
                            67000,
                            (short) 2020,
                            DISPONIVEL
                    );

            assertThat(cx.entity.getCarroceria())
                    .isSameAs(carroceria);

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService).buscaCarroceriaAtiva(request.idCarroceria());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper).toResponse(cx.entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve atualizar a cor do veículo")
        void deveAtualizarACorDoVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCores(2L)
                    .build();
            var cor = CorTestContext.corEntity(2L, "Vermelho", true);
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comCor("Vermelho")
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(coresService.buscaCorAtiva(request.idCores()))
                    .thenReturn(cor);

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(response);

            //ACT
            var resultado = veiculoService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::carroceria,
                            VeiculoResponse::cor,
                            VeiculoResponse::combustivel,
                            VeiculoResponse::valor,
                            VeiculoResponse::quilometragem,
                            VeiculoResponse::anoFabricacao,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            1L,
                            "QUV1F83",
                            "Chevrolet",
                            "Onix",
                            "Hatch",
                            "Vermelho",
                            "Etanol",
                            new BigDecimal(58000),
                            67000,
                            (short) 2020,
                            DISPONIVEL
                    );

            assertThat(cx.entity.getCor())
                    .isSameAs(cor);

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService).buscaCorAtiva(request.idCores());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper).toResponse(cx.entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve atualizar o combustível do veículo")
        void deveAtualizarOCombustivelDoVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCombustivel(2L)
                    .build();
            var combustivel = CombustivelTestContext.combustivelEntity(2L, "Eletrico", true);
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comCombustivel("Eletrico")
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(combustivelService.buscaCombustivelAtivo(request.idCombustivel()))
                    .thenReturn(combustivel);

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(response);

            //ACT
            var resultado = veiculoService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::carroceria,
                            VeiculoResponse::cor,
                            VeiculoResponse::combustivel,
                            VeiculoResponse::valor,
                            VeiculoResponse::quilometragem,
                            VeiculoResponse::anoFabricacao,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            1L,
                            "QUV1F83",
                            "Chevrolet",
                            "Onix",
                            "Hatch",
                            "Branco",
                            "Eletrico",
                            new BigDecimal(58000),
                            67000,
                            (short) 2020,
                            DISPONIVEL
                    );

            assertThat(cx.entity.getCombustivel())
                    .isSameAs(combustivel);

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService).buscaCombustivelAtivo(request.idCombustivel());
            verify(veiculoMapper).toResponse(cx.entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve atualizar a marca e modelo do veículo")
        void deveAtualizarAMarcaEModeloDoVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var marca = MarcaTestContext.criarMarca(2L, "BYD", "byd.com", true);
            var modelo = ModeloTestContext.criaModelo(2L, "Dolphin", marca, true);
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdModelo(2L)
                    .build();
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comMarca("BYD")
                    .comModelo("Dolphin")
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(modeloService.buscaModeloAtivo(request.idModelo()))
                    .thenReturn(modelo);

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(response);

            //ACT
            var resultado = veiculoService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::valor,
                            VeiculoResponse::quilometragem,
                            VeiculoResponse::anoFabricacao,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            1L,
                            "QUV1F83",
                            "BYD",
                            "Dolphin",
                            new BigDecimal(58000),
                            67000,
                            (short) 2020,
                            DISPONIVEL
                    );

            assertThat(cx.entity.getModelo())
                    .isSameAs(modelo);

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService).buscaModeloAtivo(request.idModelo());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper).toResponse(cx.entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo ao atualizar")
        void deveLancarExcecaoAoAtualizarVeiculoNaoEncontrado() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, VEICULO, ID_VALIDO);

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper, never()).toUpdate(any(), any());
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lancar exceção ao atualizar para carroceria inativa")
        void deveLancarExcecaoQuandoAtualizarParaCarroceriaInativa() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCarroceria(2L)
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(carroceriaService.buscaCarroceriaAtiva(request.idCarroceria()))
                    .thenThrow(new NotFoundException(CARROCERIA, request.idCarroceria()));

            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, ID_VALIDO));
            //Assert

            assertNotFoundResponseError(exception, CARROCERIA, request.idCarroceria());

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService).buscaCarroceriaAtiva(request.idCarroceria());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar para cor inativa")
        void deveLancarExcecaoQuandoAtualizarParaCorInativa() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCores(2L)
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(coresService.buscaCorAtiva(request.idCores()))
                    .thenThrow(new NotFoundException(COR, request.idCores()));

            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, COR, request.idCores());

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService).buscaCorAtiva(request.idCores());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar para combustível inativo")
        void deveLancarExcecaoQuandoAtualizarParaCombustivelInativo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCombustivel(2L)
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(combustivelService.buscaCombustivelAtivo(request.idCombustivel()))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, request.idCombustivel()));

            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, COMBUSTIVEL, request.idCombustivel());

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService).buscaCombustivelAtivo(request.idCombustivel());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar para modelo inativo")
        void deveLancarExcecaoQuandoAtualizarParaModeloInativo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdModelo(2L)
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(modeloService.buscaModeloAtivo(request.idModelo()))
                    .thenThrow(new NotFoundException(MODELO, request.idModelo()));

            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, MODELO, request.idModelo());

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService).buscaModeloAtivo(request.idModelo());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        @DisplayName("Deve lançar exceção ao atualizar veículo com status proibido")
        void deveLancarExcecaoAoAtualizarVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var cx = new VeiculoTestContext();
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(ID_VALIDO)
                    .comStatus(status)
                    .build();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(veiculo));
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertBusinessResponseError(excecao, "Somente anúncios disponíveis ou pausados podem ser editados.");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper, never()).toUpdate(any(), any());
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lançar exceção de placa única ao atualizar veículo")
        void deveLancarExcecaoPlacaUnicaAoAtualizarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("123FEP")
                    .build();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(veiculoRepository.existsByPlaca(request.placa()))
                    .thenReturn(true);
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.atualizar(request, ID_VALIDO));
            //Assert
            assertBusinessResponseError(excecao, "A placa informada já possui um cadastro.");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(request.placa());
            verify(veiculoMapper, never()).toUpdate(any(), any());
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }
    }
}
