package com.javacar.lojadecarro.factory.imagens;

import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.factory.carro.CarroEntityFactory;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ImagensEntityFactory {

    private final Imagem imagens;

    private ImagensEntityFactory() {
        this.imagens = new Imagem();
    }

    public static ImagensEntityFactory criarEntity() {
        return new ImagensEntityFactory();
    }

    public ImagensEntityFactory comTodosOsCampos() {
        imagens.setId(1L);
//        imagens.setUrl("https://bucket/imagens/onix.jpg");
//        imagens.setNomeArquivo("onix.jpg");
        imagens.setContentType("image/jpeg");
        imagens.setTamanho(200L);
        imagens.setPrincipal(true);
        imagens.setVeiculo(CarroEntityFactory.criarEntity().comTodosOsCampos().build());
        return this;
    }


    public ImagensEntityFactory comId(Long id) {
        imagens.setId(id);
        return this;
    }

    public Imagem build() {
        return imagens;
    }
}
