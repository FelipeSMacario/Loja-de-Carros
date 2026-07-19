package com.javacar.lojadecarro.controller;


import com.javacar.lojadecarro.service.ImagensService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/imagens")
@Tag(name = "Imagens")
public class ImagensController {

    private final ImagensService imagensService;

    @PatchMapping("/{idImagem}/perfil")
    @Operation(summary = "Atualizar qual imagem será de perfil")
    public ResponseEntity<Void> update(
            @PathVariable Long idImagem) {
        log.debug("Definindo a imagem com id: {} como perfil", idImagem);

        imagensService.definirPrincipal(idImagem);

        log.info("Imagem com id: {} definida como perfil", idImagem);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idImagem}")
    @Operation(summary = "Excluir imagem")
    public ResponseEntity<Void> delete(
            @PathVariable Long idImagem) throws IOException {

        log.debug("Removendo imagem {}", idImagem);

        imagensService.delete(idImagem);

        log.info("Imagem com id: {} deletada", idImagem);

        return ResponseEntity.noContent().build();
    }
}
