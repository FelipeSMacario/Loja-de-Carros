package com.JavangularCar.LojadeCarro.controller;


import com.JavangularCar.LojadeCarro.dto.request.ImagensRequest;
import com.JavangularCar.LojadeCarro.dto.response.ImagensResponse;
import com.JavangularCar.LojadeCarro.service.ImagensService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Imagens")
@RestController
@RequestMapping("/imagens")
public class ImagensController {

    private final ImagensService imagensService;

    @PostMapping
    @Operation(summary = "Realiza o upload das imagens e associa ao carro")
    public ResponseEntity<Void> create(
            @RequestParam("file") MultipartFile[] files,
            @RequestParam("id") String id) throws IOException {

        log.info("Realizando upload de {} imagem(ns) para o carro {}", files.length, id);

        imagensService.create(files, id);

        log.info("Upload realizado com sucesso para o carro {}", id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar todas as imagens")
    public ResponseEntity<List<ImagensResponse>> findAll() {

        log.info("Buscando todas as imagens");

        var response = imagensService.listarImagens();

        log.debug("Quantidade de imagens encontradas: {}", response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar imagem pelo ID")
    public ResponseEntity<ImagensResponse> findById(@PathVariable Long id) {

        log.info("Buscando imagem. Id: {}", id);

        var response = imagensService.findImagensById(id);

        log.debug("Imagem encontrada. Id: {}", id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar imagem")
    public ResponseEntity<ImagensResponse> update(
            @RequestBody @Valid ImagensRequest imagens,
            @PathVariable Long id) {

        log.info("Atualizando imagem. Id: {}", id);

        var response = imagensService.updateImagens(imagens, id);

        log.info("Imagem atualizada com sucesso. Id: {}", id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir imagem")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        log.info("Removendo imagem. Id: {}", id);

        imagensService.deleteImagens(id);

        log.info("Imagem removida com sucesso. Id: {}", id);

        return ResponseEntity.noContent().build();
    }
}
