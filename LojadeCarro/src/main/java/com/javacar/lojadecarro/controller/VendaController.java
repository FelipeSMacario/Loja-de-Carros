package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.VendasRequest;
import com.javacar.lojadecarro.dto.response.VendasResponse;
import com.javacar.lojadecarro.service.VendasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vendas")
@RestController
@RequestMapping("/vendas")
public class VendaController {
    private final VendasService vendasService;

    @GetMapping
    @Operation(summary = "Listar todos as vendas")
    public ResponseEntity<Page<VendasResponse>> listar(@PageableDefault(size = 9) Pageable pageable) {
        log.debug("Buscando todos as vendas");
        var response = vendasService.listar(pageable);

        log.debug("Consulta retornou {} elementos", response.getNumberOfElements());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova venda")
    public ResponseEntity<VendasResponse> criar(@RequestBody @Valid VendasRequest request) {
        log.debug("Cadastrar uma nova venda com o corpo: {}", request);
        var response = vendasService.criar(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Venda criada com sucesso com o id: {}", response.id());
        log.debug("Resposta uma nova venda: {}", response);
        return ResponseEntity.created(location).body(response);
    }
}
