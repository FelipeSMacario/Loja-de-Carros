package com.JavangularCar.LojadeCarro.controller;

import com.JavangularCar.LojadeCarro.dto.request.CoresRequest;
import com.JavangularCar.LojadeCarro.dto.response.CoresResponse;
import com.JavangularCar.LojadeCarro.entity.Cores;
import com.JavangularCar.LojadeCarro.service.CoresService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cores")
@RestController
@RequestMapping("cores")
public class CoresController {
    private final CoresService coresService;

    @PostMapping
    @Operation(summary = "Criar uma nova cor")
    public ResponseEntity<CoresResponse> create(@RequestBody @Valid CoresRequest request){
        log.info("Criando uma nova cor");
        var response = coresService.createCores(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Cor criado com sucesso");
        log.debug("Resposta uma nova cor: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as cores")
    public ResponseEntity<List<CoresResponse>> findAll(){
        log.info("Buscando todas as cores.");
        var response = coresService.listarCores();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cor por id")
    public ResponseEntity<CoresResponse> findCoresById(@PathVariable Long id){
        log.info("Buscando a cor por id: {}", id);
        var response = coresService.findCoresById(id);

        log.debug("Resposta da cor por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cor buscando por id")
    public ResponseEntity<CoresResponse> updateCores(@RequestBody @Valid CoresRequest cores, @PathVariable Long id){
        log.info("Atualizando a cor por id: {}", id);
        var response = coresService.updateCores(cores, id);

        log.debug("Resposta atualizar cor por id: {}", response);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar uma cor buscando por id")
    public ResponseEntity<Void> deleteCores(@PathVariable Long id){
        log.info("Deletando a cor por id: {}", id);
        coresService.deleteCores(id);

        log.info("Cor deletada com sucesso. Id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
