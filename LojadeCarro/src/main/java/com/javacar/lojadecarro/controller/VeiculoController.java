package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.AlterarStatusRequest;
import com.javacar.lojadecarro.dto.request.FiltrarCamposCarroRequest;
import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.ImagensResponse;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.service.VeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Tag(name = "Veiculo")
@RestController
@RequestMapping("veiculo")
public class VeiculoController {

    private final VeiculoService veiculoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cadastrar um novo veiculo")
    public ResponseEntity<VeiculoResponse> createVeiculo(@RequestBody
                                                         @Valid VeiculoRequest request,
                                                         @RequestParam("files") MultipartFile[] files) throws IOException {
        log.info("Incluindo um novo carro para venda");
        var response = veiculoService.createVeiculo(request, files);

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
    public ResponseEntity<Page<VeiculoResponse>> listarVeiculos(@PageableDefault(size = 9) Pageable pageable) {
        log.info("Buscando todos os carros.");
        var response = veiculoService.listarVeiculos(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um carro por id")
    public ResponseEntity<VeiculoResponse> findVeiculoById(@PathVariable Long id) {
        log.info("Buscando o carro por id: {}", id);
        var response = veiculoService.findVeiculoById(id);

        log.debug("Resposta do carro por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um carro buscando por id")
    public ResponseEntity<VeiculoResponse> updateVeiculo(@RequestBody @Valid VeiculoRequest request, @PathVariable Long id) {
        log.info("Atualizando o carro por id: {}", id);
        var response = veiculoService.updateVeiculo(request, id);

        log.debug("Resposta atualizar carro por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Marca o carro como vendido o retirando da lista de carros disponíveis")
    public ResponseEntity<VeiculoResponse> marcaVendido(@PathVariable Long id,
                                                        @RequestBody @Valid AlterarStatusRequest request) {
        log.info("Atualizando o carro para vendido por id: {}", id);
        var response = veiculoService.alterarStatus(id, request);

        log.debug("Resposta atualizar carro para vendido por id: {}", response);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    @Operation(summary = "Paginação que busca o carro por parãmetros")
    public ResponseEntity<Page<VeiculoResponse>> filtrarCampos(FiltrarCamposCarroRequest filtro, @PageableDefault(size = 9) Pageable pageable) {
        log.info("Paginação do carro");
        var response = veiculoService.filtrarCampos(filtro, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/imagens")
    @Operation(summary = "Listar as imagens do veículo")
    public ResponseEntity<List<ImagensResponse>> listarImagens(@PathVariable Long id) {
        log.info("Paginação do carro");
        var response = veiculoService.listarImagens(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{idVeiculo}/opcionais")
    @Operation(summary = "Desvincular opcional do veiculo")
    public ResponseEntity<Void> desvincularOpcionais(@PathVariable Long idVeiculo,
                                                     @RequestBody List<Long> ids) {
        log.info("Paginação do carro");
        veiculoService.desvincularOpcionais(idVeiculo, ids);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{idVeiculo}/opcionais")
    @Operation(summary = "Vincular opcional do veiculo")
    public ResponseEntity<Void> vincularOpcionais(@PathVariable Long idVeiculo,
                                                  @RequestBody List<Long> ids) {
        log.info("Paginação do carro");
        veiculoService.vincularOpcionais(idVeiculo, ids);

        return ResponseEntity.noContent().build();
    }


}
