package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.entity.Cor;
import com.javacar.lojadecarro.enums.Entidade;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.cor.CorEntityFactory;
import com.javacar.lojadecarro.factory.cor.CorResponseFactory;
import com.javacar.lojadecarro.mapper.CorMapper;
import com.javacar.lojadecarro.repository.CoresRepository;
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

import static com.javacar.lojadecarro.enums.Entidade.COR;
import static com.javacar.lojadecarro.factory.helper.CorHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorServiceTest {
    @Mock
    private CoresRepository corRepository;
    @Mock
    private CorMapper corMapper;
    @Spy
    private EntityValidation entityValidation;
    @InjectMocks
    private CoresService corService;

    @Test
    @DisplayName("Deve cadastrar uma cor")
    void deveCadastrarCor() {
        //Arrange
        var request = criarCorRequest();

        var entity = criarCorEntity();

        var response = criarCorResponse();

        when(corMapper.toEntity(request))
                .thenReturn(entity);

        when(corRepository.save(entity))
                .thenReturn(entity);

        when(corMapper.toResponse(entity))
                .thenReturn(response);

        //Act
        var resultado = corService.criar(request);
        //Assert
        assertCorResponse(resultado);

        verify(corMapper).toEntity(request);
        verify(corMapper).toResponse(entity);
        verify(corRepository).save(entity);

        verifyNoMoreInteractions(corMapper);
        verifyNoMoreInteractions(corRepository);
    }

    @Test
    @DisplayName("Deve listar as cores ativas")
    void deveListarAsCoresAtivas() {
        //Arrange
        var branco = criarCorEntity();

        var vermelho = CorEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Vermelho")
                .comAtivo(true)
                .build();

        var brancoResponse = criarCorResponse();

        var vermelhoResponse = CorResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Vermelho")
                .comAtivo(true)
                .build();

        var entity = List.of(branco, vermelho);

        when(corRepository.findByAtivo(true))
                .thenReturn(entity);

        when(corMapper.toResponse(branco))
                .thenReturn(brancoResponse);

        when(corMapper.toResponse(vermelho))
                .thenReturn(vermelhoResponse);
        //Act
        var resultado = corService.listar(StatusFiltro.ATIVAS);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        CorResponse::id,
                        CorResponse::nome,
                        CorResponse::ativo)
                .containsExactly(
                        tuple(ID_VALIDO, "Branco", true),
                        tuple(2L, "Vermelho", true));

        verify(corRepository).findByAtivo(true);
        verify(corRepository, never()).findAll();
        verify(corMapper).toResponse(branco);
        verify(corMapper).toResponse(vermelho);

        verifyNoMoreInteractions(
                corRepository,
                corMapper);
    }

    @Test
    @DisplayName("Deve listar as cores inativas")
    void deveListarAsCoresInativas() {
        //Arrange
        var branco = criarCorEntity();
        branco.setAtivo(false);

        var vermelho = CorEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Vermelho")
                .comAtivo(false)
                .build();

        var brancoResponse = CorResponseFactory
                .criarResponse()
                .comId(1L)
                .comNome("Branco")
                .comAtivo(false)
                .build();

        var vermelhoResponse = CorResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Vermelho")
                .comAtivo(false)
                .build();

        var entity = List.of(branco, vermelho);

        when(corRepository.findByAtivo(false))
                .thenReturn(entity);

        when(corMapper.toResponse(branco))
                .thenReturn(brancoResponse);

        when(corMapper.toResponse(vermelho))
                .thenReturn(vermelhoResponse);
        //Act
        var resultado = corService.listar(StatusFiltro.INATIVAS);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        CorResponse::id,
                        CorResponse::nome,
                        CorResponse::ativo)
                .containsExactly(
                        tuple(ID_VALIDO, "Branco", false),
                        tuple(2L, "Vermelho", false));

        verify(corRepository).findByAtivo(false);
        verify(corRepository, never()).findAll();
        verify(corMapper).toResponse(branco);
        verify(corMapper).toResponse(vermelho);

        verifyNoMoreInteractions(
                corRepository,
                corMapper
        );

    }

    @Test
    @DisplayName("Deve listar todas as cores")
    void deveListarAsCores() {
        //Arrange
        var branco = criarCorEntity();

        var vermelho = CorEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Vermelho")
                .comAtivo(false)
                .build();

        var brancoResponse = criarCorResponse();

        var vermelhoResponse = CorResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Vermelho")
                .comAtivo(false)
                .build();

        var entity = List.of(branco, vermelho);

        when(corRepository.findAll())
                .thenReturn(entity);

        when(corMapper.toResponse(branco))
                .thenReturn(brancoResponse);

        when(corMapper.toResponse(vermelho))
                .thenReturn(vermelhoResponse);
        //Act
        var resultado = corService.listar(StatusFiltro.TODAS);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        CorResponse::id,
                        CorResponse::nome,
                        CorResponse::ativo)
                .containsExactly(
                        tuple(ID_VALIDO, "Branco", true),
                        tuple(2L, "Vermelho", false));

        verify(corRepository, never()).findByAtivo(anyBoolean());
        verify(corRepository).findAll();
        verify(corMapper).toResponse(branco);
        verify(corMapper).toResponse(vermelho);

        verifyNoMoreInteractions(
                corRepository,
                corMapper
        );

    }

    @Test
    @DisplayName("Deve buscar uma cor por ID")
    void deveBuscarCorPorId() {
        //Arrange
        var entity = criarCorEntity();
        var response = criarCorResponse();

        when(corRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(corMapper.toResponse(entity))
                .thenReturn(response);
        //Act

        var resultado = corService.buscarPorId(ID_VALIDO);
        //Assert
        assertCorResponse(resultado);

        verify(corMapper).toResponse(entity);
        verify(corRepository).findById(ID_VALIDO);

        verifyNoMoreInteractions(corMapper);
        verifyNoMoreInteractions(corRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar a cor por ID")
    void deveLancarExcecaoAoBuscarCor() {
        //Arrange
        when(corRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var exception = assertThrows(NotFoundException.class,
                () -> corService.buscarPorId(ID_INVALIDO));
        //Assert
        assertNotFoundResponseError(exception, Entidade.COR, ID_INVALIDO);

        verify(corRepository).findById(ID_INVALIDO);

        verifyNoMoreInteractions(corRepository);

        verifyNoInteractions(corMapper);
    }

    @Test
    @DisplayName("Deve atualizar uma cor")
    void deveAtualizarCor() {
        //Arrange

        var request = criarCorRequest();
        var entity = criarCorEntity();
        var response = criarCorResponse();

        when(corRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        doNothing()
                .when(corMapper)
                .toUpdate(request, entity);

        when(corMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = corService.atualizar(request, ID_VALIDO);
        //Assert
        assertCorResponse(resultado);

        verify(corMapper).toResponse(entity);
        verify(corRepository).findById(ID_VALIDO);

        verifyNoMoreInteractions(corMapper);
        verifyNoMoreInteractions(corRepository);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao atualizar a cor")
    void deveLancarExcecaoAoAtualizarCor() {
        //Arrange

        var request = criarCorRequest();

        when(corRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());

        //Assert
        var exception = assertThrows(NotFoundException.class,
                () -> corService.atualizar(request, ID_INVALIDO));

        assertNotFoundResponseError(exception, Entidade.COR, ID_INVALIDO);

        verify(corRepository).findById(ID_INVALIDO);

        verifyNoMoreInteractions(corRepository);

        verifyNoInteractions(corMapper);
    }

    @Test
    @DisplayName("Deve alterar o status da cor")
    void deveAlterarStatusDoCor() {
        //Arrange
        var entity = criarCorEntity();
        var request = new StatusRequest(false);
        var response = CorResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(false)
                .build();

        when(corRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(corMapper.toResponse(entity))
                .thenReturn(response);
        //ACT
        var resultado = corService.alterarStatus(ID_VALIDO, request);
        //Assert
        assertThat(resultado)
                .isNotNull();

        assertThat(resultado.ativo()).isFalse();
        assertThat(entity.isAtivo()).isFalse();

        verify(corRepository).findById(ID_VALIDO);
        verify(corMapper).toResponse(entity);

        verifyNoMoreInteractions(corRepository);
        verifyNoMoreInteractions(corMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar o status")
    void deveLancarExcecaoAoAlterarStatusCor() {
        //Arrange
        var entity = criarCorEntity();
        var request = new StatusRequest(true);

        when(corRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var exception = assertThrows(BusinessException.class,
                () -> corService.alterarStatus(ID_VALIDO, request));

        //Assert
        assertBusinessResponseError(exception, Entidade.COR);

        assertThat(entity.isAtivo()).isTrue();

        verify(corRepository).findById(ID_VALIDO);

        verifyNoMoreInteractions(corRepository);

        verifyNoInteractions(corMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção da cor nao encontrada ao alterar status")
    void deveLancarExcecaoQuandoCorNaoEncontradaAoAlterarStatus() {
        //Arrange
        var request = new StatusRequest(true);
        //Assert
        var exception = assertThrows(NotFoundException.class,
                () -> corService.alterarStatus(ID_INVALIDO, request));

        assertNotFoundResponseError(exception, Entidade.COR, ID_INVALIDO);
        verify(corRepository).findById(ID_INVALIDO);

        verifyNoMoreInteractions(corRepository);

        verifyNoInteractions(corMapper);
    }

    @Test
    @DisplayName("Deve buscar a entidade da cor por Id")
    void deveBuscarAEntidadeCor() {
        //Arrange
        var entity = criarCorEntity();

        when(corRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var resultado = corService.buscaCor(ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        Cor::getId,
                        Cor::getNome,
                        Cor::isAtivo)
                .containsExactly(
                        1L,
                        "Branco",
                        true
                );
        verify(corRepository).findById(ID_VALIDO);
        verifyNoMoreInteractions(corRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar entidade do cor por ID")
    void deveLancarExcecaoAoBuscarEntidadeCor() {
        //Arrange
        when(corRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //ACT
        var excecao = assertThrows(NotFoundException.class,
                () -> corService.buscaCor(ID_INVALIDO));
        //Assert

        assertNotFoundResponseError(excecao, COR, ID_INVALIDO);

        verify(corRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(corRepository);
    }

}
