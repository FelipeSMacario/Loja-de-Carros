package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.config.storage.S3Config;
import com.javacar.lojadecarro.dto.response.UploadResult;
import com.javacar.lojadecarro.service.LocalStorageService;
import com.javacar.lojadecarro.service.S3StorageService;
import com.javacar.lojadecarro.service.StorageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {
        S3Config.class,
        S3StorageService.class,
        LocalStorageService.class
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "storage.provider=s3"
})
@DisplayName("Testes de integração do armazenamento S3")
class S3StorageServiceIntegrationTest {
    private static final String BUCKET = "loja-veiculos-test";

    @Autowired
    private StorageService storageService;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ApplicationContext applicationContext;

    static final LocalStackContainer LOCALSTACK =
            new LocalStackContainer(
                    DockerImageName.parse(
                            "localstack/localstack:4.14.0"
                    )
            ).withServices(LocalStackContainer.Service.S3);

    static {
        LOCALSTACK.start();
    }

    @DynamicPropertySource
    static void configurarS3(DynamicPropertyRegistry registry) {
        registry.add("storage.s3.bucket", () -> BUCKET);
        registry.add("storage.s3.region", LOCALSTACK::getRegion);
        registry.add(
                "storage.s3.endpoint",
                () -> LOCALSTACK
                        .getEndpointOverride(LocalStackContainer.Service.S3)
                        .toString()
        );
        registry.add(
                "storage.s3.access-key",
                LOCALSTACK::getAccessKey
        );
        registry.add(
                "storage.s3.secret-key",
                LOCALSTACK::getSecretKey
        );
    }

    @BeforeAll
    void criarBucket() {
        s3Client.createBucket(builder ->
                builder.bucket(BUCKET)
        );
    }

    @AfterAll
    void encerrarLocalStack() {
        LOCALSTACK.stop();
    }

    @Test
    @DisplayName("Deve enviar e recuperar arquivo no S3")
    void deveEnviarERecuperarArquivoNoS3() throws IOException {
        // Arrange
        var conteudo = "imagem de integração".getBytes();

        var file = new MockMultipartFile(
                "file",
                "foto.jpg",
                "image/jpeg",
                conteudo
        );

        // Act
        var upload = storageService.upload(file, 10L);

        var objeto = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(upload.objectKey())
                        .build()
        );

        // Assert
        assertThat(objeto.asByteArray())
                .isEqualTo(conteudo);

        assertThat(objeto.response().contentType())
                .isEqualTo("image/jpeg");

        assertThat(upload)
                .extracting(
                        UploadResult::bucket,
                        UploadResult::nomeOriginal,
                        UploadResult::contentType,
                        UploadResult::tamanho
                )
                .containsExactly(
                        BUCKET,
                        "foto.jpg",
                        "image/jpeg",
                        (long) conteudo.length
                );
    }

    @Test
    @DisplayName("Deve excluir arquivo armazenado no S3")
    void deveExcluirArquivoArmazenadoNoS3() throws IOException {
        // Arrange
        var file = new MockMultipartFile(
                "file",
                "foto-exclusao.jpg",
                "image/jpeg",
                "conteudo para exclusao".getBytes()
        );

        var upload = storageService.upload(file, 20L);

        var objetosAntes = s3Client.listObjectsV2(builder ->
                builder
                        .bucket(BUCKET)
                        .prefix(upload.objectKey())
        );

        assertThat(objetosAntes.contents())
                .extracting(S3Object::key)
                .containsExactly(upload.objectKey());

        // Act
        storageService.delete(upload.objectKey());

        // Assert
        var objetosDepois = s3Client.listObjectsV2(builder ->
                builder
                        .bucket(BUCKET)
                        .prefix(upload.objectKey())
        );

        assertThat(objetosDepois.contents())
                .isEmpty();
    }

    @Test
    @DisplayName("Deve ativar armazenamento S3")
    void deveAtivarArmazenamentoS3() {
        assertThat(storageService)
                .isInstanceOf(S3StorageService.class);

        assertThat(
                applicationContext.getBeansOfType(
                        LocalStorageService.class
                )
        ).isEmpty();
    }

    @Test
    @DisplayName("Deve baixar objeto existente no S3")
    void deveBaixarObjetoExistenteNoS3() throws IOException {
        //Arrange
        var objectKey = "%d/%s_%s".formatted(
                10L,
                UUID.randomUUID(),
                "Nome original"
        );

        var conteudoEsperado =
                "conteudo para download"
                        .getBytes(StandardCharsets.UTF_8);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(objectKey)
                        .contentType(MediaType.IMAGE_JPEG_VALUE)
                        .contentLength((long) conteudoEsperado.length)
                        .build(),
                RequestBody.fromBytes(conteudoEsperado)
        );

        //ACT
        var resultado = storageService.download(objectKey);
        //Assert
        assertThat(resultado)
                .isEqualTo(conteudoEsperado);
    }
}
