package com.javacar.lojadecarro.config.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(S3Properties.class)
@ConditionalOnProperty(
        prefix = "storage",
        name = "provider",
        havingValue = "s3"
)
public class S3Config   {
    @Bean
    public S3Client s3Client(S3Properties properties) {
        var builder = S3Client.builder()
                .region(Region.of(properties.region()));

        if (properties.endpoint() != null) {
            validarCredenciaisLocais(properties);

            builder
                    .endpointOverride(properties.endpoint())
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(
                                            properties.accessKey(),
                                            properties.secretKey()
                                    )
                            )
                    )
                    .forcePathStyle(true);
        } else {
            builder.credentialsProvider(
                    DefaultCredentialsProvider.create()
            );
        }

        return builder.build();
    }

    private void validarCredenciaisLocais(
            S3Properties properties
    ) {
        if (!StringUtils.hasText(properties.accessKey()) ||
                !StringUtils.hasText(properties.secretKey())) {
            throw new IllegalStateException(
                    "Credenciais são obrigatórias para endpoint S3 customizado."
            );
        }
    }
}
