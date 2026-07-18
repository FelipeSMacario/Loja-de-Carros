//package com.javacar.lojadecarro.service;
//
//import com.javacar.lojadecarro.dto.request.ImagensRequest;
//import com.javacar.lojadecarro.dto.response.ImagensResponse;
//import com.javacar.lojadecarro.entity.Veiculo;
//import com.javacar.lojadecarro.entity.Imagem;
//import com.javacar.lojadecarro.factory.carro.CarroEntityFactory;
//import com.javacar.lojadecarro.factory.imagens.ImagensEntityFactory;
//import com.javacar.lojadecarro.factory.imagens.ImagensRequestFactory;
//import com.javacar.lojadecarro.factory.imagens.ImagensResponseFactory;
//import com.javacar.lojadecarro.mapper.ImagensMapper;
//import com.javacar.lojadecarro.repository.ImagensRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
//import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
//import static com.javacar.lojadecarro.support.ErrorMessages.IMAGENS;
//import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
//import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.groups.Tuple.tuple;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ImagemServiceTest {
//    @Mock
//    private ImagensRepository imagensRepository;
//    @Mock
//    private ImagensMapper imagensMapper;
//    @Mock
//    private CarroService carroService;
//    @Mock
//    private StorageService storageService;
//    @InjectMocks
//    private ImagensService imagensService;
//
//    @Test
//    @DisplayName("Deve realizar upload de uma imagem")
//    void deveRealizarUploadDeUmaImagem() throws IOException {
//
//        // Arrange
//        Long idVeiculo = ID_VALIDO;
//
//        var carro = criarCarroEntity();
//
//        var imagem = criarImagemEntity();
//
//        var response = criarImagemResponse();
//
//        MultipartFile file = new MockMultipartFile(
//                "files",
//                "onix.jpg",
//                "image/jpeg",
//                "conteudo".getBytes()
//        );
//
//        MultipartFile[] files = {file};
//
//        when(carroService.buscaCarro(idVeiculo))
//                .thenReturn(carro);
//
//        when(storageService.upload(file, idVeiculo))
//                .thenReturn("https://bucket/imagens/onix.jpg");
//
//        when(imagensRepository.save(any(Imagem.class)))
//                .thenReturn(imagem);
//
//        when(imagensMapper.toResponse(imagem))
//                .thenReturn(response);
//
//        // Act
//        List<ImagensResponse> resultado =
//                imagensService.create(files, idVeiculo);
//
//        // Assert
//        assertThat(resultado)
//                .hasSize(1);
//
////        assertThat(carro.getUrl())
////                .isEqualTo("https://bucket/imagens/onix.jpg");
//
//        verify(carroService).buscaCarro(idVeiculo);
//        verify(storageService).upload(file, idVeiculo);
//        verify(imagensRepository).save(any(Imagem.class));
//        verify(imagensMapper).toResponse(imagem);
//
//        verifyNoMoreInteractions(
//                carroService,
//                storageService,
//                imagensRepository,
//                imagensMapper
//        );
//    }
//
//    @Test
//    @DisplayName("Deve listar as imagens")
//    void deveListarAsImagens(){
//        //Arrange
//        var imagem1 = criarImagemEntity();
//        var imagem2 = ImagensEntityFactory.criarEntity().comTodosOsCampos().comId(2L).build();
//        var entity = List.of(imagem1, imagem2);
//
//        var imagem1Response = criarImagemResponse();
//        var imagem2Response = ImagensResponseFactory.criarResponse().comTodosOsCampos().comId(2L).build();
//
//        when(imagensRepository.findByVeiculoId(ID_VALIDO))
//                .thenReturn(Optional.of(entity));
//
//        when(imagensMapper.toResponse(imagem1)).thenReturn(imagem1Response);
//        when(imagensMapper.toResponse(imagem2)).thenReturn(imagem2Response);
//        //Act
//        var resultado = imagensService.listarImagens(ID_VALIDO);
//        //Assert
//        assertThat(resultado)
//                .isNotNull()
//                .hasSize(2)
//                .extracting(ImagensResponse::id,
//                        ImagensResponse::url,
//                        ImagensResponse::idVeiculo)
//                .containsExactly(
//                        tuple(ID_VALIDO, "https://bucket/imagens/onix.jpg", 1L),
//                        tuple(2L, "https://bucket/imagens/onix.jpg", 1L));
//
//        verify(imagensRepository).findByVeiculoId(ID_VALIDO);
//        verify(imagensMapper).toResponse(imagem1);
//        verify(imagensMapper).toResponse(imagem2);
//
//        verifyNoMoreInteractions(imagensRepository, imagensMapper);
//    }
//
//    @Test
//    @DisplayName("Deve retornar uma exceção ao listar imagens")
//    void deveRetornarUmaExceao()  {
//        //Arrange
//        when(imagensRepository.findByVeiculoId(ID_INVALIDO))
//                .thenReturn(Optional.empty());
//        //Act
//        var excecao = assertThrows(ImagemNotFoundException.class,
//                () -> imagensService.listarImagens(ID_INVALIDO));
//        //Assert
//        assertThat(excecao)
//                .hasMessage(String.format(ID_NOT_FOUND, IMAGENS, ID_INVALIDO));
//
//        verify(imagensRepository).findByVeiculoId(ID_INVALIDO);
//        verifyNoMoreInteractions(imagensRepository);
//
//        verifyNoInteractions(imagensMapper);
//    }
//
//    @Test
//    @DisplayName("Deve filtrar uma imagem por ID")
//    void deveFiltrarUmaImagemPorId()  {
//        //Arrange
//        var entity = criarImagemEntity();
//        var response = criarImagemResponse();
//
//        when(imagensRepository.findById(ID_VALIDO))
//                .thenReturn(Optional.of(entity));
//
//        when(imagensMapper.toResponse(entity))
//                .thenReturn(response);
//        //Act
//        var resultado = imagensService.findImagensById(ID_VALIDO);
//        //Assert
//        assertImagensResponse(resultado);
//
//        verify(imagensRepository).findById(ID_VALIDO);
//        verify(imagensMapper).toResponse(entity);
//
//        verifyNoMoreInteractions(imagensRepository, imagensMapper);
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção ao buscar uma imagem por ID")
//    void deveLancarUmaExcecaoAoBuscarUmaImagemPorId() {
//        //Arrange
//        when(imagensRepository.findById(ID_INVALIDO))
//                .thenReturn(Optional.empty());
//        //Act
//        var excecao = assertThrows(ImagemNotFoundException.class,
//                () -> imagensService.findImagensById(ID_INVALIDO));
//        //Assert
//        AssertImagensException(excecao);
//        verify(imagensRepository).findById(ID_INVALIDO);
//        verify(imagensMapper, never()).toResponse(any());
//        verifyNoMoreInteractions(imagensRepository);
//    }
//
//    @Test
//    @DisplayName("Deve atualizar a imagem")
//    void deveAtualizarUmaImagem(){
//        //Arrange
//        var request = criarImagensRequest();
//        var entity = criarImagemEntity();
//        var response = criarImagemResponse();
//        var carroEntity = criarCarroEntity();
//
//        when(imagensRepository.findById(ID_VALIDO))
//                .thenReturn(Optional.of(entity));
//
//        when(carroService.buscaCarro(request.veiculoId()))
//                .thenReturn(carroEntity);
//        when(imagensRepository.save(any(Imagem.class)))
//                .thenReturn(entity);
//        when(imagensMapper.toResponse(entity))
//                .thenReturn(response);
//        //Act
//        var resultado = imagensService.updateImagens(request, ID_VALIDO);
//        //Assert
//        ArgumentCaptor<Imagem> captor =
//                ArgumentCaptor.forClass(Imagem.class);
//        assertImagensResponse(resultado);
//        verify(imagensRepository).findById(ID_VALIDO);
//        verify(carroService).buscaCarro(request.veiculoId());
//        verify(imagensRepository).save(captor.capture());
//        verify(imagensMapper).toResponse(entity);
//        Imagem imagem = captor.getValue();
//        assertThat(imagem.getVeiculo())
//                .isSameAs(carroEntity);
////        assertThat(imagem.getUrl())
////                .isEqualTo("https://bucket/imagens/onix.jpg");
//
//        verifyNoMoreInteractions(imagensRepository, imagensMapper, carroService);
//    }
//
//    @Test
//    @DisplayName("Deve lançar uma exceção ao atualizar imagem")
//    void deveLancarUmaExcecaoAoAtualizarImagem(){
//        //Arrange
//        var request = criarImagensRequest();
//        when(imagensRepository.findById(ID_INVALIDO))
//                .thenReturn(Optional.empty());
//        //Act
//        var excecao = assertThrows(ImagemNotFoundException.class,
//                () -> imagensService.updateImagens(request, ID_INVALIDO));
//        //Assert
//        AssertImagensException(excecao);
//        verify(imagensRepository).findById(ID_INVALIDO);
//
//        verify(carroService, never()).buscaCarro(request.veiculoId());
//        verify(imagensRepository, never()).save(any(Imagem.class));
//        verify(imagensMapper, never()).toResponse(any(Imagem.class));
//
//        verifyNoMoreInteractions(imagensRepository);
//    }
//
//    @Test
//    @DisplayName("Deve deletar uma imagem")
//    void deveDeletarUmaImagem() throws IOException {
//        //Arrange
//        var entity = criarImagemEntity();
//
//        when(imagensRepository.findById(ID_VALIDO))
//                .thenReturn(Optional.of(entity));
//
//        //Act
//        imagensService.deleteImagens(ID_VALIDO);
//        //Assert
//        verify(imagensRepository).findById(ID_VALIDO);
//        //verify(storageService).delete(entity.getUrl());
//        verify(imagensRepository).delete(entity);
//
//        verifyNoMoreInteractions(imagensRepository, storageService);
//    }
//
//    @Test
//    @DisplayName("Deve lançar uma exceção ao deletar uma imagem")
//    void deveLancarExcecaoAoDeletarImagem() throws IOException {
//        //Arrange
//        var entity = criarImagemEntity();
//
//        when(imagensRepository.findById(ID_INVALIDO))
//                .thenReturn(Optional.empty());
//        //Act
//        var excecao = assertThrows(ImagemNotFoundException.class,
//                () -> imagensService.deleteImagens(ID_INVALIDO));
//        //Assert
//        AssertImagensException(excecao);
//        verify(imagensRepository).findById(ID_INVALIDO);
//
//        verify(storageService, never()).delete(anyString());
//        verify(imagensRepository, never()).delete(entity);
//
//        verifyNoMoreInteractions(imagensRepository, storageService);
//    }
//
//    private static void AssertImagensException(ImagemNotFoundException excecao) {
//        assertThat(excecao)
//                .hasMessage(String.format(ID_NOT_FOUND, IMAGENS, ID_INVALIDO));
//    }
//
//    private static void assertImagensResponse(ImagensResponse resultado) {
//        assertThat(resultado)
//                .isNotNull()
//                .extracting(
//                        ImagensResponse::id,
//                        ImagensResponse::url,
//                        ImagensResponse::idVeiculo
//                ).containsExactly(
//                        ID_VALIDO,
//                        "https://bucket/imagens/onix.jpg",
//                        ID_VALIDO
//                );
//    }
//
//    private static Imagem criarImagemEntity() {
//        return ImagensEntityFactory.criarEntity()
//                .comTodosOsCampos()
//                .build();
//    }
//
//    private static ImagensResponse criarImagemResponse() {
//        return ImagensResponseFactory.criarResponse()
//                .comTodosOsCampos()
//                .build();
//    }
//
//    private static ImagensRequest criarImagensRequest() {
//        return ImagensRequestFactory.criarRequest()
//                .comTodosOsCampos()
//                .build();
//    }
//
//    private static Veiculo criarCarroEntity() {
//        return CarroEntityFactory.criarEntity()
//                .comTodosOsCampos()
//                .build();
//    }
//}
