package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaEntityFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaRequestFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaResponseFactory;
import com.javacar.lojadecarro.mapper.CarroceriaMapper;
import com.javacar.lojadecarro.repository.CarroceriaRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.CARROCERIA;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.CarroceriaHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarroceriaServiceTest {

    @Mock
    private CarroceriaMapper carroceriaMapper;

    @Mock
    private CarroceriaRepository carroceriaRepository;

    @Spy
    private EntityValidation entityValidation;

    @InjectMocks
    private CarroceriaService carroceriaService;

    @Test
    @DisplayName("Deve validar a criação da carroceria")
    void deveCriarCarroceria() {
        // Arrange
        var request = criarCarroceriaRequest();
        var entity = criarCarroceriaEntity();

        var response = criarCarroceriaResponse();

        when(carroceriaMapper.toEntity(request))
                .thenReturn(entity);
        when(carroceriaRepository.save(entity))
                .thenReturn(entity);
        when(carroceriaMapper.toResponse(entity))
                .thenReturn(response);

        // Act
        var resultado = carroceriaService.criar(request);

        // Assert
        assertCarroceriaResponse(resultado);

        verify(carroceriaMapper).toEntity(request);
        verify(carroceriaRepository).save(entity);
        verify(carroceriaMapper).toResponse(entity);
    }


    @Test
    @DisplayName("Deve listar as carrocerias ativas")
    void deveListarCarroceriasAtivas() {
        //Arrange
        var hatch = criarCarroceriaEntity();
        var suv = CarroceriaEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("SUV")
                .comAtivo(true)
                .build();

        var listaDeCarrocerias = List.of(hatch, suv);

        var hatchResponse = criarCarroceriaResponse();
        var suvResponse = CarroceriaResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("SUV")
                .comAtivo(true)
                .build();

        when(carroceriaRepository.findByAtivo(true))
                .thenReturn(listaDeCarrocerias);

        when(carroceriaMapper.toResponse(hatch))
                .thenReturn(hatchResponse);

        when(carroceriaMapper.toResponse(suv))
                .thenReturn(suvResponse);


        //ACT
        var resultado = carroceriaService.listar(StatusFiltro.ATIVAS);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(CarroceriaResponse::nome)
                .containsExactly("Hatch", "SUV");

        verify(carroceriaRepository).findByAtivo(true);
        verify(carroceriaMapper).toResponse(hatch);
        verify(carroceriaMapper).toResponse(suv);
        verify(carroceriaRepository, never()).findAll();

        verifyNoMoreInteractions(carroceriaMapper);
        verifyNoMoreInteractions(carroceriaRepository);
    }

    @Test
    @DisplayName("Deve listar as carrocerias inativas")
    void deveListarCarroceriasInativas() {
        //Arrange
        var hatch = CarroceriaEntityFactory
                .criarEntity()
                .comId(1L)
                .comNome("Hatch")
                .comAtivo(false)
                .build();
        var suv = CarroceriaEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("SUV")
                .comAtivo(false)
                .build();

        var listaDeCarrocerias = List.of(hatch, suv);

        var hatchResponse = CarroceriaResponseFactory
                .criarResponse()
                .comId(1L)
                .comNome("Hatch")
                .comAtivo(false)
                .build();
        var suvResponse = CarroceriaResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("SUV")
                .comAtivo(false)
                .build();

        when(carroceriaRepository.findByAtivo(false))
                .thenReturn(listaDeCarrocerias);

        when(carroceriaMapper.toResponse(hatch))
                .thenReturn(hatchResponse);

        when(carroceriaMapper.toResponse(suv))
                .thenReturn(suvResponse);


        //ACT
        var resultado = carroceriaService.listar(StatusFiltro.INATIVAS);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(CarroceriaResponse::nome)
                .containsExactly("Hatch", "SUV");

        verify(carroceriaRepository).findByAtivo(false);
        verify(carroceriaMapper).toResponse(hatch);
        verify(carroceriaMapper).toResponse(suv);
        verify(carroceriaRepository, never()).findAll();

        verifyNoMoreInteractions(carroceriaMapper);
        verifyNoMoreInteractions(carroceriaRepository);
    }

    @Test
    @DisplayName("Deve listar todas as carrocerias")
    void deveListarTodasCarrocerias() {
        //Arrange
        var hatch = CarroceriaEntityFactory
                .criarEntity()
                .comId(1L)
                .comNome("Hatch")
                .comAtivo(true)
                .build();
        var suv = CarroceriaEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("SUV")
                .comAtivo(true)
                .build();
        var sedan = CarroceriaEntityFactory
                .criarEntity()
                .comId(3L)
                .comNome("Sedan")
                .comAtivo(false)
                .build();

        var listaDeCarrocerias = List.of(hatch, suv, sedan);

        var hatchResponse = CarroceriaResponseFactory
                .criarResponse()
                .comId(1L)
                .comNome("Hatch")
                .comAtivo(true)
                .build();
        var suvResponse = CarroceriaResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("SUV")
                .comAtivo(true)
                .build();
        var sedanResponse = CarroceriaResponseFactory
                .criarResponse()
                .comId(3L)
                .comNome("SEDAN")
                .comAtivo(false)
                .build();

        when(carroceriaRepository.findAll())
                .thenReturn(listaDeCarrocerias);

        when(carroceriaMapper.toResponse(hatch))
                .thenReturn(hatchResponse);

        when(carroceriaMapper.toResponse(suv))
                .thenReturn(suvResponse);

        when(carroceriaMapper.toResponse(sedan))
                .thenReturn(sedanResponse);


        //ACT
        var resultado = carroceriaService.listar(StatusFiltro.TODAS);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(3)
                .extracting(CarroceriaResponse::nome)
                .containsExactly("Hatch", "SUV", "SEDAN");

        verify(carroceriaRepository).findAll();
        verify(carroceriaMapper).toResponse(hatch);
        verify(carroceriaMapper).toResponse(suv);
        verify(carroceriaRepository, never()).findByAtivo(anyBoolean());

        verifyNoMoreInteractions(carroceriaMapper);
        verifyNoMoreInteractions(carroceriaRepository);
    }

    @Test
    @DisplayName("Deve validar a busca de uma carroceria por ID")
    void deveBuscarCarroceriaPorId() {
        // Arrange
        var entity = criarCarroceriaEntity();
        var response = criarCarroceriaResponse();

        when(carroceriaRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(carroceriaMapper.toResponse(entity))
                .thenReturn(response);

        // Act
        var resultado = carroceriaService.buscaPorId(ID_VALIDO);

        // Assert
        assertCarroceriaResponse(resultado);

        verify(carroceriaRepository).findById(ID_VALIDO);
        verify(carroceriaMapper).toResponse(entity);

        verifyNoMoreInteractions(carroceriaMapper);
        verifyNoMoreInteractions(carroceriaRepository);
    }

    @Test
    @DisplayName("Deve lançar uma exceção na busca por uma carroceria")
    void deveLancarExcecaoAoBuscarCarroceriaPorId() {
        // Arrange

        when(carroceriaRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());

        // Assert
        var exception = assertThrows(
                NotFoundException.class,
                () -> carroceriaService.buscaPorId(ID_INVALIDO)
        );

        assertNotFoundResponseError(exception, CARROCERIA, ID_INVALIDO);

        verify(carroceriaRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(carroceriaRepository);

        verifyNoInteractions(carroceriaMapper);

    }


    @Test
    @DisplayName("Deve atualizar uma carroceria pelo ID")
    void deveAtualizarCarroceriaPorId() {
        //Arrange
        var request = CarroceriaRequestFactory
                .criarRequest()
                .comNome("Sedan")
                .build();
        var entity = criarCarroceriaEntity();
        var response = CarroceriaResponseFactory
                .criarResponse()
                .comId(ID_VALIDO)
                .comNome("Sedan")
                .build();

        when(carroceriaRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        doNothing()
                .when(carroceriaMapper)
                .toUpdate(any(), any());

        when(carroceriaMapper.toResponse(entity))
                .thenReturn(response);

        // ACT
        var resultado = carroceriaService.atualizar(request, ID_VALIDO);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        CarroceriaResponse::id,
                        CarroceriaResponse::nome
                )
                .containsExactly(
                        ID_VALIDO,
                        "Sedan"
                );

        verify(carroceriaRepository).findById(ID_VALIDO);
        verify(carroceriaMapper).toResponse(entity);

        verifyNoMoreInteractions(carroceriaRepository);
        verifyNoMoreInteractions(carroceriaMapper);
    }

    @Test
    @DisplayName("Deve lançar uma exceção durante a atualização de uma carroceria")
    void deveLancarExcecaoAoAtualizarCarroceriaPorId() {
        //Arrange
        var request = criarCarroceriaRequest();

        when(carroceriaRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());

        //Assert
        var exception = assertThrows(NotFoundException.class,
                () -> carroceriaService.atualizar(request, ID_INVALIDO));

        assertNotFoundResponseError(exception, CARROCERIA, ID_INVALIDO);

        verify(carroceriaRepository).findById(ID_INVALIDO);

        verifyNoMoreInteractions(carroceriaRepository);

        verifyNoInteractions(carroceriaMapper);
    }

    @Test
    @DisplayName("Deve alterar o status da carroceria")
    void deveAlterarStatusDaCarroceria() {
        //Arrange
        var entity = criarCarroceriaEntity();
        var request = new StatusRequest(false);
        var response = CarroceriaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(false)
                .build();

        when(carroceriaRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(carroceriaMapper.toResponse(entity))
                .thenReturn(response);
        //ACT
        var resultado = carroceriaService.alterarStatus(ID_VALIDO, request);
        //Assert
        assertThat(resultado)
                .isNotNull();

        assertThat(resultado.ativo()).isFalse();
        assertThat(entity.isAtivo()).isFalse();

        verify(carroceriaRepository).findById(ID_VALIDO);
        verify(carroceriaMapper).toResponse(entity);

        verifyNoMoreInteractions(carroceriaRepository);
        verifyNoMoreInteractions(carroceriaMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar o status")
    void deveLancarExcecaoAoAlterarCarroceriaPorId() {
        //Arrange
        var entity = criarCarroceriaEntity();
        var request = new StatusRequest(true);

        when(carroceriaRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var exception = assertThrows(BusinessException.class,
                () -> carroceriaService.alterarStatus(ID_VALIDO, request));

        //Assert
        assertBusinessResponseError(exception, CARROCERIA);

        assertThat(entity.isAtivo()).isTrue();

        verify(carroceriaRepository).findById(ID_VALIDO);

        verifyNoMoreInteractions(carroceriaRepository);

        verifyNoInteractions(carroceriaMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção de carroceria nao encontrada ao alterar status")
    void deveLancarExcecaoQuandoCarroceriaNaoEncontradaAoAlterarStatus() {
        //Arrange
        var request = new StatusRequest(true);
        //Assert
        var exception = assertThrows(NotFoundException.class,
                () -> carroceriaService.alterarStatus(ID_INVALIDO, request));

        assertNotFoundResponseError(exception, CARROCERIA, ID_INVALIDO);

        verify(carroceriaRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(carroceriaRepository);
        verifyNoInteractions(carroceriaMapper);
    }

    @Test
    @DisplayName("Deve buscar a entidade da carroceria por Id")
    void deveBuscarAEntidadeCarroceria() {
        //Arrange
        var entity = criarCarroceriaEntity();

        when(carroceriaRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var resultado = carroceriaService.buscaCarroceria(ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        Carroceria::getId,
                        Carroceria::getNome,
                        Carroceria::isAtivo)
                .containsExactly(
                        1L,
                        "Hatch",
                        true
                );
        verify(carroceriaRepository).findById(ID_VALIDO);
        verifyNoMoreInteractions(carroceriaRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar entidade da carroceria por ID")
    void deveLancarExcecaoAoBuscarEntidadeCarroceria() {
        //Arrange
        when(carroceriaRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //ACT
        var excecao = assertThrows(NotFoundException.class,
                () -> carroceriaService.buscaCarroceria(ID_INVALIDO));
        //Assert

        assertNotFoundResponseError(excecao, CARROCERIA, ID_INVALIDO);

        verify(carroceriaRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(carroceriaRepository);
    }
}
