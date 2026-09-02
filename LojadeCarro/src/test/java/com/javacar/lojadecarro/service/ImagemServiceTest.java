package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.UploadResult;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.imagem.ImagemEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import com.javacar.lojadecarro.repository.ImagensRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
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
    @Mock
    private StorageTransactionSupport storageTransactionSupport;

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

            assertThat(veiculo.getImagens())
                    .containsExactlyElementsOf(imagensSalvas)
                    .allSatisfy(imagemSalva ->
                            assertThat(imagemSalva.getVeiculo())
                                    .isSameAs(veiculo)
                    );

            assertThat(veiculo.getImagens())
                    .filteredOn(Imagem::isPrincipal)
                    .containsExactly(imagensSalvas.getFirst());

            verify(storageService).upload(imagemFile, veiculo.getId());
            verify(storageTransactionSupport)
                    .deleteOnRollback(primeiraImagem.getObjectKey());

            verify(storageTransactionSupport)
                    .deleteOnRollback(segundaImagem.getObjectKey());

            verifyNoMoreInteractions(storageService, storageTransactionSupport);
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
            var imagemFileArray = criarNovoImagemFile();
            var imagemFile = imagemFileArray[0];
            var imagemFile2 = imagemFileArray[1];
            var veiculo = criarVeiculoEntity();
            var upload = criarUploadResult();
            var upload2 = criarNovoUploadResult(imagemFile2);
            when(storageService.upload(imagemFile, veiculo.getId()))
                    .thenReturn(upload);
            when(storageService.upload(imagemFile2, veiculo.getId()))
                    .thenReturn(upload2);

            when(imagensRepository.saveAll(anyList()))
                    .thenThrow(new RuntimeException("Erro ao salvar"));
            //ACT
            var excecao = assertThrows(RuntimeException.class,
                    () -> imagensService.criar(imagemFileArray, veiculo));
            //Assert
            assertThat(excecao)
                    .hasMessage("Erro ao salvar");

            assertThat(veiculo.getImagens())
                    .isEmpty();

            verify(storageService).upload(imagemFile, veiculo.getId());
            verify(storageService).upload(imagemFile2, veiculo.getId());
            verify(storageService).delete(upload.objectKey());
            verify(storageService).delete(upload2.objectKey());

            verifyNoMoreInteractions(storageService, imagensRepository);
        }

        @Test
        @DisplayName("Deve deletar as imagens quando upload falhar")
        void deveDeletarImagensQuandoUploadFalhar() throws IOException {
            //Arrange
            var imagemFileArray = criarNovoImagemFile();
            var imagemFile = imagemFileArray[0];
            var imagemFile2 = imagemFileArray[1];
            var veiculo = criarVeiculoEntity();

            assertThat(veiculo.getImagens())
                    .isEmpty();

            var upload = criarUploadResult();
            when(storageService.upload(imagemFile, veiculo.getId()))
                    .thenReturn(upload);
            when(storageService.upload(imagemFile2, veiculo.getId()))
                    .thenThrow(new IOException("Erro ao realizar upload dos arquivos"));

            //ACT
            var excecao = assertThrows(IOException.class,
                    () -> imagensService.criar(imagemFileArray, veiculo));
            //Assert
            assertThat(excecao)
                    .hasMessage("Erro ao realizar upload dos arquivos");

            assertThat(veiculo.getImagens())
                    .isEmpty();



            verify(storageService).upload(imagemFile, veiculo.getId());
            verify(storageService).upload(imagemFile2, veiculo.getId());
            verify(storageService).delete(upload.objectKey());
            verify(imagensRepository, never()).saveAll(anyList());

            verifyNoMoreInteractions(
                    storageService,
                    imagensRepository
            );
        }
    }

    @Nested
    @DisplayName("Testes para definir a imagem como principal")
    class DefinirPrincipal {
        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"DISPONIVEL", "PAUSADO"}
        )
        @DisplayName("Deve definir a imagem como principal com status permitido")
        void deveDefinirAImagemComoPrincipal(StatusVeiculo statusVeiculo) {
            //Arrange
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comStatus(statusVeiculo)
                    .build();
            var imagemA = ImagemEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comVeiculo(veiculo)
                    .comPrincipal(false)
                    .build();

            var imagemB = ImagemEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comVeiculo(veiculo)
                    .comId(2L)
                    .comPrincipal(true)
                    .build();

            assertThat(imagemA.isPrincipal()).isFalse();
            assertThat(imagemB.isPrincipal()).isTrue();

            List<Imagem> imagens = new ArrayList<>();
            imagens.add(imagemA);
            imagens.add(imagemB);

            when(imagensRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(imagemA));

            when(imagensRepository.findByVeiculoId(veiculo.getId()))
                    .thenReturn(imagens);

            //ACT
            imagensService.definirPrincipal(ID_VALIDO);
            //Assert
            assertThat(imagemA.isPrincipal())
                    .isTrue();
            assertThat(imagemB.isPrincipal()).isFalse();

            verify(imagensRepository).findById(ID_VALIDO);
            verify(imagensRepository).findByVeiculoId(veiculo.getId());

            verifyNoMoreInteractions(imagensRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar a imagem ao definir principal")
        void deveLancarExcecaoAoBuscarImagemAoDefinirPrincipal() {
            //Arrange
            var imagem = criarImagemEntity();
            imagem.setVeiculo(criarVeiculoEntity());

            when(imagensRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> imagensService.definirPrincipal(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, IMAGEM, ID_VALIDO);

            verify(imagensRepository).findById(ID_VALIDO);
            verify(imagensRepository, never()).findByVeiculoId(imagem.getVeiculo().getId());

            verifyNoMoreInteractions(imagensRepository);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        void deveLancarExcecaoAoDefinirImagemComoPrincipalComStatusProibido(StatusVeiculo statusVeiculo) {
            //Arrange
            var imagemA = ImagemEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comPrincipal(false)
                    .build();
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comStatus(statusVeiculo)
                    .build();
            imagemA.setVeiculo(veiculo);

            when(imagensRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(imagemA));

            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> imagensService.definirPrincipal(ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "Somente anúncios disponíveis ou pausados podem ser editados.");

            verify(imagensRepository).findById(ID_VALIDO);
            verify(imagensRepository, never()).findByVeiculoId(anyLong());

            verifyNoMoreInteractions(imagensRepository);
            verifyNoInteractions(storageService);
        }
    }

    @Nested
    @DisplayName("Testes da exclusão da image")
    class DeletarImagem {
        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"DISPONIVEL", "PAUSADO"}
        )
        @DisplayName("Deve deletar a imagem com status permitido")
        void deveDeletarImagemComStatusPermitido(StatusVeiculo statusVeiculo) throws IOException {
            //Arrange
            var imagem = ImagemEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .build();
            List<Imagem> imagens = new ArrayList<>();
            imagens.add(imagem);

            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comImagens(imagens)
                    .comStatus(statusVeiculo)
                    .build();
            imagem.setVeiculo(veiculo);
            veiculo.setImagens(imagens);

            when(imagensRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(imagem));

            //ACT
            imagensService.delete(ID_VALIDO);

            assertThat(veiculo.getImagens())
                    .isEmpty();
            //Assert
            verify(imagensRepository).findById(ID_VALIDO);
            verify(imagensRepository).flush();
            verify(storageTransactionSupport)
                    .deleteAfterCommit(imagem.getObjectKey());

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

            verifyNoMoreInteractions(imagensRepository, storageService);

        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        @DisplayName("Deve lançar exceção ao deletar imagem com status proíbido")
        void deveLancarExcecaoAoDeletarImagemComStatusProibido(StatusVeiculo statusVeiculo) {
            //Arrange
            var imagemA = ImagemEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comVeiculo( VeiculoEntityFactory
                            .criarEntity()
                            .comTodosOsCampos()
                            .comStatus(statusVeiculo)
                            .build())
                    .comPrincipal(false)
                    .build();

            when(imagensRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(imagemA));

            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> imagensService.delete(ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "Somente anúncios disponíveis ou pausados podem ser editados.");

            verify(imagensRepository).findById(ID_VALIDO);
            verify(imagensRepository, never()).flush();

            verifyNoMoreInteractions(imagensRepository);
            verifyNoInteractions(storageService);
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
                    .thenReturn(Optional.empty());
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
