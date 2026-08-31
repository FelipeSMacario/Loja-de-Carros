package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.ImagemHelper;
import com.javacar.lojadecarro.factory.imagem.ImagemEntityFactory;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.ImagensRepository;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import com.javacar.lojadecarro.service.ImagensService;
import com.javacar.lojadecarro.service.StorageService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.IMAGEM;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Transactional
@DisplayName("Testes de exceção da service da imagem")
public class ImagemServiceIntegrationExceptionTest extends AbstractIntegrationTest {
    @Autowired
    private ImagensService imagensService;
    @MockitoBean
    private StorageService storageService;
    @MockitoBean
    private ImagensRepository imagensRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Nested
    @DisplayName("Testes exceções da criação da imagem")
    class CriarException {
        @Test
        @DisplayName("Deve lançar exceção ao fazer upload da imagem")
        void deveLancarExcecaoQuandoUploadImagem() throws IOException {
            //Arrange
            var imagemRequest = ImagemHelper.criarListImagemFile();
            var veiculo = buscaVeiculoPorPlaca("HIJ7K89");

            var imagem = imagemRequest[0];

            when(storageService.upload(eq(imagem), anyLong()))
                    .thenThrow(new IOException("Erro ao realizar upload"));
            //ACT
            var exception = assertThrows(IOException.class,
                    () -> imagensService.criar(imagemRequest, veiculo));
            //Assert
            AssertionsForClassTypes.assertThat(exception)
                    .hasMessage("Erro ao realizar upload");
        }

        @Test
        @DisplayName("Deve lançar exceção ao persistir a imagem no banco")
        void deveLancarExcecaoQuandoPersistirImagem() throws IOException {
            //Arrange
            var imagemRequest = ImagemHelper.criarListImagemFile();
            var veiculo = buscaVeiculoPorPlaca("KPB8712");

            var imagem1 = imagemRequest[0];
            var imagem2 = imagemRequest[1];
            var imagemUpload1 = ImagemHelper.criarUploadValido();
            var imagemUpload2 = ImagemHelper.criarUploadValido2();

            when(storageService.upload(eq(imagem1), anyLong()))
                    .thenReturn(imagemUpload1);

            when(storageService.upload(eq(imagem2), anyLong()))
                    .thenReturn(imagemUpload2);

            when(imagensRepository.saveAll(any()))
                    .thenThrow(new RuntimeException("Erro"));
            //ACT
            var exception = assertThrows(RuntimeException.class,
                    () -> imagensService.criar(imagemRequest, veiculo));
            //Assert
            assertThat(exception)
                    .hasMessage("Erro");

            verify(storageService).delete(imagemUpload1.objectKey());
            verify(storageService).delete(imagemUpload2.objectKey());

            verify(storageService, times(2))
                    .upload(any(), anyLong());
        }

    }

    private Veiculo buscaVeiculoPorPlaca(String placa) {
        return veiculoRepository.findByPlaca(placa).orElseThrow();
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes de exceção para definir principal")
    class DefinirPrincipalException {
        @Test
        @DisplayName("Deve lançar exceção ao definir imagem principal")
        void deveLancarExcecaoQuandoDefinirImagemPrincipal() {
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> imagensService.definirPrincipal(-1L));
            //Assert

            assertThat(exception)
                    .hasMessage(IMAGEM.naoEncontrada() + -1L);
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes de exceção para exclusão da imagem")
    class ExcluirException {
        @Test
        @DisplayName("Deve lançar exceção quando não encontrar imagem")
        void deveLancarExcecaoQuandoNaoEncontrarImagem() {
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> imagensService.delete(-1L));
            //Assert

            assertThat(exception)
                    .hasMessage(IMAGEM.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao deletar imagem da storage")
        void deveLancarExcecaoQuandoDeletarImagemStorage() throws IOException {
            //Arrange
            var imagem = ImagemEntityFactory.criarEntity().comTodosOsCampos().build();
            var veiculo = imagem.getVeiculo();
            List<Imagem> imagens = new ArrayList<>();
            imagens.add(imagem);
            veiculo.setImagens(imagens);

            when(imagensRepository.findById(1L))
                    .thenReturn(Optional.of(imagem));

            doThrow(new IOException("Erro ao deletar image"))
                    .when(storageService).delete(imagem.getObjectKey());

            //ACT
            var exception = assertThrows(IOException.class,
                    () -> imagensService.delete(1L));

            //Assert
            AssertionsForClassTypes.assertThat(exception)
                    .hasMessage("Erro ao deletar image");
        }

    }
}
