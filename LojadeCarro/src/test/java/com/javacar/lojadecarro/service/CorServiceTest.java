//package com.javacar.lojadecarro.service;
//
//import com.javacar.lojadecarro.dto.response.CorResponse;
//import com.javacar.lojadecarro.factory.cores.CoresEntityFactory;
//import com.javacar.lojadecarro.factory.cores.CoresRequestFactory;
//import com.javacar.lojadecarro.factory.cores.CoresResponseFactory;
//import com.javacar.lojadecarro.mapper.CorMapper;
//import com.javacar.lojadecarro.repository.CoresRepository;
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
//import static com.javacar.lojadecarro.support.ErrorMessages.CORES;
//import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
//import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
//import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.tuple;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CorServiceTest {
//    @Mock
//    private CoresRepository coresRepository;
//    @Mock
//    private CorMapper corMapper;
//    @InjectMocks
//    private CoresService coresService;
//
//    @Test
//    @DisplayName("Deve cadastrar um cor")
//    void deveCadastrarCor() {
//        //Arrange
//        var request = CoresRequestFactory
//                .criarRequest()
//                .comTodosOsCampos()
//                .build();
//
//        var entity = CoresEntityFactory
//                .criarEntity()
//                .comTodosOsCampos()
//                .build();
//
//        var response = CoresResponseFactory
//                .criarResponse()
//                .comTodosOsCampos()
//                .build();
//
//        when(corMapper.toEntity(request))
//                .thenReturn(entity);
//
//        when(coresRepository.save(entity))
//                .thenReturn(entity);
//
//        when(corMapper.toResponse(entity))
//                .thenReturn(response);
//
//        //Act
//        var resultado = coresService.createCores(request);
//        //Assert
//        assertThat(resultado)
//                .isNotNull()
//                .extracting(CorResponse::id,
//                        CorResponse::nome)
//                .containsExactly(ID_VALIDO, "Branco");
//
//        verify(corMapper).toEntity(request);
//        verify(corMapper).toResponse(entity);
//        verify(coresRepository).save(entity);
//    }
//
//    @Test
//    @DisplayName("Deve listar as cores")
//    void deveListarAsCores() {
//        //Arrange
//        var branco = CoresEntityFactory
//                .criarEntity()
//                .comTodosOsCampos()
//                .build();
//
//        var vermelho = CoresEntityFactory
//                .criarEntity()
//                .comId(2L)
//                .comNome("Vermelho")
//                .build();
//
//        var brancoResponse = CoresResponseFactory
//                .criarResponse()
//                .comTodosOsCampos()
//                .build();
//
//        var vermelhoResponse = CoresResponseFactory
//                .criarResponse()
//                .comId(2L)
//                .comNome("Vermelho")
//                .build();
//
//        var entity = List.of(branco, vermelho);
//
//        when(coresRepository.findAll())
//                .thenReturn(entity);
//
//        when(corMapper.toResponse(branco))
//                .thenReturn(brancoResponse);
//
//        when(corMapper.toResponse(vermelho))
//                .thenReturn(vermelhoResponse);
//        //Act
//        var resultado = coresService.listarCores();
//
//        //Assert
//        assertThat(resultado)
//                .isNotNull()
//                .hasSize(2)
//                .extracting(CorResponse::id, CorResponse::nome)
//                .containsExactly(tuple(ID_VALIDO, "Branco"), tuple(2L, "Vermelho"));
//
//        verify(coresRepository).findAll();
//        verify(corMapper).toResponse(branco);
//        verify(corMapper).toResponse(vermelho);
//
//    }
//
//    @Test
//    @DisplayName("Deve buscar uma cor por ID")
//    void deveBuscarCorPorId() {
//        //Arrange
//        var entity = CoresEntityFactory
//                .criarEntity()
//                .comTodosOsCampos()
//                .build();
//        var response = CoresResponseFactory
//                .criarResponse()
//                .comTodosOsCampos()
//                .build();
//        when(coresRepository.findById(ID_VALIDO))
//                .thenReturn(Optional.of(entity));
//
//        when(corMapper.toResponse(entity))
//                .thenReturn(response);
//        //Act
//
//        var resultado = coresService.findCoresById(ID_VALIDO);
//        //Assert
//        assertThat(resultado)
//                .isNotNull()
//                .extracting(
//                        CorResponse::id,
//                        CorResponse::nome
//                ).containsExactly(ID_VALIDO, "Branco");
//
//        verify(corMapper).toResponse(entity);
//        verify(coresRepository).findById(ID_VALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção ao buscar a cor por ID")
//    void deveLancarExcecaoAoBuscarCor() {
//        //Arrange
//        when(coresRepository.findById(ID_INVALIDO))
//                .thenReturn(Optional.empty());
//        //Act
//        var exception = assertThrows(CorNotFoundException.class,
//                () -> coresService.findCoresById(ID_INVALIDO));
//        //Assert
//        assertThat(exception)
//                .hasMessage(String.format(ID_NOT_FOUND, CORES, ID_INVALIDO));
//
//        verify(coresRepository).findById(ID_INVALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve atualizar uma cor")
//    void deveAtualizarCor() {
//        //Arrange
//
//        var request = CoresRequestFactory
//                .criarRequest()
//                .comTodosOsCampos()
//                .build();
//
//        var entity = CoresEntityFactory
//                .criarEntity()
//                .comTodosOsCampos()
//                .build();
//        var response = CoresResponseFactory
//                .criarResponse()
//                .comTodosOsCampos()
//                .build();
//
//        when(coresRepository.findById(ID_VALIDO))
//                .thenReturn(Optional.of(entity));
//
//        when(coresRepository.save(entity))
//                .thenReturn(entity);
//
//        when(corMapper.toResponse(entity))
//                .thenReturn(response);
//        //Act
//        var resultado = coresService.updateCores(request, ID_VALIDO);
//        //Assert
//        assertThat(resultado)
//                .isNotNull()
//                .extracting(
//                        CorResponse::id,
//                        CorResponse::nome
//                ).containsExactly(ID_VALIDO, "Branco");
//        verify(corMapper).toResponse(entity);
//        verify(coresRepository).findById(ID_VALIDO);
//        verify(coresRepository).save(entity);
//    }
//
//    @Test
//    @DisplayName("Deve lançar uma exceção ao atualizar a cor")
//    void deveLancarExcecaoAoAtualizarCor() {
//        //Arrange
//
//        var request = CoresRequestFactory
//                .criarRequest()
//                .comTodosOsCampos()
//                .build();
//
//        when(coresRepository.findById(ID_INVALIDO))
//                .thenReturn(Optional.empty());
//
//        //Assert
//        var exception = assertThrows(CorNotFoundException.class,
//                () -> coresService.updateCores(request, ID_INVALIDO));
//
//        assertThat(exception)
//                .hasMessage(String.format(ID_NOT_FOUND, CORES, ID_INVALIDO));
//        verify(coresRepository).findById(ID_INVALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve deletar uma cor")
//    void deveDeletarCor() {
//        //Arrange
//        var entity = CoresEntityFactory
//                .criarEntity()
//                .comTodosOsCampos()
//                .build();
//
//        when(coresRepository.findById(ID_VALIDO))
//                .thenReturn(Optional.of(entity));
//        //Act
//        coresService.deleteCores(ID_VALIDO);
//        //Assert
//        verify(coresRepository).findById(ID_VALIDO);
//        verify(coresRepository).deleteById(ID_VALIDO);
//    }
//
//    @Test
//    @DisplayName("Deve lançar uma exceção ao deletar uma cor")
//    void deveLancarExcecaoAoDeletarCor() {
//        //Arrange
//        when(coresRepository.findById(ID_INVALIDO))
//                .thenReturn(Optional.empty());
//        //Assert
//        var exception = assertThrows(CorNotFoundException.class,
//                () -> coresService.deleteCores(ID_INVALIDO));
//        assertThat(exception)
//                .hasMessage(String.format(ID_NOT_FOUND, CORES, ID_INVALIDO));
//
//        verify(coresRepository).findById(ID_INVALIDO);
//        verify(coresRepository, never())
//                .deleteById(anyLong());
//    }
//
//}
