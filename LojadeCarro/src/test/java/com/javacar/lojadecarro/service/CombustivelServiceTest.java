package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.combustivel.CombustivelEntityFactory;
import com.javacar.lojadecarro.factory.combustivel.CombustivelResponseFactory;
import com.javacar.lojadecarro.mapper.CombustivelMapper;
import com.javacar.lojadecarro.repository.CombustivelRepository;
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

import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.factory.helper.CombustivelHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CombustivelServiceTest {

    @Mock
    private CombustivelMapper combustivelMapper;

    @Mock
    private CombustivelRepository combustivelRepository;

    @Spy
    private EntityValidation entityValidation;


    @InjectMocks
    private CombustivelService combustivelService;

    @Test
    @DisplayName("Valida criação do combustível")
    void validaCriaCombustivel() {
        //Arrange
        var request = criarCombustivelRequest();

        var entity = criarCombustivelEntity();

        var response = criarCombustivelResponse();

        when(combustivelMapper.toEntity(request))
                .thenReturn(entity);

        when(combustivelRepository.save(entity))
                .thenReturn(entity);

        when(combustivelMapper
                .toResponse(entity)).thenReturn(response);

        //Act
        var resultado = combustivelService.criar(request);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        CombustivelResponse::id,
                        CombustivelResponse::nome,
                        CombustivelResponse::ativo)
                .containsExactly(
                        ID_VALIDO,
                        "Gasolina",
                        true);


        verify(combustivelMapper).toResponse(entity);
        verify(combustivelRepository).save(entity);
        verify(combustivelMapper).toEntity(request);

        verifyNoMoreInteractions(combustivelMapper);
        verifyNoMoreInteractions(combustivelRepository);
    }

    @Test
    @DisplayName("Deve listar todos os combustiveis ativos")
    void deveListarTodosOsCombustivelAtivos() {
        //Arrange
        var gasolina = criarCombustivelEntity();
        var eletrico = CombustivelEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Eletrico")
                .comAtivo(true)
                .build();

        var request = List.of(gasolina, eletrico);

        var gasolinaResponse = criarCombustivelResponse();
        var eletricoResponse = CombustivelResponseFactory.criarResponse()
                .comId(2L)
                .comNome("Eletrico")
                .comAtivo(true)
                .build();

        when(combustivelRepository.findByAtivo(true))
                .thenReturn(request);

        when(combustivelMapper.toResponse(gasolina))
                .thenReturn(gasolinaResponse);

        when(combustivelMapper.toResponse(eletrico))
                .thenReturn(eletricoResponse);
        //ACT
        var resultado = combustivelService.listarCombustiveis(StatusFiltro.ATIVAS);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        CombustivelResponse::id,
                        CombustivelResponse::nome,
                        CombustivelResponse::ativo
                )
                .containsExactly(
                        tuple(ID_VALIDO, "Gasolina", true),
                        tuple(2L, "Eletrico", true));


        verify(combustivelRepository).findByAtivo(true);
        verify(combustivelMapper).toResponse(eletrico);
        verify(combustivelMapper).toResponse(gasolina);
        verify(combustivelRepository, never()).findAll();

        verifyNoMoreInteractions(combustivelMapper);
        verifyNoMoreInteractions(combustivelRepository);
    }

    @Test
    @DisplayName("Deve listar todos os combustiveis inativos")
    void deveListarTodosOsCombustivelInativos() {
        //Arrange
        var gasolina = CombustivelEntityFactory
                .criarEntity()
                .comId(1L)
                .comNome("Gasolina")
                .comAtivo(false)
                .build();
        var eletrico = CombustivelEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Eletrico")
                .comAtivo(false)
                .build();

        var request = List.of(gasolina, eletrico);

        var gasolinaResponse = CombustivelResponseFactory.criarResponse()
                .comId(1L)
                .comNome("Gasolina")
                .comAtivo(false)
                .build();
        var eletricoResponse = CombustivelResponseFactory.criarResponse()
                .comId(2L)
                .comNome("Eletrico")
                .comAtivo(false)
                .build();

        when(combustivelRepository.findByAtivo(false))
                .thenReturn(request);

        when(combustivelMapper.toResponse(gasolina))
                .thenReturn(gasolinaResponse);

        when(combustivelMapper.toResponse(eletrico))
                .thenReturn(eletricoResponse);
        //ACT
        var resultado = combustivelService.listarCombustiveis(StatusFiltro.INATIVAS);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        CombustivelResponse::id,
                        CombustivelResponse::nome,
                        CombustivelResponse::ativo
                )
                .containsExactly(
                        tuple(ID_VALIDO, "Gasolina", false),
                        tuple(2L, "Eletrico", false));


        verify(combustivelRepository).findByAtivo(false);
        verify(combustivelMapper).toResponse(eletrico);
        verify(combustivelMapper).toResponse(gasolina);
        verify(combustivelRepository, never()).findAll();

        verifyNoMoreInteractions(combustivelMapper);
        verifyNoMoreInteractions(combustivelRepository);
    }

    @Test
    @DisplayName("Deve listar todos os combustíveis")
    void listarTodosCombustivel() {
        //Arrange
        var gasolina = criarCombustivelEntity();
        var eletrico = CombustivelEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Eletrico")
                .comAtivo(false)
                .build();

        var request = List.of(gasolina, eletrico);

        var gasolinaResponse = criarCombustivelResponse();
        var eletricoResponse = CombustivelResponseFactory.criarResponse()
                .comId(2L)
                .comNome("Eletrico")
                .comAtivo(false)
                .build();


        when(combustivelRepository.findAll())
                .thenReturn(request);

        when(combustivelMapper.toResponse(gasolina))
                .thenReturn(gasolinaResponse);

        when(combustivelMapper.toResponse(eletrico))
                .thenReturn(eletricoResponse);

        // Act
        var resultado = combustivelService.listarCombustiveis(StatusFiltro.TODAS);

        // Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        CombustivelResponse::id,
                        CombustivelResponse::nome,
                        CombustivelResponse::ativo
                )
                .containsExactly(
                        tuple(ID_VALIDO, "Gasolina", true),
                        tuple(2L, "Eletrico", false));

        verify(combustivelRepository).findAll();
        verify(combustivelMapper).toResponse(eletrico);
        verify(combustivelMapper).toResponse(gasolina);
        verify(combustivelRepository, never()).findByAtivo(anyBoolean());

        verifyNoMoreInteractions(combustivelMapper);
        verifyNoMoreInteractions(combustivelRepository);
    }


    @Test
    @DisplayName("Deve buscar um combustível por ID")
    void deveBuscarCombustivelPorId() {
        //Arrange
        var entity = criarCombustivelEntity();
        var response = criarCombustivelResponse();

        when(combustivelRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(combustivelMapper.toResponse(entity))
                .thenReturn(response);

        //Act
        var resultado = combustivelService.buscaPorId(ID_VALIDO);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        CombustivelResponse::id,
                        CombustivelResponse::nome,
                        CombustivelResponse::ativo
                )
                .containsExactly(
                        ID_VALIDO,
                        "Gasolina",
                        true);

        verify(combustivelRepository).findById(ID_VALIDO);
        verify(combustivelMapper).toResponse(entity);

        verifyNoMoreInteractions(combustivelMapper);
        verifyNoMoreInteractions(combustivelRepository);

    }

    @Test
    @DisplayName("Deve lançar uma exceção ao buscar um combustível")
    void deveLancarUmaExcecaoCombustivel() {
        //Arrange
        when(combustivelRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());

        //Assert
        var exception = assertThrows(
                NotFoundException.class,
                () -> combustivelService.buscaPorId(ID_INVALIDO)
        );

        assertNotFoundResponseError(exception, COMBUSTIVEL, ID_INVALIDO);

        verify(combustivelRepository).findById(ID_INVALIDO);

        verifyNoInteractions(combustivelMapper);
    }

    @Test
    @DisplayName("Deve atualizar um combustível")
    void deveAtualizarCombustivel() {
        //Arrange
        var request = criarCombustivelRequest();
        var entity = criarCombustivelEntity();
        var response = criarCombustivelResponse();

        when(combustivelRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        doNothing().when(combustivelMapper)
                .toUpdate(request, entity);

        when(combustivelMapper.toResponse(entity))
                .thenReturn(response);

        //Act
        var resultado = combustivelService.atualizar(request, ID_VALIDO);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        CombustivelResponse::id,
                        CombustivelResponse::nome,
                        CombustivelResponse::ativo
                )
                .containsExactly(
                        ID_VALIDO,
                        "Gasolina",
                        true);

        verify(combustivelRepository).findById(ID_VALIDO);
        verify(combustivelMapper).toResponse(entity);

        verifyNoMoreInteractions(combustivelMapper);
        verifyNoMoreInteractions(combustivelRepository);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao atualizar o combustível")
    void deveLancarExcecaoCombustivel() {
        //Arrange
        var request = criarCombustivelRequest();
        //Assert
        var exception = assertThrows(NotFoundException.class,
                () -> combustivelService.atualizar(request, ID_INVALIDO));
        assertNotFoundResponseError(exception, COMBUSTIVEL, ID_INVALIDO);

        verify(combustivelRepository).findById(ID_INVALIDO);

        verifyNoInteractions(combustivelMapper);
    }

    @Test
    @DisplayName("Deve alterar o status do combustivel")
    void deveAlterarStatusDoCombustivel() {
        //Arrange
        var entity = criarCombustivelEntity();
        var request = new StatusRequest(false);
        var response = CombustivelResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(false)
                .build();

        when(combustivelRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(combustivelMapper.toResponse(entity))
                .thenReturn(response);
        //ACT
        var resultado = combustivelService.alterarStatus(ID_VALIDO, request);
        //Assert
        assertThat(resultado)
                .isNotNull();

        assertThat(resultado.ativo()).isFalse();
        assertThat(entity.isAtivo()).isFalse();

        verify(combustivelRepository).findById(ID_VALIDO);
        verify(combustivelMapper).toResponse(entity);

        verifyNoMoreInteractions(combustivelRepository);
        verifyNoMoreInteractions(combustivelMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar o status")
    void deveLancarExcecaoAoAlterarStatusCombustivel() {
        //Arrange
        var entity = criarCombustivelEntity();
        var request = new StatusRequest(true);

        when(combustivelRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var exception = assertThrows(BusinessException.class,
                () -> combustivelService.alterarStatus(ID_VALIDO, request));

        //Assert
        assertBusinessResponseError(exception, COMBUSTIVEL);

        assertThat(entity.isAtivo()).isTrue();

        verify(combustivelRepository).findById(ID_VALIDO);

        verifyNoMoreInteractions(combustivelRepository);

        verifyNoInteractions(combustivelMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção do combustivel nao encontrada ao alterar status")
    void deveLancarExcecaoQuandoCombustivelNaoEncontradaAoAlterarStatus() {
        //Arrange
        var request = new StatusRequest(true);
        //Assert
        var exception = assertThrows(NotFoundException.class,
                () -> combustivelService.alterarStatus(ID_INVALIDO, request));

        assertNotFoundResponseError(exception, COMBUSTIVEL, ID_INVALIDO);
        verify(combustivelRepository).findById(ID_INVALIDO);

        verifyNoMoreInteractions(combustivelRepository);

        verifyNoInteractions(combustivelMapper);
    }

    @Test
    @DisplayName("Deve buscar a entidade do combustivel por Id")
    void deveBuscarAEntidadeCombustivel() {
        //Arrange
        var entity = criarCombustivelEntity();

        when(combustivelRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var resultado = combustivelService.buscaCombustivel(ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        Combustivel::getId,
                        Combustivel::getNome,
                        Combustivel::isAtivo)
                .containsExactly(
                        1L,
                        "Gasolina",
                        true
                );
        verify(combustivelRepository).findById(ID_VALIDO);
        verifyNoMoreInteractions(combustivelRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar entidade do combustivel por ID")
    void deveLancarExcecaoAoBuscarEntidadeCombustivel() {
        //Arrange
        when(combustivelRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //ACT
        var excecao = assertThrows(NotFoundException.class,
                () -> combustivelService.buscaCombustivel(ID_INVALIDO));
        //Assert

        assertNotFoundResponseError(excecao, COMBUSTIVEL, ID_INVALIDO);

        verify(combustivelRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(combustivelRepository);
    }
}
