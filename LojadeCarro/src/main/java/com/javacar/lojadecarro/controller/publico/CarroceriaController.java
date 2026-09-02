package com.javacar.lojadecarro.controller.publico;

import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.service.CarroceriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Carrocerias")
@RestController
@RequestMapping("/carrocerias")
public class CarroceriaController {

    private final CarroceriaService carroceriaService;

    @GetMapping()
    @Operation(summary = "Listar todas as carrocerias")
    public ResponseEntity<List<CarroceriaResponse>> listarCarroceriasAtivas() {
        log.debug("Iniciando a busca de todas as carrocerias ativas");
        var response = carroceriaService.listarCarroceriasAtivas();

        log.debug("Consulta de todas as carrocerias ativas realizada com sucesso");
        log.debug("A consulta de todas as carrocerias ativas retornou com o tamanho de: {} valores", response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma carroceria por id")
    public ResponseEntity<CarroceriaResponse> buscarPorIdAdministracao(@PathVariable Long id) {
        log.debug("Buscando uma carroceria por id: {}", id);
        var response = carroceriaService.buscarCarroceriaAtivaPorId(id);

        log.info("Consulta da carroceria ativa com o id: {} realizada com sucesso", id);
        log.debug("Resposta carroceria ativa por id: {}", response);
        return ResponseEntity.ok(response);
    }




}
