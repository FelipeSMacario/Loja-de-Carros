package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.KitRequest;
import com.JavangularCar.LojadeCarro.dto.response.KitResponse;
import com.JavangularCar.LojadeCarro.entity.Carro;
import com.JavangularCar.LojadeCarro.entity.Kit;
import com.JavangularCar.LojadeCarro.exception.CarroException;
import com.JavangularCar.LojadeCarro.exception.KitException;
import com.JavangularCar.LojadeCarro.factory.carro.CarroEntityFactory;
import com.JavangularCar.LojadeCarro.factory.kit.KitEntityFactory;
import com.JavangularCar.LojadeCarro.factory.kit.KitRequestFactory;
import com.JavangularCar.LojadeCarro.factory.kit.KitResponseFactory;
import com.JavangularCar.LojadeCarro.mapper.KitMapper;
import com.JavangularCar.LojadeCarro.repository.KitRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.JavangularCar.LojadeCarro.support.ErrorMessages.*;
import static com.JavangularCar.LojadeCarro.support.TestConstants.ID_INVALIDO;
import static com.JavangularCar.LojadeCarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KitServiceTest {

    @Mock
    private KitRepository kitRepository;
    @Mock
    private KitMapper kitMapper;
    @Mock
    private CarroService carroService;

    @InjectMocks
    private KitService kitService;

    @Test
    @DisplayName("Deve cadastrar um kit")
    void deveCadastrarUmKit() {
        //Arrange
        var request = criarKitRequest();
        var entity = criarKitEntity();
        var response = criarKitResponse();
        var carroEntity = criarCarroEntity();

        when(kitMapper.toEntity(request))
                .thenReturn(entity);

        when(carroService.buscaCarro(request.idCarro()))
                .thenReturn(carroEntity);

        when(kitRepository.save(entity))
                .thenReturn(entity);

        when(kitMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = kitService.createKit(request);
        //Assert
        assertKitResponse(resultado);
        assertThat(entity.getCarro())
                .isSameAs(carroEntity);

        verify(kitMapper).toEntity(request);
        verify(carroService).buscaCarro(ID_VALIDO);
        verify(kitRepository).save(entity);
        verify(kitMapper).toResponse(entity);

        verifyNoMoreInteractions(
                kitMapper,
                kitRepository,
                carroService
        );
    }


    @Test
    @DisplayName("Deve lançar uma exceção ao cadastrar um kit")
    void deveLancarUmaExcecaoAoCadastrarUmKit() {
        //Arrange
        var request = KitRequestFactory
                .criarRequest()
                .comId(ID_INVALIDO)
                .build();
        var entity = criarKitEntity();

        when(kitMapper.toEntity(request))
                .thenReturn(entity);
        when(carroService.buscaCarro(request.idCarro()))
                .thenThrow(new CarroException(request.idCarro()));
        //Act
        var excecao = assertThrows(
                CarroException.class,
                () -> kitService.createKit(request));
        //Assert
        assertThat(excecao)
                .hasMessage(String.format(ID_NOT_FOUND, CARRO, ID_INVALIDO));

        verify(kitMapper).toEntity(request);
        verify(carroService).buscaCarro(ID_INVALIDO);

        verify(kitRepository, never()).save(any());
        verify(kitMapper, never()).toResponse(any());

        verifyNoMoreInteractions(
                kitMapper,
                kitRepository,
                carroService
        );
    }


    @Test
    @DisplayName("Deve listar os kits")
    void deveListarOsKits() {
        //Arrange
        //Act
        //Assert
    }

    @Test
    @DisplayName("Deve filtrar um kit por ID")
    void deveFiltrarUmKitPorID() {
        //Arrange
        var entity = criarKitEntity();
        var response = criarKitResponse();

        when(kitRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        when(kitMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = kitService.filtrarKit(ID_VALIDO);
        //Assert
        assertKitResponse(resultado);

        verify(kitRepository).findById(ID_VALIDO);
        verify(kitMapper).toResponse(entity);

        verifyNoMoreInteractions(
                kitRepository,
                kitMapper
        );
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao filtrar um kit por ID")
    void deveLancarUmaExcecaoAoFiltrarUmKitPorID() {
        //Arrange
        when(kitRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var exception = assertThrows(KitException.class, () -> kitService.filtrarKit(ID_INVALIDO));
        //Assert
        assertKitResponseException(exception);

        verify(kitRepository).findById(ID_INVALIDO);
        verify(kitMapper, never()).toResponse(any());

        verifyNoMoreInteractions(
                kitRepository
        );
    }

    @Test
    @DisplayName("Deve atualizar um kit")
    void deveAtualizarUmKit() {
        //Arrange
        var request = KitRequestFactory
                .criarRequest()
                .comFreio(false)
                .comTodosOSCamposExcetoFreio()
                .build();
        var entity = criarKitEntity();

        var response = KitResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comFreio(false)
                .build();
        var carroEntity = criarCarroEntity();

        when(kitRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        doNothing().when(kitMapper).toUpdate(request, entity);
        when(carroService.buscaCarro(request.idCarro()))
                .thenReturn(carroEntity);
        when(kitRepository.save(entity))
                .thenReturn(entity);
        when(kitMapper.toResponse(entity))
                .thenReturn(response);

        //Act
        var resultado = kitService.updateKit(request, ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        KitResponse::id,
                        KitResponse::freioABS,
                        KitResponse::rodaLigaLeve,
                        KitResponse::automatico,
                        KitResponse::direcaoHidraulica,
                        KitResponse::arCondicionado,
                        KitResponse::quatroPortas,
                        KitResponse::bancoCouro,
                        KitResponse::idCarro,
                        KitResponse::marca,
                        KitResponse::modelo
                ).containsExactly(
                        ID_VALIDO,
                        false,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        ID_VALIDO,
                        "Chevrolet",
                        "Onix"
                );
        assertThat(request.freioABS())
                .isEqualTo(response.freioABS())
                .isNotEqualTo(entity.isFreioABS());

        verify(kitRepository).findById(ID_VALIDO);
        verify(kitMapper).toUpdate(request, entity);
        verify(carroService).buscaCarro(request.idCarro());
        verify(kitRepository).save(entity);
        verify(kitMapper).toResponse(entity);

        verifyNoMoreInteractions(
                kitMapper,
                kitRepository,
                carroService
        );
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao atualizar um kit")
    void deveLancarUmaExcecaoAoAtualizarUmKit() {
        //Arrange
        var request = criarKitRequest();
        when(kitRepository.findById(ID_INVALIDO))
        .thenReturn(Optional.empty());
        //Act
        var excecao = assertThrows(KitException.class,
                () -> kitService.updateKit(request, ID_INVALIDO));
        //Assert
        assertKitResponseException(excecao);
        verify(kitRepository).findById(ID_INVALIDO);
        verify(kitRepository, never()).save(any());

        verifyNoInteractions(carroService);
        verifyNoInteractions(kitMapper);

        verifyNoMoreInteractions(kitRepository);
    }

    @Test
    @DisplayName("Deve deletar um kit")
    void deveDeletarUmKit() {
        //Arrange
        var entity = criarKitEntity();

        when(kitRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //Act
        kitService.deleteKit(ID_VALIDO);
        //Assert
        verify(kitRepository).findById(ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao deletar um kit")
    void deveLancarUmaExcecaoAoDeletarUmKit() {
        //Arrange
        when(kitRepository.findById(ID_INVALIDO))
        .thenReturn(Optional.empty());
        //Act
        var excecao = assertThrows(KitException.class,
                () -> kitService.deleteKit(ID_INVALIDO));
        //Assert
        assertKitResponseException(excecao);
        verify(kitRepository).findById(ID_INVALIDO);
        verify(kitRepository, never()).deleteById(any());

        verifyNoMoreInteractions(kitRepository);
    }

    private KitRequest criarKitRequest() {
        return KitRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    private Kit criarKitEntity() {
        return KitEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    private KitResponse criarKitResponse() {
        return KitResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }

    private Carro criarCarroEntity() {
        return CarroEntityFactory.criarEntity().comTodosOsCampos().build();
    }

    private static void assertKitResponse(KitResponse resultado) {
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        KitResponse::id,
                        KitResponse::freioABS,
                        KitResponse::rodaLigaLeve,
                        KitResponse::automatico,
                        KitResponse::direcaoHidraulica,
                        KitResponse::arCondicionado,
                        KitResponse::quatroPortas,
                        KitResponse::bancoCouro,
                        KitResponse::idCarro,
                        KitResponse::marca,
                        KitResponse::modelo
                ).containsExactly(
                        ID_VALIDO,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        ID_VALIDO,
                        "Chevrolet",
                        "Onix"
                );

    }

    private static void assertKitResponseException(KitException excecao) {
        assertThat(excecao)
                .hasMessage(String.format(ID_NOT_FOUND, KIT, ID_INVALIDO));
    }
}
