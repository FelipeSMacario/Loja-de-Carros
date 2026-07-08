package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.exception.ModeloException;
import com.javacar.lojadecarro.factory.marca.MarcaEntityFactory;
import com.javacar.lojadecarro.factory.modelo.ModeloEntityFactory;
import com.javacar.lojadecarro.factory.modelo.ModeloRequestFactory;
import com.javacar.lojadecarro.factory.modelo.ModeloResponseFactory;
import com.javacar.lojadecarro.mapper.ModeloMapper;
import com.javacar.lojadecarro.repository.ModeloRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
import static com.javacar.lojadecarro.support.ErrorMessages.MODELO;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModeloServiceTest {

    @Mock
    private ModeloMapper modeloMapper;
    @Mock
    private MarcaService marcaService;
    @Mock
    private ModeloRepository modeloRepository;
    @InjectMocks
    private ModeloService modeloService;

    @Test
    @DisplayName("Deve cadastrar um modelo")
    void deveCadastrarModelo() {
        //Arrange
        var request = ModeloRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        var marcaEntity = MarcaEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var entity = ModeloEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var response = ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        when(modeloMapper.toEntity(request))
                .thenReturn(entity);

        when(marcaService.buscaMarca(request.idMarca()))
                .thenReturn(marcaEntity);

        when(modeloRepository.save(entity))
                .thenReturn(entity);

        when(modeloMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = modeloService.createModelo(request);
        //Assert
        assertThat(entity.getMarca())
                .isSameAs(marcaEntity);
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        ModeloResponse::id,
                        ModeloResponse::nome
                ).containsExactly(
                        ID_VALIDO,
                        "Onix"
                );
        assertThat(resultado.marcaResponse())
                .isNotNull()
                .extracting(
                        MarcaResponse::id,
                        MarcaResponse::nome,
                        MarcaResponse::url
                ).containsExactly(3L, "Chevrolet", "https://www.chevrolet.com");
        verify(modeloMapper).toEntity(request);
        verify(marcaService).buscaMarca(request.idMarca());
        verify(modeloRepository).save(entity);
        verify(modeloMapper).toResponse(entity);
    }

    @Test
    @DisplayName("Deve listar modelos")
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
                .build();

        when(modeloRepository.findAll())
                .thenReturn(entity);

        when(modeloMapper.toResponse(onixEntity))
                .thenReturn(onixResponse);
        when(modeloMapper.toResponse(celtaEntity))
                .thenReturn(celtaResponse);
        //Act
        var resultado = modeloService.listarModelo();
        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        ModeloResponse::id,
                        ModeloResponse::nome
                ).containsExactly(
                        tuple(ID_VALIDO, "Onix"),
                        tuple(2L, "Celta")
                );
        assertThat(resultado.getFirst().marcaResponse())
                .extracting(
                        MarcaResponse::id,
                        MarcaResponse::nome
                )
                .containsExactly(
                        3L,
                        "Chevrolet"
                );

        verify(modeloRepository).findAll();
        verify(modeloMapper, times(2))
                .toResponse(any(Modelo.class));
    }

    @Test
    @DisplayName("Deve buscar um modelo por ID")
    void deveBuscarModeloPorId() {
        //Arrange
        var entity = ModeloEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var response = ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(modeloRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        when(modeloMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = modeloService.findModeloById(ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        ModeloResponse::id,
                        ModeloResponse::nome)
                .containsExactly(ID_VALIDO, "Onix");
        assertThat(resultado.marcaResponse())
                .isNotNull()
                .extracting(
                        MarcaResponse::id,
                        MarcaResponse::nome
                ).containsExactly(3L, "Chevrolet");

        verify(modeloRepository).findById(ID_VALIDO);
        verify(modeloMapper).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar um modelo por ID")
    void deveBuscarModeloPorIdException() {
        //Arrange
        when(modeloRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var exception = assertThrows(ModeloException.class,
                () -> modeloService.findModeloById(ID_INVALIDO));
        //Assert
        assertThat(exception)
                .hasMessage(String.format(ID_NOT_FOUND, MODELO, ID_INVALIDO));

        verify(modeloRepository).findById(ID_INVALIDO);
    }

    @Test
    @DisplayName("Deve atualizar o modelo")
    void deveAtualizarModelo() {
        //Arrange
        var request = ModeloRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        var entity = ModeloEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        var marcaEntity = MarcaEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var response = ModeloResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(modeloRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(marcaService.buscaMarca(request.idMarca()))
                .thenReturn(marcaEntity);
        when(modeloRepository.save(entity)).thenReturn(entity);

        when(modeloMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = modeloService.updateModelo(request, ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        ModeloResponse::id,
                        ModeloResponse::nome)
                .containsExactly(ID_VALIDO, "Onix");

        assertThat(entity.getMarca())
                .isNotNull()
                .isSameAs(marcaEntity);

        verify(modeloRepository).findById(ID_VALIDO);
        verify(modeloRepository).save(entity);
        verify(marcaService).buscaMarca(request.idMarca());
        verify(modeloMapper).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar modelo")
    void deveAtualizarModeloException() {
        //Arrange
        var request = ModeloRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        when(modeloRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var exception = assertThrows(
                ModeloException.class,
                () -> modeloService.updateModelo(request, ID_INVALIDO));
        //Assert
        assertThat(exception)
                .hasMessage(String.format(ID_NOT_FOUND, MODELO, ID_INVALIDO));

        verify(modeloRepository).findById(ID_INVALIDO);
    }

    @Test
    @DisplayName("Deve deletar o modelo")
    void deveDeletarModelo() {
        //Arrange
        var entity = ModeloEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        when(modeloRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //Act
        modeloService.deleteModelo(ID_VALIDO);
        //Assert
        verify(modeloRepository).findById(ID_VALIDO);
        verify(modeloRepository).deleteById(ID_VALIDO);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao deletar")
    void deveDeletarModeloException() {
        //Arrange
        when(modeloRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var exception = assertThrows(ModeloException.class,
                () -> modeloService.deleteModelo(ID_INVALIDO));
        //Assert
        assertThat(exception)
                .hasMessage(String.format(ID_NOT_FOUND, MODELO, ID_INVALIDO));

        verify(modeloRepository).findById(ID_INVALIDO);
    }
}
