package com.javacar.lojadecarro.controller.publico;

import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.service.ModeloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Modelos")
@RestController
@RequestMapping("/modelos")
public class ModeloController {
    private final ModeloService modeloService;

    @GetMapping
    @Operation(summary = "Listar todos os modelos")
    public ResponseEntity<List<ModeloResponse>> listarModelosAtivos() {
        log.debug("Buscando todos os modelos ativos.");
        var response = modeloService.listarModelosAtivos();

        log.debug("Consulta de todos as modelos ativos realizada com sucesso");
        log.debug("A consulta de todos as modelos ativos retornou com o tamanho de: {} valores", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar modelo por id")
    public ResponseEntity<ModeloResponse> buscarModeloAtivoPorId(@PathVariable Long id) {
        log.debug("Buscando o modelo ativo por id: {}", id);
        var response = modeloService.buscarModeloAtivoPorId(id);

        log.info("Consulta do modelo ativo realizada com sucesso. id={}", id);
        log.debug("Resposta do modelo ativo por id: {}", response);
        return ResponseEntity.ok(response);
    }

}
