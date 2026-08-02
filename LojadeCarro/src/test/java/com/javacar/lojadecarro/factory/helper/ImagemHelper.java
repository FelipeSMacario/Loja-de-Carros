package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.response.UploadResult;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.factory.imagem.ImagemEntityFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public final class ImagemHelper extends BaseHelper {
    public static Imagem criarImagemEntity() {
        return ImagemEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }


    public static MultipartFile[] criarImagemFile() {
        MultipartFile file = new MockMultipartFile(
                "files",
                "onix.jpg",
                "image/jpeg",
                "conteudo".getBytes()
        );

        return new MultipartFile[]{file};
    }

    public static MultipartFile[] criarListImagemFile() {
        MultipartFile file = new MockMultipartFile(
                "files",
                "onix.jpg",
                "image/jpeg",
                "conteudo".getBytes()
        );

        MultipartFile file2 = new MockMultipartFile(
                "files2",
                "onix2.jpg",
                "image2/jpeg",
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

    public static MockMultipartFile imagem(String nome) {
        return new MockMultipartFile(
                "files",
                nome,
                MediaType.IMAGE_JPEG_VALUE,
                "imagem".getBytes()
        );
    }

    public static void assertImagem(ResultActions result,
                                    Long primeiroId,
                                    Long segundoId,
                                    String primeiroNomeOriginal,
                                    String segundoNomeOriginal,
                                    String primeiroObjetKey,
                                    String segundoObjetKey,
                                    boolean primeiroPrincipal,
                                    boolean segundoPrincipal
    ) throws Exception {
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(primeiroId))
                .andExpect(jsonPath("$[1].id").value(segundoId))
                .andExpect(jsonPath("$[0].nomeOriginal").value(primeiroNomeOriginal))
                .andExpect(jsonPath("$[1].nomeOriginal").value(segundoNomeOriginal))
                .andExpect(jsonPath("$[0].objectKey").value(primeiroObjetKey))
                .andExpect(jsonPath("$[1].objectKey").value(segundoObjetKey))
                .andExpect(jsonPath("$[0].principal").value(primeiroPrincipal))
                .andExpect(jsonPath("$[1].principal").value(segundoPrincipal));
    }

    public static UploadResult criarUploadValido() {
        return new UploadResult(
                "veiculos/1/imagem.jpg",
                "bucket-test",
                "imagem.jpg",
                "image/jpeg",
                1024L
        );
    }

    public static UploadResult criarUploadValido2() {
        return new UploadResult(
                "veiculos/2/imagem.jpg",
                "bucket2-test",
                "imagem2.jpg",
                "image2/jpeg",
                1024L
        );
    }


}
