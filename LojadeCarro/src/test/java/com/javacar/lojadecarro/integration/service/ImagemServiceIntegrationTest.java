package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.ImagemHelper;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.integration.fixture.VendaIntegrationFixture;
import com.javacar.lojadecarro.repository.ImagensRepository;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import com.javacar.lojadecarro.service.ImagensService;
import com.javacar.lojadecarro.service.StorageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.IMAGEM;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.factory.helper.ImagemHelper.criarListImagemFile;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Transactional
@Import(VendaIntegrationFixture.class)
@DisplayName("Testes da service da imagem")
public class ImagemServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ImagensService imagensService;
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private ImagensRepository imagensRepository;
    @Autowired
    protected VendaIntegrationFixture vendaIntegrationFixture;
    @PersistenceContext
    protected EntityManager entityManager;
    @MockitoBean
    private StorageService storageService;

    @Nested
    @DisplayName("Testes da criação de imagens")
    class Criar {
        @Test
        @DisplayName("Deve criar imagens")
        void deveCriarImagens() throws IOException {
            //Arrange
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", DISPONIVEL, null);
            var imagemRequest = criarListImagemFile();
            var file1 = imagemRequest[0];
            var file2 = imagemRequest[1];

            when(storageService.upload(file1, veiculo.getId()))
                    .thenReturn(ImagemHelper.criarUploadValido());

            when(storageService.upload(file2, veiculo.getId()))
                    .thenReturn(ImagemHelper.criarUploadValido2());
            //ACT
            var response = imagensService.criar(imagemRequest, veiculo);
            entityManager.flush();
            entityManager.clear();
            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            var imagensPersistidas =
                    imagensRepository.findByVeiculoId(veiculo.getId());
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .hasSize(2);

            assertThat(imagensPersistidas)
                    .hasSize(2)
                    .filteredOn(Imagem::isPrincipal)
                    .hasSize(1);

            assertThat(veiculoAtualizado.getImagens())
                    .hasSize(2)
                    .extracting(i -> i.getVeiculo().getId())
                    .containsOnly(veiculoAtualizado.getId());

            assertThat(response
                    .stream()
                    .filter(Imagem::isPrincipal)
                    .count()
            ).isEqualTo(1);

            assertThat(
                    response
                            .stream()
                            .filter(i -> !i.isPrincipal())
                            .count()
            ).isEqualTo(1);

        }

        @Test
        @DisplayName("Deve retornar a lista de imagens vazia")
        void deveRetornarListaVaziaQuandoNaoReceberArquivos() throws IOException {
            //Arrange
            var veiculo = criarVeiculoPersistido("ZX5AS7Q", DISPONIVEL, null);
            //ACT
            var response = imagensService.criar(null, veiculo);
            //Assert
            assertThat(response)
                    .isEmpty();
        }


    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes para definir uma imagem como principal")
    class DefinirPrincipal {
        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"DISPONIVEL", "PAUSADO"}
        )
        @DisplayName("Deve definir a imagem como principal com status permitido")
        void deveDefinirAImagemComoPrincipal(StatusVeiculo statusVeiculo) {
            //Arrange
            var veiculo = criarVeiculoPersistidoComImagens("ZX5AS7Q", statusVeiculo, null);
            var imagens = veiculo.getImagens();
            var imagemPrincipal = imagens.stream().filter(Imagem::isPrincipal).findFirst().orElseThrow();
            var imagensNaoPrincipais = imagens.stream().filter(i -> !i.isPrincipal()).map(Imagem::getId).toList();
            var novaPrincipal = imagensNaoPrincipais.getFirst();

            assertThat(imagens)
                    .hasSize(3);

            assertThat(imagens)
                    .filteredOn(Imagem::isPrincipal)
                    .extracting(Imagem::getId)
                    .containsExactly(imagemPrincipal.getId());

            assertThat(imagens)
                    .filteredOn(i -> !i.isPrincipal())
                    .extracting(Imagem::getId)
                    .containsExactlyInAnyOrderElementsOf(imagensNaoPrincipais);


            //ACT
            imagensService.definirPrincipal(novaPrincipal);
            entityManager.flush();
            entityManager.clear();

            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            var imagensAtualizadas = veiculoAtualizado.getImagens();

            var novasImagensNaoPrincipais = List.of(imagemPrincipal.getId(), imagensNaoPrincipais.getLast());
            //Assert
            assertThat(imagensAtualizadas)
                    .hasSize(3);

            assertThat(imagensAtualizadas)
                    .filteredOn(Imagem::isPrincipal)
                    .extracting(Imagem::getId)
                    .containsExactly(novaPrincipal);

            assertThat(imagensAtualizadas)
                    .filteredOn(i -> !i.isPrincipal())
                    .extracting(Imagem::getId)
                    .containsExactlyInAnyOrderElementsOf(novasImagensNaoPrincipais);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar a imagem ao definir principal")
        void deveLancarExcecaoAoBuscarImagemAoDefinirPrincipal() {
            //Arrange
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> imagensService.definirPrincipal(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(excecao, IMAGEM, ID_INVALIDO);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        void deveLancarExcecaoAoDefinirImagemComoPrincipalComStatusProibido(StatusVeiculo statusVeiculo) {
            //Arrange
            var veiculo = criarVeiculoPersistidoComImagens("ZX5AS7Q", statusVeiculo, null);
            var imagens = veiculo.getImagens();
            var idImagem = imagens.getFirst().getId();

            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> imagensService.definirPrincipal(idImagem));
            //Assert
            assertBusinessResponseError(exception, "Somente anúncios disponíveis ou pausados podem ser editados.");
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da exclusão da imagem")
    class DeletarImagem {
        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"DISPONIVEL", "PAUSADO"}
        )
        @DisplayName("Deve deletar a imagem com status permitido")
        void deveDeletarImagemComStatusPermitido(StatusVeiculo statusVeiculo) {
            //Arrange
            var veiculo = criarVeiculoPersistidoComImagens("ZX5AS7Q", statusVeiculo, null);
            var idVeiculo = veiculo.getId();
            entityManager.flush();
            entityManager.clear();
            var veiculoPersistido = veiculoRepository
                    .findById(idVeiculo)
                    .orElseThrow();

            var imagens = veiculoPersistido.getImagens();
            var idImagem = imagens.stream()
                    .filter(imagem -> !imagem.isPrincipal())
                    .map(Imagem::getId)
                    .findFirst()
                    .orElseThrow();
            var imagensEsperadas = imagens
                    .stream()
                    .map(Imagem::getId)
                    .filter(id -> !id.equals(idImagem))
                    .toList();

            assertThat(imagens)
                    .hasSize(3);
            //ACT
            imagensService.delete(idImagem);
            entityManager.flush();
            entityManager.clear();
            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            var imagensAtualizadas = veiculoAtualizado.getImagens();
            //Assert

            assertThat(veiculoAtualizado.getImagens())
                    .isNotEmpty()
                    .hasSize(2);

            assertThat(imagensRepository.findById(idImagem))
                    .isEmpty();

            assertThat(imagensAtualizadas)
                    .extracting(Imagem::getId)
                    .containsExactlyInAnyOrderElementsOf(imagensEsperadas)
                    .doesNotContain(idImagem);

        }

        @Test
        @DisplayName("Deve promover outra imagem ao excluir a principal")
        void deveDeletarImagemPrincipal() {
            //Arrange
            var veiculo = criarVeiculoPersistidoComImagens("ZX5AS7Q", DISPONIVEL, null);
            entityManager.flush();
            entityManager.clear();
            var imagens = veiculo.getImagens();
            var idImagem = imagens.stream()
                    .filter(Imagem::isPrincipal)
                    .map(Imagem::getId)
                    .findFirst().orElseThrow();
            var imagensEsperadas = imagens
                    .stream()
                    .map(Imagem::getId)
                    .filter(id -> !id.equals(idImagem))
                    .toList();

            assertThat(imagens)
                    .hasSize(3);

            assertThat(imagens)
                    .filteredOn(Imagem::isPrincipal)
                    .extracting(Imagem::getId)
                    .containsExactly(idImagem);
            //ACT
            imagensService.delete(idImagem);
            entityManager.flush();
            entityManager.clear();
            var veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).orElseThrow();
            var imagensAtualizadas = veiculoAtualizado.getImagens();
            //Assert

            assertThat(veiculoAtualizado.getImagens())
                    .isNotEmpty()
                    .hasSize(2);

            assertThat(imagensAtualizadas)
                    .extracting(Imagem::getId)
                    .containsExactlyInAnyOrderElementsOf(imagensEsperadas)
                    .doesNotContain(idImagem);

            assertThat(imagensAtualizadas)
                    .filteredOn(Imagem::isPrincipal)
                    .hasSize(1);

            assertThat(imagensAtualizadas)
                    .filteredOn(imagem -> !imagem.isPrincipal())
                    .hasSize(1);

        }

        @Test
        @DisplayName("Deve lançar exceção quando imagem não for encontrada")
        void deveLancarExcecaoImagemNaoEncontrada() {
            //Arrange
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> imagensService.delete(ID_INVALIDO)
            );
            //Assert
            assertNotFoundResponseError(excecao, IMAGEM, ID_INVALIDO);

        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        @DisplayName("Deve lançar exceção ao deletar imagem com status proíbido")
        void deveLancarExcecaoAoDeletarImagemComStatusProibido(StatusVeiculo statusVeiculo) {
            //Arrange
            var veiculo = criarVeiculoPersistidoComImagens("ZX5AS7Q", statusVeiculo, null);
            entityManager.flush();
            entityManager.clear();
            var imagens = veiculo.getImagens();
            var idImagem = imagens.getFirst().getId();

            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> imagensService.delete(idImagem));
            //Assert
            assertBusinessResponseError(exception, "Somente anúncios disponíveis ou pausados podem ser editados.");
        }
    }

    private Usuario criarVendedorPersistido() {
        return vendaIntegrationFixture
                .criarUsuarioPersistido("USUARIO 1", "85296374165", "usuario1@gmail.com");
    }

    private Veiculo criarVeiculoPersistido(String placa, StatusVeiculo status, Usuario usuario) {
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
                        List.of(1, 2, 3));
    }
}
