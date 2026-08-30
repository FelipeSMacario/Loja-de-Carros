package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.integration.fixture.VendaIntegrationFixture;
import com.javacar.lojadecarro.repository.ImagensRepository;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import com.javacar.lojadecarro.service.StorageService;
import com.javacar.lojadecarro.service.VeiculoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
@Transactional
@Import(VendaIntegrationFixture.class)
public abstract class AbstractVeiculoServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    protected VeiculoService veiculoService;
    @Autowired
    protected VeiculoRepository veiculoRepository;
    @MockitoBean
    protected StorageService storageService;
    @Autowired
    protected VendaIntegrationFixture vendaIntegrationFixture;
    @Autowired
    protected ImagensRepository imagensRepository;
    @PersistenceContext
    protected EntityManager entityManager;

    protected Veiculo criarVeiculoPersistido(String placa, StatusVeiculo status, Usuario usuario) {
        var vendedor = (usuario == null) ? criarVendedorPersistido() : usuario;
        return vendaIntegrationFixture
                .criarVeiculoPersistido(placa,
                        BigDecimal.valueOf(200000),
                        vendaIntegrationFixture.criarCarroceriaPersistida(),
                        vendaIntegrationFixture.criarCorPersistida(),
                        vendaIntegrationFixture.criarModeloPersistido(),
                        vendaIntegrationFixture.criarCombustivelPersistido(),
                        vendedor,
                        status);
    }
    protected Usuario criarVendedorPersistido() {
        return vendaIntegrationFixture
                .criarUsuarioPersistido("USUARIO 1", "85296374165", "usuario1@gmail.com");
    }

}
