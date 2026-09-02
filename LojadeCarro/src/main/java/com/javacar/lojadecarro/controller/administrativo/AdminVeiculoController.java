package com.javacar.lojadecarro.controller.administrativo;

import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.service.VeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin - Veiculos")
@RestController
@RequestMapping("/admin/veiculos")
public class AdminVeiculoController {
    private final VeiculoService veiculoService;

    @GetMapping
    @Operation(summary = "Listar todos os veiculos administrativo")
    public ResponseEntity<Page<VeiculoResponse>> listar(@PageableDefault(size = 9) Pageable pageable,
                                                              @RequestParam(required = false) StatusVeiculo status) {
        log.debug("Buscando todos os veiculos administrativo.");
        var response = veiculoService.listarAdministrativo(pageable, status);

        log.debug("Consulta retornou {} elementos", response.getNumberOfElements());

        return ResponseEntity.ok(response);
    }
}
