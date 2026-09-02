package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.response.ImagemResponse;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.ImagemHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("Testes das imagens do veículo")
public class VeiculoServiceImagemIntegrationTest extends AbstractVeiculoServiceIntegrationTest{
    @Nested
    @DisplayName("Testes da listagem das imagens do veiculo")
    class ListarImagens {
        @Test
        @DisplayName("Deve listar as imagens")
        void deveListarAsImagens() {
            //Arrange
            var veiculo = criarVeiculoPersistidoComImagens("ZX5AS7Q", DISPONIVEL, null);
            //Act
            var resultado = veiculoService.listarImagens(veiculo.getId());
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .hasSize(3);

            assertThat(
                    resultado
                            .stream()
                            .filter(ImagemResponse::principal)
                            .count()
            ).isEqualTo(1);

            assertThat(
                    resultado
                            .stream()
                            .filter(i -> !i.principal())
                            .count()
            ).isEqualTo(2);
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar o veiculo")
        void deveLancarExcecaoQuandoNaoEncontrarVeiculo() {
            //Act
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.listarImagens(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(excecao, VEICULO, ID_INVALIDO);
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da vinculação de imagens ao veículo")
    class VincularImagens {
        @Test
        @DisplayName("Deve vincular imagens ao veículo")
        void deveVincularImagens() throws IOException {
            //Arrange
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", DISPONIVEL, null);
            var imagemRequest = ImagemHelper.criarListImagemFile();

            var imagem1 = imagemRequest[0];
            var imagem2 = imagemRequest[1];

            when(storageService.upload(eq(imagem1), anyLong()))
                    .thenReturn(ImagemHelper.criarUploadValido());

            when(storageService.upload(eq(imagem2), anyLong()))
                    .thenReturn(ImagemHelper.criarUploadValido2());
            //ACT
            var resultado = veiculoService.vincularImagens(veiculo.getId(), imagemRequest);
            entityManager.flush();
            entityManager.clear();
            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            var primeiraImagemVeiculo = veiculoAtualizado.getImagens().getFirst();
            var ultimaImagemVeiculo = veiculoAtualizado.getImagens().getLast();
            var imagem = imagensRepository.findByVeiculoId(veiculo.getId());
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            ImagemResponse::id,
                            ImagemResponse::nomeOriginal,
                            ImagemResponse::objectKey)
                    .containsExactly(
                            tuple(primeiraImagemVeiculo.getId(), primeiraImagemVeiculo.getNomeOriginal(), primeiraImagemVeiculo.getObjectKey()),
                            tuple(ultimaImagemVeiculo.getId(), ultimaImagemVeiculo.getNomeOriginal(), ultimaImagemVeiculo.getObjectKey())
                    );

            assertThat(
                    resultado
                            .stream()
                            .filter(ImagemResponse::principal)
                            .count()
            ).isEqualTo(1);

            assertThat(
                    resultado
                            .stream()
                            .filter(i -> !i.principal())
                            .count()
            ).isEqualTo(1);

            assertThat(veiculoAtualizado.getImagens())
                    .extracting(Imagem::getId, Imagem::getObjectKey)
                    .containsExactly(
                            tuple(imagem.getFirst().getId(), imagem.getFirst().getObjectKey()),
                            tuple(imagem.getLast().getId(), imagem.getLast().getObjectKey()));
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoNaoEncontrarVeiculo() {
            //Arrange
            var imagens = ImagemHelper.criarListImagemFile();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.vincularImagens(ID_INVALIDO, imagens));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_INVALIDO);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        @DisplayName("Deve lançar exceção ao vincular imagem para veículo com status proibido")
        void deveLancarExcecaoAoVincularImagemEmVeiculoComStatusProibido(StatusVeiculo statusVeiculo) {
            //Arrange
            var imagens = ImagemHelper.criarListImagemFile();
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", statusVeiculo, null);
            var veiculoId = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularImagens(veiculoId, imagens));
            //Assert
            assertBusinessResponseError(exception, "Somente anúncios disponíveis ou pausados podem ser editados.");
        }

        @Test
        @DisplayName("Deve lançar exceção quando o upload da imagem falhar")
        void deveLancarExcecaoAoVincularImageInvalida() throws IOException {
            // Arrange
            var imagens = ImagemHelper.criarListImagemFile();
            var veiculo = criarVeiculoPersistido(
                    "ZX5AS7Q",
                    DISPONIVEL,
                    null
            );

            when(storageService.upload(imagens[0], veiculo.getId()))
                    .thenThrow(new IOException("Erro ao realizar upload"));

            // Act
            var exception = assertThrows(
                    IOException.class,
                    () -> veiculoService.vincularImagens(
                            veiculo.getId(),
                            imagens
                    )
            );

            // Assert
            assertThat(exception)
                    .hasMessage("Erro ao realizar upload");

        }
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
                        List.of(1,2,3));
    }
}
