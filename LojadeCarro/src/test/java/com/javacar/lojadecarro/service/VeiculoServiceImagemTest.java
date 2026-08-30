package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.ImagemResponse;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.VeiculoTestContext;
import com.javacar.lojadecarro.factory.imagem.ImagemEntityFactory;
import com.javacar.lojadecarro.factory.imagem.ImagemResponseFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@DisplayName("Testes das imagens do veículo")
public class VeiculoServiceImagemTest extends AbstractVeiculoServiceTest{
    @Nested
    @DisplayName("Testes da listagem de imagens")
    class ListarImagens {
        @Test
        @DisplayName("Deve listar imagens do veículo")
        void deveListarImagens() {
            //Arrange
            var cx = new VeiculoTestContext();
            var imagem1 = cx.imagens.getFirst();
            var imagem2 = ImagemEntityFactory.criarEntity()
                    .comTodosOsCampos()
                    .comId(2L)
                    .build();

            cx.entity.getImagens().addAll(List.of(imagem1, imagem2));

            var imageResponse1 = ImagemResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .build();
            var imageResponse2 = ImagemResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comId(2L)
                    .build();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(imagemMapper.toResponse(imagem1))
                    .thenReturn(imageResponse1);
            when(imagemMapper.toResponse(imagem2))
                    .thenReturn(imageResponse2);
            //ACT
            var resultado = veiculoService.listarImagens(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            ImagemResponse::id,
                            ImagemResponse::nomeOriginal
                    ).containsExactly(
                            tuple(1L, "nomeImagemOriginal"),
                            tuple(2L, "nomeImagemOriginal")
                    );

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(imagemMapper).toResponse(imagem1);
            verify(imagemMapper).toResponse(imagem2);
            verifyNoMoreInteractions(
                    veiculoRepository,
                    imagemMapper
            );
        }

        @Test
        @DisplayName("Deve lançar exceção ao listar imagens")
        void deveLancarExcecaoListarImagens() {
            //Arrange
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.listarImagens(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, VEICULO, ID_VALIDO);

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(imagemMapper, never()).toResponse(any());
            verifyNoMoreInteractions(
                    veiculoRepository,
                    imagemMapper
            );
        }
    }

    @Nested
    @DisplayName("Testes da vinculação de imagens ao veículo")
    class VincularImagens {
        @Test
        @DisplayName("Deve vincular imagens ao veículo")
        void deveVincularImagens() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            var imagem1 = cx.imagens.getFirst();
            var imagem2 = ImagemEntityFactory.criarEntity().comTodosOsCampos().comId(2L).build();
            var imagemList = List.of(imagem1, imagem2);
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(imagensService.criar(cx.imagemFile, cx.entity))
                    .thenReturn(imagemList);

            when(imagemMapper.toResponse(imagemList.getFirst()))
                    .thenReturn(cx.imagemResponseList.getFirst());

            when(imagemMapper.toResponse(imagemList.getLast()))
                    .thenReturn(cx.imagemResponseList.getLast());
            //ACT
            var resultado = veiculoService.vincularImagens(ID_VALIDO, cx.imagemFile);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            ImagemResponse::id,
                            ImagemResponse::nomeOriginal,
                            ImagemResponse::objectKey)
                    .containsExactly(
                            tuple(1L, "nomeImagemOriginal", "imagens/2026/foto.jpg"),
                            tuple(2L, "nomeImagemOriginal", "imagens/2026/foto.jpg")
                    );

            assertThat(
                    resultado
                            .stream()
                            .filter(ImagemResponse::principal)
                            .count()
            ).isEqualTo(1);

            AssertionsForClassTypes.assertThat(
                    resultado
                            .stream()
                            .filter(i -> !i.principal())
                            .count()
            ).isEqualTo(1);

            assertThat(cx.entity.getImagens())
                    .extracting(Imagem::getId, Imagem::getObjectKey)
                    .containsExactly(
                            tuple(imagem1.getId(), imagem1.getObjectKey()),
                            tuple(imagem2.getId(), imagem2.getObjectKey()));

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(imagensService).criar(cx.imagemFile, cx.entity);
            verify(imagemMapper).toResponse(imagemList.getFirst());
            verify(imagemMapper).toResponse(imagemList.getLast());
            verifyNoMoreInteractions(veiculoRepository, imagemMapper, imagensService);

        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoNaoEncontrarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.vincularImagens(ID_VALIDO, cx.imagemFile));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_VALIDO);

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(imagensService, never()).criar(any(), any());
            verify(imagemMapper, never()).toResponse(any());
            verifyNoMoreInteractions(veiculoRepository, imagemMapper, imagensService);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        @DisplayName("Deve lançar exceção ao vincular imagem para veículo com status proibido")
        void deveLancarExcecaoAoVincularImagemEmVeiculoComStatusProibido(StatusVeiculo statusVeiculo) throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(ID_VALIDO)
                    .comStatus(statusVeiculo)
                    .build();
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(veiculo));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularImagens(ID_VALIDO, cx.imagemFile));
            //Assert
            assertBusinessResponseError(exception, "Somente anúncios disponíveis ou pausados podem ser editados.");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(imagensService, never()).criar(any(), any());
            verify(imagemMapper, never()).toResponse(any());
            verifyNoMoreInteractions(veiculoRepository, imagemMapper, imagensService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao vincular imagem invalida")
        void deveLancarExcecaoAoVincularImageInvalida() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(imagensService.criar(cx.imagemFile, cx.entity))
                    .thenThrow(new IOException("Erro ao vincular imagem"));

            //ACT
            var exception = assertThrows(IOException.class,
                    () -> veiculoService.vincularImagens(ID_VALIDO, cx.imagemFile));
            //Assert

            assertThat(exception)
                    .hasMessage("Erro ao vincular imagem");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(imagensService).criar(cx.imagemFile, cx.entity);
            verify(imagemMapper, never()).toResponse(any());
            verifyNoMoreInteractions(veiculoRepository, imagemMapper, imagensService);
        }

    }
}
