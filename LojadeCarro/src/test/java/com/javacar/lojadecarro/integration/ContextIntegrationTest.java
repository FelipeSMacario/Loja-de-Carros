package com.javacar.lojadecarro.integration;

import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ContextIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;
    @Test
    @DisplayName("Deve carregar o contexto da aplicação")
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }
}
