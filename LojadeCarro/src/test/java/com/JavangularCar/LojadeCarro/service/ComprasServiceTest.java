package com.JavangularCar.LojadeCarro.service;

import com.JavangularCar.LojadeCarro.dto.request.ComprasRequest;
import com.JavangularCar.LojadeCarro.dto.response.ComprasResponse;
import com.JavangularCar.LojadeCarro.entity.Compras;
import com.JavangularCar.LojadeCarro.exception.CarroException;
import com.JavangularCar.LojadeCarro.exception.UsuarioException;
import com.JavangularCar.LojadeCarro.factory.carro.CarroEntityFactory;
import com.JavangularCar.LojadeCarro.factory.compra.ComprasEntityFactory;
import com.JavangularCar.LojadeCarro.factory.compra.ComprasRequestFactory;
import com.JavangularCar.LojadeCarro.factory.compra.ComprasResponseFactory;
import com.JavangularCar.LojadeCarro.factory.usuario.UsuarioEntityFactory;
import com.JavangularCar.LojadeCarro.mapper.ComprasMapper;
import com.JavangularCar.LojadeCarro.repository.ComprasRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.JavangularCar.LojadeCarro.support.ErrorMessages.*;
import static com.JavangularCar.LojadeCarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComprasServiceTest {
    @Mock
    private ComprasRepository comprasRepository;
    @Mock
    private ComprasMapper comprasMapper;
    @Mock
    private CarroService carroService;
    @Mock
    private UsuarioService usuarioService;
    @InjectMocks
    private ComprasService comprasService;

    @Test
    @DisplayName("Deve cadastrar uma compra")
    void deveCadastrarCompra() {
        // Arrange
        var request = criarComprasRequest();
        var entity = criarComprasEntity();

        var vendedor = UsuarioEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        var comprador = UsuarioEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comId(2L)
                .build();

        var carroEntity = CarroEntityFactory.criarEntity().comTodosOsCampos().build();

        var response = criarComprasResponse();

        when(comprasMapper.toEntity(request))
                .thenReturn(entity);

        when(carroService.buscaCarro(request.carroId()))
                .thenReturn(carroEntity);

        when(usuarioService.buscaUsuario(request.vendedorId()))
                .thenReturn(vendedor);

        when(usuarioService.buscaUsuario(request.compradorId()))
                .thenReturn(comprador);

        when(comprasRepository.save(entity))
                .thenReturn(entity);

        when(comprasMapper.toResponse(entity))
                .thenReturn(response);

        // Act
        var resultado = comprasService.createCompras(request);

        // Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        ComprasResponse::id,
                        ComprasResponse::valor

                ).containsExactly(
                        ID_VALIDO,
                        BigDecimal.valueOf(200000)
                );
        assertThat(entity.getCarro()).isSameAs(carroEntity);
        assertThat(entity.getVendedor()).isSameAs(vendedor);
        assertThat(entity.getComprador()).isSameAs(comprador);
        assertThat(entity.getDataVenda()).isNotNull();

        verify(comprasMapper).toEntity(request);
        verify(usuarioService).buscaUsuario(request.vendedorId());
        verify(usuarioService).buscaUsuario(request.compradorId());

        verify(comprasRepository).save(entity);

        verify(comprasRepository).marcaVendido(request.carroId());

        verify(comprasMapper).toResponse(entity);

        verifyNoMoreInteractions(
                comprasMapper,
                comprasRepository,
                usuarioService,
                carroService
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar um carro")
    void deveLancarExceptionNaoEncontrarCarro() {
        //Arrange
        var request = criarComprasRequest();
        var entity = criarComprasEntity();

        when(comprasMapper.toEntity(request))
                .thenReturn(entity);

        when(carroService.buscaCarro(request.carroId()))
                .thenThrow(new CarroException(request.carroId()));
        //Act
        var excecao = assertThrows(CarroException.class,
                () -> comprasService.createCompras(request));
        //Assert
        assertThat(excecao)
                .hasMessage(String.format(ID_NOT_FOUND, CARRO, request.carroId()));

        verify(comprasMapper).toEntity(request);
        verify(carroService).buscaCarro(request.carroId());

        verifyNoInteractions(usuarioService, comprasRepository);

        verifyNoMoreInteractions(comprasMapper, carroService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar um comprador")
    void deveLancarExceptionNaoEncontrarComprador() {
        //Arrange
        var request = criarComprasRequest();
        var entity = criarComprasEntity();
        var carro = CarroEntityFactory.criarEntity().comTodosOsCampos().build();

        when(comprasMapper.toEntity(request))
                .thenReturn(entity);

        when(carroService.buscaCarro(request.carroId()))
                .thenReturn(carro);

        when(usuarioService.buscaUsuario(request.compradorId()))
                .thenThrow(new UsuarioException(request.compradorId()));
        //Act
        var excecao = assertThrows(UsuarioException.class,
                () -> comprasService.createCompras(request));
        //Assert
        assertThat(excecao)
                .hasMessage(String.format(ID_NOT_FOUND, USUARIO, request.compradorId()));

        verify(comprasMapper).toEntity(request);
        verify(carroService).buscaCarro(request.carroId());
        verify(usuarioService).buscaUsuario(request.compradorId());
        verify(usuarioService, never()).buscaUsuario(request.vendedorId());

        verifyNoInteractions(comprasRepository);

        verifyNoMoreInteractions(comprasMapper, carroService, usuarioService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar um vendedor")
    void deveLancarExceptionNaoEncontrarVendedor() {
        //Arrange
        var request = criarComprasRequest();
        var entity = criarComprasEntity();
        var carro = CarroEntityFactory.criarEntity().comTodosOsCampos().build();
        var comprador = UsuarioEntityFactory.criarEntity().comTodosOsCampos().build();

        when(comprasMapper.toEntity(request))
                .thenReturn(entity);

        when(carroService.buscaCarro(request.carroId()))
                .thenReturn(carro);

        when(usuarioService.buscaUsuario(request.compradorId()))
                .thenReturn(comprador);

        when(usuarioService.buscaUsuario(request.vendedorId()))
                .thenThrow(new UsuarioException(request.vendedorId()));
        //Act
        var excecao = assertThrows(UsuarioException.class,
                () -> comprasService.createCompras(request));
        //Assert
        assertThat(excecao)
                .hasMessage(String.format(ID_NOT_FOUND, USUARIO, request.vendedorId()));

        verify(comprasMapper).toEntity(request);
        verify(carroService).buscaCarro(request.carroId());
        verify(usuarioService).buscaUsuario(request.compradorId());
        verify(usuarioService).buscaUsuario(request.vendedorId());

        verifyNoInteractions(comprasRepository);

        verifyNoMoreInteractions(comprasMapper, carroService, usuarioService);
    }


    @Test
    @DisplayName("Deve listar as compras")
    void deveListarAsCompras() {
        //Arrange

        var entity = criarComprasEntity();
        var entity2 = ComprasEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comId(3L).build();

        var entityList = List.of(entity, entity2);

        var response = criarComprasResponse();
        var response2 = ComprasResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(3L).build();
        when(comprasRepository.findAll())
                .thenReturn(entityList);

        when(comprasMapper.toResponse(entity))
                .thenReturn(response);

        when(comprasMapper.toResponse(entity2))
                .thenReturn(response2);
        //Act
        var resultado = comprasService.listarCompras();
        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        ComprasResponse::id,
                        ComprasResponse::valor
                ).containsExactly(
                        tuple(ID_VALIDO, BigDecimal.valueOf(200000)),
                        tuple(3L, BigDecimal.valueOf(200000))
                );

        verify(comprasRepository).findAll();
        verify(comprasMapper).toResponse(entity);
        verify(comprasMapper).toResponse(entity2);

        verifyNoMoreInteractions(comprasRepository, comprasMapper);

    }

    private Compras criarComprasEntity() {
        return ComprasEntityFactory
                .criarEntity()
                .comTodosOsCampos().build();
    }

    private ComprasRequest criarComprasRequest() {
        return ComprasRequestFactory
                .criarRequest()
                .comTodosOsCampos().build();
    }

    private ComprasResponse criarComprasResponse() {
        return ComprasResponseFactory
                .criarResponse()
                .comTodosOsCampos().build();
    }

}
