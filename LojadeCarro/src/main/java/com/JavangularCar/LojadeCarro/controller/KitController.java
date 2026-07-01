package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.dto.request.KitRequest;
import com.JavangularCar.LojadeCarro.dto.response.KitResponse;
import com.JavangularCar.LojadeCarro.entity.Kit;
import com.JavangularCar.LojadeCarro.service.KitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Kit")
@RestController
@RequestMapping("/kits")
public class KitController {
    private final KitService kitService;

    @PostMapping
    @Operation(summary = "Criar um novo kit")
    public ResponseEntity<KitResponse> createKit(@RequestBody @Valid KitRequest request) {
        log.info("Criando um novo kit");
        var response = kitService.createKit(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Kit criado com sucesso");
        log.debug("Resposta um novo kit: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os kits")
    public ResponseEntity<List<KitResponse>> listarKit() {
        log.info("Buscando todos os kits.");
        var response = kitService.listarKit();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar kit por id")
    public ResponseEntity<KitResponse> findKitById(@PathVariable Long id) {
        log.info("Buscando o kit por id: {}", id);
        var response = kitService.filtrarKit(id);

        log.debug("Resposta do kit por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar kit buscando por id")
    public ResponseEntity<KitResponse> updateKit(@RequestBody @Valid KitRequest request, Long id) {
        log.info("Atualizando a kit por id: {}", id);
        var response = kitService.updateKit(request, id);

        log.debug("Resposta atualizar o kit por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um kit buscando por id")
    public ResponseEntity<Void> deleteKit(@PathVariable Long id) {
        log.info("Deletando a kit por id: {}", id);
        kitService.deleteKit(id);

        log.info("Kit deletada com sucesso. Id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
