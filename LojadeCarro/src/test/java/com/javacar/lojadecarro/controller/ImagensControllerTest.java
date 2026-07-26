package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.service.ImagensService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.javacar.lojadecarro.enums.Entidade.IMAGEM;
import static com.javacar.lojadecarro.factory.helper.ImagemHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;

@WebMvcTest(ImagensController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller de imagem")
class ImagensControllerTest extends BaseControllerTest {
    private static final String URL = "/imagens";
    private static final String URL_ID = URL + "/" + ID_VALIDO;

    @MockitoBean
    private ImagensService imagensService;

    @Nested
    @DisplayName("Testes para definir imagem principal")
    class DefinirPrincipal {
        @Test
        @DisplayName("Deve definir a imagem como principal")
        void deveDefinirImagemPrincipal() throws Exception {
            //Arrange
            doNothing()
                    .when(imagensService)
                    .definirPrincipal(ID_VALIDO);
            //Act + Assert
            var resultado = performPatch(URL_ID + "/principal");
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
            var resultado = performPatch(URL_ID + "/principal");
            assertStatus404(resultado, IMAGEM, ID_VALIDO);

            verify(imagensService).definirPrincipal(ID_VALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 500 ao definir a imagem como principal")
        void deveLancar500AoDefinirImagemPrincipal() throws Exception {
            //Arrange
            doThrow(new RuntimeException("Erro inesperado"))
                    .when(imagensService).definirPrincipal(ID_VALIDO);
            //Act + Assert
            var resultado = performPatch(URL_ID + "/principal");
            assertStatus500(resultado);

            verify(imagensService).definirPrincipal(ID_VALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao definir imagem principal com ID inválido")
        void deveLancar400AoInserirStringNoId() throws Exception {
            //Act + Assert
            var resultado = performPatch(URL + "/" + "A" + "/principal");
            assertStatus400(resultado);

            verifyNoInteractions(imagensService);
        }
    }
    @Nested
    @DisplayName("Testes para deletar a imagem")
    class Deletar{
        @Test
        @DisplayName("Deve deletar a imagem")
        void deveDeletarImagem() throws Exception {
            //Arrange
            doNothing()
            .when(imagensService)
                    .delete(ID_VALIDO);
            //Act + Assert
            var resultado = performDelete(URL_ID);
            assertStatus204(resultado);

            verify(imagensService).delete(ID_VALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao deletar a imagem")
        void deveLancar404AoDeletarImagem() throws Exception {
            //Arrange
            doThrow(new NotFoundException(IMAGEM, ID_VALIDO))
                    .when(imagensService)
                    .delete(ID_VALIDO);
            //Act + Assert
            var resultado = performDelete(URL_ID);
            assertStatus404(resultado, IMAGEM, ID_VALIDO);

            verify(imagensService).delete(ID_VALIDO);
            verifyNoMoreInteractions(imagensService);
        }
        @Test
        @DisplayName("Deve lançar 500 ao deletar imagem")
        void deveLancar500AoDeletarImagem() throws Exception {
            //Arrange
            doThrow(new RuntimeException("Erro inesperado"))
                    .when(imagensService).delete(ID_VALIDO);
            //Act + Assert
            var resultado = performDelete(URL_ID);
            assertStatus500(resultado);

            verify(imagensService).delete(ID_VALIDO);
            verifyNoMoreInteractions(imagensService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao deletar imagem com ID inválido")
        void deveLancar400AoInserirStringNoId() throws Exception {
            //Act + Assert
            var resultado = performDelete(URL + "/" + "A");
            assertStatus400(resultado);

            verifyNoInteractions(imagensService);
        }
    }


}
