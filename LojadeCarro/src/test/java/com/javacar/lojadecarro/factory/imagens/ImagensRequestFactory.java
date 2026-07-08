package com.javacar.lojadecarro.factory.imagens;

import com.javacar.lojadecarro.dto.request.ImagensRequest;

public class ImagensRequestFactory {
    private String url;
    private Long carroId;


    private ImagensRequestFactory() {
    }

    public static ImagensRequestFactory imagensRequestFactory() {
        return new ImagensRequestFactory();
    }

    public static ImagensRequestFactory criarRequest() {
        return new ImagensRequestFactory();
    }

    public ImagensRequestFactory comTodosOsCampos() {
        this.url = "https://bucket/imagens/onix.jpg";
        this.carroId = 1L;
        return this;
    }


    public ImagensRequestFactory comURL(String url) {
        this.url = url;
        return this;
    }

    public ImagensRequestFactory comId(Long idCarro) {
        this.carroId = idCarro;
        return this;
    }

    public ImagensRequest build() {
        return new ImagensRequest(url,
                carroId);
    }
}
