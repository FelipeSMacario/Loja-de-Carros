package com.javacar.lojadecarro.config.storage;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "storage.s3")
public record S3Properties(
        @NotBlank String bucket,
        @NotBlank String region,
        URI endpoint,
        String accessKey,
        String secretKey
) {
}
