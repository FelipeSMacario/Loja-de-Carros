package com.javacar.lojadecarro.controller.publico;

import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.service.CombustivelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Combustíveis")
@RestController
@RequestMapping("/combustiveis")
public class CombustivelController {
    private final CombustivelService combustivelService;

    @GetMapping
    @Operation(summary = "Listar todos os combustíveis")
    public ResponseEntity<List<CombustivelResponse>> listarCombustiveisAtivas() {
        log.debug("Buscando todos as combustíveis ativos.");
        var response = combustivelService.listarCombustiveisAtivas();

        log.debug("Consulta de todos os combustiveis ativos realizada com sucesso");
        log.debug("A consulta de todos os combustiveis ativos retornou com o tamanho de: {} valores", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar combustível por id")
    public ResponseEntity<CombustivelResponse> buscarCombustivelAtivaPorId(@PathVariable Long id) {
        log.debug("Buscando o combustível por id: {}", id);
        var response = combustivelService.buscarCombustivelAtivaPorId(id);

        log.info("Busca do combustivel ativo com id: {} realizada com sucesso", id);
        log.debug("Resposta do combustível ativo por id: {}", response);
        return ResponseEntity.ok(response);
    }


}
