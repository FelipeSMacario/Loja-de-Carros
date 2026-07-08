package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.service.CarroceriaService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Carroceria")
@RestController
@RequestMapping("/carrocerias")
public class CarroceriaController {

    private final CarroceriaService carroceriaService;

    @PostMapping
    @Operation(summary = "Criar uma nova carroceria")
    public ResponseEntity<CarroceriaResponse> create(@RequestBody @Valid CarroceriaRequest request) {
        log.info("Criando uma nova carroceria");
        var response = carroceriaService.createCarroceria(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Carroceria criado com sucesso");
        log.debug("Resposta uma nova carroceria: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping()
    @Operation(summary = "Listar todas as carrocerias")
    public ResponseEntity<List<CarroceriaResponse>> findAll() {
        log.info("Buscando todas as carrocerias.");
        var response = carroceriaService.listarCarroceria();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar carroceria por id")
    public ResponseEntity<CarroceriaResponse> findById(@PathVariable Long id) {
        log.info("Buscando a carroceria por id: {}", id);
        var response = carroceriaService.findCarroceriaById(id);

        log.debug("Resposta carroceria por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar carroceria buscando por id")
    public ResponseEntity<CarroceriaResponse> update(@RequestBody @Valid CarroceriaRequest request, @PathVariable Long id) {
        log.info("Atualizando a carroceria por id: {}", id);
        var response = carroceriaService.updateCarroceria(request, id);

        log.debug("Resposta atualizar carroceria por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma carroceria buscando por id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deletando a carroceria por id: {}", id);
        carroceriaService.deleteCarroceria(id);

        log.info("Carroceria deletado com sucesso. Id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
