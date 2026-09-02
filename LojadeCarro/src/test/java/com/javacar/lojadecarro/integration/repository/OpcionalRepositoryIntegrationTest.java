package com.javacar.lojadecarro.integration.repository;

import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.OpcionalRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("Testes da repository da opcional")
class OpcionalRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private OpcionalRepository opcionalRepository;

    @Test
    @DisplayName("Testes da busca de todos os opcionais")
    @Transactional
    void deveBuscarTodosOsOpcionais() {
        var lista = criarLista();
        var opcionais = opcionalRepository.findAllByIdIn(lista);

        assertThat(opcionais)
                .isNotEmpty()
                .hasSize(4);

        assertThat(opcionais)
                .anyMatch(Opcional::isAtivo)
                .anyMatch(o -> !o.isAtivo());

        assertThat(opcionais)
                .extracting(Opcional::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 47L);

    }

    @Test
    @DisplayName("Testes da busca de todos os opcionais ativos")
    @Transactional
    void deveBuscarTodosOsOpcionaisAtivos() {
        var lista = criarLista();
        var opcionais = opcionalRepository.findAllByIdInAndAtivoTrue(lista);

        assertThat(opcionais)
                .isNotEmpty()
                .hasSize(3);

        assertThat(opcionais)
                .allMatch(Opcional::isAtivo);

        assertThat(opcionais)
                .extracting(Opcional::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    private List<Long> criarLista() {
        return List.of(1L, 2L, 3L, 47L);
    }
}
