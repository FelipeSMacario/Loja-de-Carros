//package com.javacar.lojadecarro.service;
//
//import com.javacar.lojadecarro.dto.request.MarcaRequest;
//import com.javacar.lojadecarro.dto.response.MarcaResponse;
//import com.javacar.lojadecarro.factory.marca.MarcaEntityFactory;
//import com.javacar.lojadecarro.factory.marca.MarcaRequestFactory;
//import com.javacar.lojadecarro.factory.marca.MarcaResponseFactory;
//import com.javacar.lojadecarro.mapper.MarcaMapper;
//import com.javacar.lojadecarro.repository.MarcaRepository;
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
//import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
//import static com.javacar.lojadecarro.support.ErrorMessages.MARCA;
//import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class MarcaServiceTest {
//
//    @Mock
//    private MarcaMapper marcaMapper;
//
//    @Mock
//    private MarcaRepository marcaRepository;
//
//    @InjectMocks
//    private MarcaService marcaService;
//
//    @Test
//    @DisplayName("Valida a criação da marca")
//    void deveCriarMarcaComSucesso() {
//        var request = MarcaRequestFactory.criarRequest()
//                .comTodosOsCampos()
//                .build();
//        var entity = MarcaEntityFactory.criarEntity()
//                .comTodosOsCampos()
//                .build();
//
//        var response = MarcaResponseFactory.criarResponse()
//                .comTodosOsCampos()
//                .build();
//
//
//        when(marcaMapper.toEntity(request)).thenReturn(entity);
//        when(marcaRepository.save(entity)).thenReturn(entity);
//        when(marcaMapper.toResponse(entity)).thenReturn(response);
//
//        MarcaResponse resultado = marcaService.createMarca(request);
//
//        assertThat(resultado.nome()).isEqualTo("Ford");
//
//        verify(marcaMapper).toEntity(request);
//
//        verify(marcaRepository).save(entity);
//    }
//
//    @Test
//    @DisplayName("Valida a busca da marca por ID")
//    void deveBuscarMarcaPorID() {
//        // Arrange
//        Long id = 1L;
//
//        var entity = MarcaEntityFactory.criarEntity()
//                .comTodosOsCampos()
//                .build();
//
//        var response = MarcaResponseFactory.criarResponse()
//                .comTodosOsCampos()
//                .build();
//
//        when(marcaRepository.findById(id))
//                .thenReturn(Optional.of(entity));
//
//        when(marcaMapper.toResponse(entity))
//                .thenReturn(response);
//
//        // Act
//        var resultado = marcaService.findMarcaById(id);
//
//        // Assert
//        assertThat(resultado).isNotNull();
//        assertThat(resultado.id()).isEqualTo(1L);
//        assertThat(resultado.nome()).isEqualTo("Ford");
//        assertThat(resultado.url()).isEqualTo("https://www.google.com");
//
//        verify(marcaRepository).findById(id);
//        verify(marcaMapper).toResponse(entity);
//
//    }
//
//    @Test
//    @DisplayName("Valida a exceção de marca não encontrada")
//    void deveLancarExcecao() {
//        when(marcaRepository.findById(ID_INVALIDO))
//                .thenReturn(Optional.empty());
//
//        var exception = assertThrows(
//                MarcaNotFoundException.class,
//                () -> marcaService.findMarcaById(ID_INVALIDO)
//        );
//
//        assertThat(exception)
//                .hasMessage(String.format(ID_NOT_FOUND, MARCA, ID_INVALIDO));
//
//        verify(marcaRepository).findById(ID_INVALIDO);
//
//    }
//
//    @Test
//    @DisplayName("Deve validar a atualização de uma marca")
//    void deveAtualizarMarca() {
//        // Arrange
//        Long id = 1L;
//
//        var request = new MarcaRequest("Chevrolet", "https://www.google.com");
//        var entity = MarcaEntityFactory.criarEntity().comTodosOsCampos().build();
//        var response = new MarcaResponse(
//                id,
//                "Chevrolet",
//                "https://www.google.com"
//        );
//
//        when(marcaRepository.findById(id))
//                .thenReturn(Optional.of(entity));
//
//        when(marcaRepository.save(entity))
//                .thenReturn(entity);
//
//        when(marcaMapper.toResponse(entity))
//                .thenReturn(response);
//
//        // Act
//        var resultado = marcaService.updateMarca(request, id);
//
//        // Assert
//        assertThat(entity.getNome())
//                .isEqualTo("Chevrolet");
//
//        assertThat(entity.getUrl())
//                .isEqualTo("https://www.google.com");
//
//        assertThat(resultado.nome())
//                .isEqualTo("Chevrolet");
//
//        verify(marcaRepository).findById(id);
//        verify(marcaRepository).save(entity);
//        verify(marcaMapper).toResponse(entity);
//    }
//
//    @Test
//    @DisplayName("Deve lançar a exceção durante a atualização da marca")
//    void deveLancarExcecaoAoAtualizarMarca() {
//        Long id = 99L;
//
//        var request = MarcaRequestFactory.criarRequest()
//                .comTodosOsCampos()
//                .build();
//
//        when(marcaRepository.findById(id))
//                .thenReturn(Optional.empty());
//
//        var exception = assertThrows(
//                MarcaNotFoundException.class,
//                () -> marcaService.updateMarca(request, id)
//        );
//
//        assertThat(exception)
//                .hasMessage(String.format(ID_NOT_FOUND, MARCA, ID_INVALIDO));
//
//        verify(marcaRepository).findById(id);
//    }
//
//    @Test
//    @DisplayName("Deve deletar uma marca pelo ID")
//    void deveDeletarMarcaPorID() {
//        // Arrange
//        Long id = 1L;
//
//        var entity = MarcaEntityFactory.criarEntity()
//                .comTodosOsCampos()
//                .build();
//
//        when(marcaRepository.findById(id))
//                .thenReturn(Optional.of(entity));
//
//        // Act
//        marcaService.deleteMarca(id);
//
//        // Assert
//        verify(marcaRepository).findById(id);
//        verify(marcaRepository).deleteById(id);
//    }
//
//    @Test
//    @DisplayName("Deve lançar uma exceção durante a exclusão da marca")
//    void deveLancarExcecaoAoExcluirMarca() {
//        // Arrange
//        Long id = 99L;
//        when(marcaRepository.findById(id))
//                .thenReturn(Optional.empty());
//
//        // Assert
//        var exception = assertThrows(
//                MarcaNotFoundException.class,
//                () -> marcaService.deleteMarca(id));
//
//        assertThat(exception)
//                .hasMessage(String.format(ID_NOT_FOUND, MARCA, ID_INVALIDO));
//
//        verify(marcaRepository).findById(id);
//        verify(marcaRepository, never()).deleteById(anyLong());
//    }
//
//    @Test
//    @DisplayName("Deve buscar todas as marcas")
//    void deveListarTodasAsMarcas() {
//        // Arrange
//        var chevrolet = MarcaEntityFactory.criarEntity()
//                .comId(1L)
//                .comNome("Chevrolet")
//                .comURL("https://www.google.com")
//                .build();
//
//        var fiat = MarcaEntityFactory.criarEntity()
//                .comId(2L)
//                .comNome("Fiat")
//                .comURL("https://www.google.com")
//                .build();
//
//        var marcasEntity = List.of(chevrolet, fiat);
//
//        var chevroletResponse = MarcaResponseFactory.criarResponse()
//                .comId(1L)
//                .comNome("Chevrolet")
//                .build();
//
//        var fiatResponse = MarcaResponseFactory.criarResponse()
//                .comId(2L)
//                .comNome("Fiat")
//                .build();
//
//        when(marcaRepository.findByOrderByNomeAsc())
//                .thenReturn(marcasEntity);
//
//        when(marcaMapper.toResponse(chevrolet))
//                .thenReturn(chevroletResponse);
//
//        when(marcaMapper.toResponse(fiat))
//                .thenReturn(fiatResponse);
//
//        // Act
//        var resultado = marcaService.listarMarcas();
//
//        // Assert
//        assertThat(resultado)
//                .hasSize(2)
//                .extracting(MarcaResponse::nome)
//                .containsExactly("Chevrolet", "Fiat");
//
//        verify(marcaRepository).findByOrderByNomeAsc();
//
//        verify(marcaMapper).toResponse(chevrolet);
//        verify(marcaMapper).toResponse(fiat);
//    }
//
//}
