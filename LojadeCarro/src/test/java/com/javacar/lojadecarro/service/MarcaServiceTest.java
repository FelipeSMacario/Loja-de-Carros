package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.entity.Marca;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.marca.MarcaEntityFactory;
import com.javacar.lojadecarro.factory.marca.MarcaRequestFactory;
import com.javacar.lojadecarro.factory.marca.MarcaResponseFactory;
import com.javacar.lojadecarro.mapper.MarcaMapper;
import com.javacar.lojadecarro.repository.MarcaRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import org.assertj.core.groups.Tuple;
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
import static com.javacar.lojadecarro.factory.helper.MarcaHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarcaServiceTest {

    @Mock
    private MarcaMapper marcaMapper;

    @Mock
    private MarcaRepository marcaRepository;

    @InjectMocks
    private MarcaService marcaService;
    @Spy
    private EntityValidation entityValidation;

    @Test
    @DisplayName("Valida a criação da marca")
    void deveCriarMarcaComSucesso() {
        //Arrange
        var request = criarMarcaRequest();
        var entity = criarMarcaEntity();

        var response = criarMarcaResponse();


        when(marcaMapper.toEntity(request)).thenReturn(entity);
        when(marcaRepository.save(entity)).thenReturn(entity);
        when(marcaMapper.toResponse(entity)).thenReturn(response);

        //ACT
        var resultado = marcaService.criar(request);

        //Assert
        assertMarcaResponse(resultado);

        verify(marcaMapper).toEntity(request);
        verify(marcaRepository).save(entity);

        verifyNoMoreInteractions(marcaMapper);
        verifyNoMoreInteractions(marcaRepository);
    }

    @Test
    @DisplayName("Deve listar todas as marcas ativas")
    void deveListarTodasAsMarcasAtivas() {
        //Arrange
        var marca1 = criarMarcaEntity();
        var marca2 = MarcaEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Renault")
                .comURL("youtube.com")
                .comAtivo(true)
                .build();
        var entityList = List.of(marca1, marca2);
        var marcaResponse1 = criarMarcaResponse();
        var marcaResponse2 = MarcaResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Renault")
                .comURL("youtube.com")
                .comAtivo(true)
                .build();

        when(marcaRepository.findByAtivo(true))
                .thenReturn(entityList);

        when(marcaMapper.toResponse(marca1))
                .thenReturn(marcaResponse1);

        when(marcaMapper.toResponse(marca2))
                .thenReturn(marcaResponse2);

        //ACT
        var resultado = marcaService.listar(StatusFiltro.ATIVAS);
        //Assert

        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        MarcaResponse::id,
                        MarcaResponse::nome,
                        MarcaResponse::url,
                        MarcaResponse::ativo
                ).containsExactly(
                        Tuple.tuple(1L, "Ford", "https://www.google.com", true),
                        Tuple.tuple(2L, "Renault", "youtube.com", true)
                );

        verify(marcaRepository).findByAtivo(true);
        verify(marcaRepository, never()).findAll();
        verify(marcaMapper).toResponse(marca1);
        verify(marcaMapper).toResponse(marca2);

        verifyNoMoreInteractions(marcaMapper);
        verifyNoMoreInteractions(marcaRepository);
    }

    @Test
    @DisplayName("Deve listar todas as marcas inativas")
    void deveListarTodasAsMarcasInativas() {
        //Arrange
        var marca1 = criarMarcaEntity();
        marca1.setAtivo(false);
        var marca2 = MarcaEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Renault")
                .comURL("youtube.com")
                .comAtivo(false)
                .build();
        var entityList = List.of(marca1, marca2);
        var marcaResponse1 = MarcaResponseFactory
                .criarResponse()
                .comId(1L)
                .comNome("Ford")
                .comURL("https://www.google.com")
                .comAtivo(false)
                .build();
        var marcaResponse2 = MarcaResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Renault")
                .comURL("youtube.com")
                .comAtivo(false)
                .build();

        when(marcaRepository.findByAtivo(false))
                .thenReturn(entityList);

        when(marcaMapper.toResponse(marca1))
                .thenReturn(marcaResponse1);

        when(marcaMapper.toResponse(marca2))
                .thenReturn(marcaResponse2);

        //ACT
        var resultado = marcaService.listar(StatusFiltro.INATIVAS);
        //Assert

        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        MarcaResponse::id,
                        MarcaResponse::nome,
                        MarcaResponse::url,
                        MarcaResponse::ativo
                ).containsExactly(
                        Tuple.tuple(1L, "Ford", "https://www.google.com", false),
                        Tuple.tuple(2L, "Renault", "youtube.com", false)
                );

        verify(marcaRepository).findByAtivo(false);
        verify(marcaRepository, never()).findAll();
        verify(marcaMapper).toResponse(marca1);
        verify(marcaMapper).toResponse(marca2);

        verifyNoMoreInteractions(marcaMapper);
        verifyNoMoreInteractions(marcaRepository);
    }

    @Test
    @DisplayName("Deve listar todas as marcas")
    void deveListarTodasAsMarcas() {
        //Arrange
        var marca1 = criarMarcaEntity();
        var marca2 = MarcaEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Renault")
                .comURL("youtube.com")
                .comAtivo(false)
                .build();
        var entityList = List.of(marca1, marca2);
        var marcaResponse1 = criarMarcaResponse();
        var marcaResponse2 = MarcaResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Renault")
                .comURL("youtube.com")
                .comAtivo(false)
                .build();

        when(marcaRepository.findAll())
                .thenReturn(entityList);

        when(marcaMapper.toResponse(marca1))
                .thenReturn(marcaResponse1);

        when(marcaMapper.toResponse(marca2))
                .thenReturn(marcaResponse2);

        //ACT
        var resultado = marcaService.listar(StatusFiltro.TODAS);
        //Assert

        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        MarcaResponse::id,
                        MarcaResponse::nome,
                        MarcaResponse::url,
                        MarcaResponse::ativo
                ).containsExactly(
                        Tuple.tuple(1L, "Ford", "https://www.google.com", true),
                        Tuple.tuple(2L, "Renault", "youtube.com", false)
                );

        verify(marcaRepository, never()).findByAtivo(false);
        verify(marcaRepository).findAll();
        verify(marcaMapper).toResponse(marca1);
        verify(marcaMapper).toResponse(marca2);

        verifyNoMoreInteractions(marcaMapper);
        verifyNoMoreInteractions(marcaRepository);
    }

    @Test
    @DisplayName("Valida a busca da marca por ID")
    void deveBuscarMarcaPorID() {
        // Arrange
        var entity = criarMarcaEntity();

        var response = criarMarcaResponse();

        when(marcaRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(marcaMapper.toResponse(entity))
                .thenReturn(response);

        // Act
        var resultado = marcaService.buscarPorId(ID_VALIDO);

        // Assert

        assertMarcaResponse(resultado);

        verify(marcaRepository).findById(ID_VALIDO);
        verify(marcaMapper).toResponse(entity);

        verifyNoMoreInteractions(marcaMapper);
        verifyNoMoreInteractions(marcaRepository);

    }

    @Test
    @DisplayName("Valida a exceção de marca não encontrada")
    void deveLancarExcecao() {
        when(marcaRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                NotFoundException.class,
                () -> marcaService.buscarPorId(ID_INVALIDO)
        );

        assertNotFoundResponseError(exception, MARCA, ID_INVALIDO);

        verify(marcaRepository).findById(ID_INVALIDO);

        verifyNoMoreInteractions(marcaRepository);

        verifyNoInteractions(marcaMapper);

    }

    @Test
    @DisplayName("Deve validar a atualização de uma marca")
    void deveAtualizarMarca() {
        // Arrange

        var request = criarMarcaRequest();
        var entity = criarMarcaEntity();
        var response = criarMarcaResponse();

        when(marcaRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        doNothing().when(marcaMapper)
                .toUpdate(request, entity);

        when(marcaMapper.toResponse(entity))
                .thenReturn(response);

        // Act
        var resultado = marcaService.atualizar(request, ID_VALIDO);

        // Assert
        assertMarcaResponse(resultado);

        verify(marcaRepository).findById(ID_VALIDO);
        verify(marcaMapper).toResponse(entity);

        verifyNoMoreInteractions(marcaMapper);
        verifyNoMoreInteractions(marcaRepository);
    }

    @Test
    @DisplayName("Deve lançar a exceção durante a atualização da marca")
    void deveLancarExcecaoAoAtualizarMarca() {
        var request = MarcaRequestFactory.criarRequest()
                .comTodosOsCampos()
                .build();

        when(marcaRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                NotFoundException.class,
                () -> marcaService.atualizar(request, ID_INVALIDO)
        );

        assertNotFoundResponseError(exception, MARCA, ID_INVALIDO);

        verify(marcaRepository).findById(ID_INVALIDO);

        verifyNoMoreInteractions(marcaRepository);

        verifyNoInteractions(marcaMapper);
    }

    @Test
    @DisplayName("Deve alterar o status da marca")
    void deveAlterarStatusDaMarca() {
        //Arrange
        var entity = criarMarcaEntity();
        var request = new StatusRequest(false);
        var response = MarcaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(false)
                .build();

        when(marcaRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(marcaMapper.toResponse(entity))
                .thenReturn(response);
        //ACT
        var resultado = marcaService.alterarStatus(ID_VALIDO, request);
        //Assert
        assertThat(resultado)
                .isNotNull();

        assertThat(resultado.ativo()).isFalse();
        assertThat(entity.isAtivo()).isFalse();

        verify(marcaRepository).findById(ID_VALIDO);
        verify(marcaMapper).toResponse(entity);

        verifyNoMoreInteractions(marcaRepository);
        verifyNoMoreInteractions(marcaMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar o status")
    void deveLancarExcecaoAoAlterarMarcaPorId() {
        //Arrange
        var entity = criarMarcaEntity();
        var request = new StatusRequest(true);

        when(marcaRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var exception = assertThrows(BusinessException.class,
                () -> marcaService.alterarStatus(ID_VALIDO, request));

        //Assert
        assertBusinessResponseError(exception, MARCA);

        assertThat(entity.isAtivo()).isTrue();

        verify(marcaRepository).findById(ID_VALIDO);

        verifyNoMoreInteractions(marcaRepository);

        verifyNoInteractions(marcaMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção de marca nao encontrada ao alterar status")
    void deveLancarExcecaoQuandoMarcaNaoEncontradaAoAlterarStatus() {
        //Arrange
        var request = new StatusRequest(true);
        //Assert
        var exception = assertThrows(NotFoundException.class,
                () -> marcaService.alterarStatus(ID_INVALIDO, request));

        assertNotFoundResponseError(exception, MARCA, ID_INVALIDO);

        verify(marcaRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(marcaRepository);
        verifyNoInteractions(marcaMapper);
    }

    @Test
    @DisplayName("Deve buscar a entidade da marca por Id")
    void deveBuscarAEntidadeMarca() {
        //Arrange
        var entity = criarMarcaEntity();

        when(marcaRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var resultado = marcaService.buscaMarca(ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        Marca::getId,
                        Marca::getNome,
                        Marca::isAtivo)
                .containsExactly(
                        1L,
                        "Ford",
                        true
                );
        verify(marcaRepository).findById(ID_VALIDO);
        verifyNoMoreInteractions(marcaRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar entidade da marca por ID")
    void deveLancarExcecaoAoBuscarEntidadeMarca() {
        //Arrange
        when(marcaRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //ACT
        var excecao = assertThrows(NotFoundException.class,
                () -> marcaService.buscaMarca(ID_INVALIDO));
        //Assert

        assertNotFoundResponseError(excecao, MARCA, ID_INVALIDO);

        verify(marcaRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(marcaRepository);
    }

}
