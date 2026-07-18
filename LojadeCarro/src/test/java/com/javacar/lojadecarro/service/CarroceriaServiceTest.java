//package com.javacar.lojadecarro.service;
//
//import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
//import com.javacar.lojadecarro.exception.CarroceriaException;
//import com.javacar.lojadecarro.factory.carroceria.CarroceriaEntityFactory;
//import com.javacar.lojadecarro.factory.carroceria.CarroceriaRequestFactory;
//import com.javacar.lojadecarro.factory.carroceria.CarroceriaResponseFactory;
//import com.javacar.lojadecarro.mapper.CarroceriaMapper;
//import com.javacar.lojadecarro.repository.CarroceriaRepository;
//import com.javacar.lojadecarro.support.TestConstants;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static com.javacar.lojadecarro.factory.helper.CarroceriaHelper.*;
//import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
//import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class CarroceriaServiceTest {
//
//    @Mock
//    private CarroceriaMapper carroceriaMapper;
//
//    @Mock
//    private CarroceriaRepository carroceriaRepository;
//
//    @InjectMocks
//    private CarroceriaService carroceriaService;
//
//    @Test
//    @DisplayName("Deve validar a criação da carroceria")
//    void deveCriarCarroceria() {
//        // Arrange
//        var request = criarCarroceriaRequest();
//        var entity = criarCarroceriaEntity();
//
//        var response = criarCarroceriaResponse();
//
//        when(carroceriaMapper.toEntity(request))
//                .thenReturn(entity);
//        when(carroceriaRepository.save(entity))
//                .thenReturn(entity);
//        when(carroceriaMapper.toResponse(entity))
//                .thenReturn(response);
//
//        // Act
//        var resultado = carroceriaService.createCarroceria(request);
//
//        // Assert
//        assertCarroceriaResponse(resultado);
//
//        verify(carroceriaMapper).toEntity(request);
//        verify(carroceriaRepository).save(entity);
//        verify(carroceriaMapper).toResponse(entity);
//    }
//
//
//    @Test
//    @DisplayName("Deve listar todas as carrocerias")
//    void deveListarCarrocerias() {
//        //Arrange
//        var hatch = CarroceriaEntityFactory
//                .criarEntity()
//                .comId(1L)
//                .comNome("Hatch")
//                .build();
//        var suv = CarroceriaEntityFactory
//                .criarEntity()
//                .comId(2L)
//                .comNome("SUV")
//                .build();
//
//        var listaDeCarrocerias = List.of(hatch, suv);
//
//        var hatchResponse = CarroceriaResponseFactory
//                .criarResponse()
//                .comId(1L)
//                .comNome("Hatch")
//                .build();
//        var suvResponse = CarroceriaResponseFactory
//                .criarResponse()
//                .comId(2L)
//                .comNome("SUV")
//                .build();
//
//        when(carroceriaRepository.findAll())
//                .thenReturn(listaDeCarrocerias);
//
//        when(carroceriaMapper.toResponse(hatch))
//                .thenReturn(hatchResponse);
//
//        when(carroceriaMapper.toResponse(suv))
//                .thenReturn(suvResponse);
//
//        //ACT
//        var resultado = carroceriaService.listarCarroceria();
//
//        //Assert
//        assertThat(resultado).isNotEmpty();
//        assertThat(resultado)
//                .hasSize(2)
//                .extracting(CarroceriaResponse::nome)
//                .containsExactly("Hatch", "SUV");
//
//        verify(carroceriaRepository).findAll();
//        verify(carroceriaMapper).toResponse(hatch);
//        verify(carroceriaMapper).toResponse(suv);
//    }
//
//    @Test
//    @DisplayName("Deve validar a busca de uma carroceria por ID")
//    void deveBuscarCarroceriaPorId() {
//        // Arrange
//
//        var entity = criarCarroceriaEntity();
//
//        var response = criarCarroceriaResponse();
//
//        when(carroceriaRepository.findById(ID_VALIDO))
//                .thenReturn(Optional.of(entity));
//        when(carroceriaMapper.toResponse(entity))
//                .thenReturn(response);
//
//        // Act
//        var resultado = carroceriaService.findCarroceriaById(ID_VALIDO);
//
//        // Assert
//        assertCarroceriaResponse(resultado);
//
//        verify(carroceriaRepository).findById(ID_VALIDO);
//        verify(carroceriaMapper).toResponse(entity);
//    }
//
//    @Test
//    @DisplayName("Deve lançar uma exceção na busca por uma carroceria")
//    void deveLancarExcecaoAoBuscarCarroceriaPorId() {
//        // Arrange
//
//        when(carroceriaRepository.findById(ID_INVALIDO))
//                .thenReturn(Optional.empty());
//
//        // Assert
//        var exception = assertThrows(
//                CarroceriaException.class,
//                () -> carroceriaService.findCarroceriaById(ID_INVALIDO)
//        );
//
//        assertCarroceriaResponseErrror(exception, ID_INVALIDO);
//
//        verify(carroceriaRepository).findById(ID_INVALIDO);
//    }
//
//
//    @Test
//    @DisplayName("Deve atualizar uma carroceria pelo ID")
//    void deveAtualizarCarroceriaPorId() {
//        //Arrange
//        var request = CarroceriaRequestFactory
//                .criarRequest()
//                .comNome("Sedan")
//                .build();
//        var entity = criarCarroceriaEntity();
//        var response = CarroceriaResponseFactory
//                .criarResponse()
//                .comId(ID_VALIDO)
//                .comNome("Sedan")
//                .build();
//
//        when(carroceriaRepository.findById(ID_VALIDO))
//                .thenReturn(Optional.of(entity));
//
//        when(carroceriaRepository.save(entity))
//                .thenReturn(entity);
//        when(carroceriaMapper.toResponse(entity))
//                .thenReturn(response);
//
//        // ACT
//        var resultado = carroceriaService.updateCarroceria(request, ID_VALIDO);
//
//        //Assert
//        assertThat(resultado)
//                .isNotNull()
//                .extracting(
//                        CarroceriaResponse::id,
//                        CarroceriaResponse::nome
//                )
//                .containsExactly(
//                        ID_VALIDO,
//                        "Sedan"
//                );
//        assertThat(request.nome()).isEqualTo("Sedan");
//        assertThat(resultado.id()).isEqualTo(ID_VALIDO);
//
//        verify(carroceriaRepository).findById(ID_VALIDO);
//        verify(carroceriaRepository).save(entity);
//        verify(carroceriaMapper).toResponse(entity);
//    }
//
//    @Test
//    @DisplayName("Deve lançar uma exceção durante a atualização de uma carroceria")
//    void deveLancarExcecaoAoAtualizarCarroceriaPorId() {
//        //Arrange
//        var request = criarCarroceriaRequest();
//
//        when(carroceriaRepository.findById(ID_INVALIDO))
//                .thenReturn(Optional.empty());
//
//        //Assert
//        var exception = assertThrows(CarroceriaException.class, () -> carroceriaService.updateCarroceria(request, ID_INVALIDO));
//        assertCarroceriaResponseErrror(exception, TestConstants.ID_INVALIDO);
//
//        verify(carroceriaRepository).findById(ID_INVALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve deletar uma carroceria por id")
//    void deveDeletarCarroceriaPorId() {
//        //Arrange
//        var entity = criarCarroceriaEntity();
//
//        when(carroceriaRepository.findById(ID_VALIDO))
//                .thenReturn(Optional.of(entity));
//
//        //Act
//        carroceriaService.deleteCarroceria(ID_VALIDO);
//
//        //Assert
//        verify(carroceriaRepository).findById(ID_VALIDO);
//        verify(carroceriaRepository).deleteById(ID_VALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve lançar uma exceção ao deletar uma carroceria")
//    void deveLancarExcecaoAoDeletarCarroceria() {
//        //Arrange
//        when(carroceriaRepository.findById(ID_INVALIDO))
//                .thenReturn(Optional.empty());
//
//        //Assert
//        var exception = assertThrows(CarroceriaException.class,
//                () -> carroceriaService.deleteCarroceria(ID_INVALIDO));
//
//        assertCarroceriaResponseErrror(exception, ID_INVALIDO);
//
//        verify(carroceriaRepository).findById(ID_INVALIDO);
//
//    }
//}
