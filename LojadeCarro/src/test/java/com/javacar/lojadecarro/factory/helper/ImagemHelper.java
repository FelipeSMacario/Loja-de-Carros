package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.response.ImagemResponse;
import com.javacar.lojadecarro.dto.response.UploadResult;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.factory.imagem.ImagemEntityFactory;
import com.javacar.lojadecarro.factory.imagem.ImagemResponseFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public final class ImagemHelper {
    public static Imagem criarImagemEntity() {
        return ImagemEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    public static ImagemResponse criarImagemResponse() {
        return ImagemResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }

    public static MultipartFile[] criarImagemFile(){
        MultipartFile file = new MockMultipartFile(
                "files",
                "onix.jpg",
                "image/jpeg",
                "conteudo".getBytes()
        );

        return new MultipartFile[]{file};
    }

    public static MultipartFile[] criarListImagemFile(){
        MultipartFile file = new MockMultipartFile(
                "files",
                "onix.jpg",
                "image/jpeg",
                "conteudo".getBytes()
        );

        MultipartFile file2 = new MockMultipartFile(
                "files2",
                "onix2.jpg",
                "image/jpeg",
                "conteudo".getBytes()
        );

        return new MultipartFile[]{file, file2};
    }

    public static List<Imagem> criarListaImagem() {
        var imagemEntity = criarImagemEntity();
        return List.of(imagemEntity);
    }

    public static UploadResult criarUploadResult() {
        return new UploadResult(
                1L + "onix.jpg",
                "uploads",
                "onix.jpg",
                "image/jpeg",
                200L
        );
    }
}
