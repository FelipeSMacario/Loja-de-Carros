package com.javacar.lojadecarro.dto.response;

public record ImagemDownload(
        byte[] conteudo,
        String contentType,
        String nomeOriginal
) {
}
