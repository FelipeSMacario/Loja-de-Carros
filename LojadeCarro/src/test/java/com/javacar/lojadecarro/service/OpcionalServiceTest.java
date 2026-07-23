package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.opcional.OpcionalEntityFactory;
import com.javacar.lojadecarro.factory.opcional.OpcionalResponseFactory;
import com.javacar.lojadecarro.mapper.OpcionalMapper;
import com.javacar.lojadecarro.repository.OpcionalRepository;
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

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;
import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpcionalServiceTest {

    @Mock
    private OpcionalRepository opcionalRepository;
    @Mock
    private OpcionalMapper opcionalMapper;
    @InjectMocks
    private OpcionalService opcionalService;
    @Spy
    private EntityValidation entityValidation;

    @Test
    @DisplayName("Deve cadastrar um opcional")
    void deveCadastrarUmOpcional() {
        //Arrange
        var request = criarOpcionalRequest();
        var entity = criarOpcionalEntity();
        var response = criarOpcionalResponse();

        when(opcionalMapper.toEntity(request))
                .thenReturn(entity);

        when(opcionalRepository.save(entity))
                .thenReturn(entity);

        when(opcionalMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = opcionalService.criar(request);
        //Assert
        assertOpcionalResponse(resultado);

        verify(opcionalMapper).toEntity(request);
        verify(opcionalRepository).save(entity);
        verify(opcionalMapper).toResponse(entity);

        verifyNoMoreInteractions(
                opcionalMapper,
                opcionalRepository
        );
    }


    @Test
    @DisplayName("Deve listar os opcionais ativos")
    void deveListarOpcionaisAtivos() {
        //Arrange
        var entity1 = criarOpcionalEntity();
        var entity2 = OpcionalEntityFactory
                .criarEntity()
                .comNome("Banco de couro")
                .comId(2L)
                .comAtivo(true)
                .build();
        var entity = List.of(entity1, entity2);

        var response1 = criarOpcionalResponse();

        var response2 = OpcionalResponseFactory
                .criarResponse()
                .comNome("Banco de couro")
                .comId(2L)
                .comAtivo(true)
                .build();

        when(opcionalRepository.findByAtivo(true))
                .thenReturn(entity);

        when(opcionalMapper.toResponse(entity1))
                .thenReturn(response1);
        when(opcionalMapper.toResponse(entity2))
                .thenReturn(response2);
        //Act
        var resultado = opcionalService.listar(StatusFiltro.ATIVAS);
        //Assert

        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        OpcionalResponse::id,
                        OpcionalResponse::nome,
                        OpcionalResponse::ativo
                )
                .containsExactly(
                        tuple(1L, "Freio ABS", true),
                        tuple(2L, "Banco de couro", true)
                );

        verify(opcionalRepository, never()).findAll();
        verify(opcionalRepository).findByAtivo(true);
        verify(opcionalMapper).toResponse(entity1);
        verify(opcionalMapper).toResponse(entity2);

        verifyNoMoreInteractions(opcionalRepository, opcionalMapper);

    }

    @Test
    @DisplayName("Deve listar os opcionais inativos")
    void deveListarOpcionaisInativos() {
        //Arrange
        var entity1 = criarOpcionalEntity();
        entity1.setAtivo(false);
        var entity2 = OpcionalEntityFactory
                .criarEntity()
                .comNome("Banco de couro")
                .comId(2L)
                .comAtivo(false)
                .build();
        var entity = List.of(entity1, entity2);

        var response1 = OpcionalResponseFactory
                .criarResponse()
                .comNome("Freio ABS")
                .comId(1L)
                .comAtivo(false)
                .build();

        var response2 = OpcionalResponseFactory
                .criarResponse()
                .comNome("Banco de couro")
                .comId(2L)
                .comAtivo(false)
                .build();

        when(opcionalRepository.findByAtivo(false))
                .thenReturn(entity);

        when(opcionalMapper.toResponse(entity1))
                .thenReturn(response1);
        when(opcionalMapper.toResponse(entity2))
                .thenReturn(response2);
        //Act
        var resultado = opcionalService.listar(StatusFiltro.INATIVAS);
        //Assert

        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        OpcionalResponse::id,
                        OpcionalResponse::nome,
                        OpcionalResponse::ativo
                )
                .containsExactly(
                        tuple(1L, "Freio ABS", false),
                        tuple(2L, "Banco de couro", false)
                );

        verify(opcionalRepository, never()).findAll();
        verify(opcionalRepository).findByAtivo(false);
        verify(opcionalMapper).toResponse(entity1);
        verify(opcionalMapper).toResponse(entity2);

        verifyNoMoreInteractions(opcionalRepository, opcionalMapper);
    }

    @Test
    @DisplayName("Deve listar todos os opcionais")
    void deveListarTodosOpcionais() {
        //Arrange
        var entity1 = criarOpcionalEntity();
        var entity2 = OpcionalEntityFactory
                .criarEntity()
                .comNome("Banco de couro")
                .comId(2L)
                .comAtivo(false)
                .build();
        var entity = List.of(entity1, entity2);

        var response1 = criarOpcionalResponse();

        var response2 = OpcionalResponseFactory
                .criarResponse()
                .comNome("Banco de couro")
                .comId(2L)
                .comAtivo(false)
                .build();

        when(opcionalRepository.findAll())
                .thenReturn(entity);

        when(opcionalMapper.toResponse(entity1))
                .thenReturn(response1);
        when(opcionalMapper.toResponse(entity2))
                .thenReturn(response2);
        //Act
        var resultado = opcionalService.listar(StatusFiltro.TODAS);
        //Assert

        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        OpcionalResponse::id,
                        OpcionalResponse::nome,
                        OpcionalResponse::ativo
                )
                .containsExactly(
                        tuple(1L, "Freio ABS", true),
                        tuple(2L, "Banco de couro", false)
                );

        verify(opcionalRepository).findAll();
        verify(opcionalRepository, never()).findByAtivo(anyBoolean());
        verify(opcionalMapper).toResponse(entity1);
        verify(opcionalMapper).toResponse(entity2);

        verifyNoMoreInteractions(opcionalRepository, opcionalMapper);
    }

    @Test
    @DisplayName("Deve filtrar um opcional por ID")
    void deveFiltrarOpcionalPorId() {
        //Arrange
        var entity = criarOpcionalEntity();
        var response = criarOpcionalResponse();

        when(opcionalRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        when(opcionalMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = opcionalService.buscarPorId(ID_VALIDO);
        //Assert
        assertOpcionalResponse(resultado);

        verify(opcionalRepository).findById(ID_VALIDO);
        verify(opcionalMapper).toResponse(entity);

        verifyNoMoreInteractions(
                opcionalRepository,
                opcionalMapper
        );
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao filtrar um opcional por ID")
    void deveLancarEcecaoFiltrarOpcionalPorId() {
        //Arrange
        when(opcionalRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var exception = assertThrows(NotFoundException.class,
                () -> opcionalService.buscarPorId(ID_INVALIDO));
        //Assert
        assertNotFoundResponseError(exception, OPCIONAL, ID_INVALIDO);

        verify(opcionalRepository).findById(ID_INVALIDO);

        verifyNoMoreInteractions(opcionalRepository);

        verifyNoInteractions(opcionalMapper);
    }

    @Test
    @DisplayName("Deve atualizar um opcional")
    void deveAtualizarUmOpcional() {
        //Arrange
        var request = criarOpcionalRequest();
        var entity = criarOpcionalEntity();

        var response = criarOpcionalResponse();

        when(opcionalRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        doNothing().when(opcionalMapper)
                .toUpdate(request, entity);

        when(opcionalMapper.toResponse(entity))
                .thenReturn(response);

        //Act
        var resultado = opcionalService.atualizar(request, ID_VALIDO);
        //Assert
        assertOpcionalResponse(resultado);

        verify(opcionalRepository).findById(ID_VALIDO);
        verify(opcionalMapper).toResponse(entity);

        verifyNoMoreInteractions(
                opcionalMapper,
                opcionalRepository
        );
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao atualizar um opcional")
    void deveLancarExcecaoAtualizarUmOpcional() {
        //Arrange
        var request = criarOpcionalRequest();

        when(opcionalRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var excecao = assertThrows(NotFoundException.class,
                () -> opcionalService.atualizar(request, ID_INVALIDO));
        //Assert
        assertNotFoundResponseError(excecao, OPCIONAL, ID_INVALIDO);


        verify(opcionalRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(opcionalRepository);

        verifyNoInteractions(opcionalMapper);
    }

    @Test
    @DisplayName("Deve alterar o status do opcional")
    void deveAlterarStatusDoOpcional() {
        //Arrange
        var entity = criarOpcionalEntity();
        var request = new StatusRequest(false);
        var response = OpcionalResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(false)
                .build();

        when(opcionalRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(opcionalMapper.toResponse(entity))
                .thenReturn(response);
        //ACT
        var resultado = opcionalService.alterarStatus(ID_VALIDO, request);
        //Assert
        assertThat(resultado)
                .isNotNull();

        assertThat(resultado.ativo()).isFalse();
        assertThat(entity.isAtivo()).isFalse();

        verify(opcionalRepository).findById(ID_VALIDO);
        verify(opcionalMapper).toResponse(entity);

        verifyNoMoreInteractions(opcionalRepository);
        verifyNoMoreInteractions(opcionalMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar o status")
    void deveLancarExcecaoAoAlterarStatusCor() {
        //Arrange
        var entity = criarOpcionalEntity();
        var request = new StatusRequest(true);

        when(opcionalRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var exception = assertThrows(BusinessException.class,
                () -> opcionalService.alterarStatus(ID_VALIDO, request));

        //Assert
        assertBusinessResponseError(exception, OPCIONAL);

        assertThat(entity.isAtivo()).isTrue();

        verify(opcionalRepository).findById(ID_VALIDO);

        verifyNoMoreInteractions(opcionalRepository);

        verifyNoInteractions(opcionalMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção do opcional nao encontrada ao alterar status")
    void deveLancarExcecaoQuandoOpcionalNaoEncontradaAoAlterarStatus() {
        //Arrange
        var request = new StatusRequest(true);
        //Assert
        var exception = assertThrows(NotFoundException.class,
                () -> opcionalService.alterarStatus(ID_INVALIDO, request));

        assertNotFoundResponseError(exception, OPCIONAL, ID_INVALIDO);
        verify(opcionalRepository).findById(ID_INVALIDO);

        verifyNoMoreInteractions(opcionalRepository);

        verifyNoInteractions(opcionalMapper);
    }

    @Test
    @DisplayName("Deve listar opcionais por ids")
    void deveListarOpcionaisPorIds() {
        //Arrange
        List<Long> ids = List.of(ID_VALIDO, 2L, 3L);
        var entity = criarOpcionalEntity();
        var entity2 = OpcionalEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Banco de couro")
                .comAtivo(true)
                .build();
        var entity3 = OpcionalEntityFactory
                .criarEntity()
                .comId(3L)
                .comNome("Automatico")
                .comAtivo(true)
                .build();

        var listEntity = List.of(entity, entity2, entity3);

        when(opcionalRepository.findAllByIdIn(ids))
                .thenReturn(listEntity);
        //ACT
        var resultado = opcionalService.buscarOpcionais(ids);
        //Assert

        assertThat(resultado)
                .isNotNull()
                .hasSize(3)
                .extracting(
                        Opcional::getId,
                        Opcional::getNome,
                        Opcional::isAtivo
                ).containsExactly(
                        tuple(1L, "Freio ABS", true),
                        tuple(2L, "Banco de couro", true),
                        tuple(3L, "Automatico", true)
                );

        verify(opcionalRepository).findAllByIdIn(ids);
        verifyNoMoreInteractions(opcionalRepository);
    }

    @Test
    @DisplayName("Deve buscar a entidade do opcinal por Id")
    void deveBuscarAEntidadeOpcional() {
        //Arrange
        var entity = criarOpcionalEntity();

        when(opcionalRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //ACT
        var resultado = opcionalService.buscaOpcional(ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        Opcional::getId,
                        Opcional::getNome,
                        Opcional::isAtivo)
                .containsExactly(
                        1L,
                        "Freio ABS",
                        true
                );
        verify(opcionalRepository).findById(ID_VALIDO);
        verifyNoMoreInteractions(opcionalRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar entidade do opcional por ID")
    void deveLancarExcecaoAoBuscarEntidadeOpcional() {
        //Arrange
        when(opcionalRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //ACT
        var excecao = assertThrows(NotFoundException.class,
                () -> opcionalService.buscaOpcional(ID_INVALIDO));
        //Assert

        assertNotFoundResponseError(excecao, OPCIONAL, ID_INVALIDO);

        verify(opcionalRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(opcionalRepository);
    }

}
