package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.dto.request.AlterarStatusRequest;
import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.VeiculoTestContext;
import com.javacar.lojadecarro.service.VeiculoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.enums.StatusVeiculo.PAUSADO;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.factory.helper.ImagemHelper.assertImagem;
import static com.javacar.lojadecarro.factory.helper.ImagemHelper.imagem;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.assertVeiculo;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.assertVeiculoList;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VeiculoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller do veiculo")
public class VeiculoControllerTest extends BaseControllerTest {
    private static final String URL = "/veiculo";
    private static final String URL_ID = URL + "/" + ID_VALIDO;
    private static final String URL_IMAGEM = URL_ID + "/imagens";
    private static final String URL_OPCIONAL = URL_ID + "/opcionais";

    @MockitoBean
    private VeiculoService veiculoService;

    @Nested
    @DisplayName("Testes do cadastro do veiculo")
    class Criar {

        @Test
        @DisplayName("Deve cadastrar uma veiculo")
        void deveCadastrarVeiculo() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoService.criar(
                    any(VeiculoRequest.class),
                    any(MultipartFile[].class)
            )).thenReturn(cx.response);

            //Act + Assert
            var resultado = performPost(
                    URL,
                    cx.request,
                    imagem("foto1.jpg"),
                    imagem("foto2.jpg")
            );
            assertVeiculo(resultado,
                    status().isCreated(),
                    ID_VALIDO,
                    "QUV1F83",
                    "Chevrolet",
                    "Onix",
                    new BigDecimal(58000),
                    67000D,
                    (short) 2020,
                    DISPONIVEL
            );

            ArgumentCaptor<MultipartFile[]> captor =
                    ArgumentCaptor.forClass(MultipartFile[].class);

            verify(veiculoService).criar(
                    eq(cx.request),
                    captor.capture()
            );

            MultipartFile[] arquivos = captor.getValue();

            assertThat(arquivos)
                    .hasSize(2);

            assertThat(arquivos[0].getOriginalFilename())
                    .isEqualTo("foto1.jpg");

            assertThat(arquivos[1].getOriginalFilename())
                    .isEqualTo("foto2.jpg");
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao cadastrar veiculo")
        void deveLancar400aoCadastroVeiculo() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var resultado = performPost(
                    URL,
                    cx.requestIncompleto,
                    imagem("foto1.jpg"),
                    imagem("foto2.jpg")
            );

            assertStatus400(resultado);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve cadastrar um veículo sem imagens")
        void deveCadastrarVeiculoSemImagens() throws Exception {
            // Arrange
            var cx = new VeiculoTestContext();

            when(veiculoService.criar(
                    any(VeiculoRequest.class),
                    nullable(MultipartFile[].class)
            )).thenReturn(cx.response);

            // Act
            var resultado = performPost(URL, cx.request, new MockMultipartFile[0]);

            // Assert
            assertVeiculo(
                    resultado,
                    status().isCreated(),
                    ID_VALIDO,
                    "QUV1F83",
                    "Chevrolet",
                    "Onix",
                    new BigDecimal("58000"),
                    67000D,
                    (short) 2020,
                    DISPONIVEL
            );

            ArgumentCaptor<MultipartFile[]> captor =
                    ArgumentCaptor.forClass(MultipartFile[].class);

            verify(veiculoService).criar(eq(cx.request), captor.capture());

            assertThat(captor.getValue()).isEmpty();

            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 500 ao cadastrar veiculo")
        void deveLancar500aoCadastroVeiculo() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            when(veiculoService.criar(any(VeiculoRequest.class),
                    any(MultipartFile[].class))
            )
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPost(
                    URL,
                    cx.request,
                    imagem("foto1.jpg"),
                    imagem("foto2.jpg")
            );

            assertStatus500(resultado);
            verify(veiculoService).criar(any(VeiculoRequest.class),
                    any(MultipartFile[].class));
            verifyNoMoreInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes da listagem dos veiculos")
    class Listar {
        @Test
        @DisplayName("Deve listar os veiculos sem filtro")
        void deveListarOsVeiculosSemFiltro() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            Page<VeiculoResponse> page =
                    new PageImpl<>(List.of(cx.veiculoResponse1, cx.veiculoResponse2));

            when(veiculoService.listar(any(Pageable.class), isNull()))
                    .thenReturn(page);
            //Act + Assert
            var resultado = performGet(URL);
            assertVeiculoList(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    2L,
                    "QUV1F83",
                    "QUV1F83",
                    "Chevrolet",
                    "Chevrolet",
                    "Onix",
                    "Onix",
                    new BigDecimal(58000),
                    new BigDecimal(58000),
                    67000D,
                    67000D,
                    (short) 2020,
                    (short) 2020,
                    PAUSADO,
                    PAUSADO
            );

            verify(veiculoService).listar(any(Pageable.class), isNull());
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve listar os veiculos com filtro")
        void deveListarOsVeiculosComFiltro() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            Page<VeiculoResponse> page =
                    new PageImpl<>(List.of(cx.veiculoResponse1, cx.veiculoResponse2));

            when(veiculoService.listar(any(Pageable.class), eq(PAUSADO)))
                    .thenReturn(page);
            //Act + Assert

            var resultado = performGet(URL, "status", PAUSADO.toString());
            assertVeiculoList(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    2L,
                    "QUV1F83",
                    "QUV1F83",
                    "Chevrolet",
                    "Chevrolet",
                    "Onix",
                    "Onix",
                    new BigDecimal(58000),
                    new BigDecimal(58000),
                    67000D,
                    67000D,
                    (short) 2020,
                    (short) 2020,
                    PAUSADO,
                    PAUSADO
            );

            verify(veiculoService).listar(any(Pageable.class), eq(PAUSADO));
            verifyNoMoreInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes da busca do veiculo por ID")
    class Buscar {
        @Test
        @DisplayName("Deve buscar o veiculo por ID")
        void deveBuscarVeiculoPorID() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoService.buscarPorId(ID_VALIDO))
                    .thenReturn(cx.response);

            //Act + Assert
            var resultado = performGet(URL_ID);
            assertVeiculo(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "QUV1F83",
                    "Chevrolet",
                    "Onix",
                    new BigDecimal(58000),
                    67000D,
                    (short) 2020,
                    DISPONIVEL
            );

            verify(veiculoService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao buscar veiculo")
        void deveLancar404AoBuscarVeiculoPorID() throws Exception {
            //Arrange
            when(veiculoService.buscarPorId(ID_VALIDO))
                    .thenThrow(new NotFoundException(VEICULO, ID_VALIDO));

            //Act + Assert
            var resultado = performGet(URL_ID);
            assertStatus404(resultado, VEICULO, ID_VALIDO);

            verify(veiculoService).buscarPorId(ID_VALIDO);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao buscar veiculo com ID invalido")
        void deveLancar4040AoBuscarVeiculoPorIDInvalido() throws Exception {
            //Act + Assert
            var resultado = performGet(URL + "/A");
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes de atualização do veiculo")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o veiculo")
        void deveAtualizarVeiculo() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            when(veiculoService.atualizar(cx.request, ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPut(URL_ID, cx.request);
            assertVeiculo(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "QUV1F83",
                    "Chevrolet",
                    "Onix",
                    new BigDecimal("58000"),
                    67000D,
                    (short) 2020,
                    DISPONIVEL
            );
            verify(veiculoService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao atualizar veiculo com ID incorreto")
        void deveLancar404AoAtualizarVeiculoIncorreto() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            when(veiculoService.atualizar(cx.request, ID_VALIDO))
                    .thenThrow(new NotFoundException(VEICULO, ID_VALIDO));
            //Act + Assert
            var resultado = performPut(URL_ID, cx.request);
            assertStatus404(resultado, VEICULO, ID_VALIDO);

            verify(veiculoService).atualizar(cx.request, ID_VALIDO);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao atualizar veiculo com valores incorretos")
        void deveLancar400AoAtualizarVeiculoValoresIncorretos() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var resultado = performPut(URL_ID, cx.requestIncompleto);
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao atualizar veiculo com ID invalido")
        void deveLancar400AoAtualizarVeiculoValoresInvalido() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var resultado = performPut(URL + "/A", cx.request);
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status")
        void deveAlterarStatus() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            var status = new AlterarStatusRequest(PAUSADO);

            when(veiculoService.alterarStatus(ID_VALIDO, status))
                    .thenReturn(cx.response);
            //ACT + assert
            var resultado = performPatch(URL_ID + "/status", status);
            assertVeiculo(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "QUV1F83",
                    "Chevrolet",
                    "Onix",
                    new BigDecimal("58000"),
                    67000D,
                    (short) 2020,
                    DISPONIVEL
            );

            verify(veiculoService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao alterar status")
        void deveLancar400aoAlterarStatus() throws Exception {
            //Arrange
            var status = new AlterarStatusRequest(null);
            //ACT + assert
            var resultado = performPatch(URL_ID + "/status", status);

            assertStatus400(resultado);
            verifyNoInteractions(veiculoService);

        }

        @Test
        @DisplayName("Deve lançar 404 ao alterar status")
        void deveLancar404aoAlterarStatus() throws Exception {
            //Arrange
            var status = new AlterarStatusRequest(PAUSADO);

            when(veiculoService.alterarStatus(ID_VALIDO, status))
                    .thenThrow(new NotFoundException(VEICULO, ID_VALIDO));
            //ACT + assert
            var resultado = performPatch(URL_ID + "/status", status);
            assertStatus404(resultado,
                    VEICULO,
                    ID_VALIDO);

            verify(veiculoService).alterarStatus(ID_VALIDO, status);
            verifyNoMoreInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes para listar imagens do veiculo")
    class ListarImagens {
        @Test
        @DisplayName("Deve listar as imagens do veiculo")
        void deveListarImagensVeiculo() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            when(veiculoService.listarImagens(ID_VALIDO))
                    .thenReturn(cx.imagemResponseList);
            //Act + Assert
            var resultado = performGet(URL_IMAGEM);
            assertImagem(resultado,
                    ID_VALIDO,
                    2L,
                    "nomeImagemOriginal",
                    "nomeImagemOriginal",
                    "imagens/2026/foto.jpg",
                    "imagens/2026/foto.jpg",
                    true,
                    true
            );

            verify(veiculoService).listarImagens(ID_VALIDO);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao listar as imagens do veiculo")
        void deveLancar404AoListarImagensVeiculo() throws Exception {
            //Arrange
            when(veiculoService.listarImagens(ID_VALIDO))
                    .thenThrow(new NotFoundException(VEICULO, ID_VALIDO));
            //Act + Assert
            var resultado = performGet(URL_IMAGEM);
            assertStatus404(resultado, VEICULO, ID_VALIDO);

            verify(veiculoService).listarImagens(ID_VALIDO);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao inserir ID invalido")
        void deveLancar400AoInserirIdInvalido() throws Exception {
            //Arrange
            when(veiculoService.listarImagens(ID_VALIDO))
                    .thenThrow(new NotFoundException(VEICULO, ID_VALIDO));
            //Act + Assert
            var resultado = performGet(URL + "/A/imagens");
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes para desvincular os opcionais")
    class DesvincularOpcionais {
        @Test
        @DisplayName("Deve desvincular os opcionais")
        void deveDesvincularAsOpcionais() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();

            doNothing()
                    .when(veiculoService)
                    .desvincularOpcionais(ID_VALIDO, cx.idsOpcionais);
            //Act + Assert
            var resultado = performDelete(URL_OPCIONAL, "idsOpcionais", cx.idsOpcionais);
            assertStatus204(resultado);

            verify(veiculoService).desvincularOpcionais(ID_VALIDO, cx.idsOpcionais);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao desvincular os opcionais")
        void deveLancar400DesvincularAsOpcionais() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performDelete(URL_OPCIONAL, "idsOpcionais", List.of());
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao desvincular os opcionais com ID invalido")
        void deveLancar400DesvincularAsOpcionaisComIdInvalido() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var resultado = performDelete(URL + "/A/opcionais", "idsOpcionais", cx.idsOpcionais);
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao inserir veiculo incorreto")
        void deveLancar404AoInserirVeiculoIncorreto() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();

            doThrow(new NotFoundException(VEICULO, ID_VALIDO))
                    .when(veiculoService)
                    .desvincularOpcionais(ID_VALIDO, cx.idsOpcionais);
            //Act + Assert
            var resultado = performDelete(URL_OPCIONAL, "idsOpcionais", cx.idsOpcionais);
            assertStatus404(resultado, VEICULO, ID_VALIDO);

            verify(veiculoService).desvincularOpcionais(ID_VALIDO, cx.idsOpcionais);
            verifyNoMoreInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes para vincular os opcionais")
    class VincularOpcionais {
        @Test
        @DisplayName("Deve vincular os opcionais")
        void deveVincularOpcionais() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();

            doNothing()
                    .when(veiculoService)
                    .vincularOpcionais(ID_VALIDO, cx.veiculoOpcionaisRequest.opcionais());
            //Act + Assert
            var resultado = performPatch(URL_OPCIONAL, cx.veiculoOpcionaisRequest);
            assertStatus204(resultado);

            verify(veiculoService).vincularOpcionais(ID_VALIDO, cx.veiculoOpcionaisRequest.opcionais());
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao vincular os opcionais")
        void deveLancar400DesvincularAsOpcionais() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var resultado = performPatch(URL_OPCIONAL, cx.veiculoOpcionaisRequestIncompleto);
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao vincular as opcionais com ID invalido")
        void deveLancar400VincularAsOpcionaisComIdInvalido() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var resultado = performPatch(URL + "/A/opcionais", cx.veiculoOpcionaisRequest.opcionais());
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao inserir veiculo incorreto")
        void deveLancar404AoInserirVeiculoIncorreto() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();

            doThrow(new NotFoundException(VEICULO, ID_VALIDO))
                    .when(veiculoService)
                    .vincularOpcionais(ID_VALIDO, cx.veiculoOpcionaisRequest.opcionais());
            //Act + Assert
            var resultado = performPatch(URL_OPCIONAL, cx.veiculoOpcionaisRequest);
            assertStatus404(resultado, VEICULO, ID_VALIDO);

            verify(veiculoService).vincularOpcionais(ID_VALIDO, cx.veiculoOpcionaisRequest.opcionais());
            verifyNoMoreInteractions(veiculoService);
        }
    }
}
