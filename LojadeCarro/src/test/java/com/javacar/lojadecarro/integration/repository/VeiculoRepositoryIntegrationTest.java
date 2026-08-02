package com.javacar.lojadecarro.integration.repository;

import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.enums.StatusVeiculo.VENDIDO;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class VeiculoRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Test
    void deveBuscarVeiculosDisponiveis() {
        var veiculo = veiculoRepository.findByStatusVeiculo(DISPONIVEL, Pageable.unpaged());

        assertThat(veiculo).isNotEmpty().allMatch(v -> v.getStatusVeiculo() == DISPONIVEL);
    }

    @Test
    void deveBuscarVeiculosVendidos() {
        var veiculo = veiculoRepository.findByStatusVeiculo(VENDIDO, Pageable.unpaged());

        assertThat(veiculo).isNotEmpty().allMatch(v -> v.getStatusVeiculo() == VENDIDO);
    }

    @Test
    @Transactional
    void deveBuscarVeiculoComSeusRelacionamentos() {
        var veiculos = veiculoRepository.findByStatusVeiculo(DISPONIVEL, Pageable.unpaged()).getContent();

        assertThat(veiculos)
                .isNotEmpty();

        var veiculo = veiculos.getFirst();

        assertThat(veiculo.getValor())
                .isPositive();

        assertThat(veiculo)
                .extracting(
                        Veiculo::getModelo,
                        Veiculo::getCarroceria,
                        Veiculo::getCor,
                        Veiculo::getCombustivel,
                        Veiculo::getVendedor,
                        Veiculo::getDataCadastro
                ).doesNotContainNull();
        assertThat(veiculo.getModelo().getMarca())
                .isNotNull();

        assertThat(List.of(
                veiculo.getModelo().getNome(),
                veiculo.getModelo().getMarca().getNome(),
                veiculo.getCarroceria().getNome(),
                veiculo.getCor().getNome(),
                veiculo.getCombustivel().getNome(),
                veiculo.getVendedor().getNome()
        ))
                .isNotEmpty()
                .allSatisfy(nome -> assertThat(nome).isNotBlank());

    }

    @Test
    @Transactional
    void deveBuscarVeiculoComSeusOpcionais() {
        var veiculo = veiculoRepository
                .findByPlaca("ABC1D23")
                .orElseThrow();

        assertThat(veiculo.getOpcionais())
                .isNotEmpty();

        assertThat(veiculo.getOpcionais())
                .allMatch(vo -> vo.getOpcional() != null);

        assertThat(veiculo.getOpcionais())
                .allMatch(vo -> vo.getOpcional().getNome() != null &&
                        !vo.getOpcional().getNome().isBlank());
    }

    @Test
    @Transactional
    void deveBuscarImagensPorVeiculo() {
        var veiculo = veiculoRepository.findByPlaca("ABC1D23").orElseThrow();

        assertThat(veiculo.getImagens())
                .isNotEmpty();

        assertThat(veiculo.getImagens())
                .allSatisfy(imagem ->
                        assertThat(imagem.getNomeOriginal())
                                .isNotBlank()
                );
    }

}
