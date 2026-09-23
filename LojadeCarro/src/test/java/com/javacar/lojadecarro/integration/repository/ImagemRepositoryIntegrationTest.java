package com.javacar.lojadecarro.integration.repository;

import com.javacar.lojadecarro.entity.*;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.integration.fixture.VendaIntegrationFixture;
import com.javacar.lojadecarro.repository.ImagensRepository;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Transactional
@Import(VendaIntegrationFixture.class)
class ImagemRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ImagensRepository imagensRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private VendaIntegrationFixture vendaIntegrationFixture;

    @PersistenceContext
    protected EntityManager entityManager;

    @Test
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
    void deveValidarSomenteUmaImagemPrincipal() {

        var imagens = imagensRepository.findByVeiculoId(1L);

        assertThat(imagens)
                .isNotEmpty();

        assertThat(imagens)
                .filteredOn(Imagem::isPrincipal)
                .hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar a imagem principal do veículo")
    void deveBuscarImagemPrincipalDoVeiculo() {
        //Arrange
        var veiculo = criarVeiculoPersistidoComImagens("ZX5BS9Q", DISPONIVEL, criarVendedorPersistido());
        var idVeiculo = veiculo.getId();

        var idImagemPrincipal = veiculo.getImagens()
                .stream()
                .filter(Imagem::isPrincipal)
                .map(Imagem::getId)
                .findFirst()
                .orElseThrow();

        entityManager.flush();
        entityManager.clear();
        //ACT
        var resultado = imagensRepository.findByVeiculo_IdAndPrincipalTrue(idVeiculo).orElseThrow();
        //Assert
        assertThat(resultado)
                .extracting(
                        Imagem::getId,
                        imagem -> imagem.getVeiculo().getId(),
                        Imagem::isPrincipal
                )
                .containsExactly(
                        idImagemPrincipal,
                        idVeiculo,
                        true
                );

        assertThat(resultado.getId())
                .isEqualTo(idImagemPrincipal);
        assertThat(resultado.getVeiculo().getId())
                .isEqualTo(idVeiculo);
    }

    @Test
    @DisplayName("Deve buscar as imagens principais dos veículos informados")
    void deveBuscarImagensPrincipaisDosVeiculos() {
        //Arrange
        var usuario = criarVendedorPersistido();
        var carroceria = vendaIntegrationFixture.criarCarroceriaPersistida();
        var cor = vendaIntegrationFixture.criarCorPersistida();
        var modelo = vendaIntegrationFixture.criarModeloPersistido();
        var combustivel = vendaIntegrationFixture.criarCombustivelPersistido();

        var veiculo = criarVeiculoPersistidoComImagens("Q458F39", DISPONIVEL, usuario, carroceria, cor, modelo, combustivel, List.of(31, 32, 33));
        var veiculo2 = criarVeiculoPersistidoComImagens("Q458F49", DISPONIVEL, usuario, carroceria, cor, modelo, combustivel, List.of(41, 42, 43));
        var veiculo3 = criarVeiculoPersistidoComImagens("Q458F59", DISPONIVEL, usuario, carroceria, cor, modelo, combustivel, List.of(51, 52, 53));
        var veiculo4 = criarVeiculoPersistidoComImagens("Q458F69", DISPONIVEL, usuario, carroceria, cor, modelo, combustivel, List.of(61, 62, 63));
        var veiculos = List.of(
                veiculo,
                veiculo2,
                veiculo3,
                veiculo4
        );

        var idsVeiculos = veiculos.stream()
                .map(Veiculo::getId)
                .toList();

        var idsImagensPrincipais = veiculos.stream()
                .flatMap(item -> item.getImagens().stream())
                .filter(Imagem::isPrincipal)
                .map(Imagem::getId)
                .toList();

        entityManager.flush();
        entityManager.clear();
        //ACT
        var resultado = imagensRepository.findByVeiculo_IdInAndPrincipalTrue(idsVeiculos);

        //Assert
        assertThat(resultado)
                .hasSize(4)
                .allMatch(Imagem::isPrincipal)
                .extracting(Imagem::getId)
                .containsExactlyInAnyOrderElementsOf(
                        idsImagensPrincipais
                );

        assertThat(resultado)
                .extracting(imagem ->
                        imagem.getVeiculo().getId()
                )
                .containsExactlyInAnyOrderElementsOf(
                        idsVeiculos
                );

        assertThat(resultado)
                .extracting(imagem ->
                        imagem.getVeiculo().getId()
                )
                .containsExactlyInAnyOrderElementsOf(
                        idsVeiculos
                );
    }

    private Veiculo criarVeiculoPersistidoComImagens(String placa,
                                                     StatusVeiculo status,
                                                     Usuario usuario,
                                                     Carroceria carroceria,
                                                     Cor cor,
                                                     Modelo modelo,
                                                     Combustivel combustivel,
                                                     List<Integer> contadoresImagens) {
        var vendedor = (usuario == null) ? criarVendedorPersistido() : usuario;
        return vendaIntegrationFixture
                .criarVeiculoPersistidoComImagens(placa,
                        BigDecimal.valueOf(200000),
                        carroceria,
                        cor,
                        modelo,
                        combustivel,
                        vendedor,
                        status,
                        contadoresImagens);
    }

    private Veiculo criarVeiculoPersistidoComImagens(String placa, StatusVeiculo status, Usuario usuario) {
        var vendedor = (usuario == null) ? criarVendedorPersistido() : usuario;
        return vendaIntegrationFixture
                .criarVeiculoPersistidoComImagens(placa,
                        BigDecimal.valueOf(200000),
                        vendaIntegrationFixture.criarCarroceriaPersistida(),
                        vendaIntegrationFixture.criarCorPersistida(),
                        vendaIntegrationFixture.criarModeloPersistido(),
                        vendaIntegrationFixture.criarCombustivelPersistido(),
                        vendedor,
                        status,
                        List.of(21, 22, 23));
    }

    private Usuario criarVendedorPersistido() {
        return vendaIntegrationFixture
                .criarUsuarioPersistido("USUARIO 1", "85296374165", "usuario1@gmail.com");
    }

}
