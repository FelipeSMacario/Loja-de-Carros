package com.javacar.lojadecarro.controller.publico;

import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.service.OpcionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Opcionais")
@RestController
@RequestMapping("/opcionais")
public class OpcionalController {
    private final OpcionalService opcionalService;

    @GetMapping
    @Operation(summary = "Listar todos os opcionals")
    public ResponseEntity<List<OpcionalResponse>> listar() {
        log.debug("Buscando todos os opcionais ativos.");
        var response = opcionalService.listarOpcionaisAtivas();

        log.debug("Consulta de todos os opcionais ativos realizada com sucesso");
        log.debug("A consulta de todos os opcionais ativos retornou com o tamanho de: {} valores", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar opcional por id")
    public ResponseEntity<OpcionalResponse> buscarPorId(@PathVariable Long id) {
        log.debug("Buscando o opcional por id: {}", id);
        var response = opcionalService.buscarOpcionalAtivoPorId(id);

        log.info("Consulta do opcional ativo realizada com sucesso. id={}", id);
        log.debug("Resposta do opcional ativo por id: {}", response);
        return ResponseEntity.ok(response);
    }
}
