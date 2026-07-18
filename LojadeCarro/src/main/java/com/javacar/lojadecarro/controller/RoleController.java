package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.service.RolesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Roles")
@RestController
@RequestMapping("roles")
public class RoleController {
    private final RolesService rolesService;

    @GetMapping
    @Operation(summary = "Listar todas as roles")
    public ResponseEntity<List<RoleResponse>> findAll(@RequestParam(defaultValue = "ATIVAS") StatusFiltro status) {
        log.info("Buscando todas as roles.");
        var response = rolesService.listarRoles(status);

        log.debug("Retorno da listagem das roles: {}", response);
        return ResponseEntity.ok(response);
    }


}
