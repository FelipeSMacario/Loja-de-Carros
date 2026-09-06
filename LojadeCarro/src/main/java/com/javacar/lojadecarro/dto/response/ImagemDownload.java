package com.javacar.lojadecarro.dto.response;

import lombok.ToString;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class ImagemDownload {

    @ToString.Exclude
    byte[] conteudo;

    String contentType;
    String nomeOriginal;
}
