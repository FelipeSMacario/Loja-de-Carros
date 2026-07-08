package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.exception.CombustivelException;
import com.javacar.lojadecarro.factory.combustivel.CombustivelEntityFactory;
import com.javacar.lojadecarro.factory.combustivel.CombustivelRequestFactory;
import com.javacar.lojadecarro.factory.combustivel.CombustivelResponseFactory;
import com.javacar.lojadecarro.mapper.CombustivelMapper;
import com.javacar.lojadecarro.repository.CombustivelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.support.ErrorMessages.COMBUSTIVEL;
import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CombustivelServiceTest {

    private static final Long ID_COMBUSTIVEL = 1L;
    private static final Long ID_COMBUSTIVEL_EXCECAO = 99L;


    @Mock
    private CombustivelMapper combustivelMapper;

    @Mock
    private CombustivelRepository combustivelRepository;

    @InjectMocks
    private CombustivelService combustivelService;

    @Test
    @DisplayName("Valida criação do combustível")
    void validaCriaCombustivel() {
        //Arrange
        var request = CombustivelRequestFactory.criarRequest().comTodosOsCampos().build();

        var entity = CombustivelEntityFactory.criarEntity().comTodosOsCampos().build();

        var response = CombustivelResponseFactory.criarResponse().comTodosOsCampos().build();

        when(combustivelMapper.toEntity(request)).thenReturn(entity);

        when(combustivelRepository.save(entity)).thenReturn(entity);

        when(combustivelMapper.toResponse(entity)).thenReturn(response);

        //Act
        var resultado = combustivelService.createCombustivel(request);

        //Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).extracting(CombustivelResponse::id, CombustivelResponse::nome).containsExactly(ID_COMBUSTIVEL, "Gasolina");

        verify(combustivelMapper).toResponse(entity);
        verify(combustivelRepository).save(entity);
        verify(combustivelMapper).toEntity(request);
    }

    @Test
    @DisplayName("Deve listar todos os combustíveis")
    void listarTodosCombustivel() {
        //Arrange
        var gasolina = CombustivelEntityFactory.criarEntity().comTodosOsCampos().build();
        var eletrico = CombustivelEntityFactory.criarEntity().comNome("Eletrico").build();

        var request = List.of(gasolina, eletrico);

        var gasolinaResponse = CombustivelResponseFactory.criarResponse().comTodosOsCampos().build();
        var eletricoResponse = CombustivelResponseFactory.criarResponse().comId(2L).comNome("Eletrico").build();


        when(combustivelRepository.findAll()).thenReturn(request);

        when(combustivelMapper.toResponse(gasolina)).thenReturn(gasolinaResponse);

        when(combustivelMapper.toResponse(eletrico)).thenReturn(eletricoResponse);

        // Act
        var resultado = combustivelService.listarCombustivel();

        // Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(CombustivelResponse::nome, CombustivelResponse::id)
                .containsExactly(tuple("Gasolina", ID_COMBUSTIVEL), tuple("Eletrico", 2L));

        verify(combustivelRepository).findAll();
        verify(combustivelMapper).toResponse(eletrico);
        verify(combustivelMapper).toResponse(gasolina);
    }


    @Test
    @DisplayName("Deve buscar um combustível por ID")
    void deveBuscarCombustivelPorId() {
        //Arrange
        var entity = CombustivelEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var response = CombustivelResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(combustivelRepository
                .findById(ID_COMBUSTIVEL))
                .thenReturn(Optional.of(entity));

        when(combustivelMapper
                .toResponse(entity))
                .thenReturn(response);

        //Act
        var resultado = combustivelService
                .findCombustivelById(ID_COMBUSTIVEL);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        CombustivelResponse::id, CombustivelResponse::nome
                )
                .containsExactly(ID_COMBUSTIVEL, "Gasolina");

        verify(combustivelRepository)
                .findById(ID_COMBUSTIVEL);
        verify(combustivelMapper)
                .toResponse(entity);

    }

    @Test
    @DisplayName("Deve lançar uma exceção ao buscar um combustível")
    void deveLancarUmaExcecaoCombustivel() {
        //Arrange
        when(combustivelRepository.findById(ID_COMBUSTIVEL_EXCECAO))
                .thenReturn(Optional.empty());

        //Assert
        var exception = assertThrows(CombustivelException.class,
                () -> combustivelService.findCombustivelById(ID_COMBUSTIVEL_EXCECAO));

        assertThat(exception)
                .hasMessage(String.format(ID_NOT_FOUND, COMBUSTIVEL, ID_INVALIDO));

        verify(combustivelRepository).findById(ID_COMBUSTIVEL_EXCECAO);
    }

    @Test
    @DisplayName("Deve atualizar um combustível")
    void deveAtualizarCombustivel() {
        //Arrange
        var request = CombustivelRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        var entity = CombustivelEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var response = CombustivelResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(combustivelRepository.findById(ID_COMBUSTIVEL))
                .thenReturn(Optional.of(entity));
        when(combustivelRepository.save(entity))
                .thenReturn(entity);
        when(combustivelMapper.toResponse(entity))
                .thenReturn(response);

        //Act
        var resultado = combustivelService.updateCombustivel(request, ID_COMBUSTIVEL);

        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(CombustivelResponse::id, CombustivelResponse::nome)
                .containsExactly(ID_COMBUSTIVEL, "Gasolina");

        verify(combustivelRepository).findById(ID_COMBUSTIVEL);
        verify(combustivelMapper).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao atualizar o combustível")
    void deveLancarExcecaoCombustivel() {
        //Arrange
        var request = CombustivelRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        //Assert
        var exception = assertThrows(CombustivelException.class,
                () -> combustivelService.updateCombustivel(request, ID_COMBUSTIVEL_EXCECAO));
        assertThat(exception)
                .hasMessage(String.format(ID_NOT_FOUND, COMBUSTIVEL, ID_INVALIDO));

        verify(combustivelRepository).findById(ID_COMBUSTIVEL_EXCECAO);
    }

    @Test
    @DisplayName("Deve deletar um combustível")
    void deveDeletarCombustivel() {
        //Arrange
        var entity = CombustivelEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        when(combustivelRepository.findById(ID_COMBUSTIVEL))
                .thenReturn(Optional.of(entity));

        //Act
        combustivelService.deleteCombustivel(ID_COMBUSTIVEL);

        //Assert
        verify(combustivelRepository).deleteById(ID_COMBUSTIVEL);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao deletar um combustível")
    void deveLancarExcecaoDeletarCombustivel() {
        //Arrange
        when(combustivelRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Assert
        var exception = assertThrows(CombustivelException.class,
                () -> combustivelService.deleteCombustivel(ID_INVALIDO));

        assertThat(exception)
                .hasMessage(String.format(ID_NOT_FOUND, COMBUSTIVEL, ID_INVALIDO));

        verify(combustivelRepository).findById(ID_INVALIDO);
    }
}
