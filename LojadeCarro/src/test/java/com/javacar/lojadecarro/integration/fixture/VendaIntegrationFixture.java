package com.javacar.lojadecarro.integration.fixture;

import com.javacar.lojadecarro.entity.*;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.repository.*;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.enums.StatusVeiculo.RESERVADO;

@RequiredArgsConstructor
public class VendaIntegrationFixture {
    private final VeiculoRepository veiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarroceriaRepository carroceriaRepository;
    private final CoresRepository coresRepository;
    private final ModeloRepository modeloRepository;
    private final MarcaRepository marcaRepository;
    private final CombustivelRepository combustivelRepository;


    public Usuario criarUsuarioPersistido(String nome, String cpf, String email) {
        var usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setPassword("password-hash-teste");
        usuario.setAtivo(true);
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        return usuarioRepository.save(usuario);
    }

    public Veiculo criarVeiculoPersistido(String placa,
                                          BigDecimal valor,
                                          Carroceria carroceria,
                                          Cor cor,
                                          Modelo modelo,
                                          Combustivel combustivel,
                                          Usuario vendedor,
                                          StatusVeiculo status) {
        var veiculo = new Veiculo();
        veiculo.setAnoFabricacao((short) 2014);
        veiculo.setMotor("1.4 flex");
        veiculo.setPlaca(placa);
        veiculo.setQuilometragem(40000);
        veiculo.setValor(valor);
        veiculo.setDescricao("Teste descricao");
        veiculo.setDataCadastro(LocalDateTime.now());
        veiculo.setStatusVeiculo(status);
        veiculo.setCarroceria(carroceria);
        veiculo.setCor(cor);
        veiculo.setModelo(modelo);
        veiculo.setCombustivel(combustivel);
        veiculo.setVendedor(vendedor);
        return veiculoRepository.save(veiculo);
    }

    public Carroceria criarCarroceriaPersistida() {
        var carroceria = new Carroceria();
        carroceria.setNome("CARROCERIA TESTE");
        carroceria.setAtivo(true);
        carroceria.setDataCadastro(LocalDateTime.now());

        return carroceriaRepository.save(carroceria);
    }

    public Cor criarCorPersistida() {
        var cor = new Cor();
        cor.setNome("COR TESTE");
        cor.setAtivo(true);
        cor.setDataCadastro(LocalDateTime.now());

        return coresRepository.save(cor);
    }

    public Modelo criarModeloPersistido() {
        var modelo = new Modelo();
        modelo.setNome("MODELO TESTE");
        modelo.setMarca(criarMarcaPersistida());
        modelo.setAtivo(true);
        modelo.setDataCadastro(LocalDateTime.now());

        return modeloRepository.save(modelo);
    }

    private Marca criarMarcaPersistida() {
        var marca = new Marca();
        marca.setNome("MARCA TESTE");
        marca.setUrl("URL TESTE");
        marca.setAtivo(true);
        marca.setDataCadastro(LocalDateTime.now());

        return marcaRepository.save(marca);
    }

    public Combustivel criarCombustivelPersistido() {
        var combustivel = new Combustivel();
        combustivel.setNome("COMBUSTIVEL TESTE");
        combustivel.setAtivo(true);
        combustivel.setDataCadastro(LocalDateTime.now());

        return combustivelRepository.save(combustivel);
    }
}
