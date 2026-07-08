package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.CarroRequest;
import com.javacar.lojadecarro.dto.request.FiltrarCamposCarroRequest;
import com.javacar.lojadecarro.dto.response.CarroResponse;
import com.javacar.lojadecarro.service.CarroService;
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
@Tag(name = "Carro")
@RestController
@RequestMapping("carro")
public class CarroController {

    private final CarroService carroService;

    @PostMapping
    @Operation(summary = "Criar um novo carro")
    public ResponseEntity<CarroResponse> createCarro(@RequestBody @Valid CarroRequest request) {
        log.info("Incluindo um novo carro para venda");
        var response = carroService.createCarro(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Marca criado com sucesso");
        log.debug("Resposta uma nova marca: {}", response);
        return ResponseEntity.created(location).body(response);

    }

    @GetMapping
    @Operation(summary = "Listar todos os carros")
    public ResponseEntity<Page<CarroResponse>> listarCarros(@PageableDefault(size = 9) Pageable pageable) {
        log.info("Buscando todos os carros.");
        var response = carroService.listarCarros(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um carro por id")
    public ResponseEntity<CarroResponse> findCarroById(@PathVariable Long id) {
        log.info("Buscando o carro por id: {}", id);
        var response = carroService.findCarroById(id);

        log.debug("Resposta do carro por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um carro buscando por id")
    public ResponseEntity<CarroResponse> updateCarro(@RequestBody @Valid CarroRequest request, @PathVariable Long id) {
        log.info("Atualizando o carro por id: {}", id);
        var response = carroService.updateCarro(request, id);

        log.debug("Resposta atualizar carro por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/vendido/{id}")
    @Operation(summary = "Marca o carro como vendido o retirando da lista de carros disponíveis")
    public ResponseEntity<CarroResponse> marcaVendido(@RequestBody @Valid CarroRequest carro, @PathVariable Long id){
        log.info("Atualizando o carro para vendido por id: {}", id);
        var response = carroService.marcarVendido(carro, id);

        log.debug("Resposta atualizar carro para vendido por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um carro buscando por id")
    public ResponseEntity<Void> deleteCarro(@PathVariable Long id) {
        log.info("Deletando o carro por id: {}", id);
        carroService.deleteCarro(id);

        log.info("Carro deletado com sucesso. Id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Paginação que busca o carro por parãmetros")
    public ResponseEntity<Page<CarroResponse>> filtrarCampos(FiltrarCamposCarroRequest filtro, @PageableDefault(size = 9) Pageable pageable) {
        log.info("Paginação do carro");
        var response = carroService.filtrarCampos(filtro, pageable);

        return ResponseEntity.ok(response);
    }

}
