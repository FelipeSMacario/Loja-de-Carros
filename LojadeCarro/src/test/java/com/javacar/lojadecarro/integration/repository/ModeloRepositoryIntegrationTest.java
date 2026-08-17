package com.javacar.lojadecarro.integration.repository;

import com.javacar.lojadecarro.entity.Marca;
import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.MarcaRepository;
import com.javacar.lojadecarro.repository.ModeloRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("Testes da repository do modelo")
class ModeloRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private MarcaRepository marcaRepository;

    @DisplayName("Deve buscar um modelo ativo")
    @Test
    void deveBuscarSomenteModelosAtivos() {

        var modelos = modeloRepository.findByAtivo(true);

        assertThat(modelos)
                .isNotEmpty()
                .allMatch(Modelo::isAtivo);
    }

    @Test
    @DisplayName("Deve validar que o modelo está vinculado a uma marca")
    @Transactional
    void devePossuirMarcaAssociadaAoModelo() {

        var modelos = modeloRepository.findByAtivo(true);

        var modelo = modelos.getFirst();

        assertThat(modelo.getMarca())
                .isNotNull();
        assertThat(modelo.getMarca().getId())
                .isNotNull();
    }

    @Test
    @DisplayName("Deve buscar um modelo ativo com marca ativa")
    @Transactional
    void deveBuscarModeloAtivoComMarcaAtiva() {
        var marca = criarMarca("Exemplo marca", "Exemplo URL", true);
        var modelo = criarModelo("Exemplo modelo", marca, true);
        var modelos = modeloRepository.findByAtivoTrueAndMarca_AtivoTrue();
        assertThat(modelos)
                .isNotEmpty()
                .allMatch(Modelo::isAtivo);

        assertThat(modelos)
                .extracting(Modelo::getMarca)
                .isNotEmpty()
                .allMatch(Marca::isAtivo);

        assertThat(modelos)
                .anyMatch(m -> m.getId().equals(modelo.getId()));
    }

    @Test
    @DisplayName("Não deve retornar modelo ativo vinculado a uma marca inativa")
    @Transactional
    void deveRemoverModeloAtivoComMarcaInativa() {
        var marcaAtiva = criarMarca("Marca válida", "url-valida", true);
        var modeloPermitido = criarModelo("Modelo válido", marcaAtiva, true);

        var marcaInativa = criarMarca("Marca inativa", "url-inativa", false);
        var modeloBloqueado = criarModelo("Modelo bloqueado", marcaInativa, true);
        var modelos = modeloRepository.findByAtivoTrueAndMarca_AtivoTrue();

        assertThat(modelos)
                .extracting(Modelo::getId)
                .contains(modeloPermitido.getId())
                .doesNotContain(modeloBloqueado.getId());
    }

    @Test
    @DisplayName("Não deve retornar modelo inativo vinculado a uma marca inativa")
    @Transactional
    void eveRemoverModeloInativoComMarcaInativa() {
        var marcaAtiva = criarMarca("Marca válida2", "url-valida2", true);
        var modeloPermitido = criarModelo("Modelo válido2", marcaAtiva, true);

        var marcaBloqueada = criarMarca("Exemplo marca3", "Exemplo URL3", true);
        var modeloInativo = criarModelo("Exemplo modelo3", marcaBloqueada, false);
        var modelos = modeloRepository.findByAtivoTrueAndMarca_AtivoTrue();
        assertThat(modelos)
                .isNotEmpty();
        assertThat(modelos)
                .noneMatch(m -> m.getId().equals(modeloInativo.getId()));
    }

    @Test
    @DisplayName("Deve buscar um modelo ativo com marca ativa")
    @Transactional
    void deveBuscarModeloAtivoComMarcaAtivo() {
        var marca = criarMarca("Exemplo marca4", "Exemplo URL4", true);
        var modelo = criarModelo("Exemplo modelo4", marca, true);

        var modeloAtivo = modeloRepository.findByIdAndAtivoTrueAndMarca_AtivoTrue(modelo.getId())
                .orElseThrow();

        assertThat(modeloAtivo)
                .extracting(
                        Modelo::getId,
                        Modelo::getNome,
                        Modelo::isAtivo
                ).containsExactly(
                        modelo.getId(),
                        modelo.getNome(),
                        modelo.isAtivo()
                );

    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar modelo inativo com marca ativa")
    @Transactional
    void deveRetornarVazioModeloInativoComMarcaAtiva() {
        var marca = criarMarca("Exemplo marca4", "Exemplo URL4", true);
        var modelo = criarModelo("Exemplo modelo4", marca, false);

        var modeloAtivo = modeloRepository.findByIdAndAtivoTrueAndMarca_AtivoTrue(modelo.getId());

        assertThat(modeloAtivo).isEmpty();

    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar modelo ativo com marca inativa")
    @Transactional
    void deveRetornarVazioModeloAtivoComMarcaInativa() {
        var marca = criarMarca("Exemplo marca5", "Exemplo URL5", false);
        var modelo = criarModelo("Exemplo modelo5", marca, true);

        var modeloAtivo = modeloRepository.findByIdAndAtivoTrueAndMarca_AtivoTrue(modelo.getId());

        assertThat(modeloAtivo).isEmpty();

    }

    private Marca criarMarca(String nome, String url, boolean ativo) {
        Marca marca = new Marca();
        marca.setNome(nome);
        marca.setUrl(url);
        marca.setAtivo(ativo);
        return marcaRepository.saveAndFlush(marca);
    }

    private Modelo criarModelo(String nome, Marca marca, boolean ativo) {
        Modelo modelo = new Modelo();
        modelo.setNome(nome);
        modelo.setMarca(marca);
        modelo.setAtivo(ativo);
        return modeloRepository.saveAndFlush(modelo);

    }

}
