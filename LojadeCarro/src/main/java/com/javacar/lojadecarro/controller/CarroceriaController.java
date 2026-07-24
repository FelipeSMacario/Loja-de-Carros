package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.CarroceriaRequest;
import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
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
    @Operation(summary = "Cadastrar uma nova carroceria")
    public ResponseEntity<CarroceriaResponse> criar(@RequestBody @Valid CarroceriaRequest request) {
        log.debug("Recebendo uma requisição de cadastro de carroceria com o corpo: {}", request);
        var response = carroceriaService.criar(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Carroceria criada  com sucesso com o id: {}", response.id());
        log.debug("Resposta da criação da carroceria: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping()
    @Operation(summary = "Listar todas as carrocerias")
    public ResponseEntity<List<CarroceriaResponse>> listar(@RequestParam(defaultValue = "ATIVAS")
                                                            StatusFiltro status) {
        log.debug("Iniciando a busca de todas as carrocerias com o status: {}", status);
        var response = carroceriaService.listar(status);

        log.debug("Consulta de todas as carrocerias com o status: {} realizada com sucesso", status);
        log.debug("A consulta de todas as carrocerias retornou com o tamanho de: {} valores", response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma carroceria por id")
    public ResponseEntity<CarroceriaResponse> buscaPorId(@PathVariable Long id) {
        log.debug("Buscando uma carroceria por id: {}", id);
        var response = carroceriaService.buscaPorId(id);

        log.info("Consulta da carroceria com o id: {} realizada com sucesso", id);
        log.debug("Resposta carroceria por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar carroceria por id")
    public ResponseEntity<CarroceriaResponse> atualizar(@RequestBody @Valid CarroceriaRequest request,
                                                     @PathVariable Long id) {
        log.debug("Iniciando a atualização da carroceria com id: {} e com o corpo: {}", id, request);
        var response = carroceriaService.atualizar(request, id);

        log.info("Carroceria com o id: {} atualizada com sucesso", response.id());
        log.debug("Resposta atualizar carroceria por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar o status da carroceria")
    public ResponseEntity<CarroceriaResponse> alterarStatus(@PathVariable Long id, @RequestBody @Valid StatusRequest request) {
        log.debug("Solicitação para alterar o status da carroceria para: {}", request.ativo());
        var response = carroceriaService.alterarStatus(id, request);

        log.info("Carroceria alterada com sucesso. status= {}", response.ativo());
        return ResponseEntity.ok(response);
    }


}
