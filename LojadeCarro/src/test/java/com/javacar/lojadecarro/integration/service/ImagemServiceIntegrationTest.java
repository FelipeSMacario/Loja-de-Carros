package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.factory.helper.ImagemHelper;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.ImagensRepository;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import com.javacar.lojadecarro.service.ImagensService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("Testes da service da imagem")
public class ImagemServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ImagensService imagensService;
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private ImagensRepository imagensRepository;

    @Nested
    @DisplayName("Testes da criação de imagens")
    class Criar {
        @Test
        @DisplayName("Deve criar imagens")
        @Transactional
        void deveCriarImagens() throws IOException {
            //Arrange
            var imagemRequest = ImagemHelper.criarListImagemFile();
            var veiculo = buscaVeiculoPorPlaca("KPB8712");

            //ACT
            var response = imagensService.criar(imagemRequest, veiculo);
            var veiculoAtualizado = buscaVeiculoPorPlaca("KPB8712");
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .hasSize(2);

            assertThat(veiculoAtualizado.getImagens())
                    .hasSize(2)
                    .extracting(i -> i.getVeiculo().getId())
                    .containsOnly(veiculoAtualizado.getId());

            assertThat(veiculoAtualizado.getImagens())
                    .extracting(Imagem::getObjectKey,
                            Imagem::getBucket)
                    .doesNotContainNull();
        }

        @Test
        @DisplayName("Deve retornar a lista de imagens vazia")
        void deveRetornarListaVaziaQuandoNaoReceberArquivos() throws IOException {
            //Arrange
            var request = buscaVeiculoPorPlaca("HIJ7K89");
            //ACT
            var response = imagensService.criar(null, request);
            //Assert
            assertThat(response)
                    .isEmpty();
        }


    }

    @Nested
    @DisplayName("Testes para definir uma imagem como principal")
    class DefinirPrincipal {
        @Test
        @DisplayName("Deve definir uma imagem como principal")
        @Transactional
        void deveDefinirUmaImagemComoPrincipal() {
            //Arrange
            var imagem = buscaImagem("loja-veiculos", "veiculos/1/corolla-lateral.jpg");
            var idImagem = imagem.getId();
            var idVeiculo = imagem.getVeiculo().getId();
            //ACT
            imagensService.definirPrincipal(idImagem);
            var veiculo = buscaVeiculo(idVeiculo);
            //Assert
            assertThat(veiculo.getImagens())
                    .isNotEmpty()
                    .hasSize(3);

            assertThat(
                    veiculo.getImagens()
                            .stream()
                            .filter(Imagem::isPrincipal)
                            .count()
            ).isEqualTo(1);
            assertThat(
                    veiculo.getImagens()
                            .stream()
                            .filter(Imagem::isPrincipal)
                            .map(Imagem::getId)
            ).containsExactly(idImagem);
            assertThat(
                    veiculo.getImagens()
                            .stream()
                            .filter(i -> !i.isPrincipal())
                            .count()
            ).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Testes da exclusão da imagem")
    class Exclusao {
        @Test
        @DisplayName("Deve deletar uma imagem")
        @Transactional
        void deveDeletarUmaImagem() throws IOException {
            //Arrange
            var imagem = buscaImagem("loja-veiculos", "veiculos/2/civic-painel.jpg");
            var idImagem = imagem.getId();
            var idVeiculo = imagem.getVeiculo().getId();
            //ACT
            imagensService.delete(idImagem);
            // força sincronização do Persistence Context
            imagensRepository.flush();
            var veiculo = buscaVeiculo(idVeiculo);
            //Assert

            assertThat(veiculo.getImagens())
                    .isNotEmpty()
                    .hasSize(1);

            assertThat(veiculo.getImagens())
                    .extracting(Imagem::getId)
                    .noneMatch(id -> id.equals(idVeiculo));

        }
    }


    private Veiculo buscaVeiculoPorPlaca(String placa) {
        return veiculoRepository.findByPlaca(placa).orElseThrow();
    }

    private Imagem buscaImagem(String bucket, String objectKey) {
        return imagensRepository.findByBucketAndObjectKey(bucket, objectKey).orElseThrow();
    }

    private Veiculo buscaVeiculo(Long id) {
        return veiculoRepository.findById(id).orElseThrow();
    }
}
