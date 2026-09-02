package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.VeiculoController;
import com.javacar.lojadecarro.dto.request.VeiculoOpcionaisRequest;
import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.VeiculoTestContext;
import com.javacar.lojadecarro.factory.veiculo.VeiculoResponseFactory;
import com.javacar.lojadecarro.service.VeiculoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.CARROCERIA;
import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.*;
import static com.javacar.lojadecarro.enums.StatusVeiculo.VENDIDO;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.*;
import static com.javacar.lojadecarro.factory.helper.ImagemHelper.assertImagem;
import static com.javacar.lojadecarro.factory.helper.ImagemHelper.imagem;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.assertVeiculo;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.assertVeiculoList;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VeiculoController.class)
@DisplayName("Testes da controller do veiculo")
public class VeiculoControllerTest extends BaseControllerTest {
    private static final String URL = "/veiculos";
    private static final String URL_MEUS_ANUNCIOS = URL + "/meus-anuncios";
    private static final String URL_ID = URL + "/" + ID_VALIDO;
    private static final String URL_PAUSAR = URL_ID + "/pausar";
    private static final String URL_REATIVAR = URL_ID + "/reativar";
    private static final String URL_IMAGEM = URL_ID + "/imagens";
    private static final String URL_OPCIONAL = URL_ID + "/opcionais";
    private final PageRequest pageable =
            PageRequest.of(0, 9);

    @MockitoBean
    private VeiculoService veiculoService;

    @Nested
    @DisplayName("Testes do cadastro do veiculo")
    class Criar {
        @Test
        @DisplayName("Deve cadastrar um veiculo")
        void deveCadastrarVeiculo() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoService.criar(
                    any(VeiculoRequest.class),
                    any(MultipartFile[].class),
                    eq(ID_VALIDO)
            )).thenReturn(cx.response);

            //Act + Assert
            var resultado = performPostComAutenticacao(
                    URL,
                    cx.request,
                    ID_JWT,
                    ROLE_USUARIO,
                    imagem("foto1.jpg"),
                    imagem("foto2.jpg")
            );
            assertVeiculo(resultado,
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

            verify(veiculoService).criar(
                    eq(cx.request),
                    captor.capture(),
                    eq(ID_VALIDO)
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
        @DisplayName("Deve retornar 404 ao não encontrar um relacionamento")
        void deveRetornar404AoNaoEncontrarRelacionamento() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoService.criar(
                    any(VeiculoRequest.class),
                    any(MultipartFile[].class),
                    eq(ID_VALIDO)
            )).thenThrow(new NotFoundException(CARROCERIA, ID_INVALIDO));
            //Act + Assert
            var exception = performPostComAutenticacao(
                    URL,
                    cx.request,
                    ID_JWT,
                    ROLE_USUARIO,
                    imagem("foto1.jpg"),
                    imagem("foto2.jpg")
            );

            assertStatus404(exception, CARROCERIA, ID_INVALIDO);
        }
        @Test
        @DisplayName("Deve retornar 400 ao cadastrar veiculo")
        void deveRetornar400aoCadastroVeiculo() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var resultado = performPostComAutenticacao(
                    URL,
                    cx.requestIncompleto,
                    ID_JWT,
                    ROLE_USUARIO,
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
                    nullable(MultipartFile[].class),
                    eq(ID_VALIDO)
            )).thenReturn(cx.response);

            // Act
            var resultado = performPostComAutenticacao(URL, cx.request, ID_JWT, ROLE_USUARIO, new MockMultipartFile[0]);

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

            verify(veiculoService).criar(eq(cx.request), captor.capture(), eq(ID_VALIDO));

            assertThat(captor.getValue()).isEmpty();

            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao cadastrar veiculo")
        void deveRetornar500aoCadastroVeiculo() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            when(veiculoService.criar(any(VeiculoRequest.class),
                    any(MultipartFile[].class),
                    eq(ID_VALIDO))
            )
                    .thenThrow(new RuntimeException("Erro inesperado"));
            //Act + Assert
            var resultado = performPostComAutenticacao(
                    URL,
                    cx.request,
                    ID_JWT,
                    ROLE_USUARIO,
                    imagem("foto1.jpg"),
                    imagem("foto2.jpg")
            );

            assertStatus500(resultado);
            verify(veiculoService).criar(any(VeiculoRequest.class),
                    any(MultipartFile[].class),
                    eq(ID_VALIDO));
            verifyNoMoreInteractions(veiculoService);
        }
        @Test
        @DisplayName("Deve retornar 401 ao cadastrar um veiculo")
        void deveRetornar401AoCadastrarUmVeiculo() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoService.criar(
                    any(VeiculoRequest.class),
                    any(MultipartFile[].class),
                    eq(ID_VALIDO)
            )).thenReturn(cx.response);

            //Act + Assert
            var resultado = performPost(
                    URL,
                    cx.request,
                    imagem("foto1.jpg"),
                    imagem("foto2.jpg")
            );
            assertStatus401(resultado);
            verifyNoMoreInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes da listagem dos veiculos")
    class Listar {
        @Test
        @DisplayName("Deve listar os veiculos ativos")
        void deveListarOsVeiculosAtivos() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            Page<VeiculoResponse> page =
                    new PageImpl<>(List.of(cx.veiculoResponse1, cx.veiculoResponse2));

            when(veiculoService.listarAtivos(any(Pageable.class)))
                    .thenReturn(page);
            //Act + Assert
            var resultado = performGet(URL);
            assertVeiculoList(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    2L,
                    "QUV1F83",
                    "QUG3S35",
                    "Chevrolet",
                    "Ford",
                    "Onix",
                    "Mustang",
                    new BigDecimal("58000"),
                    new BigDecimal("80000"),
                    67000D,
                    67000D,
                    (short) 2020,
                    (short) 2020,
                    DISPONIVEL,
                    DISPONIVEL
            );

            verify(veiculoService).listarAtivos(any(Pageable.class));
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
                    new BigDecimal("58000"),
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
        @DisplayName("Deve retornar 400 ao buscar veiculo com ID invalido")
        void deveRetornar400AoBuscarVeiculoPorIDInvalido() throws Exception {
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
            var resultado = performPutComAutenticacao(URL_ID, cx.request, ID_JWT, ROLE_ADM);
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
        @DisplayName("Deve retornar 401 ao atualizar o veiculo")
        void deveRetornar401AoAtualizarVeiculo() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            when(veiculoService.atualizar(cx.request, ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPut(URL_ID, cx.request);
            assertStatus401(resultado);

            verifyNoInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 404 ao atualizar veiculo com ID incorreto")
        void deveLancar404AoAtualizarVeiculoIncorreto() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            when(veiculoService.atualizar(cx.request, ID_VALIDO))
                    .thenThrow(new NotFoundException(VEICULO, ID_VALIDO));
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ID, cx.request, ID_JWT, ROLE_ADM);
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
            var resultado = performPutComAutenticacao(URL_ID, cx.requestIncompleto, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao atualizar veiculo com ID invalido")
        void deveLancar400AoAtualizarVeiculoValoresInvalido() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var resultado = performPutComAutenticacao(URL + "/a", cx.request, ID_JWT, ROLE_ADM);
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes da pausa do veículo")
    class Pausar {
        @Test
        @DisplayName("Deve pausar um veículo")
        void devePausarUmVeiculo() throws Exception {
            //Arrange
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comStatus(PAUSADO)
                    .build();
            when(veiculoService.pausarVeiculo(ID_VALIDO))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_PAUSAR, ID_JWT, ROLE_ADM);
            resultado
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusVeiculo").value(PAUSADO.toString()));

            verify(veiculoService).pausarVeiculo(ID_VALIDO);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao pausar um veículo")
        void deveRetornar404AoPausarVeiculo() throws Exception {
            //Arrange

            when(veiculoService.pausarVeiculo(ID_VALIDO))
                    .thenThrow(new  NotFoundException(VEICULO, ID_VALIDO));
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_PAUSAR, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, VEICULO, ID_VALIDO);

            verify(veiculoService).pausarVeiculo(ID_VALIDO);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao pausar um veículo")
        void deveRetornar401AoPausarVeiculo() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performPatch(URL_PAUSAR);
            assertStatus401(resultado);

            verifyNoInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes da reativação do veículo")
    class Reativar {
        @Test
        @DisplayName("Deve reativar um veículo")
        void deveReativarUmVeiculo() throws Exception {
            //Arrange
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comStatus(DISPONIVEL)
                    .build();
            when(veiculoService.reativarVeiculo(ID_VALIDO))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_REATIVAR, ID_JWT, ROLE_ADM);
            resultado
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusVeiculo").value(DISPONIVEL.toString()));

            verify(veiculoService).reativarVeiculo(ID_VALIDO);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao reativar um veículo")
        void deveRetornar404AoReativarVeiculo() throws Exception {
            //Arrange
            when(veiculoService.reativarVeiculo(ID_VALIDO))
                    .thenThrow(new NotFoundException(VEICULO, ID_VALIDO));
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_REATIVAR, ID_JWT, ROLE_ADM);
            assertStatus404(resultado, VEICULO, ID_VALIDO);

            verify(veiculoService).reativarVeiculo(ID_VALIDO);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao reativar um veículo")
        void deveRetornar401AoReativarVeiculo() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performPatch(URL_REATIVAR);
            assertStatus401(resultado);

            verifyNoInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes da vinculação de imagens ao veículo")
    class VincularImagem {
        @Test
        @DisplayName("Deve vincular as imagens")
        void deveVincularAsImagens() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            when(veiculoService.vincularImagens(eq(ID_VALIDO), any(MultipartFile[].class)))
                    .thenReturn(cx.imagemResponseList);
            //Act + Assert
            var resultado = performPostComAutenticacao(URL_IMAGEM, null, ID_JWT, ROLE_ADM,
                    imagem("foto1.jpg"),
                    imagem("foto2.jpg"));

            resultado
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[0].nomeOriginal").value("nomeImagemOriginal"))
                    .andExpect(jsonPath("$[1].nomeOriginal").value("nomeImagemOriginal"))
                    .andExpect(jsonPath("$[0].objectKey").value("imagens/2026/foto.jpg"))
                    .andExpect(jsonPath("$[1].objectKey").value("imagens/2026/foto.jpg"));

            var captor = ArgumentCaptor.forClass(MultipartFile[].class);

            verify(veiculoService).vincularImagens(
                    eq(ID_VALIDO),
                    captor.capture()
            );

            assertThat(captor.getValue())
                    .hasSize(2);

            assertThat(captor.getValue()[0].getOriginalFilename())
                    .isEqualTo("foto1.jpg");
            assertThat(captor.getValue()[1].getOriginalFilename())
                    .isEqualTo("foto2.jpg");
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao vincular as imagens")
        void deveRetornar404AoVincularAsImagens() throws Exception {
            //Arrange
            when(veiculoService.vincularImagens(eq(ID_VALIDO), any(MultipartFile[].class)))
                    .thenThrow(new  NotFoundException(VEICULO, ID_VALIDO));
            //Act + Assert
            var exception = performPostComAutenticacao(URL_IMAGEM, null, ID_JWT, ROLE_ADM, imagem("foto1.jpg"));

            assertStatus404(exception, VEICULO, ID_VALIDO);

            verify(veiculoService).vincularImagens(eq(ID_VALIDO), any(MultipartFile[].class));
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao vincular as imagens")
        void deveRetornar401AoVincularAsImagens() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performPost(URL_IMAGEM, null, imagem("foto1.jpg"));
            assertStatus401(exception);
            verifyNoInteractions(veiculoService);
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
                    false
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
            var resultado = performDeleteComAutenticacao(URL_OPCIONAL,
                    "idsOpcionais",
                    cx.idsOpcionais,
                    ID_JWT,
                    ROLE_USUARIO);
            assertStatus204(resultado);

            verify(veiculoService).desvincularOpcionais(ID_VALIDO, cx.idsOpcionais);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao desvincular os opcionais")
        void deveLancar400DesvincularAsOpcionais() throws Exception {
            //Arrange
            //Act + Assert
            var resultado = performDeleteComAutenticacao(URL_OPCIONAL, "idsOpcionais", List.of(), ID_JWT, ROLE_USUARIO);
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao desvincular os opcionais com ID invalido")
        void deveLancar400DesvincularAsOpcionaisComIdInvalido() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var resultado = performDeleteComAutenticacao(URL + "/A/opcionais", "idsOpcionais", cx.idsOpcionais, ID_JWT, ROLE_USUARIO);
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
            var resultado = performDeleteComAutenticacao(URL_OPCIONAL, "idsOpcionais", cx.idsOpcionais, ID_JWT, ROLE_USUARIO);
            assertStatus404(resultado, VEICULO, ID_VALIDO);

            verify(veiculoService).desvincularOpcionais(ID_VALIDO, cx.idsOpcionais);
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao desvincular os opcionais")
        void deveRetornar401AoDesvincularOpcionais() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var exception = performDelete(URL_OPCIONAL, "idsOpcionais", cx.idsOpcionais);
            assertStatus401(exception);
            verifyNoInteractions(veiculoService);
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
            var resultado = performPostComAutenticacao(URL_OPCIONAL, cx.veiculoOpcionaisRequest, ID_JWT, ROLE_USUARIO);
            assertStatus204(resultado);

            verify(veiculoService).vincularOpcionais(ID_VALIDO, cx.veiculoOpcionaisRequest.opcionais());
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao vincular os opcionais")
        void deveLancar400DesvincularAsOpcionais() throws Exception {
            //Arrange
            var request = new VeiculoOpcionaisRequest(List.of());
            //Act + Assert
            var resultado = performPostComAutenticacao(URL_OPCIONAL, request, ID_JWT, ROLE_USUARIO);
            assertStatus400(resultado);

            verifyNoInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve lançar 400 ao vincular as opcionais com ID invalido")
        void deveLancar400VincularAsOpcionaisComIdInvalido() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var resultado = performPostComAutenticacao(URL + "/A/opcionais", cx.veiculoOpcionaisRequest, ID_JWT, ROLE_USUARIO);
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
            var resultado = performPostComAutenticacao(URL_OPCIONAL, cx.veiculoOpcionaisRequest, ID_JWT, ROLE_USUARIO);
            assertStatus404(resultado, VEICULO, ID_VALIDO);

            verify(veiculoService).vincularOpcionais(ID_VALIDO, cx.veiculoOpcionaisRequest.opcionais());
            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao vincular os opcionais")
        void deveRetornar401AoVincularOpcionais() throws Exception {
            //Arrange
            var cx = new VeiculoTestContext();
            //Act + Assert
            var exception = performPost(URL_OPCIONAL, cx.veiculoOpcionaisRequest);
            assertStatus401(exception);
            verifyNoInteractions(veiculoService);
        }
    }

    @Nested
    @DisplayName("Testes da listagem dos veículos do usuário")
    class ListarMeusAnuncios {
        @Test
        @DisplayName("Deve listar os anúncios do usuário autenticado")
        void deveListarMeusAnuncios() throws Exception {
            // Arrange
            var cx = new VeiculoTestContext();

            var page = new PageImpl<>(
                    List.of(cx.veiculoResponse1, cx.veiculoResponse2),
                    PageRequest.of(0, 9),
                    2
            );

            when(veiculoService.listarMeusAnuncios(
                    any(Pageable.class),
                    eq(ID_VALIDO),
                    isNull()
            )).thenReturn(page);

            // Act
            var resultado = performGetComAutenticacao(
                    URL_MEUS_ANUNCIOS,
                    ID_JWT,
                    ROLE_USUARIO
            );

            // Assert
            resultado
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.totalElements").value(2));

            var captor = ArgumentCaptor.forClass(Pageable.class);

            verify(veiculoService).listarMeusAnuncios(
                    captor.capture(),
                    eq(ID_VALIDO),
                    isNull()
            );

            assertThat(captor.getValue())
                    .extracting(
                            Pageable::getPageNumber,
                            Pageable::getPageSize
                    )
                    .containsExactly(0, 9);

            verifyNoMoreInteractions(veiculoService);
        }

        @ParameterizedTest
        @EnumSource(StatusVeiculo.class)
        @DisplayName("Deve listar meus anúncios por status")
        void deveListarMeusAnunciosPorStatus(
                StatusVeiculo statusVeiculo
        ) throws Exception {
            // Arrange
            var page = veiculosResponseList(statusVeiculo);

            when(veiculoService.listarMeusAnuncios(
                    any(Pageable.class),
                    eq(ID_VALIDO),
                    eq(statusVeiculo)
            )).thenReturn(page);

            // Act
            var resultado = performGetComAutenticacao(
                    URL_MEUS_ANUNCIOS,
                    "status",
                    statusVeiculo.name(),
                    ID_JWT,
                    ROLE_USUARIO
            );

            // Assert
            resultado
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(
                            "$.content[*].statusVeiculo",
                            everyItem(is(statusVeiculo.name()))
                    ));

            verify(veiculoService).listarMeusAnuncios(
                    any(Pageable.class),
                    eq(ID_VALIDO),
                    eq(statusVeiculo)
            );

            verifyNoMoreInteractions(veiculoService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao listar meus anúncios")
        void deveRetornar401AoListarMeusAnuncios() throws Exception {
            var resultado = performGet(URL_MEUS_ANUNCIOS);

            assertStatus401(resultado);
            verifyNoInteractions(veiculoService);
        }
        @Test
        @DisplayName("Deve retornar 400 ao informar status inválido")
        void deveRetornar400AoInformarStatusInvalido() throws Exception {
            var resultado = performGetComAutenticacao(
                    URL_MEUS_ANUNCIOS,
                    "status",
                    "ALUGADO",
                    ID_JWT,
                    ROLE_USUARIO
            );

            assertStatus400(resultado);
            verifyNoInteractions(veiculoService);
        }
    }


    private Page<VeiculoResponse> veiculosResponseList(StatusVeiculo status) {
        var response1 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comStatus(status == null ? DISPONIVEL : status)
                .build();

        var response2 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(2L)
                .comStatus(status == null ? PAUSADO : status)
                .build();

        var response3 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(3L)
                .comStatus(status == null ? RESERVADO : status)
                .build();

        var response4 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(4L)
                .comStatus(status == null ? VENDIDO : status)
                .build();

        return new PageImpl<>(
                List.of(response1, response2, response3, response4),
                pageable,
                4);
    }

}
