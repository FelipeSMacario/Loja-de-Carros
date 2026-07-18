package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.VendasRequest;
import com.javacar.lojadecarro.dto.response.VendasResponse;
import com.javacar.lojadecarro.service.VendasService;
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
@Tag(name = "Vendas")
@RestController
@RequestMapping("/vendas")
public class VendaController {
    private final VendasService vendasService;

    @GetMapping
    public ResponseEntity<List<VendasResponse>> findAll(){
        log.info("Buscando todas as compras.");
        var response = vendasService.listarVendas();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<VendasResponse> create(@RequestBody @Valid VendasRequest request){
        log.info("Criando uma nova compra");
        var response =  vendasService.create(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Compra criado com sucesso");
        return ResponseEntity.created(location).body(response);
    }
}
