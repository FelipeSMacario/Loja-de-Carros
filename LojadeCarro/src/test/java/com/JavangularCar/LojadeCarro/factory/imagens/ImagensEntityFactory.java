package com.JavangularCar.LojadeCarro.factory.imagens;

import com.JavangularCar.LojadeCarro.entity.Imagens;
import com.JavangularCar.LojadeCarro.factory.carro.CarroEntityFactory;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ImagensEntityFactory {

    private final Imagens imagens;

    private ImagensEntityFactory() {
        this.imagens = new Imagens();
    }

    public static ImagensEntityFactory criarEntity() {
        return new ImagensEntityFactory();
    }

    public ImagensEntityFactory comTodosOsCampos() {
        imagens.setId(1L);
        imagens.setUrl("https://bucket/imagens/onix.jpg");
        imagens.setNomeArquivo("onix.jpg");
        imagens.setContentType("image/jpeg");
        imagens.setTamanho(200L);
        imagens.setPrincipal(true);
        imagens.setCarro(CarroEntityFactory.criarEntity().comTodosOsCampos().build());
        return this;
    }


    public ImagensEntityFactory comId(Long id) {
        imagens.setId(id);
        return this;
    }

    public Imagens build() {
        return imagens;
    }
}
