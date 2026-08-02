package com.javacar.lojadecarro.integration.repository;

import com.javacar.lojadecarro.entity.Marca;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.MarcaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class MarcaRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MarcaRepository marcaRepository;

    @Test
    void deveBuscarSomenteMarcasAtivas() {
        var marcas = marcaRepository.findByAtivo(true);

        assertThat(marcas)
                .isNotEmpty()
                .allMatch(Marca::isAtivo);
    }
    @Test
    void deveNaoRetornarMarcasInativas() {

        var marcas = marcaRepository.findByAtivo(false);

        assertThat(marcas)
                .isNotEmpty()
                .allMatch(marca -> !marca.isAtivo());
    }
}
