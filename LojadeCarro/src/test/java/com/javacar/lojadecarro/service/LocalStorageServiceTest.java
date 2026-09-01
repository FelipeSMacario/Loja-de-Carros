package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.UploadResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalStorageServiceTest {

    private LocalStorageService localStorageService;
    @TempDir
    Path root;


    @BeforeEach
    void setup() {
        localStorageService =
                new LocalStorageService(root.toString());
    }


    @Nested
    @DisplayName("Teste de upload")
    class Upload {

        @Test
        @DisplayName("Deve fazer upload da imagem")
        void deveFazerUploadDoArquivo() throws IOException {
            var conteudo = "imagem teste".getBytes();

            MultipartFile file = new MockMultipartFile(
                    "file",
                    "foto.jpg",
                    "image/jpeg",
                    conteudo
            );

            var result = localStorageService.upload(file, 1L);

            assertThat(result)
                    .extracting(
                            UploadResult::bucket,
                            UploadResult::nomeOriginal,
                            UploadResult::contentType,
                            UploadResult::tamanho
                    )
                    .containsExactly(
                            "uploads",
                            "foto.jpg",
                            "image/jpeg",
                            (long) conteudo.length
                    );

            assertThat(result.objectKey())
                    .startsWith("1/")
                    .endsWith("_foto.jpg");

            var arquivoSalvo = root.resolve(result.objectKey());

            assertThat(arquivoSalvo)
                    .exists()
                    .hasBinaryContent(conteudo);
        }


        @Test
        @DisplayName("Deve criar pasta do veículo ao fazer upload")
        void deveCriarPastaDoVeiculo() throws IOException {
            var file = new MockMultipartFile(
                    "file",
                    "foto.jpg",
                    "image/jpeg",
                    "conteudo".getBytes()
            );

            var result = localStorageService.upload(file, 10L);

            assertThat(root.resolve("10"))
                    .isDirectory();

            assertThat(root.resolve(result.objectKey()))
                    .exists();
        }

        @Test
        @DisplayName("Deve rejeitar arquivo sem nome original")
        void deveRejeitarArquivoSemNomeOriginal() {
            var file = new MockMultipartFile(
                    "file",
                    null,
                    "image/jpeg",
                    "conteudo".getBytes()
            );

            assertThatThrownBy(() ->
                    localStorageService.upload(file, 1L)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Nome original do arquivo é obrigatório.");

            assertThat(root.resolve("1")).doesNotExist();
        }

        @Test
        @DisplayName("Deve rejeitar arquivo com nome em branco original")
        void deveRejeitarArquivoComNomeEmBranco() {
            var file = new MockMultipartFile(
                    " ",
                    null,
                    "image/jpeg",
                    "conteudo".getBytes()
            );

            assertThatThrownBy(() ->
                    localStorageService.upload(file, 1L)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Nome original do arquivo é obrigatório.");

            assertThat(root.resolve("1")).doesNotExist();
        }

        @Test
        @DisplayName("Deve remover arquivo parcial quando o upload falhar")
        void deveRemoverArquivoParcialQuandoUploadFalhar() throws IOException {
            var file = mock(MultipartFile.class);

            when(file.getOriginalFilename())
                    .thenReturn("foto.jpg");

            doAnswer(invocation -> {
                Path destino = invocation.getArgument(0);

                Files.writeString(destino, "conteúdo parcial");

                throw new IOException("Falha durante a transferência");
            }).when(file).transferTo(any(Path.class));

            assertThatThrownBy(() ->
                    localStorageService.upload(file, 1L)
            )
                    .isInstanceOf(IOException.class)
                    .hasMessage("Falha durante a transferência");

            assertThat(root.resolve("1"))
                    .isDirectory()
                    .isEmptyDirectory();
        }
    }


    @Nested
    @DisplayName("Teste de exclusão")
    class Delete {

        @Test
        @DisplayName("Deve deletar arquivo existente")
        void deveDeletarArquivo() throws IOException {
            var file = new MockMultipartFile(
                    "file",
                    "foto.jpg",
                    "image/jpeg",
                    "conteudo".getBytes()
            );

            var result = localStorageService.upload(file, 1L);
            var arquivo = root.resolve(result.objectKey());

            assertThat(arquivo).exists();

            localStorageService.delete(result.objectKey());

            assertThat(arquivo).doesNotExist();
        }

        @Test
        @DisplayName("Deve rejeitar chave fora do diretório configurado")
        void deveRejeitarPathTraversal() {

            assertThatThrownBy(() ->
                    localStorageService.delete("../arquivo.jpg")
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Chave de objeto inválida.");
        }

        @Test
        @DisplayName("Não deve lançar exceção ao deletar arquivo inexistente")
        void naoDeveFalharAoDeletarArquivoInexistente() {

            assertThatCode(() ->
                    localStorageService.delete("1/arquivo-inexistente.jpg")
            ).doesNotThrowAnyException();
        }
    }
}