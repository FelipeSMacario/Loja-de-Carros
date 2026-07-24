package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.UploadResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@ExtendWith(MockitoExtension.class)
class LocalStorageServiceTest {

    private LocalStorageService localStorageService;

    @BeforeEach
    void setup() {
        localStorageService = new LocalStorageService();
    }

    @AfterEach
    void cleanUp() throws IOException {
        Path uploads = Paths.get("uploads");

        if (Files.exists(uploads)) {
            Files.walk(uploads)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }


    @Nested
    @DisplayName("Teste de upload")
    class Upload {


        @Test
        @DisplayName("Deve fazer upload da imagem")
        void deveFazerUploadDoArquivo() throws IOException {

            MultipartFile file = new MockMultipartFile(
                    "file",
                    "foto.jpg",
                    "image/jpeg",
                    "imagem teste".getBytes()
            );

            var result = localStorageService.upload(file, 1L);


            assertThat(result.objectKey()).isNotBlank();
            assertThat(result.bucket()).isEqualTo("uploads");
            assertThat(result.nomeOriginal()).isEqualTo("foto.jpg");
            assertThat(result.contentType()).isEqualTo("image/jpeg");
            assertThat(result.tamanho()).isEqualTo((long) "imagem teste".getBytes().length);


            Path arquivoSalvo = Paths.get("uploads")
                    .resolve(result.objectKey());

            assertThat(Files.exists(arquivoSalvo)).isTrue();
        }


        @Test
        @DisplayName("Deve criar pasta do veículo ao fazer upload")
        void deveCriarPastaDoVeiculo() throws IOException {

            MultipartFile file = new MockMultipartFile(
                    "file",
                    "foto.jpg",
                    "image/jpeg",
                    "conteudo".getBytes()
            );


            UploadResult result = localStorageService.upload(file, 10L);


            Path pastaVeiculo = Paths.get("uploads")
                    .resolve("10");


            assertThat(Files.exists(pastaVeiculo)).isTrue();
            assertThat(Files.exists(Paths.get("uploads")
                    .resolve(result.objectKey()))).isTrue();
        }
    }


    @Nested
    @DisplayName("Teste de exclusão")
    class Delete {


        @Test
        @DisplayName("Deve deletar arquivo existente")
        void deveDeletarArquivo() throws IOException {

            MultipartFile file = new MockMultipartFile(
                    "file",
                    "foto.jpg",
                    "image/jpeg",
                    "conteudo".getBytes()
            );


            UploadResult result = localStorageService.upload(file, 1L);


            Path arquivo = Paths.get("uploads")
                    .resolve(result.objectKey());


            assertThat(Files.exists(arquivo)).isTrue();


            localStorageService.delete(result.objectKey());


            assertThat(Files.exists(arquivo)).isFalse();
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