package com.javacar.lojadecarro.controller;


import com.javacar.lojadecarro.dto.request.ImagensRequest;
import com.javacar.lojadecarro.dto.response.ImagensResponse;
import com.javacar.lojadecarro.service.ImagensService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/{idCarro}")
    @Operation(summary = "Realiza o upload das imagens do carro")
    public ResponseEntity<List<ImagensResponse>> create(
            @PathVariable Long idCarro,
            @RequestParam("files") MultipartFile[] files) throws IOException {

        log.info("Recebida requisição de upload de {} imagem(ns) para o carro {}",
                files.length, idCarro);

        var response = imagensService.create(files, idCarro);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(value = "/carro/{idCarro}")
    @Operation(summary = "Listar imagens do carro")
    public ResponseEntity<List<ImagensResponse>> findAll(
            @PathVariable Long idCarro) {

        log.info("Buscando imagens do carro {}", idCarro);

        var response = imagensService.listarImagens(idCarro);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{idImagem}")
    @Operation(summary = "Buscar imagem pelo ID")
    public ResponseEntity<ImagensResponse> findById(
            @PathVariable Long idImagem) {

        log.info("Buscando imagem {}", idImagem);

        var response = imagensService.findImagensById(idImagem);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{idImagem}")
    @Operation(summary = "Atualizar informações da imagem")
    public ResponseEntity<ImagensResponse> update(
            @PathVariable Long idImagem,
            @RequestBody @Valid ImagensRequest request) {

        log.info("Atualizando imagem {}", idImagem);

        var response = imagensService.updateImagens(request, idImagem);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{idImagem}")
    @Operation(summary = "Excluir imagem")
    public ResponseEntity<Void> delete(
            @PathVariable Long idImagem) throws IOException {

        log.info("Removendo imagem {}", idImagem);

        imagensService.deleteImagens(idImagem);

        return ResponseEntity.noContent().build();
    }
}
