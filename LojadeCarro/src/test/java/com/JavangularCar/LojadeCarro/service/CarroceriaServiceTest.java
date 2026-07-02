package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.response.CarroceriaResponse;
import com.JavangularCar.LojadeCarro.exception.CarroceriaException;
import com.JavangularCar.LojadeCarro.factory.carroceria.CarroceriaEntityFactory;
import com.JavangularCar.LojadeCarro.factory.carroceria.CarroceriaRequestFactory;
import com.JavangularCar.LojadeCarro.factory.carroceria.CarroceriaResponseFactory;
import com.JavangularCar.LojadeCarro.mapper.CarroceriaMapper;
import com.JavangularCar.LojadeCarro.repository.CarroceriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarroceriaServiceTest {

    private static final Long ID_CARROCERIA = 1L;
    private static final Long ID_CARROCERIA_EXCECAO = 99L;
    private static final String CARROCERIA_NAO_ENCONTRADA_COM_O_ID = "Carroceria não encontrada com o id: ";
    @Mock
    private CarroceriaMapper carroceriaMapper;

    @Mock
    private CarroceriaRepository carroceriaRepository;

    @InjectMocks
    private CarroceriaService carroceriaService;

    @Test
    @DisplayName("Deve validar a criação da carroceria")
    void deveCriarCarroceria() {
        // Arrange
        var request = CarroceriaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        var entity = CarroceriaEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        var response = CarroceriaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(carroceriaMapper.toEntity(request))
                .thenReturn(entity);
        when(carroceriaRepository.save(entity))
                .thenReturn(entity);
        when(carroceriaMapper.toResponse(entity))
                .thenReturn(response);

        // Act
        var resultado = carroceriaService.createCarroceria(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado)
                .extracting(
                        CarroceriaResponse::id,
                        CarroceriaResponse::nome
                )
                .containsExactly(
                        1L,
                        "Hatch"
                );

        verify(carroceriaMapper).toEntity(request);
        verify(carroceriaRepository).save(entity);
        verify(carroceriaMapper).toResponse(entity);
    }

    @Test
    @DisplayName("Deve listar todas as carrocerias")
    void deveListarCarrocerias() {
        //Arrange
        var hatch = CarroceriaEntityFactory
                .criarEntity()
                .comId(1L)
                .comNome("Hatch")
                .build();
        var suv = CarroceriaEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("SUV")
                .build();

        var listaDeCarrocerias = List.of(hatch, suv);

        var hatchResponse = CarroceriaResponseFactory
                .criarResponse()
                .comId(1L)
                .comNome("Hatch")
                .build();
        var suvResponse = CarroceriaResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("SUV")
                .build();

        when(carroceriaRepository.findAll())
                .thenReturn(listaDeCarrocerias);

        when(carroceriaMapper.toResponse(hatch))
                .thenReturn(hatchResponse);

        when(carroceriaMapper.toResponse(suv))
                .thenReturn(suvResponse);

        //ACT
        var resultado = carroceriaService.listarCarroceria();

        //Assert
        assertThat(resultado).isNotEmpty();
        assertThat(resultado)
                .hasSize(2)
                .extracting(CarroceriaResponse::nome)
                .containsExactly("Hatch", "SUV");

        verify(carroceriaRepository).findAll();
        verify(carroceriaMapper).toResponse(hatch);
        verify(carroceriaMapper).toResponse(suv);
    }

    @Test
    @DisplayName("Deve validar a busca de uma carroceria por ID")
    void deveBuscarCarroceriaPorId() {
        // Arrange
        Long id = ID_CARROCERIA;

        var entity = CarroceriaEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        var response = CarroceriaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        when(carroceriaRepository.findById(id))
                .thenReturn(Optional.of(entity));
        when(carroceriaMapper.toResponse(entity))
                .thenReturn(response);

        // Act
        var resultado = carroceriaService.findCarroceriaById(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado)
                .extracting(
                        CarroceriaResponse::id,
                        CarroceriaResponse::nome
                )
                .containsExactly(
                        1L,
                        "Hatch"
                );

        verify(carroceriaRepository).findById(id);
        verify(carroceriaMapper).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar uma exceção na busca por uma carroceria")
    void deveLancarExcecaoAoBuscarCarroceriaPorId() {
        // Arrange
        Long id = ID_CARROCERIA_EXCECAO;

        when(carroceriaRepository.findById(id))
                .thenReturn(Optional.empty());

        // Assert
        var exception = assertThrows(
                CarroceriaException.class,
                () -> carroceriaService.findCarroceriaById(id)
        );

        assertThat(exception)
                .hasMessage("Carroceria não encontrada com o id: " + id);

        verify(carroceriaRepository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar uma carroceria pelo ID")
    void deveAtualizarCarroceriaPorId() {
        //Arrange
        Long id = ID_CARROCERIA;
        var request = CarroceriaRequestFactory
                .criarRequest()
                .comNome("Sedan")
                .build();
        var entity = CarroceriaEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var response = CarroceriaResponseFactory
                .criarResponse()
                .comId(id)
                .comNome("Sedan")
                .build();

        when(carroceriaRepository.findById(id))
                .thenReturn(Optional.of(entity));

        when(carroceriaRepository.save(entity))
                .thenReturn(entity);
        when(carroceriaMapper.toResponse(entity))
                .thenReturn(response);

        // ACT
        var resultado = carroceriaService.updateCarroceria(request, id);

        //Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado)
                .extracting(
                        CarroceriaResponse::id,
                        CarroceriaResponse::nome
                )
                .containsExactly(
                        ID_CARROCERIA,
                        "Sedan"
                );
        assertThat("Sedan").isEqualTo(request.nome());
        assertThat(1L).isEqualTo(resultado.id());

        verify(carroceriaRepository).findById(id);
        verify(carroceriaRepository).save(entity);
        verify(carroceriaMapper).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar uma exceção durante a atualização de uma carroceria")
    void deveLancarExcecaoAoAtualizarCarroceriaPorId() {
        //Arrange
        Long id = ID_CARROCERIA_EXCECAO;
        var request = CarroceriaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        when(carroceriaRepository.findById(id))
                .thenReturn(Optional.empty());

        //Assert
        var exception = assertThrows(CarroceriaException.class, () -> carroceriaService.updateCarroceria(request, id));
        assertThat(exception)
                .hasMessage(CARROCERIA_NAO_ENCONTRADA_COM_O_ID + id);

        verify(carroceriaRepository).findById(id);
    }

    @Test
    @DisplayName("Deve deletar uma carroceria por id")
    void deveDeletarCarroceriaPorId() {
        //Arrange
        Long id = ID_CARROCERIA;
        var entity = CarroceriaEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        when(carroceriaRepository.findById(id))
                .thenReturn(Optional.of(entity));

        //Act
        carroceriaService.deleteCarroceria(id);

        //Assert
        verify(carroceriaRepository).findById(id);
        verify(carroceriaRepository).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao deletar uma carroceria")
    void deveLancarExcecaoAoDeletarCarroceria() {
        //Arrange
        Long id = ID_CARROCERIA_EXCECAO;
        when(carroceriaRepository.findById(id))
                .thenReturn(Optional.empty());

        //Assert
        var exception = assertThrows(CarroceriaException.class, () -> carroceriaService.deleteCarroceria(id));

        assertThat(exception)
                .hasMessage(CARROCERIA_NAO_ENCONTRADA_COM_O_ID + id);

        verify(carroceriaRepository).findById(id);

    }
}
