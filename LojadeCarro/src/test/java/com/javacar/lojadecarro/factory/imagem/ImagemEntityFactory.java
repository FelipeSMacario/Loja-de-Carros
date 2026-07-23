package com.javacar.lojadecarro.factory.imagem;

import com.javacar.lojadecarro.entity.Imagem;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.javacar.lojadecarro.utils.Utils.ZONE;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ImagemEntityFactory {

    private final Imagem imagem;

    private ImagemEntityFactory() {
        this.imagem = new Imagem();
    }

    public static ImagemEntityFactory criarEntity() {
        return new ImagemEntityFactory();
    }

    public ImagemEntityFactory comTodosOsCampos() {
        imagem.setId(1L);
        imagem.setNomeOriginal("nomeImagemOriginal");
        imagem.setObjectKey("imagens/2026/foto.jpg");
        imagem.setBucket("bucketImagem");
        imagem.setContentType("image/jpeg");
        imagem.setTamanho(200L);
        imagem.setPrincipal(true);
        imagem.setDataCadastro(LocalDateTime.now(ZONE));
        return this;
    }
    public ImagemEntityFactory comId(Long id) {
        imagem.setId(id);
        return this;
    }

    public Imagem build() {
        return imagem;
    }
}
