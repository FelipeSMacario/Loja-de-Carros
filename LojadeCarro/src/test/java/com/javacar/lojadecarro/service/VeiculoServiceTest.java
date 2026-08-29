package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.ImagemResponse;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.entity.VeiculoOpcional;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaTestContext;
import com.javacar.lojadecarro.factory.combustivel.CombustivelTestContext;
import com.javacar.lojadecarro.factory.cor.CorTestContext;
import com.javacar.lojadecarro.factory.helper.OpcionalHelper;
import com.javacar.lojadecarro.factory.helper.VeiculoTestContext;
import com.javacar.lojadecarro.factory.imagem.ImagemEntityFactory;
import com.javacar.lojadecarro.factory.imagem.ImagemResponseFactory;
import com.javacar.lojadecarro.factory.marca.MarcaTestContext;
import com.javacar.lojadecarro.factory.modelo.ModeloTestContext;
import com.javacar.lojadecarro.factory.opcional.OpcionalEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoResponseFactory;
import com.javacar.lojadecarro.mapper.ImagemMapper;
import com.javacar.lojadecarro.mapper.VeiculoMapper;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.*;
import static com.javacar.lojadecarro.enums.StatusVeiculo.*;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Testes do serviço de veículos")
class VeiculoServiceTest extends BaseServiceTest {
    @Mock
    private VeiculoRepository veiculoRepository;
    @Mock
    private VeiculoMapper veiculoMapper;
    @Mock
    private ImagemMapper imagemMapper;
    @Mock
    private CarroceriaService carroceriaService;
    @Mock
    private CoresService coresService;
    @Mock
    private ModeloService modeloService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private CombustivelService combustivelService;
    @Mock
    private OpcionalService opcionalService;
    @Mock
    private ImagensService imagensService;
    @InjectMocks
    private VeiculoService veiculoService;

    private final PageRequest pageable = PageRequest.of(0, 10);

    @Nested
    @DisplayName("Testes da criação do veículo")
    class Criar {
        @Test
        @DisplayName("Deve cadastrar um veiculo")
        void deveCadastrarUmVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(cx.request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(cx.request.idModelo()))
                    .thenReturn(cx.modelo);

            when(combustivelService.buscaCombustivelAtivo(cx.request.idCombustivel()))
                    .thenReturn(cx.combustivel);

            when(opcionalService.buscarOpcionaisAtivos(cx.request.idsOpcionais()))
                    .thenReturn(cx.opcionais);

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(cx.response);

            when(imagensService.criar(cx.imagemFile, cx.entity))
                    .thenReturn(cx.imagens);

            when(veiculoRepository.save(cx.entity))
                    .thenReturn(cx.entity);

            //ACT
            var resultado = veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO);
            //Assert
            assertVeiculoResponse(resultado);
            assertThat(cx.entity.getStatusVeiculo())
                    .isEqualTo(DISPONIVEL);

            assertThat(cx.entity.getOpcionais())
                    .extracting(vo -> vo.getOpcional().getId())
                    .containsExactlyElementsOf(
                            cx.opcionais.stream()
                                    .map(Opcional::getId)
                                    .toList()
                    );

            assertThat(cx.entity.getImagens())
                    .containsExactlyElementsOf(cx.imagens);

            assertThat(cx.entity.getVendedor())
                    .isSameAs(cx.usuario);

            assertThat(cx.entity.getCarroceria())
                    .isSameAs(cx.carroceria);

            assertThat(cx.entity.getCor())
                    .isSameAs(cx.cor);

            assertThat(cx.entity.getModelo())
                    .isSameAs(cx.modelo);

            assertThat(cx.entity.getCombustivel())
                    .isSameAs(cx.combustivel);

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper).toEntity(cx.request);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService).buscaCorAtiva(cx.request.idCores());
            verify(modeloService).buscaModeloAtivo(cx.request.idModelo());
            verify(combustivelService).buscaCombustivelAtivo(cx.request.idCombustivel());
            verify(opcionalService).buscarOpcionaisAtivos(cx.request.idsOpcionais());
            verify(imagensService).criar(cx.imagemFile, cx.entity);
            verify(veiculoRepository).save(cx.entity);
            verify(veiculoMapper).toResponse(cx.entity);

            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção de usuário inativo ao criar veiculo")
        void deveLancarExcecaoUsuarioInativo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, USUARIO, ID_VALIDO);

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper, never()).toEntity(any());
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção quando a placa já estiver cadastrada")
        void deveLancarExcecaoPlacaJaEstiverCadastrado() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "A placa informada já possui um cadastro.");

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper, never()).toEntity(any());
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção de carroceria não encontrada ao criar veiculo")
        void deveLancarExcecaoCarroceriaNaoEncontradaAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);
            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);
            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenThrow(new NotFoundException(CARROCERIA, cx.request.idCarroceria()));

            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, CARROCERIA, cx.request.idCarroceria());

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper).toEntity(cx.request);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar cor não encontrada ao criar veiculo")
        void deveLancarExcecaoCorNaoEncontradaAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);
            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);
            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenReturn(cx.carroceria);
            when(coresService.buscaCorAtiva(cx.request.idCores()))
                    .thenThrow(new NotFoundException(COR, cx.request.idCores()));

            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, COR, cx.request.idCores());

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper).toEntity(cx.request);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService).buscaCorAtiva(cx.request.idCores());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar modelo não encontrado ao criar veiculo")
        void deveLancarExcecaoModeloNaoEncontradoAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(cx.request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(cx.request.idModelo()))
                    .thenThrow(new NotFoundException(MODELO, cx.request.idModelo()));
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, MODELO, cx.request.idModelo());

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper).toEntity(cx.request);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService).buscaCorAtiva(cx.request.idCores());
            verify(modeloService).buscaModeloAtivo(cx.request.idModelo());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractionsCriar();

        }


        @Test
        @DisplayName("Deve lançar exceção ao buscar combustível não encontrado ao criar veiculo")
        void deveLancarExcecaoCombustivelNaoEncontradoAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(cx.request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(cx.request.idModelo()))
                    .thenReturn(cx.modelo);

            when(combustivelService.buscaCombustivelAtivo(cx.request.idCombustivel()))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, cx.request.idCombustivel()));

            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, COMBUSTIVEL, cx.request.idCombustivel());

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper).toEntity(cx.request);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService).buscaCorAtiva(cx.request.idCores());
            verify(modeloService).buscaModeloAtivo(cx.request.idModelo());
            verify(combustivelService).buscaCombustivelAtivo(cx.request.idCombustivel());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção informar opcionais duplicados ao criar veiculo")
        void deveLancarExcecaoOpcionalDuplicadoAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            var idsRepetido = List.of(ID_VALIDO, 2L, ID_VALIDO);
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comOpcionais(idsRepetido)
                    .build();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(any()))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(request.idModelo()))
                    .thenReturn(cx.modelo);

            when(combustivelService.buscaCombustivelAtivo(request.idCombustivel()))
                    .thenReturn(cx.combustivel);

            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.criar(request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "A requisição possui opcionais duplicadas.");


            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(request.placa());
            verify(veiculoMapper).toEntity(any());
            verify(carroceriaService).buscaCarroceriaAtiva(request.idCarroceria());
            verify(coresService).buscaCorAtiva(request.idCores());
            verify(modeloService).buscaModeloAtivo(request.idModelo());
            verify(combustivelService).buscaCombustivelAtivo(request.idCombustivel());
            verify(veiculoRepository, never()).save(any());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar opcional não encontrado ao criar veiculo")
        void deveLancarExcecaoOpcionalNaoEncontradoAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            var idsRepetido = List.of(ID_VALIDO, 2L, ID_INVALIDO);
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comOpcionais(idsRepetido)
                    .build();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(any()))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(request.idModelo()))
                    .thenReturn(cx.modelo);

            when(combustivelService.buscaCombustivelAtivo(request.idCombustivel()))
                    .thenReturn(cx.combustivel);

            when(opcionalService.buscarOpcionaisAtivos(request.idsOpcionais()))
                    .thenReturn(cx.opcionais);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.criar(request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "Um ou mais opcionais não foram encontrados.");


            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(request.placa());
            verify(veiculoMapper).toEntity(any());
            verify(carroceriaService).buscaCarroceriaAtiva(request.idCarroceria());
            verify(coresService).buscaCorAtiva(request.idCores());
            verify(modeloService).buscaModeloAtivo(request.idModelo());
            verify(combustivelService).buscaCombustivelAtivo(request.idCombustivel());
            verify(veiculoRepository, never()).save(any());
            verify(opcionalService).buscarOpcionaisAtivos(request.idsOpcionais());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção informar imagens invalidas  ao criar veiculo")
        void deveLancarExcecaoImagemInvalidaAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();

            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(cx.request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(cx.request.idModelo()))
                    .thenReturn(cx.modelo);

            when(combustivelService.buscaCombustivelAtivo(cx.request.idCombustivel()))
                    .thenReturn(cx.combustivel);

            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoRepository.save(cx.entity))
                    .thenReturn(cx.entity);

            when(opcionalService.buscarOpcionaisAtivos(cx.request.idsOpcionais()))
                    .thenReturn(cx.opcionais);

            when(imagensService.criar(cx.imagemFile, cx.entity))
                    .thenThrow(new IOException("Erro ao fazer upload"));
            //ACT
            var excecao = assertThrows(IOException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertThat(excecao)
                    .hasMessage("Erro ao fazer upload");

            verify(veiculoMapper).toEntity(cx.request);
            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService).buscaCorAtiva(cx.request.idCores());
            verify(modeloService).buscaModeloAtivo(cx.request.idModelo());
            verify(combustivelService).buscaCombustivelAtivo(cx.request.idCombustivel());
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoRepository).save(cx.entity);
            verify(opcionalService).buscarOpcionaisAtivos(cx.request.idsOpcionais());
            verify(imagensService).criar(cx.imagemFile, cx.entity);
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractionsCriar();
        }
    }

    @Nested
    @DisplayName("Testes da listagem de veículos")
    class Listar {
        @Test
        @DisplayName("Deve listar todos os veiculos")
        void deveListarTodosVeiculos() {
            //Arrange
            var pagina = veiculosPage(pageable);
            var responseList = veiculosResponseList();


            when(veiculoRepository.findAll(pageable))
                    .thenReturn(pagina);
            when(veiculoMapper.toResponse(pagina.getContent().getFirst()))
                    .thenReturn(responseList.getFirst());
            when(veiculoMapper.toResponse(pagina.getContent().get(1)))
                    .thenReturn(responseList.get(1));
            when(veiculoMapper.toResponse(pagina.getContent().get(2)))
                    .thenReturn(responseList.get(2));
            when(veiculoMapper.toResponse(pagina.getContent().getLast()))
                    .thenReturn(responseList.getLast());

            //ACT
            var resultado = veiculoService.listarAdministrativo(pageable, null);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(4)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, VENDIDO),
                            tuple(2L, DISPONIVEL),
                            tuple(3L, PAUSADO),
                            tuple(4L, VENDIDO)
                    );

            verify(veiculoRepository).findAll(pageable);
            verify(veiculoRepository, never()).findByStatusVeiculo(any(StatusVeiculo.class), eq(pageable));
            verify(veiculoMapper).toResponse(pagina.getContent().getFirst());
            verify(veiculoMapper).toResponse(pagina.getContent().get(1));
            verify(veiculoMapper).toResponse(pagina.getContent().get(2));
            verify(veiculoMapper).toResponse(pagina.getContent().getLast());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper
            );
        }

        @ParameterizedTest
        @EnumSource(StatusVeiculo.class)
        @DisplayName("Deve listar veiculos por status")
        void deveListarVeiculosPorStatus(StatusVeiculo statusVeiculo) {
            //Arrange
            var veiculoPage = veiculosPageStatus(pageable, statusVeiculo);
            var veiculoResponseList = veiculosResponseListStatus(statusVeiculo);

            when(veiculoRepository.findByStatusVeiculo(statusVeiculo, pageable))
                    .thenReturn(veiculoPage);
            when(veiculoMapper.toResponse(veiculoPage.getContent().getFirst()))
                    .thenReturn(veiculoResponseList.getFirst());

            when(veiculoMapper.toResponse(veiculoPage.getContent().getLast()))
                    .thenReturn(veiculoResponseList.getLast());

            //ACT
            var resultado = veiculoService.listarAdministrativo(pageable, statusVeiculo);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, statusVeiculo),
                            tuple(2L, statusVeiculo)
                    );

            verify(veiculoRepository, never()).findAll(pageable);
            verify(veiculoRepository).findByStatusVeiculo(statusVeiculo, pageable);
            verify(veiculoMapper).toResponse(veiculoPage.getContent().getFirst());
            verify(veiculoMapper).toResponse(veiculoPage.getContent().getLast());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper
            );
        }

        @Test
        @DisplayName("Deve retornar uma lista vazia")
        void deveRetornarUmaListaVazia() {
            //Arrange
            when(veiculoRepository.findAll(pageable))
                    .thenReturn(Page.empty());

            //ACT
            var resultado = veiculoService.listarAdministrativo(pageable, null);
            //Assert
            assertThat(resultado)
                    .isEmpty();

            verify(veiculoRepository).findAll(pageable);
            verify(veiculoRepository, never()).findByStatusVeiculo(any(StatusVeiculo.class), eq(pageable));
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper
            );
        }
    }

    @Nested
    @DisplayName("Testes da listagem de veículos disponíveis")
    class ListarDisponveis {
        @Test
        @DisplayName("Deve listar veiculos disponiveis")
        void deveListarVeiculosDisponiveis() {
            //Arrange
            var veiculoPage = veiculosPageStatus(pageable, DISPONIVEL);
            var veiculoResponseList = veiculosResponseListStatus(DISPONIVEL);

            when(veiculoRepository.findByStatusVeiculo(DISPONIVEL, pageable))
                    .thenReturn(veiculoPage);
            when(veiculoMapper.toResponse(veiculoPage.getContent().getFirst()))
                    .thenReturn(veiculoResponseList.getFirst());

            when(veiculoMapper.toResponse(veiculoPage.getContent().getLast()))
                    .thenReturn(veiculoResponseList.getLast());

            //ACT
            var resultado = veiculoService.listarAtivos(pageable);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, DISPONIVEL),
                            tuple(2L, DISPONIVEL)
                    );

            verify(veiculoRepository).findByStatusVeiculo(DISPONIVEL, pageable);
            verify(veiculoMapper).toResponse(veiculoPage.getContent().getFirst());
            verify(veiculoMapper).toResponse(veiculoPage.getContent().getLast());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper
            );
        }
    }

    @Nested
    @DisplayName("Testes da listagem dos veículos do usuário autenticado")
    class ListarVeiculosUsuarioAutenticado {
        @Test
        @DisplayName("Deve listar todos os veículos do usuário autenticado")
        void deveListarVeiculosUsuarioAutenticado() {
            //Arrange
            var cx = new VeiculoTestContext();
            var pagina = veiculosPage(pageable);
            var responseList = veiculosResponseList();

            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);
            when(veiculoRepository.findByVendedor_Id(cx.usuario.getId(), pageable))
                    .thenReturn(pagina);
            when(veiculoMapper.toResponse(pagina.getContent().getFirst()))
                    .thenReturn(responseList.getFirst());
            when(veiculoMapper.toResponse(pagina.getContent().get(1)))
                    .thenReturn(responseList.get(1));
            when(veiculoMapper.toResponse(pagina.getContent().get(2)))
                    .thenReturn(responseList.get(2));
            when(veiculoMapper.toResponse(pagina.getContent().getLast()))
                    .thenReturn(responseList.getLast());
            //ACT
            var response = veiculoService.listarMeusAnuncios(pageable, cx.usuario.getId(), null);
            //Assert
            assertThat(response)
                    .isNotNull()
                    .hasSize(4)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, VENDIDO),
                            tuple(2L, DISPONIVEL),
                            tuple(3L, PAUSADO),
                            tuple(4L, VENDIDO)
                    );

            assertThat(pagina.getContent())
                    .extracting(v -> v.getVendedor().getId())
                    .allMatch(id -> id.equals(cx.usuario.getId()));

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).findByVendedor_Id(cx.usuario.getId(), pageable);
            verify(veiculoRepository, never()).findByVendedor_IdAndStatusVeiculo(anyLong(), any(), any());
            verify(veiculoMapper).toResponse(pagina.getContent().getFirst());
            verify(veiculoMapper).toResponse(pagina.getContent().get(1));
            verify(veiculoMapper).toResponse(pagina.getContent().get(2));
            verify(veiculoMapper).toResponse(pagina.getContent().getLast());

            verifyNoMoreInteractions(usuarioService, veiculoRepository, veiculoMapper);
        }

        @ParameterizedTest
        @EnumSource(StatusVeiculo.class)
        @DisplayName("Deve listar todos os veículos do usuário autenticado por status")
        void deveListarVeiculosUsuarioAutenticadoPorStatus(StatusVeiculo statusVeiculo) {
            //Arrange
            var cx = new VeiculoTestContext();
            var veiculoPage = veiculosPageStatus(pageable, statusVeiculo);
            var veiculoResponseList = veiculosResponseListStatus(statusVeiculo);

            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);
            when(veiculoRepository.findByVendedor_IdAndStatusVeiculo(cx.usuario.getId(), statusVeiculo, pageable))
                    .thenReturn(veiculoPage);
            when(veiculoMapper.toResponse(veiculoPage.getContent().getFirst()))
                    .thenReturn(veiculoResponseList.getFirst());

            when(veiculoMapper.toResponse(veiculoPage.getContent().getLast()))
                    .thenReturn(veiculoResponseList.getLast());
            //ACT
            var response = veiculoService.listarMeusAnuncios(pageable, cx.usuario.getId(), statusVeiculo);
            //Assert
            assertThat(response)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, statusVeiculo),
                            tuple(2L, statusVeiculo)
                    );

            assertThat(veiculoPage.getContent())
                    .extracting(v -> v.getVendedor().getId())
                    .allMatch(id -> id.equals(cx.usuario.getId()));

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository, never()).findByVendedor_Id(cx.usuario.getId(), pageable);
            verify(veiculoRepository).findByVendedor_IdAndStatusVeiculo(cx.usuario.getId(), statusVeiculo, pageable);
            verify(veiculoMapper).toResponse(veiculoPage.getContent().getFirst());
            verify(veiculoMapper).toResponse(veiculoPage.getContent().getLast());

            verifyNoMoreInteractions(usuarioService, veiculoRepository, veiculoMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção de usuário inativo")
        void deveLancarExcecaoUsuarioInativo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var usuarioId = cx.usuario.getId();
            when(usuarioService.buscaUsuarioAtivo(cx.usuario.getId()))
                    .thenThrow(new NotFoundException(USUARIO, cx.usuario.getId()));
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.listarMeusAnuncios(pageable, usuarioId, null));
            //Assert
            assertNotFoundResponseError(exception, USUARIO, usuarioId);
            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository, never()).findByVendedor_Id(anyLong(), any());
            verify(veiculoRepository, never()).findByVendedor_IdAndStatusVeiculo(anyLong(), any(), any());
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractions(usuarioService, veiculoRepository, veiculoMapper);

        }
    }

    @Nested
    @DisplayName("Testes da busca do veículo disponível por ID")
    class Buscar {
        @Test
        @DisplayName("Deve buscar o veículo disponivel por ID")
        void deveBuscarVeiculoPorId() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findByIdAndStatusVeiculo(ID_VALIDO, DISPONIVEL))
                    .thenReturn(Optional.of(cx.entity));

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(cx.response);
            //ACT
            var resultado = veiculoService.buscarPorId(ID_VALIDO);
            //Assert
            assertVeiculoResponse(resultado);

            verify(veiculoRepository).findByIdAndStatusVeiculo(ID_VALIDO, DISPONIVEL);
            verify(veiculoMapper).toResponse(cx.entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar veículo indisponível")
        void deveLancarExcecaoAOBuscarVeiculo() {
            //Arrange
            when(veiculoRepository.findByIdAndStatusVeiculo(ID_INVALIDO, DISPONIVEL))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.buscarPorId(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(excecao, VEICULO, ID_INVALIDO);
            verify(veiculoRepository).findByIdAndStatusVeiculo(ID_INVALIDO, DISPONIVEL);
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);

        }
    }

    @Nested
    @DisplayName("Testes da atualização do veículo")
    class Atualizar {
        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"DISPONIVEL", "PAUSADO"}
        )
        @DisplayName("Deve atualizar a placa do veículo com status permitido")
        void deveAtualizarPlacaDeVeiculoComStatusPermitido(StatusVeiculo status) {
            //Arrange
            var entity = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comStatus(status)
                    .build();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("123FEP")
                    .build();
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comPlaca("123FEP")
                    .comStatus(status)
                    .build();

            when(veiculoRepository.findById(entity.getId()))
                    .thenReturn(Optional.of(entity));

            when(veiculoRepository.existsByPlaca(request.placa()))
                    .thenReturn(false);

            when(veiculoMapper.toResponse(entity))
                    .thenReturn(response);

            //ACT
            var resultado = veiculoService.atualizar(request, entity.getId());
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::valor,
                            VeiculoResponse::quilometragem,
                            VeiculoResponse::anoFabricacao,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            1L,
                            request.placa(),
                            "Chevrolet",
                            "Onix",
                            new BigDecimal(58000),
                            67000,
                            (short) 2020,
                            status
                    );

            assertThat(entity.getStatusVeiculo())
                    .isEqualTo(status);

            verify(veiculoRepository).findById(entity.getId());
            verify(veiculoRepository).existsByPlaca(request.placa());
            verify(veiculoMapper).toUpdate(request, entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper).toResponse(entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve atualizar a carroceria do veículo")
        void deveAtualizarACarroceriaDoVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCarroceria(2L)
                    .build();
            var carroceria = CarroceriaTestContext.carroceriaEntity(2L, "Conversivel", true);
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comCarroceria("Conversivel")
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(carroceriaService.buscaCarroceriaAtiva(request.idCarroceria()))
                    .thenReturn(carroceria);

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(response);

            //ACT
            var resultado = veiculoService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::carroceria,
                            VeiculoResponse::cor,
                            VeiculoResponse::combustivel,
                            VeiculoResponse::valor,
                            VeiculoResponse::quilometragem,
                            VeiculoResponse::anoFabricacao,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            1L,
                            "QUV1F83",
                            "Chevrolet",
                            "Onix",
                            "Conversivel",
                            "Branco",
                            "Etanol",
                            new BigDecimal(58000),
                            67000,
                            (short) 2020,
                            DISPONIVEL
                    );

            assertThat(cx.entity.getCarroceria())
                    .isSameAs(carroceria);

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService).buscaCarroceriaAtiva(request.idCarroceria());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper).toResponse(cx.entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve atualizar a cor do veículo")
        void deveAtualizarACorDoVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCores(2L)
                    .build();
            var cor = CorTestContext.corEntity(2L, "Vermelho", true);
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comCor("Vermelho")
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(coresService.buscaCorAtiva(request.idCores()))
                    .thenReturn(cor);

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(response);

            //ACT
            var resultado = veiculoService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::carroceria,
                            VeiculoResponse::cor,
                            VeiculoResponse::combustivel,
                            VeiculoResponse::valor,
                            VeiculoResponse::quilometragem,
                            VeiculoResponse::anoFabricacao,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            1L,
                            "QUV1F83",
                            "Chevrolet",
                            "Onix",
                            "Hatch",
                            "Vermelho",
                            "Etanol",
                            new BigDecimal(58000),
                            67000,
                            (short) 2020,
                            DISPONIVEL
                    );

            assertThat(cx.entity.getCor())
                    .isSameAs(cor);

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService).buscaCorAtiva(request.idCores());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper).toResponse(cx.entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve atualizar o combustível do veículo")
        void deveAtualizarOCombustivelDoVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCombustivel(2L)
                    .build();
            var combustivel = CombustivelTestContext.combustivelEntity(2L, "Eletrico", true);
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comCombustivel("Eletrico")
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(combustivelService.buscaCombustivelAtivo(request.idCombustivel()))
                    .thenReturn(combustivel);

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(response);

            //ACT
            var resultado = veiculoService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::carroceria,
                            VeiculoResponse::cor,
                            VeiculoResponse::combustivel,
                            VeiculoResponse::valor,
                            VeiculoResponse::quilometragem,
                            VeiculoResponse::anoFabricacao,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            1L,
                            "QUV1F83",
                            "Chevrolet",
                            "Onix",
                            "Hatch",
                            "Branco",
                            "Eletrico",
                            new BigDecimal(58000),
                            67000,
                            (short) 2020,
                            DISPONIVEL
                    );

            assertThat(cx.entity.getCombustivel())
                    .isSameAs(combustivel);

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService).buscaCombustivelAtivo(request.idCombustivel());
            verify(veiculoMapper).toResponse(cx.entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve atualizar a marca e modelo do veículo")
        void deveAtualizarAMarcaEModeloDoVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var marca = MarcaTestContext.criarMarca(2L, "BYD", "byd.com", true);
            var modelo = ModeloTestContext.criaModelo(2L, "Dolphin", marca, true);
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdModelo(2L)
                    .build();
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comMarca("BYD")
                    .comModelo("Dolphin")
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(modeloService.buscaModeloAtivo(request.idModelo()))
                    .thenReturn(modelo);

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(response);

            //ACT
            var resultado = veiculoService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::valor,
                            VeiculoResponse::quilometragem,
                            VeiculoResponse::anoFabricacao,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            1L,
                            "QUV1F83",
                            "BYD",
                            "Dolphin",
                            new BigDecimal(58000),
                            67000,
                            (short) 2020,
                            DISPONIVEL
                    );

            assertThat(cx.entity.getModelo())
                    .isSameAs(modelo);

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService).buscaModeloAtivo(request.idModelo());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper).toResponse(cx.entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo ao atualizar")
        void deveLancarExcecaoAoAtualizarVeiculoNaoEncontrado() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, VEICULO, ID_VALIDO);

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper, never()).toUpdate(any(), any());
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lancar exceção ao atualizar para carroceria inativa")
        void deveLancarExcecaoQuandoAtualizarParaCarroceriaInativa() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCarroceria(2L)
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(carroceriaService.buscaCarroceriaAtiva(request.idCarroceria()))
                    .thenThrow(new NotFoundException(CARROCERIA, request.idCarroceria()));

            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, ID_VALIDO));
            //Assert

            assertNotFoundResponseError(exception, CARROCERIA, request.idCarroceria());

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService).buscaCarroceriaAtiva(request.idCarroceria());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar para cor inativa")
        void deveLancarExcecaoQuandoAtualizarParaCorInativa() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCores(2L)
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(coresService.buscaCorAtiva(request.idCores()))
                    .thenThrow(new NotFoundException(COR, request.idCores()));

            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, COR, request.idCores());

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService).buscaCorAtiva(request.idCores());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar para combustível inativo")
        void deveLancarExcecaoQuandoAtualizarParaCombustivelInativo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCombustivel(2L)
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(combustivelService.buscaCombustivelAtivo(request.idCombustivel()))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, request.idCombustivel()));

            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, COMBUSTIVEL, request.idCombustivel());

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService).buscaCombustivelAtivo(request.idCombustivel());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar para modelo inativo")
        void deveLancarExcecaoQuandoAtualizarParaModeloInativo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdModelo(2L)
                    .build();

            when(veiculoRepository.findById(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.entity));

            when(modeloService.buscaModeloAtivo(request.idModelo()))
                    .thenThrow(new NotFoundException(MODELO, request.idModelo()));

            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, MODELO, request.idModelo());

            verify(veiculoRepository).findById(cx.entity.getId());
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper).toUpdate(request, cx.entity);
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService).buscaModeloAtivo(request.idModelo());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        @DisplayName("Deve lançar exceção ao atualizar veículo com status proibido")
        void deveLancarExcecaoAoAtualizarVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var cx = new VeiculoTestContext();
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(ID_VALIDO)
                    .comStatus(status)
                    .build();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(veiculo));
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertBusinessResponseError(excecao, "Somente anúncios disponíveis ou pausados podem ser editados.");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper, never()).toUpdate(any(), any());
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }

        @Test
        @DisplayName("Deve lançar exceção de placa única ao atualizar veículo")
        void deveLancarExcecaoPlacaUnicaAoAtualizarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("123FEP")
                    .build();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(veiculoRepository.existsByPlaca(request.placa()))
                    .thenReturn(true);
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.atualizar(request, ID_VALIDO));
            //Assert
            assertBusinessResponseError(excecao, "A placa informada já possui um cadastro.");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(request.placa());
            verify(veiculoMapper, never()).toUpdate(any(), any());
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    combustivelService);
        }
    }

    @Nested
    @DisplayName("Testes para pausar o veículo")
    class Pausar {
        @Test
        @DisplayName("Deve pausar o veículo")
        void devePausarOVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comStatus(PAUSADO)
                    .build();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(response);
            //ACT
            var resultado = veiculoService.pausarVeiculo(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo,
                            VeiculoResponse::placa
                    ).containsExactly(cx.entity.getId(), PAUSADO, cx.entity.getPlaca());

            assertThat(cx.entity)
                    .extracting(Veiculo::getStatusVeiculo)
                    .isEqualTo(PAUSADO);

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper).toResponse(cx.entity);
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoNaoEncontrarVeiculo() {
            //Arrange
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.pausarVeiculo(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_VALIDO);

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                mode = EnumSource.Mode.EXCLUDE,
                names = "DISPONIVEL"
        )
        @DisplayName("Deve lançar exceção ao pausar veículo com status proibido")
        void deveLancarExcecaoPausarVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(ID_VALIDO)
                    .comStatus(status)
                    .build();
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(veiculo));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.pausarVeiculo(ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "Somente um veículo disponível pode ser pausado");
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }
    }

    @Nested
    @DisplayName("Testes para reativar o veículo")
    class Reativar {
        @Test
        @DisplayName("Deve reativar o veículo")
        void deveReativarOVeiculo() {
            //Arrange
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comStatus(PAUSADO)
                    .build();
            var response = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comStatus(DISPONIVEL)
                    .build();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(veiculo));

            when(veiculoMapper.toResponse(veiculo))
                    .thenReturn(response);
            //ACT
            var resultado = veiculoService.reativarVeiculo(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo,
                            VeiculoResponse::placa
                    ).containsExactly(veiculo.getId(), DISPONIVEL, veiculo.getPlaca());

            assertThat(veiculo)
                    .extracting(Veiculo::getStatusVeiculo)
                    .isEqualTo(DISPONIVEL);

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper).toResponse(veiculo);
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoNaoEncontrarVeiculo() {
            //Arrange
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.reativarVeiculo(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_VALIDO);

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                mode = EnumSource.Mode.EXCLUDE,
                names = "PAUSADO"
        )
        @DisplayName("Deve lançar exceção ao reativar veículo com status proibido")
        void deveLancarExcecaoReativarVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(ID_VALIDO)
                    .comStatus(status)
                    .build();
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(veiculo));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.reativarVeiculo(ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "Somente um veículo pausado pode ser reativado");
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);
        }
    }

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

    @Nested
    @DisplayName("Testes da desvinculação de opcionais ao veículo")
    class DesvincularOpcionais {
        @Test
        @DisplayName("Deve desvincular um opcional")
        void deveDesvincularUmOpcional() {
            //Arrange
            var cx = new VeiculoTestContext();
            cx.opcionais.forEach(opcion -> {
                var veiculoOpcional = new VeiculoOpcional(cx.entity, opcion);
                cx.entity.getOpcionais().add(veiculoOpcional);
            });
            var listOpcionais = List.of(1L);
            var opcionais = List.of(OpcionalHelper.criarOpcionalEntity());

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionais(listOpcionais))
                    .thenReturn(opcionais);
            //ACT

            veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais);
            //Assert

            assertThat(cx.entity.getOpcionais())
                    .isNotNull()
                    .hasSize(1);

            assertThat(cx.entity.getOpcionais().getFirst().getOpcional())
                    .isNotNull()
                    .extracting(
                            Opcional::getId,
                            Opcional::getNome
                    ).containsExactly(
                            2L,
                            "Automatico"
                    );

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionais(listOpcionais);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );

        }

        @Test
        @DisplayName("Deve lançar exceção ao inserir opcionais duplicados")
        void deveLancarExcecaoAoInserirOpcionaisDuplicados() {
            //Arrange
            var listOpcionais = List.of(1L, 2L, 2L);

            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(exception, "A requisição possui opcionais duplicadas.");

            verify(veiculoRepository, never()).findById(anyLong());
            verify(opcionalService, never()).buscarOpcionais(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoAoNaoEncontrarVeiculo() {
            //Arrange
            var listOpcionais = List.of(1L, 2L);
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_VALIDO);
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService, never()).buscarOpcionais(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        @DisplayName("Deve lançar exceção ao desvincular opcionais para veículo com status proibido")
        void deveLancarExcecaoAoDesvincularOpcionaisEmVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var listOpcionais = List.of(1L);
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(ID_VALIDO)
                    .comStatus(status)
                    .build();
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(veiculo));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(exception, "Somente anúncios disponíveis ou pausados podem ser editados.");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService, never()).buscarOpcionais(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lançar exceçao de opcionais não existem")
        void deveLancarExcecaoOpcionaisNaoExistem() {
            //Arrange
            var cx = new VeiculoTestContext();
            var listOpcionais = List.of(1L, 2L, 3L);

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionais(listOpcionais))
                    .thenReturn(cx.opcionais);

            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(excecao, "Um ou mais opcionais não foram encontrados.");
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionais(listOpcionais);
            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService);
        }

        @Test
        @DisplayName("Deve lançar exceção não possui opcional")
        void deveLancarExcecaoNaoPossuiOpcional() {
            //Arrange
            var cx = new VeiculoTestContext();
            cx.opcionais.forEach(opcion -> {
                var veiculoOpcional = new VeiculoOpcional(cx.entity, opcion);
                cx.entity.getOpcionais().add(veiculoOpcional);
            });
            var listOpcionais = List.of(3L);
            var opcionais = List.of(OpcionalEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(3L)
                    .comNome("Teto solar")
                    .build());

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionais(listOpcionais))
                    .thenReturn(opcionais);
            //ACT

            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(exception, "O Veiculo informado não possui esse opcional");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionais(listOpcionais);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );

        }
    }

    @Nested
    @DisplayName("Testes da vinculação de opcionais ao veículo")
    class VincularOpcionais {
        @Test
        @DisplayName("Deve vincular um opcional")
        void deveVincularUmOpcional() {
            //Arrange
            var cx = new VeiculoTestContext();
            cx.opcionais.forEach(opcion -> {
                var veiculoOpcional = new VeiculoOpcional(cx.entity, opcion);
                cx.entity.getOpcionais().add(veiculoOpcional);
            });
            var listOpcionais = List.of(3L);
            var opcionais = List.of(OpcionalEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(3L)
                    .comNome("Teto solar")
                    .build());

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionaisAtivos(listOpcionais))
                    .thenReturn(opcionais);

            //ACT
            veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais);
            //Assert
            assertThat(cx.entity.getOpcionais())
                    .isNotNull()
                    .hasSize(3);

            assertThat(cx.entity.getOpcionais())
                    .extracting(vo -> vo.getOpcional().getId())
                    .containsExactlyInAnyOrder(1L, 2L, 3L);

            assertThat(cx.entity.getOpcionais())
                    .allMatch(vo -> vo.getVeiculo() == cx.entity);
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionaisAtivos(listOpcionais);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lancar exceção de opcionais duplicados")
        void deveLancarExcecaoDuplicados() {
            //Arrange
            var lista = List.of(1L, 1L);
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(ID_VALIDO, lista));
            //Assert
            assertBusinessResponseError(excecao, "A requisição possui opcionais duplicadas.");

            verify(veiculoRepository, never()).findById(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veículo")
        void deveLancarExcecaoAoNaoEncontrarVeiculo() {
            //Arrange
            var listOpcionais = List.of(1L, 2L);
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertNotFoundResponseError(exception, VEICULO, ID_VALIDO);
            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @ParameterizedTest
        @EnumSource(
                value = StatusVeiculo.class,
                names = {"RESERVADO", "VENDIDO"}
        )
        @DisplayName("Deve lançar exceção ao vincular opcionais para veículo com status proibido")
        void deveLancarExcecaoAoVincularOpcionaisEmVeiculoComStatusProibido(StatusVeiculo status) {
            //Arrange
            var listOpcionais = List.of(1L);
            var veiculo = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(ID_VALIDO)
                    .comStatus(status)
                    .build();
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(veiculo));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(exception, "Somente anúncios disponíveis ou pausados podem ser editados.");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lançar exceção de opcionais não existentes")
        void deveLancarExcecaoOpcionaisNaoExistente() {
            //Arrange
            var cx = new VeiculoTestContext();
            var listOpcionais = List.of(1L);
            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionaisAtivos(listOpcionais))
                    .thenReturn(Collections.emptyList());
            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert
            assertBusinessResponseError(excecao, "Um ou mais opcionais não foram encontrados.");

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionaisAtivos(listOpcionais);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }

        @Test
        @DisplayName("Deve lançar exceção de opcional já existente")
        void deveLancarExcecaoOpcionaisExistente() {
            //Arrange
            var cx = new VeiculoTestContext();
            cx.opcionais.forEach(opcion -> {
                var veiculoOpcional = new VeiculoOpcional(cx.entity, opcion);
                cx.entity.getOpcionais().add(veiculoOpcional);
            });
            var listOpcionais = List.of(1L);
            var opcionais = List.of(OpcionalHelper.criarOpcionalEntity());

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));

            when(opcionalService.buscarOpcionaisAtivos(listOpcionais))
                    .thenReturn(opcionais);

            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais));
            //Assert

            assertBusinessResponseError(excecao, OPCIONAL.jaAtiva());

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(opcionalService).buscarOpcionaisAtivos(listOpcionais);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    opcionalService
            );
        }
    }


    private void verifyNoMoreInteractionsCriar() {
        verifyNoMoreInteractions(
                veiculoMapper,
                carroceriaService,
                coresService,
                modeloService,
                usuarioService,
                combustivelService,
                opcionalService,
                imagensService,
                veiculoRepository
        );
    }

    private Page<Veiculo> veiculosPage(PageRequest pageable) {
        var entity1 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comStatus(VENDIDO)
                .build();
        var entity2 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comId(2L)
                .comStatus(DISPONIVEL)
                .build();

        var entity3 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comId(3L)
                .comStatus(PAUSADO)
                .build();

        var entity4 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comId(4L)
                .comStatus(VENDIDO)
                .build();

        return new PageImpl<>(
                List.of(entity1, entity2, entity3, entity4),
                pageable,
                4
        );
    }

    private Page<Veiculo> veiculosPageStatus(PageRequest pageable, StatusVeiculo status) {
        var entity1 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comStatus(status)
                .build();
        var entity2 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comId(2L)
                .comStatus(status)
                .build();

        return new PageImpl<>(
                List.of(entity1, entity2),
                pageable,
                2
        );
    }

    private List<VeiculoResponse> veiculosResponseList() {
        var response1 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comStatus(VENDIDO)
                .build();

        var response2 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(2L)
                .comStatus(DISPONIVEL)
                .build();

        var response3 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(3L)
                .comStatus(PAUSADO)
                .build();

        var response4 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(4L)
                .comStatus(VENDIDO)
                .build();

        return List.of(response1, response2, response3, response4);
    }

    private List<VeiculoResponse> veiculosResponseListStatus(StatusVeiculo status) {
        var response1 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comStatus(status)
                .build();

        var response2 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(2L)
                .comStatus(status)
                .build();

        return List.of(response1, response2);
    }

}
