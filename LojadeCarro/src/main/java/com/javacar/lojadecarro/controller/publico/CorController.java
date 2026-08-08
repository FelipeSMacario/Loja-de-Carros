package com.javacar.lojadecarro.controller.publico;

import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.service.CoresService;
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
@Tag(name = "Cores")
@RestController
@RequestMapping("/cores")
public class CorController {
    private final CoresService coresService;

    @GetMapping
    @Operation(summary = "Listar todas as cores")
    public ResponseEntity<List<CorResponse>> listarCoresAtivas() {
        log.debug("Buscando todas as cores ativas");
        var response = coresService.listarCoresAtivas();

        log.debug("Consulta de todas as cores ativas");
        log.debug("A consulta de todas as cores ativas retornou com o tamanho de: {}", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cor por id")
    public ResponseEntity<CorResponse> buscarCorAtivaPorId(@PathVariable Long id) {
        log.debug("Buscando a cor por id: {}", id);
        var response = coresService.buscarCorAtivaPorId(id);

        log.info("Consulta da cor realizada com sucesso. id={}", id);
        log.debug("Resposta da cor por id: {}", response);
        return ResponseEntity.ok(response);
    }
}
