package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.AlterarStatusRequest;
import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.ImagemResponse;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.enums.StatusVeiculo;
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
    public ResponseEntity<VeiculoResponse> criar(@RequestBody
                                                 @Valid VeiculoRequest request,
                                                 @RequestPart("files") MultipartFile[] files) throws IOException {
        log.debug("Cadastrar um novo veiculo com o corpo: {}", request);
        var response = veiculoService.criar(request, files);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Veiculo criado com sucesso com o id: {}", response.id());
        log.debug("Resposta um novo veiculo: {}", response);
        return ResponseEntity.created(location).body(response);

    }

    @GetMapping
    @Operation(summary = "Listar todos os veiculos")
    public ResponseEntity<Page<VeiculoResponse>> listar(@PageableDefault(size = 9) Pageable pageable,
                                                        @RequestParam(required = false) StatusVeiculo status) {
        log.debug("Buscando todos os veiculos com o status: {}.", status);
        var response = veiculoService.listar(pageable, status);

        log.debug("Consulta retornou {} elementos", response.getNumberOfElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um veiculo por id")
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable Long id) {
        log.debug("Buscando o veiculo por id: {}", id);
        var response = veiculoService.buscarPorId(id);

        log.info("Consulta do veiculo realizada com sucesso. id={}", id);
        log.debug("Resposta do veiculo por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um veiculo buscando por id")
    public ResponseEntity<VeiculoResponse> atualizar(@RequestBody @Valid VeiculoRequest request, @PathVariable Long id) {
        log.debug("Atualizando o veiculo com id: {} para o corpo: {}", id, request);
        var response = veiculoService.atualizar(request, id);

        log.info("Veiculo com o id: {} atualizado com sucesso", id);
        log.debug("Resposta para atualizar o veiculo por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar o status do veiculo")
    public ResponseEntity<VeiculoResponse> alterarStatus(@PathVariable Long id,
                                                         @RequestBody @Valid AlterarStatusRequest request) {
        log.debug("Alterando status do veiculo com id: {} para o status: {}", id, request.status());
        var response = veiculoService.alterarStatus(id, request);

        log.info("Status do veiculo com o id: {} alterado com sucesso", id);
        log.debug("Resposta da alteração de status para o id: {}. Resposta: {}", id, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/imagens")
    @Operation(summary = "Listar as imagem do veículo")
    public ResponseEntity<List<ImagemResponse>> listarImagens(@PathVariable Long id) {
        log.debug("Listando as imagem do veiculo com o id: {}", id);
        var response = veiculoService.listarImagens(id);

        log.debug("Consulta de todas as imagem para o veiculo com id: {} realizada com sucesso", id);
        log.debug("A consulta de todas as imagem do veiculo retornou com o tamanho de: {} valores", response.size());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{idVeiculo}/opcionais")
    @Operation(summary = "Desvincular opcional do veiculo")
    public ResponseEntity<Void> desvincularOpcionais(@PathVariable Long idVeiculo,
                                                     @RequestParam List<Long> ids) {
        log.debug("Desvinculando opcionais {} do usuário com id: {}", ids, idVeiculo);
        veiculoService.desvincularOpcionais(idVeiculo, ids);

        log.info("Opcionais desvinculados com sucesso. Id: {}", idVeiculo);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{idVeiculo}/opcionais")
    @Operation(summary = "Vincular opcional do veiculo")
    public ResponseEntity<Void> vincularOpcionais(@PathVariable Long idVeiculo,
                                                  @RequestBody List<Long> ids) {
        log.debug("Vinculando opcionais {} do usuário com id: {}", ids, idVeiculo);
        veiculoService.vincularOpcionais(idVeiculo, ids);

        log.info("Opcionais vinculados com sucesso. Id: {}", idVeiculo);
        return ResponseEntity.noContent().build();
    }

}
