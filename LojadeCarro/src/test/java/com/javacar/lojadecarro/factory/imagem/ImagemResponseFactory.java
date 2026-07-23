package com.javacar.lojadecarro.factory.imagem;

import com.javacar.lojadecarro.dto.response.ImagemResponse;

public class ImagemResponseFactory {
    private Long id;
    private String nomeOriginal;
    private String objectKey;
    private boolean principal;

    private ImagemResponseFactory() {
    }

    public static ImagemResponseFactory criarResponse() {
        return new ImagemResponseFactory();
    }

    public ImagemResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.nomeOriginal = "nomeImagemOriginal";
        this.objectKey = "imagens/2026/foto.jpg";
        this.principal = true;
        return this;
    }

    public ImagemResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }

    public ImagemResponseFactory comNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
        return this;
    }

    public ImagemResponseFactory comObjectKey(String objectKey) {
        this.objectKey = objectKey;
        return this;
    }

    public ImagemResponseFactory comPrincipal(boolean principal) {
        this.principal = principal;
        return this;
    }

    public ImagemResponse build() {
        return new ImagemResponse(
                id,
                nomeOriginal,
                objectKey,
                principal);
    }
}
