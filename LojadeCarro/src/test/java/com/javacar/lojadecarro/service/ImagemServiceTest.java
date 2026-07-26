package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.UploadResult;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.imagem.ImagemEntityFactory;
import com.javacar.lojadecarro.repository.ImagensRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.IMAGEM;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.factory.helper.ImagemHelper.*;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.criarVeiculoEntity;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do serviço da imagem")
class ImagemServiceTest {
    @Mock
    private ImagensRepository imagensRepository;
    @Mock
    private StorageService storageService;
    @InjectMocks
    private ImagensService imagensService;

    @Nested
    @DisplayName("Testes referentes ao upload da imagem")
    class CriarImagem {
        @Test
        @DisplayName("Deve realizar upload de uma imagem")
        void deveRealizarUploadDeUmaImagem() throws IOException {
            // Arrange
            var imagemFileArray = criarListImagemFile();
            var imagemFile = imagemFileArray[0];
            var imagemFile2 = imagemFileArray[1];
            var veiculo = criarVeiculoEntity();
            var upload = criarUploadResult();
            var upload2 = new UploadResult(
                    1L + "onix2.jpg",
                    "uploads",
                    "onix2.jpg",
                    "image/jpeg",
                    200L
            );

            when(storageService.upload(imagemFile, veiculo.getId()))
                    .thenReturn(upload);

            when(storageService.upload(imagemFile2, veiculo.getId()))
                    .thenReturn(upload2);

            when(imagensRepository.saveAll(anyList()))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            // Act
            var resultado = imagensService.criar(imagemFileArray, veiculo);

            // Assert
            ArgumentCaptor<List<Imagem>> captor =
                    ArgumentCaptor.forClass(List.class);

            verify(imagensRepository).saveAll(captor.capture());

            List<Imagem> imagensSalvas = captor.getValue();

            assertThat(imagensSalvas).hasSize(2);

            var imagem = imagensSalvas.getFirst();

            assertThat(imagem.getObjectKey())
                    .isEqualTo(upload.objectKey());

            assertThat(resultado)
                    .containsExactlyElementsOf(imagensSalvas);

            assertThat(imagem.getBucket())
                    .isEqualTo(upload.bucket());

            var primeiraImagem = imagensSalvas.get(0);
            var segundaImagem = imagensSalvas.get(1);
            assertThat(primeiraImagem.getObjectKey())
                    .isEqualTo(upload.objectKey());

            assertThat(segundaImagem.getObjectKey())
                    .isEqualTo(upload2.objectKey());

            verify(storageService).upload(imagemFile, veiculo.getId());
            verifyNoMoreInteractions(storageService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao fazer upload do arquivo")
        void deveLancarExceaoAoFazerUploadDoArquivo() throws IOException {
            //Arrange
            var imagemFileArray = criarImagemFile();
            var imagemFile = imagemFileArray[0];
            var veiculo = criarVeiculoEntity();

            when(storageService.upload(imagemFile, veiculo.getId()))
                    .thenThrow(new IOException());

            //ACT
            var resultado = assertThrows(IOException.class,
                    () -> imagensService.criar(imagemFileArray, veiculo));
            //Assert
            assertThat(resultado)
                    .isInstanceOf(IOException.class);

            verify(storageService).upload(imagemFile, veiculo.getId());
            verifyNoMoreInteractions(storageService);

            verifyNoInteractions(imagensRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao salvar imagem no banco")
        void deveLancarExcecaoSalvarImagemBanco() throws IOException {
            //Arrange
            var imagemFileArray = criarImagemFile();
            var imagemFile = imagemFileArray[0];
            var veiculo = criarVeiculoEntity();
            var upload = criarUploadResult();
            when(storageService.upload(imagemFile, veiculo.getId()))
                    .thenReturn(upload);

            when(imagensRepository.saveAll(anyList()))
                    .thenThrow(new RuntimeException("Erro ao salvar"));
            //ACT
            var excecao = assertThrows(RuntimeException.class,
                    () -> imagensService.criar(imagemFileArray, veiculo));
            //Assert
            assertThat(excecao)
                    .hasMessage("Erro ao salvar");

            verify(storageService).upload(imagemFile, veiculo.getId());
            verify(storageService).delete(upload.objectKey());
            verify(imagensRepository).saveAll(anyList());

            verifyNoMoreInteractions(storageService);
        }
    }

    @Nested
    @DisplayName("Testes para definir a imagem como principal")
    class DefinirPrincipal {
        @Test
        @DisplayName("Deve definir a imagem como principal")
        void deveDefinirAImagemComoPrincipal() {
            //Arrange
            var imagem = ImagemEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comPrincipal(false)
                    .build();
            imagem.setVeiculo(criarVeiculoEntity());

            when(imagensRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(imagem));

            //ACT
            imagensService.definirPrincipal(ID_VALIDO);
            //Assert
            assertThat(imagem.isPrincipal())
                    .isTrue();


            var inOrder = inOrder(imagensRepository);

            inOrder.verify(imagensRepository).findById(ID_VALIDO);
            inOrder.verify(imagensRepository).desmarcarPrincipal(ID_VALIDO);


            verifyNoMoreInteractions(imagensRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao definir principal")
        void deveLancarExcecaoPrincipal() {
            //Arrange
            var imagem = criarImagemEntity();
            imagem.setVeiculo(criarVeiculoEntity());

            when(imagensRepository.findById(ID_VALIDO))
                    .thenThrow(new NotFoundException(IMAGEM, ID_VALIDO));
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> imagensService.definirPrincipal(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, IMAGEM, ID_VALIDO);

            verify(imagensRepository).findById(ID_VALIDO);
            verify(imagensRepository, never()).desmarcarPrincipal(imagem.getVeiculo().getId());
        }
    }

    @Nested
    @DisplayName("Testes da exclusão da image")
    class DeletarImagem {
        @Test
        @DisplayName("Deve deletar a imagem")
        void deveDeletarImagem() throws IOException {
            //Arrange
            var imagem = criarImagemEntity();

            when(imagensRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(imagem));

            //ACT
            imagensService.delete(ID_VALIDO);
            //Assert
            verify(imagensRepository).findById(ID_VALIDO);
            verify(storageService).delete(imagem.getObjectKey());
            verify(imagensRepository).delete(imagem);

            verifyNoMoreInteractions(imagensRepository, storageService);
        }

        @Test
        @DisplayName("Deve lançar exceção quando imagem não for encontrada")
        void deveLancarExcecaoImagemNaoEncontrada() throws IOException {
            //Arrange
            when(imagensRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> imagensService.delete(ID_VALIDO)
            );
            //Assert
            assertNotFoundResponseError(excecao, IMAGEM, ID_VALIDO);
            verify(imagensRepository).findById(ID_VALIDO);
            verify(storageService, never()).delete(anyString());
            verify(imagensRepository, never()).delete(any());

            verifyNoMoreInteractions(imagensRepository, storageService);

        }

        @Test
        @DisplayName("Deve lançar exceção ao deletar imagem local")
        void deveLancarExcecaoLocal() throws IOException {
            //Arrange
            var imagem = criarImagemEntity();

            when(imagensRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(imagem));
            doThrow(new IOException())
                    .when(storageService).delete(imagem.getObjectKey());
            //ACT
            var excecao = assertThrows(IOException.class,
                    () -> imagensService.delete(ID_VALIDO)
            );
            //Assert
            assertThat(excecao)
                    .isInstanceOf(IOException.class);

            verify(imagensRepository).findById(ID_VALIDO);
            verify(storageService).delete(imagem.getObjectKey());

            verify(imagensRepository, never()).delete(imagem);

            verifyNoMoreInteractions(imagensRepository, storageService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao deletar a imagem no banco")
        void deveLancarExcecaoDeletarImagemBanco() throws IOException {
            //Arrange
            var imagem = criarImagemEntity();

            when(imagensRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(imagem));

            doThrow(new RuntimeException("Erro ao deletar a imagem no banco"))
                    .when(imagensRepository).delete(imagem);
            //ACT
            var excecao = assertThrows(RuntimeException.class,
                    () -> imagensService.delete(ID_VALIDO)
            );
            //Assert
            assertThat(excecao)
                    .hasMessage("Erro ao deletar a imagem no banco");

            verify(imagensRepository).findById(ID_VALIDO);
            verify(storageService).delete(imagem.getObjectKey());
            verify(imagensRepository).delete(imagem);

            verifyNoMoreInteractions(imagensRepository, storageService);
        }
    }

    @Nested
    @DisplayName("Testes da busca da entidade da imagem")
    class BuscarImagem {
        @Test
        @DisplayName("Deve buscar a entidade imagem por ID")
        void deveBuscarImagemPorId() {
            //Arrange
            var imagem = criarImagemEntity();
            when(imagensRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(imagem));
            //ACT
            var resultado = imagensService.buscaImagem(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            Imagem::getId,
                            Imagem::getNomeOriginal,
                            Imagem::getObjectKey,
                            Imagem::getBucket,
                            Imagem::getContentType,
                            Imagem::getTamanho,
                            Imagem::isPrincipal
                    ).containsExactly(
                            ID_VALIDO,
                            "nomeImagemOriginal",
                            "imagens/2026/foto.jpg",
                            "bucketImagem",
                            "image/jpeg",
                            200L,
                            true
                    );

            verify(imagensRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(imagensRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar entidade imagem")
        void deveLancarExcecaoBuscarEntidadeImagem() {
            //Arrange
            when(imagensRepository.findById(ID_VALIDO))
                    .thenThrow(new NotFoundException(IMAGEM, ID_VALIDO));
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> imagensService.buscaImagem(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, IMAGEM, ID_VALIDO);
            verify(imagensRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(imagensRepository);
        }
    }
}
