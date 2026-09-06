package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.config.storage.S3Properties;
import com.javacar.lojadecarro.dto.response.UploadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "storage",
        name = "provider",
        havingValue = "s3"
)
public class S3StorageService implements StorageService{
    private final S3Client s3Client;
    private final S3Properties properties;

    @Override
    public byte[] download(String objectKey) throws IOException {
        var request = GetObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .build();

        try {
            return s3Client
                    .getObjectAsBytes(request)
                    .asByteArray();
        } catch (SdkException exception) {
            throw new IOException(
                    "Erro ao baixar arquivo do S3.",
                    exception
            );
        }
    }

    @Override
    public UploadResult upload(
            MultipartFile file,
            Long idVeiculo
    ) throws IOException {
        var nomeOriginal = extrairNomeOriginal(file);

        var objectKey = "%d/%s_%s".formatted(
                idVeiculo,
                UUID.randomUUID(),
                nomeOriginal
        );

        var contentType = Optional
                .ofNullable(file.getContentType())
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        var request = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();

        try (var inputStream = file.getInputStream()) {
            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            inputStream,
                            file.getSize()
                    )
            );
        } catch (SdkException exception) {
            throw new IOException(
                    "Erro ao enviar arquivo para o S3.",
                    exception
            );
        }

        return new UploadResult(
                objectKey,
                properties.bucket(),
                nomeOriginal,
                contentType,
                file.getSize()
        );
    }

    @Override
    public void delete(String objectKey) throws IOException {
        var request = DeleteObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .build();

        try {
            s3Client.deleteObject(request);
        } catch (SdkException exception) {
            throw new IOException(
                    "Erro ao excluir arquivo do S3.",
                    exception
            );
        }
    }

    private String extrairNomeOriginal(MultipartFile file) {
        var nomeOriginal = file.getOriginalFilename();

        if (nomeOriginal == null || nomeOriginal.isBlank()) {
            throw new IllegalArgumentException(
                    "Nome original do arquivo é obrigatório."
            );
        }

        return Paths.get(nomeOriginal)
                .getFileName()
                .toString();
    }
}
