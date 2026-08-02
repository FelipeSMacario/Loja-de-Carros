package com.javacar.lojadecarro.integration.repository;

import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.ModeloRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ModeloRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ModeloRepository modeloRepository;

    @Test
    void deveBuscarSomenteModelosAtivos() {

        var modelos = modeloRepository.findByAtivo(true);

        assertThat(modelos)
                .isNotEmpty()
                .allMatch(Modelo::isAtivo);
    }

    @Test
    @Transactional
    void devePossuirMarcaAssociadaAoModelo() {

        var modelos = modeloRepository.findByAtivo(true);

        var modelo = modelos.getFirst();

        assertThat(modelo.getMarca())
                .isNotNull();
        assertThat(modelo.getMarca().getId())
                .isNotNull();
    }
}
