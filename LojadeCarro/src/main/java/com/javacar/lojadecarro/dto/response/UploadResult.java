package com.javacar.lojadecarro.dto.response;

public record UploadResult(String objectKey,
                           String bucket,
                           String nomeOriginal,
                           String contentType,
                           Long tamanho) {
}
