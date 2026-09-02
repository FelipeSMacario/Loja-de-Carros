package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.entity.*;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.factory.helper.ImagemHelper;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.integration.fixture.VendaIntegrationFixture;
import com.javacar.lojadecarro.repository.ImagensRepository;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import com.javacar.lojadecarro.service.ImagensService;
import com.javacar.lojadecarro.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("Testes transacionais da service de imagens")
@Import(VendaIntegrationFixture.class)
class ImagensServiceTransactionIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ImagensService imagensService;

    @Autowired
    private ImagensRepository imagensRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private VendaIntegrationFixture vendaIntegrationFixture;

    @MockitoBean
    private StorageService storageService;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setup() {
        transactionTemplate =
                new TransactionTemplate(transactionManager);
    }

    @Test
    @DisplayName("Deve remover do storage imagens carregadas quando a transação externa fizer rollback")
    void deveRemoverImagensQuandoTransacaoExternaFizerRollback() throws IOException {
        //Arrange
        var veiculoCriado  = criarVeiculoPersistido(
                "Z7Y46T5",
                DISPONIVEL,
                vendaIntegrationFixture.criarUsuarioPersistido("USUARIO 5", "12347845695", "usuario5@email.com"),
                vendaIntegrationFixture.criarCarroceriaPersistida("CARROERIA 5", true),
                vendaIntegrationFixture.criarCorPersistida("COR 5", true),
                vendaIntegrationFixture.criarModeloPersistido("MODELO 5", "MARCA 5", true),
                vendaIntegrationFixture.criarCombustivelPersistido("COMBUSTIVEL 5", true)
        );
        var veiculoId = veiculoCriado .getId();
        var files = ImagemHelper.criarListImagemFile();
        var upload1 = ImagemHelper.criarUploadValido();
        var upload2 = ImagemHelper.criarUploadValido2();

        when(storageService.upload(files[0], veiculoId))
                .thenReturn(upload1);

        when(storageService.upload(files[1], veiculoId))
                .thenReturn(upload2);

        // Act — abre uma nova transação e recarrega o veículo
        assertThatThrownBy(() ->
                transactionTemplate.executeWithoutResult(status -> {
                    var veiculo = veiculoRepository
                            .findById(veiculoId)
                            .orElseThrow();

                    try {
                        imagensService.criar(files, veiculo);
                    } catch (IOException exception) {
                        throw new UncheckedIOException(exception);
                    }

                    throw new RuntimeException("Falha posterior");
                })
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Falha posterior");

        assertThat(
                imagensRepository.findByBucketAndObjectKey(
                        upload1.bucket(),
                        upload1.objectKey()
                )
        ).isEmpty();

        assertThat(
                imagensRepository.findByBucketAndObjectKey(
                        upload2.bucket(),
                        upload2.objectKey()
                )
        ).isEmpty();

        verify(storageService).delete(upload1.objectKey());
        verify(storageService).delete(upload2.objectKey());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Não deve remover do storage quando a exclusão sofrer rollback")
    void naoDeveRemoverDoStorageQuandoExclusaoSofrerRollback() throws IOException {
        // Arrange
        var veiculo = criarVeiculoPersistidoComImagens(
                "Z7Y46T1",
                DISPONIVEL,
                vendaIntegrationFixture.criarUsuarioPersistido("USUARIO 12", "12347845699", "usuario12@email.com"),
                vendaIntegrationFixture.criarCarroceriaPersistida("CARROERIA 12", true),
                vendaIntegrationFixture.criarCorPersistida("COR 12", true),
                vendaIntegrationFixture.criarModeloPersistido("MODELO 12", "MARCA 12", true),
                vendaIntegrationFixture.criarCombustivelPersistido("COMBUSTIVEL 12", true),
                List.of(7,8,9)
        );

        var imagem = veiculo.getImagens().getFirst();
        var idImagem = imagem.getId();
        var objectKey = imagem.getObjectKey();

        // Act
        assertThatThrownBy(() ->
                transactionTemplate.executeWithoutResult(status -> {
                    imagensService.delete(idImagem);

                    throw new RuntimeException("Falha posterior");
                })
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Falha posterior");

        // Assert
        assertThat(imagensRepository.findById(idImagem))
                .isPresent();

        verify(storageService, never())
                .delete(objectKey);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve remover do storage após confirmar a exclusão no banco")
    void deveRemoverDoStorageAposCommit() throws IOException{
        // Arrange
        var veiculo = criarVeiculoPersistidoComImagens(
                "Z7Y46T2",
                DISPONIVEL,
                vendaIntegrationFixture.criarUsuarioPersistido("USUARIO 3", "12347845698", "usuario3@email.com"),
                vendaIntegrationFixture.criarCarroceriaPersistida("CARROERIA 3", true),
                vendaIntegrationFixture.criarCorPersistida("COR 3", true),
                vendaIntegrationFixture.criarModeloPersistido("MODELO 3", "MARCA 3", true),
                vendaIntegrationFixture.criarCombustivelPersistido("COMBUSTIVEL 3", true),
                List.of(4,5,6));

        var imagem = veiculo.getImagens().getFirst();
        var idImagem = imagem.getId();
        var objectKey = imagem.getObjectKey();

        // Act
        transactionTemplate.executeWithoutResult(status ->
                {
                    imagensService.delete(idImagem);
                }
        );

        // Assert
        assertThat(imagensRepository.findById(idImagem))
                .isEmpty();

        verify(storageService)
                .delete(objectKey);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve manter o commit quando a exclusão no storage falhar")
    void deveManterCommitQuandoExclusaoNoStorageFalhar()
            throws IOException {

        // Arrange
        var veiculo = criarVeiculoPersistidoComImagens(
                "Z7Y46T3",
                DISPONIVEL,
                vendaIntegrationFixture.criarUsuarioPersistido("USUARIO 4", "12347845697", "usuario4@email.com"),
                vendaIntegrationFixture.criarCarroceriaPersistida("CARROERIA 4", true),
                vendaIntegrationFixture.criarCorPersistida("COR 4", true),
                vendaIntegrationFixture.criarModeloPersistido("MODELO 4", "MARCA 4", true),
                vendaIntegrationFixture.criarCombustivelPersistido("COMBUSTIVEL 4", true),
                List.of(10,11,12));

        var imagem = veiculo.getImagens().getFirst();
        var idImagem = imagem.getId();
        var objectKey = imagem.getObjectKey();

        doThrow(new IOException("Storage indisponível"))
                .when(storageService)
                .delete(objectKey);

        // Act + Assert
        assertThatCode(() ->
                transactionTemplate.executeWithoutResult(status ->
                        {
                            imagensService.delete(idImagem);
                        }
                )
        ).doesNotThrowAnyException();

        assertThat(imagensRepository.findById(idImagem))
                .isEmpty();

        verify(storageService)
                .delete(objectKey);
    }

    private Veiculo criarVeiculoPersistido(String placa,
                                           StatusVeiculo status,
                                           Usuario usuario,
                                           Carroceria carroceria,
                                           Cor cor,
                                           Modelo modelo,
                                           Combustivel combustivel) {
        return vendaIntegrationFixture
                .criarVeiculoPersistido(
                        placa,
                        BigDecimal.valueOf(200000),
                        carroceria,
                        cor,
                        modelo,
                        combustivel,
                        usuario,
                        status);
    }

    private Veiculo criarVeiculoPersistidoComImagens(
            String placa,
            StatusVeiculo status,
            Usuario usuario,
            Carroceria carroceria,
            Cor cor,
            Modelo modelo,
            Combustivel combustivel,
            List<Integer> ids
    ) {
        return vendaIntegrationFixture
                .criarVeiculoPersistidoComImagens(
                        placa,
                        BigDecimal.valueOf(200000),
                        carroceria,
                        cor,
                        modelo,
                        combustivel,
                        usuario,
                        status,
                        ids
                );
    }
}
