package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.CarroRequest;
import com.JavangularCar.LojadeCarro.dto.request.FiltrarCamposCarroRequest;
import com.JavangularCar.LojadeCarro.dto.response.CarroResponse;
import com.JavangularCar.LojadeCarro.entity.Carro;
import com.JavangularCar.LojadeCarro.exception.CarroException;
import com.JavangularCar.LojadeCarro.factory.carro.CarroEntityFactory;
import com.JavangularCar.LojadeCarro.factory.carro.CarroRequestFactory;
import com.JavangularCar.LojadeCarro.factory.carro.CarroResponseFactory;
import com.JavangularCar.LojadeCarro.factory.carroceria.CarroceriaEntityFactory;
import com.JavangularCar.LojadeCarro.factory.combustivel.CombustivelEntityFactory;
import com.JavangularCar.LojadeCarro.factory.cores.CoresEntityFactory;
import com.JavangularCar.LojadeCarro.factory.marca.MarcaEntityFactory;
import com.JavangularCar.LojadeCarro.factory.modelo.ModeloEntityFactory;
import com.JavangularCar.LojadeCarro.factory.usuario.UsuarioEntityFactory;
import com.JavangularCar.LojadeCarro.mapper.CarroMapper;
import com.JavangularCar.LojadeCarro.repository.CarroRepository;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.JavangularCar.LojadeCarro.support.ErrorMessages.CARRO;
import static com.JavangularCar.LojadeCarro.support.ErrorMessages.ID_NOT_FOUND;
import static com.JavangularCar.LojadeCarro.support.TestConstants.ID_INVALIDO;
import static com.JavangularCar.LojadeCarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarroServiceTest {
    @Mock
    private CarroRepository carroRepository;
    @Mock
    private CarroMapper carroMapper;
    @Mock
    private CarroceriaService carroceriaService;
    @Mock
    private MarcaService marcaService;
    @Mock
    private CoresService coresService;
    @Mock
    private ModeloService modeloService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private CombustivelService combustivelService;
    @InjectMocks
    private CarroService carroService;

    @Test
    @DisplayName("Deve criar um carro")
    public void deveCriarUmCarro() {
        //Arrange
        var request = criarCarroRequest();

        var entity = criarCarroEntity();

        var response = criarCarroResponse();
        var carroceria = CarroceriaEntityFactory.criarEntity().comTodosOsCampos().build();
        var marca = MarcaEntityFactory.criarEntity().comTodosOsCampos().build();
        var cor = CoresEntityFactory.criarEntity().comTodosOsCampos().build();
        var modelo = ModeloEntityFactory.criarEntity().comTodosOsCampos().build();
        var usuario = UsuarioEntityFactory.criarEntity().comTodosOsCampos().build();
        var combustivel = CombustivelEntityFactory.criarEntity().comTodosOsCampos().build();

        when(carroMapper.toEntity(request))
                .thenReturn(entity);

        when(carroceriaService.buscaCarroceria(request.idCarroceria()))
                .thenReturn(carroceria);
        when(marcaService.buscaMarca(request.idMarca()))
                .thenReturn(marca);
        when(coresService.buscaCores(request.idCores()))
                .thenReturn(cor);
        when(modeloService.buscaModelo(request.idModelo()))
                .thenReturn(modelo);
        when(usuarioService.buscaUsuario(request.idUsuario()))
                .thenReturn(usuario);
        when(combustivelService.buscaCombustivel(request.idCombustivel()))
                .thenReturn(combustivel);

        when(carroRepository.save(entity))
                .thenReturn(entity);

        when(carroMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = carroService.createCarro(request);
        //Assert
        this.assertCarroResponse(resultado);
        assertThat(entity.getMarca())
                .isSameAs(marca);

        assertThat(entity.getModelo())
                .isSameAs(modelo);

        assertThat(entity.getUsuario())
                .isSameAs(usuario);

        assertThat(entity.getCarroceria())
                .isSameAs(carroceria);

        assertThat(entity.getCombustivel())
                .isSameAs(combustivel);

        assertThat(entity.getCores())
                .isSameAs(cor);

        verify(carroMapper).toEntity(request);
        verify(carroceriaService).buscaCarroceria(request.idCarroceria());
        verify(marcaService).buscaMarca(request.idMarca());
        verify(coresService).buscaCores(request.idCores());
        verify(modeloService).buscaModelo(request.idModelo());
        verify(usuarioService).buscaUsuario(request.idUsuario());
        verify(combustivelService).buscaCombustivel(request.idCombustivel());
        verify(carroRepository).save(entity);
        verify(carroMapper).toResponse(entity);

        verifyNoMoreInteractions(
                carroMapper,
                carroceriaService,
                marcaService,
                coresService,
                modeloService,
                usuarioService,
                combustivelService,
                carroRepository
        );
    }

    @Test
    @DisplayName("Deve listar carros paginados")
    void deveListarCarrosPaginados() {
        // Arrange
        var pageable = PageRequest.of(0, 10);

        var onix = criarCarroEntity();

        var celta = CarroEntityFactory
                .criarEntity()
                .comId(2L)
                .comPlaca("ABC1234")
                .build();

        var pagina = new PageImpl<>(
                List.of(onix, celta),
                pageable,
                2
        );

        var onixResponse = criarCarroResponse();

        var celtaResponse = CarroResponseFactory
                .criarResponse()
                .comId(2L)
                .comPlaca("ABC1234")
                .build();

        when(carroRepository.findAll(pageable))
                .thenReturn(pagina);

        when(carroMapper.toResponse(onix))
                .thenReturn(onixResponse);

        when(carroMapper.toResponse(celta))
                .thenReturn(celtaResponse);

        // Act
        Page<CarroResponse> resultado = carroService.listarCarros(pageable);

        // Assert
        assertThat(resultado.getContent())
                .hasSize(2)
                .extracting(
                        CarroResponse::id,
                        CarroResponse::placa
                )
                .containsExactly(
                        tuple(ID_VALIDO, "QUV1F836"),
                        tuple(2L, "ABC1234")
                );

        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getNumber()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);

        verify(carroRepository).findAll(pageable);
        verify(carroMapper).toResponse(onix);
        verify(carroMapper).toResponse(celta);

        verifyNoMoreInteractions(carroRepository, carroMapper);
    }


    @Test
    @DisplayName("Deve buscar um carro por ID")
    void buscaCarroPorID() {
        //Arrange
        var entity = criarCarroEntity();
        var response = criarCarroResponse();

        when(carroRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(carroMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = carroService.findCarroById(ID_VALIDO);
        //Assert
        this.assertCarroResponse(resultado);

        verify(carroRepository).findById(ID_VALIDO);
        verify(carroMapper).toResponse(entity);

        verifyNoMoreInteractions(
                carroRepository,
                carroMapper
        );
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao buscar um carro por ID")
    void buscaCarroPorIDException() {
        //Arrange
        when(carroRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var excecao = assertThrows(CarroException.class,
                () -> carroService.findCarroById(ID_INVALIDO));
        //Assert
        assertCarroResponseErro(excecao);

        verify(carroRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(carroRepository);
    }

    @Test
    @DisplayName("Deve atualizar o carro")
    void atualizaCarro() {
        //Arrange
        var request = criarCarroRequest();
        var entity = criarCarroEntity();
        var response = criarCarroResponse();

        when(carroRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        doNothing().when(carroMapper).toUpdate(request, entity);

        when(carroRepository.save(entity))
                .thenReturn(entity);

        when(carroMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = carroService.updateCarro(request, ID_VALIDO);
        //Assert
        this.assertCarroResponse(resultado);
        verify(carroRepository).findById(ID_VALIDO);
        verify(carroMapper).toResponse(entity);
        verify(carroMapper).toUpdate(request, entity);
        verify(carroRepository).save(entity);

        verifyNoMoreInteractions(carroRepository, carroMapper);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao atualizar o carro")
    void atualizaCarroException() {
        //Arrange
        var request = criarCarroRequest();
        when(carroRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var exception = assertThrows(CarroException.class,
                () -> carroService.updateCarro(request, ID_INVALIDO));
        //Assert
        this.assertCarroResponseErro(exception);
    }

    @Test
    @DisplayName("Deve deletar o carro")
    void deletaCarro() {
        //Arrange
        var entity = criarCarroEntity();
        when(carroRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //Act
        carroService.deleteCarro(ID_VALIDO);
        //Assert
        verify(carroRepository).deleteById(ID_VALIDO);
        verifyNoMoreInteractions(carroRepository);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao deletar o carro")
    void deletaCarroException() {
        //Arrange
        when(carroRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var excecao = assertThrows(CarroException.class,
                () -> carroService.deleteCarro(ID_INVALIDO));
        //Assert
        this.assertCarroResponseErro(excecao);
        verify(carroRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(carroRepository);
    }


    @Test
    @DisplayName("Deve filtrar o carro por campos")
    void filtraCarroPorCampo() {
        // Arrange
        var pageable = PageRequest.of(0, 10);
        var marca = "Chevrolet";
        var modelo = "Onix";
        var anoInicio = 2020;
        var anoFim = 2021;
        var valorInicio = 58000D;
        var valorFim = 65000D;
        var quilometragem = 58000D;

        var filtro =
                new FiltrarCamposCarroRequest(marca, modelo, anoInicio, anoFim, valorInicio, valorFim, quilometragem);

        var onix = criarCarroEntity();

        var celta = CarroEntityFactory
                .criarEntity()
                .comId(2L)
                .comPlaca("ABC1234")
                .build();

        var pagina = new PageImpl<>(
                List.of(onix, celta),
                pageable,
                2
        );

        var onixResponse = criarCarroResponse();

        var celtaResponse = CarroResponseFactory
                .criarResponse()
                .comId(2L)
                .comPlaca("ABC1234")
                .build();

        when(carroRepository.
                FindByCampos(marca, modelo, anoInicio, anoFim, valorInicio, valorFim, quilometragem, pageable))
                .thenReturn(pagina);

        when(carroMapper.toResponse(onix))
                .thenReturn(onixResponse);

        when(carroMapper.toResponse(celta))
                .thenReturn(celtaResponse);

        // Act
        var resultado = carroService.filtrarCampos(filtro, pageable);

        // Assert
        assertThat(resultado.getContent())
                .hasSize(2)
                .extracting(
                        CarroResponse::id,
                        CarroResponse::placa
                )
                .containsExactly(
                        tuple(ID_VALIDO, "QUV1F836"),
                        tuple(2L, "ABC1234")
                );

        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getNumber()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);

        verify(carroRepository).FindByCampos(marca, modelo, anoInicio, anoFim, valorInicio, valorFim, quilometragem, pageable);
        verify(carroMapper).toResponse(onix);
        verify(carroMapper).toResponse(celta);

        verifyNoMoreInteractions(carroRepository, carroMapper);
    }

    @Test
    @DisplayName("Deve marcar o carro como vendido")
    void marcarCarroComoVendido() {
        //Arrange
        var request = criarCarroRequest();
        var entity = criarCarroEntity();
        var response = criarCarroResponse();

        when(carroRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        doNothing()
                .when(carroMapper)
                .toUpdate(request, entity);

        when(carroRepository
                .save(entity))
                .thenReturn(entity);
        when(carroMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = carroService.marcarVendido(request, ID_VALIDO);
        //Assert
        this.assertCarroResponse(resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao marcar como vendido")
    void marcarCarroComoVendidoException() {
        //Arrange
        var request = criarCarroRequest();

        when(carroRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var excecao = assertThrows(CarroException.class,
                () -> carroService.marcarVendido(request, ID_INVALIDO));
        //Assert
        this.assertCarroResponseErro(excecao);

        verify(carroRepository).findById(ID_INVALIDO);

        verify(carroRepository, never())
                .save(any());

        verifyNoMoreInteractions(carroRepository);
    }
    @Test
    @DisplayName("Deve buscar um carro por ID")
    void buscaCarroPorId() {
        //Arrange
        var entity = criarCarroEntity();

        when(carroRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //Act
        var resultado = carroService.buscaCarro(ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        Carro::getId,
                        Carro::getPlaca,
                        Carro::getValor,
                        Carro::getQuilometragem,
                        Carro::getAnoFabricacao,
                        Carro::getUrl,
                        Carro::isAtivo
                ).containsExactly(
                        ID_VALIDO,
                        "QUV1F836",
                        BigDecimal.valueOf(58000),
                        67000.98D,
                        2020,
                        "www.google.com",
                        true
                );
        verify(carroRepository).findById(ID_VALIDO);
        verifyNoMoreInteractions(carroRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar um carro")
    void deveLancarExcecaoAoBuscaCarro() {
        //Arrange
        when(carroRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var excecao = assertThrows(CarroException.class,
                () -> carroService.buscaCarro(ID_INVALIDO));
        //Assert
        this.assertCarroResponseErro(excecao);

        verify(carroRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(carroRepository);
    }

    private void assertCarroResponseErro(CarroException excecao) {
        assertThat(excecao)
                .hasMessage(String.format(ID_NOT_FOUND, CARRO, ID_INVALIDO));
    }

    private AbstractListAssert<?, List<?>, Object, ObjectAssert<Object>> assertCarroResponse(CarroResponse resultado) {
        return assertThat(resultado)
                .isNotNull()
                .extracting(
                        CarroResponse::id,
                        CarroResponse::placa,
                        CarroResponse::marca,
                        CarroResponse::modelo,
                        CarroResponse::valor,
                        CarroResponse::quilometragem,
                        CarroResponse::anoFabricacao,
                        CarroResponse::url,
                        CarroResponse::ativo
                ).containsExactly(
                        ID_VALIDO,
                        "QUV1F836",
                        "Chevrolet",
                        "Onix",
                        BigDecimal.valueOf(58000),
                        67000.98D,
                        2020,
                        "www.google.com",
                        true
                );
    }
    private CarroResponse criarCarroResponse() {
        return CarroResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }
    private CarroRequest criarCarroRequest() {
        return CarroRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }


    private Carro criarCarroEntity() {
        return CarroEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }
}
