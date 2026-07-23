package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.AlterarStatusRequest;
import com.javacar.lojadecarro.dto.response.ImagemResponse;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.entity.VeiculoOpcional;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.OpcionalHelper;
import com.javacar.lojadecarro.factory.helper.VeiculoTestContext;
import com.javacar.lojadecarro.factory.imagem.ImagemEntityFactory;
import com.javacar.lojadecarro.factory.imagem.ImagemResponseFactory;
import com.javacar.lojadecarro.factory.opcional.OpcionalEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoResponseFactory;
import com.javacar.lojadecarro.mapper.ImagemMapper;
import com.javacar.lojadecarro.mapper.VeiculoMapper;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.*;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.enums.StatusVeiculo.VENDIDO;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {
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

    @Nested
    class criar {
        @Test
        @DisplayName("Deve cadastrar um veiculo")
        void deveCadastrarUmVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            mockDependenciasCriacaoCompleta(cx);
            when(veiculoRepository.save(cx.entity))
                    .thenReturn(cx.entity);

            //ACT
            var resultado = veiculoService.criar(cx.request, cx.imagemFile);
            //Assert
            assertVeiculoResponse(resultado);
            assertDependenciasVeiculoCompleto(cx, DISPONIVEL);
            verifyVeiculos(cx);
        }


        @Test
        @DisplayName("Deve lançar exceção de carroceria nao encontrada ao criar veiculo")
        void deveLancarExcecaoCarroceriaNaoEncontradaAoCriarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);
            mockDependenciasCriacaoAteCarroceria(cx);

            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile));
            //Assert
            assertNotFoundResponseError(excecao, CARROCERIA, cx.request.idCarroceria());
            verifyVeiculosAteCarroceria(cx);
            verify(veiculoMapper).toEntity(cx.request);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar cor nao encontrada ao criar veiculo")
        void deveLancarExcecaoCorNaoEncontradaAoCriarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);
            mockDependenciasCriacaoAteCor(cx);

            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile));
            //Assert
            assertNotFoundResponseError(excecao, COR, cx.request.idCarroceria());
            verify(veiculoMapper).toEntity(cx.request);
            verifyVeiculosAteCor(cx);
            verifyNoInteractions(veiculoRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar modelo nao encontrado ao criar veiculo")
        void deveLancarExcecaoModeloNaoEncontradoAoCriarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);
            mockDependenciasCriacaoAteModelo(cx);

            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile));
            //Assert
            assertNotFoundResponseError(excecao, MODELO, cx.request.idCarroceria());
            verify(veiculoMapper).toEntity(cx.request);
            verifyVeiculosAteModelo(cx);

            verifyNoInteractions(veiculoRepository);

        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar usuario nao encontrado ao criar veiculo")
        void deveLancarExcecaoUsuarioNaoEncontradoAoCriarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            mockDependenciasCriacaoAteUsuario(cx);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile));
            //Assert
            assertNotFoundResponseError(excecao, USUARIO, cx.request.idCarroceria());
            verify(veiculoMapper).toEntity(cx.request);
            verifyVeiculosAteUsuario(cx);
            verifyNoInteractions(veiculoRepository);

        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar combustivel nao encontrado ao criar veiculo")
        void deveLancarExcecaoCombustivelNaoEncontradoAoCriarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();
            mockDependenciasCriacaoAteCombustivel(cx);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);

            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile));
            //Assert
            assertNotFoundResponseError(excecao, COMBUSTIVEL, cx.request.idCarroceria());
            verifyVeiculosAteCombustivel(cx);
            verify(veiculoMapper).toEntity(cx.request);
            verifyNoInteractions(veiculoRepository);

        }

        @Test
        @DisplayName("Deve lançar exceção informar opcionais duplicados ao criar veiculo")
        void deveLancarExcecaoOpcionalDuplicadoAoCriarVeiculo() {
            //Arrange
            var idsRepetido = List.of(ID_VALIDO, 2L, ID_VALIDO);
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comOpcionais(idsRepetido)
                    .build();

            var cx = new VeiculoTestContext();

            when(veiculoMapper.toEntity(request))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceria(cx.request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCor(cx.request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModelo(cx.request.idModelo()))
                    .thenReturn(cx.modelo);

            when(usuarioService.buscaUsuario(cx.request.idUsuario()))
                    .thenReturn(cx.usuario);

            when(combustivelService.buscaCombustivel(cx.request.idCombustivel()))
                    .thenReturn(cx.combustivel);

            when(veiculoRepository.save(cx.entity))
                    .thenReturn(cx.entity);


            //ACT
            var excecao = assertThrows(BusinessException.class,
                    () -> veiculoService.criar(request, cx.imagemFile));
            //Assert
            assertBusinessResponseError(excecao, "A requisição possui opcionais duplicadas.");

            verify(veiculoMapper).toEntity(request);
            verify(carroceriaService).buscaCarroceria(cx.request.idCarroceria());
            verify(coresService).buscaCor(cx.request.idCores());
            verify(modeloService).buscaModelo(cx.request.idModelo());
            verify(usuarioService).buscaUsuario(cx.request.idUsuario());
            verify(combustivelService).buscaCombustivel(cx.request.idCombustivel());
            verify(veiculoRepository).save(cx.entity);
            verify(veiculoMapper, never()).toResponse(cx.entity);

            verifyNoMoreInteractions(
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    usuarioService,
                    combustivelService,
                    veiculoRepository
            );

            verifyNoInteractions(
                    opcionalService,
                    imagensService
            );
        }

        @Test
        @DisplayName("Deve lançar exceção informar imagens invalidas  ao criar veiculo")
        void deveLancarExcecaoImagemInvalidaAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();

            mockDependenciasCriacaoSemImagemEOpcionaisExcecao(cx);

            when(veiculoRepository.save(cx.entity))
                    .thenReturn(cx.entity);

            when(opcionalService.buscarOpcionais(cx.request.idsOpcionais()))
                    .thenReturn(cx.opcionais);

            when(imagensService.criar(cx.imagemFile, cx.entity))
                    .thenThrow(new IOException("Erro ao fazer upload"));
            //ACT
            var excecao = assertThrows(IOException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile));
            //Assert
            assertThat(excecao)
                    .hasMessage("Erro ao fazer upload");

            verifyVeiculosSemImagemEOpcionaisExcecao(cx);

            verify(veiculoRepository).save(cx.entity);
            verify(opcionalService).buscarOpcionais(cx.request.idsOpcionais());
            verify(veiculoMapper, never()).toResponse(cx.entity);

            verifyNoMoreInteractions(
                    veiculoMapper,
                    carroceriaService,
                    coresService,
                    modeloService,
                    usuarioService,
                    combustivelService,
                    veiculoRepository,
                    opcionalService,
                    imagensService
            );
        }
    }

    @Nested
    class listar {
        @Test
        @DisplayName("Deve listar veiculos vendidos")
        void deveListarVeiculosVendidos() {
            //Arrange
            var pageable = PageRequest.of(0, 10);
            var entity1 = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comStatus(VENDIDO)
                    .build();
            var entity2 = VeiculoEntityFactory
                    .criarEntity()
                    .comTodosOsCampos()
                    .comId(2L)
                    .comStatus(VENDIDO)
                    .build();

            var pagina = new PageImpl<>(
                    List.of(entity1, entity2),
                    pageable,
                    2
            );

            var response1 = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comStatus(VENDIDO)
                    .build();

            var response2 = VeiculoResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comId(2L)
                    .comStatus(VENDIDO)
                    .build();


            when(veiculoRepository.findByStatusVeiculo(VENDIDO, pageable))
                    .thenReturn(pagina);
            when(veiculoMapper.toResponse(entity1))
                    .thenReturn(response1);

            when(veiculoMapper.toResponse(entity2))
                    .thenReturn(response2);

            //ACT
            var resultado = veiculoService.listar(pageable, VENDIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, VENDIDO),
                            tuple(2L, VENDIDO)
                    );

            verify(veiculoRepository, never()).findAll(pageable);
            verify(veiculoRepository).findByStatusVeiculo(VENDIDO, pageable);
            verify(veiculoMapper).toResponse(entity1);
            verify(veiculoMapper).toResponse(entity2);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper
            );
        }

        @Test
        @DisplayName("Deve listar todos os veiculos")
        void deveListarTodosVeiculos() {
            //Arrange
            var pageable = PageRequest.of(0, 10);
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

            var pagina = new PageImpl<>(
                    List.of(entity1, entity2),
                    pageable,
                    2
            );

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


            when(veiculoRepository.findAll(pageable))
                    .thenReturn(pagina);
            when(veiculoMapper.toResponse(entity1))
                    .thenReturn(response1);

            when(veiculoMapper.toResponse(entity2))
                    .thenReturn(response2);

            //ACT
            var resultado = veiculoService.listar(pageable, null);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, VENDIDO),
                            tuple(2L, DISPONIVEL)
                    );

            verify(veiculoRepository).findAll(pageable);
            verify(veiculoRepository, never()).findByStatusVeiculo(any(StatusVeiculo.class), eq(pageable));
            verify(veiculoMapper).toResponse(entity1);
            verify(veiculoMapper).toResponse(entity2);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper
            );
        }
    }

    @Nested
    class buscarPorId {
        @Test
        @DisplayName("Deve buscar o veiculo por ID")
        void deveBuscarVeiculoPorId() {
            //Arrange
            var entity = criarVeiculoEntity();
            var response = criarVeiculoResponse();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(veiculoMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = veiculoService.buscarPorId(ID_VALIDO);
            //Assert
            assertVeiculoResponse(resultado);

            verify(veiculoRepository).findById(ID_VALIDO);
            verify(veiculoMapper).toResponse(entity);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar veiculo")
        void deveLancarExcecaoAOBuscarVeiculo() {
            //Arrange
            when(veiculoRepository.findById(ID_INVALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.buscarPorId(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(excecao, VEICULO, ID_INVALIDO);
            verify(veiculoRepository).findById(ID_INVALIDO);

            verifyNoMoreInteractions(veiculoRepository);

            verifyNoInteractions(veiculoMapper);
        }
    }

    @Nested
    class atualizar {
        @Test
        @DisplayName("Deve atualizar o veiculo")
        void deveAtualizarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));
            doNothing()
                    .when(veiculoMapper).toUpdate(cx.request, cx.entity);

            mockDependenciasCriacaoSemImagemEOpcionais(cx);

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(cx.response);

            //ACT
            var resultado = veiculoService.atualizar(cx.request, ID_VALIDO);
            //Assert
            assertVeiculoResponse(resultado);
            assertDependenciasVeiculoCompletoSemImagemEOpcional(cx);

            verify(veiculoRepository).findById(ID_VALIDO);

            verifyVeiculosSemImagemEOpcionais(cx);

        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar veiculo ao atualizar")
        void deveLancarExcecaoAoAtualizarVeiculoNaoEncontrado() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findById(ID_INVALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(cx.request, ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(excecao, VEICULO, ID_INVALIDO);
            verify(veiculoRepository).findById(ID_INVALIDO);
            verifyNoMoreInteractions(veiculoRepository);

            verifyNoInteractions(
                    carroceriaService,
                    coresService,
                    modeloService,
                    usuarioService,
                    combustivelService,
                    veiculoMapper
            );
        }

        @Test
        @DisplayName("Deve lançar exceção de carroceria ao atualizar veiculo")
        void deveLancarExcecaoBuscarCarroceriaNaoEncontradaAoAtualizarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));
            doNothing()
                    .when(veiculoMapper).toUpdate(cx.request, cx.entity);

            mockDependenciasCriacaoAteCarroceria(cx);
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, CARROCERIA, ID_VALIDO);
            verifyVeiculosAteCarroceria(cx);

            verify(veiculoRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(veiculoRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção de cor ao atualizar veiculo")
        void deveLancarExcecaoBuscarCorNaoEncontradaAoAtualizarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));
            doNothing()
                    .when(veiculoMapper).toUpdate(cx.request, cx.entity);

            mockDependenciasCriacaoAteCor(cx);
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, COR, ID_VALIDO);
            verify(veiculoRepository).findById(ID_VALIDO);
            verifyVeiculosAteCor(cx);
        }

        @Test
        @DisplayName("Deve lançar exceção de modelo ao atualizar veiculo")
        void deveLancarExcecaoBuscaModeloNaoEncontradoAoAtualizarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));
            doNothing()
                    .when(veiculoMapper).toUpdate(cx.request, cx.entity);

            mockDependenciasCriacaoAteModelo(cx);
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, MODELO, ID_VALIDO);
            verify(veiculoRepository).findById(ID_VALIDO);
            verifyVeiculosAteModelo(cx);
        }

        @Test
        @DisplayName("Deve lançar exceção de usuario ao atualizar veiculo")
        void deveLancarExcecaoBuscaUsuarioNaoEncontradoAoAtualizarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));
            doNothing()
                    .when(veiculoMapper).toUpdate(cx.request, cx.entity);

            mockDependenciasCriacaoAteUsuario(cx);
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, USUARIO, ID_VALIDO);
            verify(veiculoRepository).findById(ID_VALIDO);
            verifyVeiculosAteUsuario(cx);
        }

        @Test
        @DisplayName("Deve lançar exceção de combustivel ao atualizar veiculo")
        void deveLancarExcecaoBuscaCombustivelNaoEncontradoAoAtualizarVeiculo() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.entity));
            doNothing()
                    .when(veiculoMapper).toUpdate(cx.request, cx.entity);

            mockDependenciasCriacaoAteCombustivel(cx);
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, COMBUSTIVEL, ID_VALIDO);
            verify(veiculoRepository).findById(ID_VALIDO);
            verifyVeiculosAteCombustivel(cx);
        }

        @Nested
        class alterarStatus {
            @Test
            @DisplayName("Deve alterar o status do veiculo")
            void deveAlterarStatusDoveiculo() {
                //Arrange
                var cx = new VeiculoTestContext();

                var response = VeiculoResponseFactory
                        .criarResponse()
                        .comTodosOsCampos()
                        .comStatus(VENDIDO)
                        .build();


                when(veiculoRepository.findById(ID_VALIDO))
                        .thenReturn(Optional.of(cx.entity));

                when(veiculoMapper.toResponse(cx.entity))
                        .thenReturn(response);
                //ACT
                var resultado = veiculoService.alterarStatus(ID_VALIDO, new AlterarStatusRequest(VENDIDO));
                //Assert
                assertThat(resultado).isNotNull();

                assertThat(cx.entity.getStatusVeiculo()).isEqualTo(VENDIDO);
                assertThat(response.statusVeiculo()).isEqualTo(VENDIDO);

                verify(veiculoRepository).findById(ID_VALIDO);
                verify(veiculoMapper).toResponse(cx.entity);

                verifyNoMoreInteractions(
                        veiculoRepository,
                        veiculoMapper
                );
            }


            @Test
            @DisplayName("Deve lançar exceção ao alterar status")
            void deveLancarExcecaoAlterarStatus() {
                //Arrange
                var cx = new VeiculoTestContext();
                when(veiculoRepository.findById(ID_VALIDO))
                        .thenReturn(Optional.of(cx.entity));
                //ACT
                var excecao = assertThrows(BusinessException.class,
                        () -> veiculoService.alterarStatus(ID_VALIDO, new AlterarStatusRequest(DISPONIVEL)));
                //Assert
                assertBusinessResponseError(excecao, "Status informado correspondente ao status atual");

                verify(veiculoRepository).findById(ID_VALIDO);

                verifyNoMoreInteractions(veiculoRepository);

                verifyNoInteractions(veiculoMapper);
            }

            @Test
            @DisplayName("Deve lançar veiculo nao encontrado ao alterar status")
            void deveLancarVeiculoNaoEncontrado() {
                //Arrange
                when(veiculoRepository.findById(ID_VALIDO))
                        .thenReturn(Optional.empty());
                //ACT
                var excecao = assertThrows(NotFoundException.class,
                        () -> veiculoService.alterarStatus(ID_VALIDO, new AlterarStatusRequest(DISPONIVEL)));
                //Assert
                assertNotFoundResponseError(excecao, VEICULO, ID_VALIDO);

                verify(veiculoRepository).findById(ID_VALIDO);
                verifyNoMoreInteractions(veiculoRepository);

                verifyNoInteractions(veiculoMapper);
            }
        }

        @Nested
        class buscaVeiculo {
            @Test
            @DisplayName("Deve buscar a entidade veiculo")
            void deveBuscarAEntidadeVeiculo() {
                //Arrange
                var cx = new VeiculoTestContext();
                when(veiculoRepository.findById(ID_VALIDO))
                        .thenReturn(Optional.of(cx.entity));
                //ACT
                var resultado = veiculoService.buscaVeiculo(ID_VALIDO);
                //Assert

                assertThat(resultado)
                        .isNotNull()
                        .extracting(
                                Veiculo::getId,
                                Veiculo::getAnoFabricacao,
                                Veiculo::getMotor,
                                Veiculo::getDescricao,
                                Veiculo::getPlaca,
                                Veiculo::getQuilometragem,
                                Veiculo::getValor,
                                Veiculo::getStatusVeiculo
                        ).containsExactly(
                                ID_VALIDO,
                                (short) 2020,
                                "1.0",
                                "Documentos em dia",
                                "QUV1F836",
                                67000,
                                new BigDecimal(58000),
                                DISPONIVEL
                        );

                verify(veiculoRepository).findById(ID_VALIDO);
                verifyNoMoreInteractions(veiculoRepository);
            }

            @Test
            @DisplayName("Deve lançar exceção ao buscar entidade veiculo")
            void deveLancarExcecaoBuscaEntidadeVeiculo() {
                //Arrange
                when(veiculoRepository.findById(ID_VALIDO))
                        .thenThrow(new NotFoundException(VEICULO, ID_VALIDO));
                //ACT
                var excecao = assertThrows(NotFoundException.class,
                        () -> veiculoService.buscaVeiculo(ID_VALIDO));
                //Assert
                assertNotFoundResponseError(excecao, VEICULO, ID_VALIDO);

                verify(veiculoRepository).findById(ID_VALIDO);
                verifyNoMoreInteractions(veiculoRepository);
            }
        }

        @Nested
        class listarImagens {
            @Test
            @DisplayName("Deve listar imagens do veiculo")
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
                        .thenThrow(new NotFoundException(VEICULO, ID_VALIDO));
                //ACT
                var excecao = assertThrows(NotFoundException.class,
                        () -> veiculoService.listarImagens(ID_VALIDO));
                //Assert
                assertNotFoundResponseError(excecao, VEICULO, ID_VALIDO);

                verify(veiculoRepository).findById(ID_VALIDO);

                verifyNoMoreInteractions(veiculoRepository);

                verifyNoMoreInteractions(imagemMapper);
            }
        }

        @Nested
        class desvincularOpcionais {
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
            @DisplayName("Deve lançar exceção não possui opcional")
            void deveLancarExcecaoNaoPossuiOpcional() {
                //Arrange
                var cx = new VeiculoTestContext();
                cx.opcionais.forEach(opcion -> {
                    var veiculoOpcional = new VeiculoOpcional(cx.entity, opcion);
                    cx.entity.getOpcionais().add(veiculoOpcional);
                });
                var listOpcionais = List.of(3L);
                var opcionais = List.of(OpcionalHelper.criarOpcionalEntity());

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

            @Test
            @DisplayName("Deve lançar exceçao de opcionais nao existem")
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
        }

        @Nested
        class vincularOpcionais {
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

                when(opcionalService.buscarOpcionais(listOpcionais))
                        .thenReturn(opcionais);

                //ACT
                veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais);
                //Assert
                assertThat(cx.entity.getOpcionais())
                        .isNotNull()
                        .hasSize(3);
                verify(veiculoRepository).findById(ID_VALIDO);
                verify(opcionalService).buscarOpcionais(listOpcionais);

                verifyNoMoreInteractions(
                        veiculoRepository,
                        opcionalService
                );
            }

            @Test
            @DisplayName("Deve lancar exceção de opcionais duplicados")
            void deveLancarExcecaoDuplicados() {
                //Arrange
                //ACT
                var excecao = assertThrows(BusinessException.class,
                        () -> veiculoService.vincularOpcionais(ID_VALIDO, List.of(1L, 1L)));
                //Assert
                assertBusinessResponseError(excecao, "A requisição possui opcionais duplicadas.");

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

                when(opcionalService.buscarOpcionais(listOpcionais))
                        .thenReturn(Collections.emptyList());
                //ACT
                var excecao = assertThrows(BusinessException.class,
                        () -> veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais));
                //Assert
                assertBusinessResponseError(excecao, "Um ou mais opcionais não foram encontrados.");

                verify(veiculoRepository).findById(ID_VALIDO);
                verify(opcionalService).buscarOpcionais(listOpcionais);

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

                when(opcionalService.buscarOpcionais(listOpcionais))
                        .thenReturn(opcionais);

                //ACT
                var excecao = assertThrows(BusinessException.class,
                        () -> veiculoService.vincularOpcionais(ID_VALIDO, listOpcionais));
                //Assert

                assertBusinessResponseError(excecao, OPCIONAL.jaAtiva());

                verify(veiculoRepository).findById(ID_VALIDO);
                verify(opcionalService).buscarOpcionais(listOpcionais);

                verifyNoMoreInteractions(
                        veiculoRepository,
                        opcionalService
                );
            }
        }
    }

    private void mockDependenciasCriacaoCompleta(VeiculoTestContext ctx) throws IOException {

        when(veiculoMapper.toEntity(ctx.request))
                .thenReturn(ctx.entity);

        when(carroceriaService.buscaCarroceria(ctx.request.idCarroceria()))
                .thenReturn(ctx.carroceria);

        when(coresService.buscaCor(ctx.request.idCores()))
                .thenReturn(ctx.cor);

        when(modeloService.buscaModelo(ctx.request.idModelo()))
                .thenReturn(ctx.modelo);

        when(usuarioService.buscaUsuario(ctx.request.idUsuario()))
                .thenReturn(ctx.usuario);

        when(combustivelService.buscaCombustivel(ctx.request.idCombustivel()))
                .thenReturn(ctx.combustivel);

        when(opcionalService.buscarOpcionais(ctx.request.idsOpcionais()))
                .thenReturn(ctx.opcionais);

        when(veiculoMapper.toResponse(ctx.entity))
                .thenReturn(ctx.response);

        when(imagensService.criar(ctx.imagemFile, ctx.entity))
                .thenReturn(ctx.imagens, ctx.imagens);
    }

    private void mockDependenciasCriacaoAteCarroceria(VeiculoTestContext ctx) {

        when(carroceriaService.buscaCarroceria(ctx.request.idCarroceria()))
                .thenThrow(new NotFoundException(CARROCERIA, ctx.request.idCarroceria()));

    }

    private void mockDependenciasCriacaoAteCor(VeiculoTestContext ctx) {


        when(carroceriaService.buscaCarroceria(ctx.request.idCarroceria()))
                .thenReturn(ctx.carroceria);

        when(coresService.buscaCor(ctx.request.idCores()))
                .thenThrow(new NotFoundException(COR, ctx.request.idCarroceria()));
    }

    private void mockDependenciasCriacaoAteModelo(VeiculoTestContext ctx) {
        when(carroceriaService.buscaCarroceria(ctx.request.idCarroceria()))
                .thenReturn(ctx.carroceria);

        when(coresService.buscaCor(ctx.request.idCores()))
                .thenReturn(ctx.cor);

        when(modeloService.buscaModelo(ctx.request.idModelo()))
                .thenThrow(new NotFoundException(MODELO, ctx.request.idCarroceria()));
    }

    private void mockDependenciasCriacaoAteUsuario(VeiculoTestContext ctx) {


        when(carroceriaService.buscaCarroceria(ctx.request.idCarroceria()))
                .thenReturn(ctx.carroceria);

        when(coresService.buscaCor(ctx.request.idCores()))
                .thenReturn(ctx.cor);

        when(modeloService.buscaModelo(ctx.request.idModelo()))
                .thenReturn(ctx.modelo);

        when(usuarioService.buscaUsuario(ctx.request.idUsuario()))
                .thenThrow(new NotFoundException(USUARIO, ctx.request.idCarroceria()));
    }

    private void mockDependenciasCriacaoAteCombustivel(VeiculoTestContext ctx) {


        when(carroceriaService.buscaCarroceria(ctx.request.idCarroceria()))
                .thenReturn(ctx.carroceria);

        when(coresService.buscaCor(ctx.request.idCores()))
                .thenReturn(ctx.cor);

        when(modeloService.buscaModelo(ctx.request.idModelo()))
                .thenReturn(ctx.modelo);

        when(usuarioService.buscaUsuario(ctx.request.idUsuario()))
                .thenReturn(ctx.usuario);
        when(combustivelService.buscaCombustivel(ctx.request.idCombustivel()))
                .thenThrow(new NotFoundException(COMBUSTIVEL, ctx.request.idCarroceria()));
    }

    private void mockDependenciasCriacaoSemImagemEOpcionais(VeiculoTestContext ctx) {

        when(carroceriaService.buscaCarroceria(ctx.request.idCarroceria()))
                .thenReturn(ctx.carroceria);

        when(coresService.buscaCor(ctx.request.idCores()))
                .thenReturn(ctx.cor);

        when(modeloService.buscaModelo(ctx.request.idModelo()))
                .thenReturn(ctx.modelo);

        when(usuarioService.buscaUsuario(ctx.request.idUsuario()))
                .thenReturn(ctx.usuario);

        when(combustivelService.buscaCombustivel(ctx.request.idCombustivel()))
                .thenReturn(ctx.combustivel);

    }

    private void mockDependenciasCriacaoSemImagemEOpcionaisExcecao(VeiculoTestContext ctx) {

        when(veiculoMapper.toEntity(ctx.request))
                .thenReturn(ctx.entity);

        when(carroceriaService.buscaCarroceria(ctx.request.idCarroceria()))
                .thenReturn(ctx.carroceria);

        when(coresService.buscaCor(ctx.request.idCores()))
                .thenReturn(ctx.cor);

        when(modeloService.buscaModelo(ctx.request.idModelo()))
                .thenReturn(ctx.modelo);

        when(usuarioService.buscaUsuario(ctx.request.idUsuario()))
                .thenReturn(ctx.usuario);

        when(combustivelService.buscaCombustivel(ctx.request.idCombustivel()))
                .thenReturn(ctx.combustivel);

    }

    private void verifyVeiculos(VeiculoTestContext cx) throws IOException {
        verify(veiculoMapper).toEntity(cx.request);
        verify(carroceriaService).buscaCarroceria(cx.request.idCarroceria());
        verify(coresService).buscaCor(cx.request.idCores());
        verify(modeloService).buscaModelo(cx.request.idModelo());
        verify(usuarioService).buscaUsuario(cx.request.idUsuario());
        verify(combustivelService).buscaCombustivel(cx.request.idCombustivel());
        verify(opcionalService).buscarOpcionais(cx.request.idsOpcionais());
        verify(imagensService).criar(cx.imagemFile, cx.entity);
        verify(veiculoRepository).save(cx.entity);
        verify(veiculoMapper).toResponse(cx.entity);

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

    private void verifyVeiculosAteCarroceria(VeiculoTestContext cx) {

        verify(carroceriaService).buscaCarroceria(cx.request.idCarroceria());

        verifyNoMoreInteractions(
                veiculoMapper,
                carroceriaService
        );

        verifyNoInteractions(
                coresService,
                modeloService,
                usuarioService,
                combustivelService
        );
    }

    private void verifyVeiculosAteCor(VeiculoTestContext cx) {

        verify(carroceriaService).buscaCarroceria(cx.request.idCarroceria());
        verify(coresService).buscaCor(cx.request.idCores());

        verifyNoMoreInteractions(
                veiculoMapper,
                carroceriaService,
                coresService
        );

        verifyNoInteractions(
                modeloService,
                usuarioService,
                combustivelService,
                opcionalService,
                imagensService

        );
    }

    private void verifyVeiculosAteModelo(VeiculoTestContext cx) {

        verify(carroceriaService).buscaCarroceria(cx.request.idCarroceria());
        verify(coresService).buscaCor(cx.request.idCores());
        verify(modeloService).buscaModelo(cx.request.idModelo());

        verifyNoMoreInteractions(
                veiculoMapper,
                carroceriaService,
                coresService,
                modeloService
        );

        verifyNoInteractions(
                usuarioService,
                combustivelService,
                opcionalService,
                imagensService

        );
    }

    private void verifyVeiculosAteUsuario(VeiculoTestContext cx) {

        verify(carroceriaService).buscaCarroceria(cx.request.idCarroceria());
        verify(coresService).buscaCor(cx.request.idCores());
        verify(modeloService).buscaModelo(cx.request.idModelo());
        verify(usuarioService).buscaUsuario(cx.request.idUsuario());

        verifyNoMoreInteractions(
                veiculoMapper,
                carroceriaService,
                coresService,
                modeloService,
                usuarioService
        );

        verifyNoInteractions(
                combustivelService,
                opcionalService,
                imagensService

        );
    }

    private void verifyVeiculosAteCombustivel(VeiculoTestContext cx) {

        verify(carroceriaService).buscaCarroceria(cx.request.idCarroceria());
        verify(coresService).buscaCor(cx.request.idCores());
        verify(modeloService).buscaModelo(cx.request.idModelo());
        verify(usuarioService).buscaUsuario(cx.request.idUsuario());

        verifyNoMoreInteractions(
                veiculoMapper,
                carroceriaService,
                coresService,
                modeloService,
                usuarioService,
                combustivelService
        );

        verifyNoInteractions(
                opcionalService,
                imagensService
        );
    }

    private void verifyVeiculosSemImagemEOpcionais(VeiculoTestContext cx) {
        verify(carroceriaService).buscaCarroceria(cx.request.idCarroceria());
        verify(coresService).buscaCor(cx.request.idCores());
        verify(modeloService).buscaModelo(cx.request.idModelo());
        verify(usuarioService).buscaUsuario(cx.request.idUsuario());
        verify(combustivelService).buscaCombustivel(cx.request.idCombustivel());
        verify(veiculoMapper).toResponse(cx.entity);

        verifyNoMoreInteractions(
                veiculoMapper,
                carroceriaService,
                coresService,
                modeloService,
                usuarioService,
                combustivelService,
                veiculoRepository
        );
    }

    private void verifyVeiculosSemImagemEOpcionaisExcecao(VeiculoTestContext cx) {
        verify(veiculoMapper).toEntity(cx.request);
        verify(carroceriaService).buscaCarroceria(cx.request.idCarroceria());
        verify(coresService).buscaCor(cx.request.idCores());
        verify(modeloService).buscaModelo(cx.request.idModelo());
        verify(usuarioService).buscaUsuario(cx.request.idUsuario());
        verify(combustivelService).buscaCombustivel(cx.request.idCombustivel());

        verifyNoMoreInteractions(
                veiculoMapper,
                carroceriaService,
                coresService,
                modeloService,
                usuarioService,
                combustivelService,
                veiculoRepository
        );
    }

}
