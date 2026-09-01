package com.javacar.lojadecarro.integration.fixture;

import com.javacar.lojadecarro.entity.*;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.repository.*;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.javacar.lojadecarro.utils.Utils.ZONE;

@RequiredArgsConstructor
public class VendaIntegrationFixture {
    private final VeiculoRepository veiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarroceriaRepository carroceriaRepository;
    private final CoresRepository coresRepository;
    private final ModeloRepository modeloRepository;
    private final MarcaRepository marcaRepository;
    private final CombustivelRepository combustivelRepository;
    private final ImagensRepository imagensRepository;
    private final OpcionalRepository opcionalRepository;


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

    public Veiculo criarVeiculoPersistidoComImagens(String placa,
                                                    BigDecimal valor,
                                                    Carroceria carroceria,
                                                    Cor cor,
                                                    Modelo modelo,
                                                    Combustivel combustivel,
                                                    Usuario vendedor,
                                                    StatusVeiculo status) {
        var veiculo = criarVeiculoPersistido(placa, valor, carroceria, cor, modelo, combustivel, vendedor, status);
        List<Imagem> imagems = new ArrayList<>();
        imagems.add(criarImagem(1L, veiculo, true));
        imagems.add(criarImagem(2L, veiculo, false));
        imagems.add(criarImagem(3L, veiculo, false));

        veiculo.setImagens(imagems);

        return veiculoRepository.save(veiculo);
    }

    public Imagem criarImagem(Long id, Veiculo veiculo, boolean principal) {
        var imagem = new Imagem();
        imagem.setId(id);
        imagem.setNomeOriginal("nomeImagemOriginal" + "/" + id);
        imagem.setObjectKey("imagens/2026/" + id + "/foto.jpg");
        imagem.setBucket("bucketImagem" + "/" + id);
        imagem.setContentType("image/" + id + "/jpeg");
        imagem.setTamanho(200L);
        imagem.setPrincipal(principal);
        imagem.setDataCadastro(LocalDateTime.now(ZONE));
        imagem.setVeiculo(veiculo);
        return imagensRepository.save(imagem);
    }

    public Veiculo criarVeiculoPersistidoComOpcionais(String placa,
                                                      BigDecimal valor,
                                                      Carroceria carroceria,
                                                      Cor cor,
                                                      Modelo modelo,
                                                      Combustivel combustivel,
                                                      Usuario vendedor,
                                                      StatusVeiculo status) {
        var veiculo = criarVeiculoPersistido(placa, valor, carroceria, cor, modelo, combustivel, vendedor, status);
        veiculo.adicionarOpcional(criarOpcional("Nitrogênio", true));
        veiculo.adicionarOpcional(criarOpcional("Suspenção magnética", true));
        veiculo.adicionarOpcional(criarOpcional("Leds", true));

        return veiculoRepository.save(veiculo);
    }

    public Opcional criarOpcional(String nome, boolean ativo) {
        var opcional = new Opcional();
        opcional.setNome(nome);
        opcional.setAtivo(ativo);

        return opcionalRepository.save(opcional);
    }

    public Carroceria criarCarroceriaPersistida() {
        var carroceria = new Carroceria();
        carroceria.setNome("CARROCERIA TESTE");
        carroceria.setAtivo(true);
        carroceria.setDataCadastro(LocalDateTime.now());

        return carroceriaRepository.save(carroceria);
    }

    public Carroceria criarCarroceriaPersistida(String nome, boolean ativo) {
        var carroceria = new Carroceria();
        carroceria.setNome(nome);
        carroceria.setAtivo(ativo);
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

    public Cor criarCorPersistida(String nome, boolean ativo) {
        var cor = new Cor();
        cor.setNome(nome);
        cor.setAtivo(ativo);
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

    public Modelo criarModeloPersistido(String nome, boolean ativo) {
        var modelo = new Modelo();
        modelo.setNome(nome);
        modelo.setMarca(criarMarca2Persistida());
        modelo.setAtivo(ativo);
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

    private Marca criarMarca2Persistida() {
        var marca = new Marca();
        marca.setNome("MARCA TESTE2");
        marca.setUrl("URL TESTE2");
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

    public Combustivel criarCombustivelPersistido(String nome, boolean ativo) {
        var combustivel = new Combustivel();
        combustivel.setNome(nome);
        combustivel.setAtivo(ativo);
        combustivel.setDataCadastro(LocalDateTime.now());

        return combustivelRepository.save(combustivel);
    }
}
