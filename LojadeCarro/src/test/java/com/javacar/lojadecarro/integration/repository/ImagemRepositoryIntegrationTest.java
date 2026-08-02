package com.javacar.lojadecarro.integration.repository;

import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.ImagensRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ImagemRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ImagensRepository imagensRepository;

    @Test
    @Transactional
    void deveBuscarImagensDoVeiculo() {
        var imagens = imagensRepository.findByVeiculoId(1L);
        assertThat(imagens)
                .isNotEmpty();
        assertThat(imagens)
                .hasSize(3);
        assertThat(imagens)
                .allMatch(imagem -> imagem.getVeiculo() != null);
    }

    @Test
    @Transactional
    void deveValidarSomenteUmaImagemPrincipal() {

        var imagens = imagensRepository.findByVeiculoId(1L);

        assertThat(imagens)
                .isNotEmpty();

        assertThat(imagens)
                .filteredOn(Imagem::isPrincipal)
                .hasSize(1);
    }


}
