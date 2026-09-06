package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.response.ImagemDownload;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.service.ImagensService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.javacar.lojadecarro.enums.Entidade.IMAGEM;
import static com.javacar.lojadecarro.factory.helper.ImagemHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImagensController.class)
@DisplayName("Testes da controller de imagem")
class ImagensControllerTest extends BaseControllerTest {
    private static final String URL = "/imagens";
    private static final String URL_ID = URL + "/" + ID_VALIDO;
    private static final String URL_DOWNLOAD = URL_ID + "/conteudo";

    @MockitoBean
    private ImagensService imagensService;

    @Nested
    @DisplayName("Testes para definir imagem principal")
    class DefinirPrincipal {
        @Test
        @DisplayName("Deve definir a imagem como principal")
        void deveDefinirImagemPrincipal() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_ID + "/principal", ID_JWT, ROLE_ADM);
            assertStatus204(resultado);

            verify(imagensService).definirPrincipal(ID_VALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao definir a imagem como principal")
        void deveLancar404AoDefinirImagemPrincipal() throws Exception {
            //Arrange
            doThrow(new NotFoundException(IMAGEM, ID_VALIDO))
                    .when(imagensService).definirPrincipal(ID_VALIDO);
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_ID + "/principal", ID_JWT, ROLE_ADM);
            assertStatus404(resultado, IMAGEM, ID_VALIDO);

            verify(imagensService).definirPrincipal(ID_VALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 500 ao definir a imagem como principal")
        void deveLancar500AoDefinirImagemPrincipal() throws Exception {
            //Arrange
            doThrow(new RuntimeException("Erro inesperado"))
                    .when(imagensService).definirPrincipal(ID_INVALIDO);
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL + "/" + ID_INVALIDO + "/principal", ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(imagensService).definirPrincipal(ID_INVALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao definir imagem principal com ID inválido")
        void deveLancar400AoInserirStringNoId() throws Exception {
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL + "/" + "A" + "/principal", ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 401 ao definir imagem principal com ID inválido")
        void deveLancar401AoInserirStringNoId() throws Exception {
            //Act + Assert
            var resultado = performPatch(URL_ID + "/principal");
            assertStatus401(resultado);

            verifyNoInteractions(imagensService);
        }
    }

    @Nested
    @DisplayName("Testes para deletar a imagem")
    class Deletar {
        @Test
        @DisplayName("Deve deletar a imagem")
        void deveDeletarImagem() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performDeleteComAutenticacao(URL_ID,  ID_JWT, ROLE_ADM);
            assertStatus204(resultado);

            verify(imagensService).delete(ID_VALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao deletar a imagem")
        void deveLancar404AoDeletarImagem() throws Exception {
            //Arrange
            doThrow(new NotFoundException(IMAGEM, ID_INVALIDO))
                    .when(imagensService)
                    .delete(ID_INVALIDO);
            //Act + Assert
            var resultado = performDeleteComAutenticacao(URL + "/" + ID_INVALIDO,  ID_JWT, ROLE_ADM);
            assertStatus404(resultado, IMAGEM, ID_INVALIDO);

            verify(imagensService).delete(ID_INVALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 500 ao deletar imagem")
        void deveLancar500AoDeletarImagem() throws Exception {
            //Arrange
            doThrow(new RuntimeException("Erro inesperado"))
                    .when(imagensService).delete(ID_VALIDO);
            //Act + Assert
            var resultado = performDeleteComAutenticacao(URL_ID,  ID_JWT, ROLE_ADM);
            assertStatus500(resultado);

            verify(imagensService).delete(ID_VALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao deletar imagem com ID inválido")
        void deveLancar400AoInserirStringNoId() throws Exception {
            //Act + Assert
            var resultado = performDeleteComAutenticacao(URL + "/" + "A", ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 401 ao deletar imagem com ID inválido")
        void deveLancar401AoInserirStringNoId() throws Exception {
            //Act + Assert
            var resultado = performDelete(URL_ID);
            assertStatus401(resultado);

            verifyNoInteractions(imagensService);
        }
    }

    @Nested
    @DisplayName("Testes do download da imagem")
    class Download {
        @Test
        @DisplayName("Deve baixar o conteúdo da imagem")
        void deveBaixarConteudoDaImagem() throws Exception {
            // Arrange
            var conteudo =
                    "bytes da imagem".getBytes(StandardCharsets.UTF_8);

            var download = new ImagemDownload(
                    conteudo,
                    MediaType.IMAGE_JPEG_VALUE,
                    "foto.jpg"
            );

            when(imagensService.download(ID_VALIDO))
                    .thenReturn(download);

            // Act + Assert
            var resultado = performGet(URL_DOWNLOAD);
            resultado
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                    .andExpect(content().bytes(conteudo))
                    .andExpect(header().longValue(
                            HttpHeaders.CONTENT_LENGTH,
                            conteudo.length
                    ))
                    .andExpect(header().string(
                            HttpHeaders.CONTENT_DISPOSITION,
                            containsString("inline")
                    ))
                    .andExpect(header().string(
                            HttpHeaders.CONTENT_DISPOSITION,
                            containsString("foto.jpg")
                    ));

            verify(imagensService).download(ID_VALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve retornar 404 quando a imagem não for encontrada")
        void deveRetornar404QuandoImagemNaoForEncontrada() throws Exception {
            //Arrange
            when(imagensService.download(ID_INVALIDO))
                    .thenThrow(new NotFoundException(IMAGEM, ID_INVALIDO));
            //Act + Assert
            var exception = performGet(URL + "/" + ID_INVALIDO + "/conteudo");
            assertStatus404(exception, IMAGEM, ID_INVALIDO);

            verify(imagensService).download(ID_INVALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve retornar 500 quando ocorrer falha ao baixar a imagem")
        void deveRetornar500QuandoOcorrerFalhaAoBaixarImagem() throws Exception {
            //Arrange
            when(imagensService.download(ID_VALIDO))
                    .thenThrow(new IOException("Erro ao baixar a imagem"));
            //Act + Assert
            var exception = performGet(URL_DOWNLOAD);
            assertStatus500(exception);

            verify(imagensService).download(ID_VALIDO);
            verifyNoMoreInteractions(imagensService);
        }
    }
}
