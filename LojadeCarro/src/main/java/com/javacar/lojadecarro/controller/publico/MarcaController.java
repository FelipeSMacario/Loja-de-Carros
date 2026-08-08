package com.javacar.lojadecarro.controller.publico;

import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.service.MarcaService;
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
@Tag(name = "Marcas")
@RestController
@RequestMapping("/marcas")
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping
    @Operation(summary = "Listar todas as marcas")
    public ResponseEntity<List<MarcaResponse>> listar() {
        log.debug("Buscando todas as marcas ativas.");
        var response = marcaService.listarMarcasAtivas();

        log.debug("Consulta de todas as marcas ativas realizada com sucesso");
        log.debug("A consulta de todos as marcas ativas retornou com o tamanho de: {} valores", response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar marca por id")
    public ResponseEntity<MarcaResponse> buscarPorId(@PathVariable Long id) {
        log.debug("Buscando a marca ativa por id: {}", id);
        var response = marcaService.buscarMarcaAtivaPorId(id);

        log.info("Consulta da marca ativa realizada com sucesso. id= {}", id);
        log.debug("Resposta da marca ativa por id: {}", response);
        return ResponseEntity.ok(response);
    }
}
