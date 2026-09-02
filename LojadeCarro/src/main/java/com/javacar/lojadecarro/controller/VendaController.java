package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.VendaRequest;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.enums.StatusVenda;
import com.javacar.lojadecarro.service.VendasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vendas")
@RestController
@RequestMapping("/vendas")
public class VendaController {
    private final VendasService vendasService;

    @GetMapping
    @Operation(summary = "Listar todos as vendas")
    public ResponseEntity<Page<VendaResponse>> listar(@PageableDefault(size = 9) Pageable pageable,
                                                      @RequestParam(required = false) StatusVenda status) {
        log.debug("Buscando todos as vendas");
        var response = vendasService.listar(pageable, status);

        log.debug("Consulta retornou {} elementos", response.getNumberOfElements());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova venda")
    public ResponseEntity<VendaResponse> criar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid VendaRequest request) {
        log.debug("Cadastrar uma nova venda com o corpo: {}", request);
        var idUsuario = Long.valueOf(jwt.getSubject());
        var response = vendasService.criar(request, idUsuario);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        log.info("Venda criada com sucesso com o id: {}", response.id());
        log.debug("Resposta uma nova venda: {}", response);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{idVenda}")
    @Operation(summary = "Buscar uma venda por ID")
    public ResponseEntity<VendaResponse> buscarPorId(@PathVariable Long idVenda) {
        log.debug("Buscando uma venda por ID: {}", idVenda);
        var response = vendasService.buscarPorId(idVenda);

        log.info("Consulta da venda realizada com sucesso. id={}", idVenda);
        log.debug("Resposta da venda por id: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/minhas-compras")
    @Operation(summary = "Deve listar as compras do usuário")
    public ResponseEntity<Page<VendaResponse>> buscarMinhasCompras(@AuthenticationPrincipal Jwt jwt,
                                                                   @PageableDefault(
                                                                           size = 9,
                                                                           sort = "dataVenda",
                                                                           direction = Sort.Direction.DESC
                                                                   )
                                                                   Pageable pageable,
                                                                   @RequestParam(required = false) StatusVenda status) {
        var idUsuario = Long.valueOf(jwt.getSubject());
        var response = vendasService.buscarMinhasCompras(idUsuario, pageable, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/minhas-vendas")
    @Operation(summary = "Deve listar as vendas do usuário")
    public ResponseEntity<Page<VendaResponse>> buscarMinhasVendas(@AuthenticationPrincipal Jwt jwt,
                                                                  @PageableDefault(
                                                                          size = 9,
                                                                          sort = "dataVenda",
                                                                          direction = Sort.Direction.DESC
                                                                  )
                                                                  Pageable pageable,
                                                                  @RequestParam(required = false) StatusVenda status) {
        var idUsuario = Long.valueOf(jwt.getSubject());
        var response = vendasService.buscarMinhasVendas(idUsuario, pageable, status);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("{idVenda}/cancelar")
    @Operation(summary = "Cancelar a venda")
    public ResponseEntity<VendaResponse> cancelarVenda(@PathVariable Long idVenda) {
        log.debug("Cancelando a venda com o id: {}", idVenda);
        var response = vendasService.cancelarVenda(idVenda);

        return ResponseEntity.ok(response);
    }
    @PatchMapping("{idVenda}/concluir")
    @Operation(summary = "Concluir a venda")
    public ResponseEntity<VendaResponse> concluirVenda(@PathVariable Long idVenda) {
        log.debug("Concluindo a venda com o id: {}", idVenda);
        var response = vendasService.concluirVenda(idVenda);

        return ResponseEntity.ok(response);
    }
}
