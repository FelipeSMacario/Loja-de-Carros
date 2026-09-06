package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.config.storage.S3Properties;
import com.javacar.lojadecarro.dto.response.UploadResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do armazenamento no S3")
class S3StorageServiceTest {
    private static final String BUCKET = "loja-veiculos-test";

    @Mock
    private S3Client s3Client;

    private S3StorageService s3StorageService;

    @BeforeEach
    void setup() {
        var properties = new S3Properties(
                BUCKET,
                "us-east-1",
                null,
                null,
                null
        );

        s3StorageService = new S3StorageService(
                s3Client,
                properties
        );
    }

    @Nested
    @DisplayName("Testes de upload")
    class Upload {

        @Test
        @DisplayName("Deve enviar arquivo para o S3")
        void deveEnviarArquivoParaOS3() throws IOException {
            // Arrange
            var conteudo = "conteúdo da imagem".getBytes();

            var file = new MockMultipartFile(
                    "file",
                    "foto.jpg",
                    "image/jpeg",
                    conteudo
            );

            var requestCaptor =
                    ArgumentCaptor.forClass(PutObjectRequest.class);

            // Act
            var resultado = s3StorageService.upload(file, 10L);

            // Assert
            verify(s3Client).putObject(
                    requestCaptor.capture(),
                    any(RequestBody.class)
            );

            var request = requestCaptor.getValue();

            assertThat(request.bucket())
                    .isEqualTo(BUCKET);

            assertThat(request.key())
                    .startsWith("10/")
                    .endsWith("_foto.jpg");

            assertThat(request.contentType())
                    .isEqualTo("image/jpeg");

            assertThat(request.contentLength())
                    .isEqualTo((long) conteudo.length);

            assertThat(resultado)
                    .extracting(
                            UploadResult::objectKey,
                            UploadResult::bucket,
                            UploadResult::nomeOriginal,
                            UploadResult::contentType,
                            UploadResult::tamanho
                    )
                    .containsExactly(
                            request.key(),
                            BUCKET,
                            "foto.jpg",
                            "image/jpeg",
                            (long) conteudo.length
                    );

            verifyNoMoreInteractions(s3Client);
        }

        @Test
        @DisplayName("Deve usar content type padrão quando não for informado")
        void deveUsarContentTypePadrao() throws IOException {
            // Arrange
            var file = new MockMultipartFile(
                    "file",
                    "foto.jpg",
                    null,
                    "conteudo".getBytes()
            );

            var captor = ArgumentCaptor.forClass(PutObjectRequest.class);

            // Act
            var resultado = s3StorageService.upload(file, 10L);

            // Assert
            verify(s3Client).putObject(
                    captor.capture(),
                    any(RequestBody.class)
            );

            assertThat(captor.getValue().contentType())
                    .isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);

            assertThat(resultado.contentType())
                    .isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);

            verifyNoMoreInteractions(s3Client);
        }

        @Test
        @DisplayName("Deve rejeitar arquivo sem nome original")
        void deveRejeitarArquivoSemNomeOriginal() {
            // Arrange
            var file = new MockMultipartFile(
                    "file",
                    " ",
                    "image/jpeg",
                    "conteudo".getBytes()
            );

            // Act + Assert
            assertThatThrownBy(() ->
                    s3StorageService.upload(file, 10L)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Nome original do arquivo é obrigatório.");

            verifyNoInteractions(s3Client);
        }

        @Test
        @DisplayName("Deve converter falha do SDK em IOException")
        void deveConverterFalhaDoSdkEmIOException() {
            // Arrange
            var file = new MockMultipartFile(
                    "file",
                    "foto.jpg",
                    "image/jpeg",
                    "conteudo".getBytes()
            );

            when(s3Client.putObject(
                    any(PutObjectRequest.class),
                    any(RequestBody.class)
            )).thenThrow(
                    SdkClientException.create("S3 indisponível")
            );

            // Act + Assert
            assertThatThrownBy(() ->
                    s3StorageService.upload(file, 10L)
            )
                    .isInstanceOf(IOException.class)
                    .hasMessage("Erro ao enviar arquivo para o S3.")
                    .hasCauseInstanceOf(SdkClientException.class);

            verify(s3Client).putObject(
                    any(PutObjectRequest.class),
                    any(RequestBody.class)
            );

            verifyNoMoreInteractions(s3Client);
        }
    }

    @Nested
    @DisplayName("Testes de exclusão")
    class Delete {

        @Test
        @DisplayName("Deve excluir objeto do S3")
        void deveExcluirObjetoDoS3() throws IOException {
            // Arrange
            var objectKey = "10/identificador_foto.jpg";

            var requestCaptor =
                    ArgumentCaptor.forClass(DeleteObjectRequest.class);

            // Act
            s3StorageService.delete(objectKey);

            // Assert
            verify(s3Client).deleteObject(
                    requestCaptor.capture()
            );

            assertThat(requestCaptor.getValue())
                    .extracting(
                            DeleteObjectRequest::bucket,
                            DeleteObjectRequest::key
                    )
                    .containsExactly(
                            BUCKET,
                            objectKey
                    );

            verifyNoMoreInteractions(s3Client);
        }

        @Test
        @DisplayName("Deve converter falha do SDK ao excluir objeto")
        void deveConverterFalhaDoSdkAoExcluirObjeto() {
            // Arrange
            var objectKey = "10/identificador_foto.jpg";

            when(s3Client.deleteObject(
                    any(DeleteObjectRequest.class)
            )).thenThrow(
                    SdkClientException.create("S3 indisponível")
            );

            // Act + Assert
            assertThatThrownBy(() ->
                    s3StorageService.delete(objectKey)
            )
                    .isInstanceOf(IOException.class)
                    .hasMessage("Erro ao excluir arquivo do S3.")
                    .hasCauseInstanceOf(SdkClientException.class);

            verify(s3Client).deleteObject(
                    any(DeleteObjectRequest.class)
            );

            verifyNoMoreInteractions(s3Client);
        }
    }

    @Nested
    @DisplayName("Testes do download")
    class Download {
        @Test
        @DisplayName("Deve baixar arquivo armazenado no S3")
        void deveBaixarArquivoArmazenadoNoS3() throws IOException {
            // Arrange
            var objectKey = "10/identificador_foto.jpg";
            var conteudo = "conteudo da imagem".getBytes();

            var response = GetObjectResponse.builder()
                    .contentType("image/jpeg")
                    .contentLength((long) conteudo.length)
                    .build();

            when(s3Client.getObjectAsBytes(
                    any(GetObjectRequest.class)
            )).thenReturn(
                    ResponseBytes.fromByteArray(
                            response,
                            conteudo
                    )
            );

            var captor =
                    ArgumentCaptor.forClass(GetObjectRequest.class);

            // Act
            var resultado = s3StorageService.download(objectKey);

            // Assert
            assertThat(resultado)
                    .isEqualTo(conteudo);

            verify(s3Client).getObjectAsBytes(
                    captor.capture()
            );

            assertThat(captor.getValue())
                    .extracting(
                            GetObjectRequest::bucket,
                            GetObjectRequest::key
                    )
                    .containsExactly(
                            BUCKET,
                            objectKey
                    );

            verifyNoMoreInteractions(s3Client);
        }

        @Test
        @DisplayName("Deve converter falha do SDK em IOException ao baixar arquivo")
        void deveConverterFalhaDoSdkEmIOExceptionAoBaixarArquivo() {
            //Arrange
            var objectKey = "10/identificador_foto.jpg";
            when(s3Client.getObjectAsBytes(
                    any(GetObjectRequest.class)
            )).thenThrow(SdkClientException.create("S3 indisponível"));
            //ACT
            var exception = assertThrows(IOException.class,
                    () -> s3StorageService.download(objectKey));
            //Assert
            assertThat(exception)
                    .hasMessage("Erro ao baixar arquivo do S3.")
                    .hasCauseInstanceOf(SdkClientException.class);

            verify(s3Client).getObjectAsBytes(any(GetObjectRequest.class));

            verifyNoMoreInteractions(s3Client);
        }
    }


}
