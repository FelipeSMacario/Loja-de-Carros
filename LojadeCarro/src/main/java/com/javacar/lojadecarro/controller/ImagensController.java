package com.javacar.lojadecarro.controller;


import com.javacar.lojadecarro.dto.response.ImagensResponse;
import com.javacar.lojadecarro.service.ImagensService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/imagens")
@Tag(name = "Imagens")
public class ImagensController {

    private final ImagensService imagensService;

    @PatchMapping("/{idImagem}")
    @Operation(summary = "Atualizar qual imagem será de perfil")
    public ResponseEntity<Void> update(
            @PathVariable Long idImagem) {

        log.info("Atualizando imagem {}", idImagem);

        imagensService.definirPrincipal(idImagem);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idImagem}")
    @Operation(summary = "Excluir imagem")
    public ResponseEntity<Void> delete(
            @PathVariable Long idImagem) throws IOException {

        log.info("Removendo imagem {}", idImagem);

        imagensService.delete(idImagem);

        return ResponseEntity.noContent().build();
    }
}
