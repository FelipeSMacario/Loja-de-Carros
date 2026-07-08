package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.response.CoresResponse;
import com.JavangularCar.LojadeCarro.exception.CoresException;
import com.JavangularCar.LojadeCarro.factory.cores.CoresEntityFactory;
import com.JavangularCar.LojadeCarro.factory.cores.CoresRequestFactory;
import com.JavangularCar.LojadeCarro.factory.cores.CoresResponseFactory;
import com.JavangularCar.LojadeCarro.mapper.CoresMapper;
import com.JavangularCar.LojadeCarro.repository.CoresRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.JavangularCar.LojadeCarro.support.ErrorMessages.CORES;
import static com.JavangularCar.LojadeCarro.support.ErrorMessages.ID_NOT_FOUND;
import static com.JavangularCar.LojadeCarro.support.TestConstants.ID_INVALIDO;
import static com.JavangularCar.LojadeCarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoresServiceTest {
    @Mock
    private CoresRepository coresRepository;
    @Mock
    private CoresMapper coresMapper;
    @InjectMocks
    private CoresService coresService;

    @Test
    @DisplayName("Deve cadastrar um cor")
    void deveCadastrarCor() {
        //Arrange
        var request = CoresRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        var entity = CoresEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        var response = CoresResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(coresMapper.toEntity(request))
                .thenReturn(entity);

        when(coresRepository.save(entity))
                .thenReturn(entity);

        when(coresMapper.toResponse(entity))
                .thenReturn(response);

        //Act
        var resultado = coresService.createCores(request);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(CoresResponse::id,
                        CoresResponse::nome)
                .containsExactly(ID_VALIDO, "Branco");

        verify(coresMapper).toEntity(request);
        verify(coresMapper).toResponse(entity);
        verify(coresRepository).save(entity);
    }

    @Test
    @DisplayName("Deve listar as cores")
    void deveListarAsCores() {
        //Arrange
        var branco = CoresEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        var vermelho = CoresEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Vermelho")
                .build();

        var brancoResponse = CoresResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        var vermelhoResponse = CoresResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Vermelho")
                .build();

        var entity = List.of(branco, vermelho);

        when(coresRepository.findAll())
                .thenReturn(entity);

        when(coresMapper.toResponse(branco))
                .thenReturn(brancoResponse);

        when(coresMapper.toResponse(vermelho))
                .thenReturn(vermelhoResponse);
        //Act
        var resultado = coresService.listarCores();

        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(CoresResponse::id, CoresResponse::nome)
                .containsExactly(tuple(ID_VALIDO, "Branco"), tuple(2L, "Vermelho"));

        verify(coresRepository).findAll();
        verify(coresMapper).toResponse(branco);
        verify(coresMapper).toResponse(vermelho);

    }

    @Test
    @DisplayName("Deve buscar uma cor por ID")
    void deveBuscarCorPorId() {
        //Arrange
        var entity = CoresEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var response = CoresResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        when(coresRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(coresMapper.toResponse(entity))
                .thenReturn(response);
        //Act

        var resultado = coresService.findCoresById(ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        CoresResponse::id,
                        CoresResponse::nome
                ).containsExactly(ID_VALIDO, "Branco");

        verify(coresMapper).toResponse(entity);
        verify(coresRepository).findById(ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar a cor por ID")
    void deveLancarExcecaoAoBuscarCor() {
        //Arrange
        when(coresRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var exception = assertThrows(CoresException.class,
                () -> coresService.findCoresById(ID_INVALIDO));
        //Assert
        assertThat(exception)
                .hasMessage(String.format(ID_NOT_FOUND, CORES, ID_INVALIDO));

        verify(coresRepository).findById(ID_INVALIDO);
    }

    @Test
    @DisplayName("Deve atualizar uma cor")
    void deveAtualizarCor() {
        //Arrange

        var request = CoresRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        var entity = CoresEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var response = CoresResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(coresRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(coresRepository.save(entity))
                .thenReturn(entity);

        when(coresMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = coresService.updateCores(request, ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        CoresResponse::id,
                        CoresResponse::nome
                ).containsExactly(ID_VALIDO, "Branco");
        verify(coresMapper).toResponse(entity);
        verify(coresRepository).findById(ID_VALIDO);
        verify(coresRepository).save(entity);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao atualizar a cor")
    void deveLancarExcecaoAoAtualizarCor() {
        //Arrange

        var request = CoresRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        when(coresRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());

        //Assert
        var exception = assertThrows(CoresException.class,
                () -> coresService.updateCores(request, ID_INVALIDO));

        assertThat(exception)
                .hasMessage(String.format(ID_NOT_FOUND, CORES, ID_INVALIDO));
        verify(coresRepository).findById(ID_INVALIDO);
    }

    @Test
    @DisplayName("Deve deletar uma cor")
    void deveDeletarCor() {
        //Arrange
        var entity = CoresEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        when(coresRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //Act
        coresService.deleteCores(ID_VALIDO);
        //Assert
        verify(coresRepository).findById(ID_VALIDO);
        verify(coresRepository).deleteById(ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao deletar uma cor")
    void deveLancarExcecaoAoDeletarCor() {
        //Arrange
        when(coresRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Assert
        var exception = assertThrows(CoresException.class,
                () -> coresService.deleteCores(ID_INVALIDO));
        assertThat(exception)
                .hasMessage(String.format(ID_NOT_FOUND, CORES, ID_INVALIDO));

        verify(coresRepository).findById(ID_INVALIDO);
        verify(coresRepository, never())
                .deleteById(anyLong());
    }

}
