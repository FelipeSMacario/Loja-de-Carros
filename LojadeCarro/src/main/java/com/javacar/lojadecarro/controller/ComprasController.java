package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.ComprasRequest;
import com.javacar.lojadecarro.dto.response.ComprasResponse;
import com.javacar.lojadecarro.service.ComprasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Compras")
@RestController
@RequestMapping("/compras")
public class ComprasController {
    private final ComprasService comprasService;

    @GetMapping
    public ResponseEntity<List<ComprasResponse>> findAll(){
        log.info("Buscando todas as compras.");
        var response = comprasService.listarCompras();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ComprasResponse> create(@RequestBody @Valid ComprasRequest compras){
        log.info("Criando uma nova compra");
        var response =  comprasService.createCompras(compras);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Compra criado com sucesso");
        log.debug("Resposta uma nova compra: {}", response);
        return ResponseEntity.created(location).body(response);
    }
}
