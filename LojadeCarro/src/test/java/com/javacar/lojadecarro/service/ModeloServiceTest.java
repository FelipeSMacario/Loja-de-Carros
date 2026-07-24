package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.modelo.ModeloEntityFactory;
import com.javacar.lojadecarro.factory.modelo.ModeloResponseFactory;
import com.javacar.lojadecarro.mapper.ModeloMapper;
import com.javacar.lojadecarro.repository.ModeloRepository;
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

import static com.javacar.lojadecarro.enums.Entidade.MARCA;
import static com.javacar.lojadecarro.enums.Entidade.MODELO;
import static com.javacar.lojadecarro.factory.helper.MarcaHelper.criarMarcaEntity;
import static com.javacar.lojadecarro.factory.helper.ModeloHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModeloServiceTest {

    @Mock
    private ModeloMapper modeloMapper;
    @Mock
    private MarcaService marcaService;
    @Mock
    private ModeloRepository modeloRepository;
    @Spy
    private EntityValidation entityValidation;
    @InjectMocks
    private ModeloService modeloService;

    @Test
    @DisplayName("Deve cadastrar um modelo")
    void deveCadastrarModelo() {
        //Arrange
        var request = criarModeloRequest();
        var entity = criarModeloEntity();
        var marcaEntity = criarMarcaEntity();
        var response = criarModeloResponse();

        when(modeloMapper.toEntity(request))
                .thenReturn(entity);

        when(marcaService.buscaMarca(request.idMarca()))
                .thenReturn(marcaEntity);

        when(modeloRepository.save(entity))
                .thenReturn(entity);

        when(modeloMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = modeloService.criar(request);
        //Assert
        assertThat(entity.getMarca())
                .isSameAs(marcaEntity);

        assertModeloResponse(resultado);

        verify(modeloMapper).toEntity(request);
        verify(marcaService).buscaMarca(request.idMarca());
        verify(modeloRepository).save(entity);
        verify(modeloMapper).toResponse(entity);

        verifyNoMoreInteractions(modeloMapper);
        verifyNoMoreInteractions(marcaService);
        verifyNoMoreInteractions(modeloRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar marca invalida na criação do modelo")
    void deveLancarExceaoBuscaMarcaInvalida() {
        //Arrange
        var request = criarModeloRequest();
        var entity = criarModeloEntity();

        when(modeloMapper.toEntity(request))
                .thenReturn(entity);

        when(marcaService.buscaMarca(request.idMarca()))
                .thenThrow(new NotFoundException(MARCA, request.idMarca()));
        //ACT
        var excecao = assertThrows(NotFoundException.class,
                () -> modeloService.criar(request));
        //Assert
        assertNotFoundResponseError(excecao, MARCA, request.idMarca());

        verify(modeloMapper).toEntity(request);
        verify(marcaService).buscaMarca(request.idMarca());

        verifyNoMoreInteractions(modeloMapper);
        verifyNoMoreInteractions(marcaService);

        verifyNoInteractions(modeloRepository);
    }

    @Test
    @DisplayName("Deve listar os modelos ativos")
    void deveListarModelosAtivos() {
        //Arrange

        var onixEntity = ModeloEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var celtaEntity = ModeloEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Celta")
                .comAtivo(true)
                .build();

        var entity = List.of(onixEntity, celtaEntity);

        var onixResponse = ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        var celtaResponse = ModeloResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Celta")
                .comAtivo(true)
                .build();

        when(modeloRepository.findByAtivo(true))
                .thenReturn(entity);

        when(modeloMapper.toResponse(onixEntity))
                .thenReturn(onixResponse);
        when(modeloMapper.toResponse(celtaEntity))
                .thenReturn(celtaResponse);
        //Act
        var resultado = modeloService.listar(StatusFiltro.ATIVAS);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        ModeloResponse::id,
                        ModeloResponse::nome,
                        ModeloResponse::ativo
                ).containsExactly(
                        tuple(ID_VALIDO, "Onix", true),
                        tuple(2L, "Celta", true)
                );

        verify(modeloRepository).findByAtivo(true);
        verify(modeloRepository, never()).findAll();
        verify(modeloMapper).toResponse(onixEntity);
        verify(modeloMapper).toResponse(celtaEntity);

        verifyNoMoreInteractions(modeloMapper);
        verifyNoMoreInteractions(modeloRepository);
    }

    @Test
    @DisplayName("Deve listar todos os modelos inativos")
    void deveListarModelosInativos() {
        //Arrange

        var onixEntity = ModeloEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comAtivo(false)
                .build();
        var celtaEntity = ModeloEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Celta")
                .comAtivo(false)
                .build();

        var entity = List.of(onixEntity, celtaEntity);

        var onixResponse = ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(false)
                .build();
        var celtaResponse = ModeloResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Celta")
                .comAtivo(false)
                .build();

        when(modeloRepository.findByAtivo(false))
                .thenReturn(entity);

        when(modeloMapper.toResponse(onixEntity))
                .thenReturn(onixResponse);
        when(modeloMapper.toResponse(celtaEntity))
                .thenReturn(celtaResponse);
        //Act
        var resultado = modeloService.listar(StatusFiltro.INATIVAS);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        ModeloResponse::id,
                        ModeloResponse::nome,
                        ModeloResponse::ativo
                ).containsExactly(
                        tuple(ID_VALIDO, "Onix", false),
                        tuple(2L, "Celta", false)
                );

        verify(modeloRepository, never()).findAll();
        verify(modeloRepository).findByAtivo(false);
        verify(modeloMapper).toResponse(onixEntity);
        verify(modeloMapper).toResponse(celtaEntity);

        verifyNoMoreInteractions(modeloMapper);
        verifyNoMoreInteractions(modeloRepository);
    }

    @Test
    @DisplayName("Deve listar todos os modelos")
    void deveListarModelos() {
        //Arrange

        var onixEntity = ModeloEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var celtaEntity = ModeloEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Celta")
                .comAtivo(false)
                .build();

        var entity = List.of(onixEntity, celtaEntity);

        var onixResponse = ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        var celtaResponse = ModeloResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Celta")
                .comAtivo(false)
                .build();

        when(modeloRepository.findAll())
                .thenReturn(entity);

        when(modeloMapper.toResponse(onixEntity))
                .thenReturn(onixResponse);
        when(modeloMapper.toResponse(celtaEntity))
                .thenReturn(celtaResponse);
        //Act
        var resultado = modeloService.listar(StatusFiltro.TODAS);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        ModeloResponse::id,
                        ModeloResponse::nome,
                        ModeloResponse::ativo
                ).containsExactly(
                        tuple(ID_VALIDO, "Onix", true),
                        tuple(2L, "Celta", false)
                );

        verify(modeloRepository).findAll();
        verify(modeloRepository, never()).findByAtivo(anyBoolean());
        verify(modeloMapper).toResponse(onixEntity);
        verify(modeloMapper).toResponse(celtaEntity);

        verifyNoMoreInteractions(modeloMapper);
        verifyNoMoreInteractions(modeloRepository);
    }

    @Test
    @DisplayName("Deve buscar um modelo por ID")
    void deveBuscarModeloPorId() {
        //Arrange
        var entity = criarModeloEntity();
        var response = criarModeloResponse();

        when(modeloRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        when(modeloMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = modeloService.buscarPorId(ID_VALIDO);
        //Assert
        assertModeloResponse(resultado);

        verify(modeloRepository).findById(ID_VALIDO);
        verify(modeloMapper).toResponse(entity);

        verifyNoMoreInteractions(modeloMapper);
        verifyNoMoreInteractions(modeloRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar um modelo por ID")
    void deveBuscarModeloPorIdException() {
        //Arrange
        when(modeloRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var exception = assertThrows(NotFoundException.class,
                () -> modeloService.buscarPorId(ID_INVALIDO));
        //Assert
        assertNotFoundResponseError(exception, MODELO, ID_INVALIDO);

        verify(modeloRepository).findById(ID_INVALIDO);

        verifyNoMoreInteractions(modeloRepository);

        verifyNoInteractions(modeloMapper);
        verifyNoInteractions(marcaService);
    }

    @Test
    @DisplayName("Deve atualizar o modelo")
    void deveAtualizarModelo() {
        //Arrange
        var request = criarModeloRequest();
        var entity = criarModeloEntity();
        var marcaEntity = criarMarcaEntity();
        var response = criarModeloResponse();

        when(modeloRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        doNothing()
                .when(modeloMapper)
                .toUpdate(request, entity);

        when(marcaService.buscaMarca(request.idMarca()))
                .thenReturn(marcaEntity);

        when(modeloMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = modeloService.atualizar(request, ID_VALIDO);
        //Assert
        assertModeloResponse(resultado);

        assertThat(entity.getMarca())
                .isNotNull()
                .isSameAs(marcaEntity);

        verify(modeloRepository).findById(ID_VALIDO);
        verify(marcaService).buscaMarca(request.idMarca());
        verify(modeloMapper).toResponse(entity);

        verifyNoMoreInteractions(modeloMapper);
        verifyNoMoreInteractions(marcaService);
        verifyNoMoreInteractions(modeloRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar modelo")
    void deveAtualizarModeloException() {
        //Arrange
        var request = criarModeloRequest();

        when(modeloRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var exception = assertThrows(
                NotFoundException.class,
                () -> modeloService.atualizar(request, ID_INVALIDO));
        //Assert
        assertNotFoundResponseError(exception, MODELO, ID_INVALIDO);

        verify(modeloRepository).findById(ID_INVALIDO);

        verifyNoMoreInteractions(modeloRepository);

        verifyNoInteractions(modeloMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um modelo com marca invalida")
    void deveLancarExceaoAtualizarMarcaInvalida() {
        //Arrange
        var request = criarModeloRequest();
        var entity = criarModeloEntity();

        when(modeloRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.of(entity));

        doNothing()
                .when(modeloMapper)
                .toUpdate(request, entity);
        when(marcaService.buscaMarca(request.idMarca()))
                .thenThrow(new NotFoundException(MARCA, request.idMarca()));
        //Act
        var excecao = assertThrows(NotFoundException.class,
                (() -> modeloService.atualizar(request, ID_INVALIDO)));
        //Assert
        assertNotFoundResponseError(excecao, MARCA, request.idMarca());

        verify(modeloRepository).findById(ID_INVALIDO);
        verify(marcaService).buscaMarca(request.idMarca());

        verifyNoMoreInteractions(modeloRepository);
        verifyNoMoreInteractions(marcaService);
        verifyNoMoreInteractions(modeloMapper);
    }

    @Test
    @DisplayName("Deve alterar o status do modelo")
    void deveAlterarStatusDaModelo() {
        //Arrange
        var entity = criarModeloEntity();
        var request = new StatusRequest(false);
        var response = ModeloResponseFactory
                .criarResponse()
                .comId(1L)
                .comNome("Onix")
                .comAtivo(false)
                .build();

        when(modeloRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(modeloMapper.toResponse(entity))
                .thenReturn(response);
        //ACT
        var resultado = modeloService.alterarStatus(ID_VALIDO, request);
        //Assert
        assertThat(resultado)
                .isNotNull();

        assertThat(resultado.ativo()).isFalse();
        assertThat(entity.isAtivo()).isFalse();

        verify(modeloRepository).findById(ID_VALIDO);
        verify(modeloMapper).toResponse(entity);

        verifyNoMoreInteractions(modeloRepository);
        verifyNoMoreInteractions(modeloMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar o status")
    void deveLancarExcecaoAoAlterarModeloPorId() {
        //Arrange
        var entity = criarModeloEntity();
        var request = new StatusRequest(true);

        when(modeloRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var exception = assertThrows(BusinessException.class,
                () -> modeloService.alterarStatus(ID_VALIDO, request));

        //Assert
        assertBusinessResponseError(exception, MODELO);

        assertThat(entity.isAtivo()).isTrue();

        verify(modeloRepository).findById(ID_VALIDO);

        verifyNoMoreInteractions(modeloRepository);

        verifyNoInteractions(modeloMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção de modelo nao encontrada ao alterar status")
    void deveLancarExcecaoQuandoModeloNaoEncontradaAoAlterarStatus() {
        //Arrange
        var request = new StatusRequest(true);
        //Assert
        var exception = assertThrows(NotFoundException.class,
                () -> modeloService.alterarStatus(ID_INVALIDO, request));

        assertNotFoundResponseError(exception, MODELO, ID_INVALIDO);

        verify(modeloRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(modeloRepository);
        verifyNoInteractions(modeloMapper);
    }

    @Test
    @DisplayName("Deve buscar a entidade do modelo por Id")
    void deveBuscarAEntidadeModelo() {
        //Arrange
        var entity = criarModeloEntity();

        when(modeloRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var resultado = modeloService.buscaModelo(ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        Modelo::getId,
                        Modelo::getNome,
                        Modelo::isAtivo)
                .containsExactly(
                        1L,
                        "Onix",
                        true
                );
        verify(modeloRepository).findById(ID_VALIDO);
        verifyNoMoreInteractions(modeloRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar entidade do modelo por ID")
    void deveLancarExcecaoAoBuscarEntidadeModelo() {
        //Arrange
        when(modeloRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //ACT
        var excecao = assertThrows(NotFoundException.class,
                () -> modeloService.buscaModelo(ID_INVALIDO));
        //Assert

        assertNotFoundResponseError(excecao, MODELO, ID_INVALIDO);

        verify(modeloRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(modeloRepository);
    }

}
