package com.javacar.lojadecarro.controller;


import com.javacar.lojadecarro.service.ImagensService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/imagens")
@Tag(name = "Imagens")
public class ImagensController {

    private final ImagensService imagensService;

    @PatchMapping("/{idImagem}/principal")
    @Operation(summary = "Atualizar qual imagem será de perfil")
    public ResponseEntity<Void> definirPrincipal(
            @PathVariable Long idImagem) {
        log.debug("Definindo a imagem com id: {} como perfil", idImagem);

        imagensService.definirPrincipal(idImagem);

        log.info("Imagem com id: {} definida como perfil", idImagem);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idImagem}")
    @Operation(summary = "Excluir imagem")
    public ResponseEntity<Void> deletar(
            @PathVariable Long idImagem) throws IOException {

        log.debug("Removendo imagem {}", idImagem);

        imagensService.delete(idImagem);

        log.info("Imagem com id: {} deletada", idImagem);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idImagem}/conteudo")
    @Operation(summary = "Baixar conteúdo da imagem")
    public ResponseEntity<byte[]> baixar(
            @PathVariable Long idImagem
    ) throws IOException {
        log.debug("Baixando conteúdo da imagem: {}", idImagem);

        var download = imagensService.download(idImagem);

        var contentDisposition = ContentDisposition
                .inline()
                .filename(
                        download.nomeOriginal(),
                        StandardCharsets.UTF_8
                )
                .build();

        return ResponseEntity
                .ok()
                .contentType(
                        MediaType.parseMediaType(
                                download.contentType()
                        )
                )
                .contentLength(download.conteudo().length)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition.toString()
                )
                .body(download.conteudo());
    }
}
