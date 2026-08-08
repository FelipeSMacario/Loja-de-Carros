package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.VeiculoOpcionaisRequest;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Tag(name = "Veiculos")
@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cadastrar um novo veiculo")
    public ResponseEntity<VeiculoResponse> criar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("request")
            @Valid VeiculoRequest request,
            @RequestPart(value = "files", required = false)
            MultipartFile[] files
    ) throws IOException {
        log.debug("Cadastrar um novo veiculo com o corpo: {}", request);
        var idUsuario = Long.valueOf(jwt.getSubject());
        var imagens = files == null ? new MultipartFile[0] : files;
        var response = veiculoService.criar(request, imagens, idUsuario);

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
    @Operation(summary = "Listar todos os veiculos ativos")
    public ResponseEntity<Page<VeiculoResponse>> listarAtivos(@PageableDefault(
            size = 9,
            sort = "dataCadastro",
            direction = Sort.Direction.DESC
    ) Pageable pageable) {
        log.debug("Buscando todos os veiculos ativos.");
        var response = veiculoService.listarAtivos(pageable);

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

    @PatchMapping("/{id}/pausar")
    @Operation(summary = "Pausar veiculo")
    public ResponseEntity<VeiculoResponse> pausarVeiculo(@PathVariable Long id) {
        log.debug("Alterando status para pausado do veiculo com id: {}", id);
        var response = veiculoService.pausarVeiculo(id);

        log.info("Veiculo com o id: {} pausado com sucesso", id);
        log.debug("Resposta da pausa para o veiculo com id: {}. Resposta: {}", id, response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar veiculo")
    public ResponseEntity<VeiculoResponse> reativarVeiculo(@PathVariable Long id) {
        log.debug("Reativar status do veiculo com id: {}", id);
        var response = veiculoService.reativarVeiculo(id);

        log.info("Veiculo com o id: {} reativado com sucesso", id);
        log.debug("Resposta da reativação para o veiculo com id: {}. Resposta: {}", id, response);
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/{idVeiculo}/imagens",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<List<ImagemResponse>> vincularImagens(
            @PathVariable Long idVeiculo,
            @RequestPart("files") MultipartFile[] files
    ) throws IOException {
        log.debug("Vinculando imagens {} do veiculo com id: {}", files.length, idVeiculo);
        var response = veiculoService.vincularImagens(idVeiculo, files);

        log.info("Imagens vinculadas com sucesso. Id: {}", idVeiculo);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
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
                                                     @RequestParam List<Long> idsOpcionais) {
        log.debug("Desvinculando opcionais {} do veiculo com id: {}", idsOpcionais, idVeiculo);
        veiculoService.desvincularOpcionais(idVeiculo, idsOpcionais);

        log.info("Opcionais desvinculados com sucesso. Id: {}", idVeiculo);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idVeiculo}/opcionais")
    @Operation(summary = "Vincular opcional do veiculo")
    public ResponseEntity<Void> vincularOpcionais(@PathVariable Long idVeiculo,
                                                  @RequestBody @Valid VeiculoOpcionaisRequest request) {
        log.debug("Vinculando opcionais {} do veiculo com id: {}", request.opcionais(), idVeiculo);
        veiculoService.vincularOpcionais(idVeiculo, request.opcionais());

        log.info("Opcionais vinculados com sucesso. Id: {}", idVeiculo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/meus-anuncios")
    public ResponseEntity<Page<VeiculoResponse>> listarMeusAnuncios(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(
                    size = 9,
                    sort = "dataCadastro",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable,
            @RequestParam(required = false)
            StatusVeiculo status) {
        var idUsuario = Long.valueOf(jwt.getSubject());
        log.debug("Buscando todos os anuncios do usuario com id: {}.", idUsuario);
        var response = veiculoService.listarMeusAnuncios(pageable, idUsuario, status);

        log.debug("Consulta dos meus anuncios retornou {} elementos", response.getNumberOfElements());

        return ResponseEntity.ok(response);
    }

}
