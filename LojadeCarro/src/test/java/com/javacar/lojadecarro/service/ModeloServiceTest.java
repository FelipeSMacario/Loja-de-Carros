package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.marca.MarcaTestContext;
import com.javacar.lojadecarro.factory.modelo.ModeloResponseFactory;
import com.javacar.lojadecarro.factory.modelo.ModeloTestContext;
import com.javacar.lojadecarro.mapper.ModeloMapper;
import com.javacar.lojadecarro.repository.ModeloRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.MARCA;
import static com.javacar.lojadecarro.enums.Entidade.MODELO;
import static com.javacar.lojadecarro.factory.helper.ModeloHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ModeloServiceTest extends BaseServiceTest {

    @Mock
    private ModeloMapper modeloMapper;
    @Mock
    private MarcaService marcaService;
    @Mock
    private ModeloRepository modeloRepository;
    @InjectMocks
    private ModeloService modeloService;

    @DisplayName("Testes de criação do modelo")
    @Nested
    class Criar {
        @Test
        @DisplayName("Deve cadastrar um modelo")
        void deveCadastrarModelo() {
            //Arrange
            var cx = new ModeloTestContext();
            var entity = criarModeloPadrao();

            when(modeloRepository.existsByNome(cx.request.nome()))
                    .thenReturn(false);

            when(marcaService.buscaMarcaAtiva(cx.request.idMarca()))
                    .thenReturn(entity.getMarca());

            when(modeloMapper.toEntity(cx.request))
                    .thenReturn(entity);

            when(modeloRepository.save(entity))
                    .thenReturn(entity);

            when(modeloMapper.toResponse(entity))
                    .thenReturn(cx.response);
            //Act
            var resultado = modeloService.criar(cx.request);
            //Assert

            assertModeloResponse(resultado);

            verify(modeloRepository).existsByNome(cx.request.nome());
            verify(modeloMapper).toEntity(cx.request);
            verify(marcaService).buscaMarcaAtiva(cx.request.idMarca());
            verify(modeloRepository).save(entity);
            verify(modeloMapper).toResponse(entity);

            verifyNoMoreInteractions(modeloMapper, marcaService, modeloRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção de nome unico")
        void deveLancarExcecaoNomeUnico() {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloRepository.existsByNome(cx.request.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class, () ->
                    modeloService.criar(cx.request));
            //Assert
            assertThat(exception)
                    .hasMessage(MODELO.nomeJaExistente());

            verify(modeloRepository).existsByNome(cx.request.nome());
            verifyNoMoreInteractions(modeloRepository);

            verifyNoInteractions(marcaService, modeloMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar marca inativa")
        void deveLancarExcecaoMarcaInativa() {
            //Arrange
            var cx = new ModeloTestContext();

            when(modeloRepository.existsByNome(cx.request.nome()))
                    .thenReturn(false);

            when(marcaService.buscaMarcaAtiva(cx.request.idMarca()))
                    .thenThrow(new NotFoundException(MARCA, cx.request.idMarca()));

            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> modeloService.criar(cx.request));
            //Assert
            assertNotFoundResponseError(excecao, MARCA, cx.request.idMarca());

            verify(modeloRepository).existsByNome(cx.request.nome());
            verify(marcaService).buscaMarcaAtiva(cx.request.idMarca());

            verifyNoMoreInteractions(modeloRepository, marcaService);

            verifyNoInteractions(modeloMapper);

        }
    }

    @DisplayName("Testes da listagem de modelos ADM")
    @Nested
    class ListarADM {
        @Test
        @DisplayName("Deve listar os modelos ativos")
        void deveListarModelosAtivos() {
            //Arrange
            var entity1 = criarModeloPadrao();
            var entity2 = ModeloTestContext.criaModelo(2L, "Celta", true);
            var entity = List.of(entity1, entity2);

            var response1 = criarModeloResponsePadrao();
            var response2 = ModeloTestContext.criaModeloResponse(2L, "Celta", true);

            when(modeloRepository.findByAtivo(true))
                    .thenReturn(entity);

            when(modeloMapper.toResponse(entity1))
                    .thenReturn(response1);
            when(modeloMapper.toResponse(entity2))
                    .thenReturn(response2);
            //Act
            var resultado = modeloService.listarAdministracao(StatusFiltro.ATIVAS);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            ModeloResponse::id,
                            ModeloResponse::nome,
                            ModeloResponse::ativo
                    ).containsExactly(
                            tuple(ID_VALIDO, "Onix", true),
                            tuple(2L, "Celta", true)
                    );

            verify(modeloRepository).findByAtivo(true);
            verify(modeloRepository, never()).findAll();
            verify(modeloMapper).toResponse(entity1);
            verify(modeloMapper).toResponse(entity2);

            verifyNoMoreInteractions(modeloMapper, modeloRepository);
        }

        @Test
        @DisplayName("Deve listar todos os modelos inativos")
        void deveListarModelosInativos() {
            //Arrange
            var entity1 = criarModeloPadraoInativo();
            var entity2 = ModeloTestContext.criaModelo(2L, "Celta", false);
            var entity = List.of(entity1, entity2);

            var response1 = criarModeloResponsePadraoInativo();
            var response2 = ModeloTestContext.criaModeloResponse(2L, "Celta", false);

            when(modeloRepository.findByAtivo(false))
                    .thenReturn(entity);

            when(modeloMapper.toResponse(entity1))
                    .thenReturn(response1);
            when(modeloMapper.toResponse(entity2))
                    .thenReturn(response2);
            //Act
            var resultado = modeloService.listarAdministracao(StatusFiltro.INATIVAS);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            ModeloResponse::id,
                            ModeloResponse::nome,
                            ModeloResponse::ativo
                    ).containsExactly(
                            tuple(ID_VALIDO, "Onix", false),
                            tuple(2L, "Celta", false)
                    );

            verify(modeloRepository, never()).findAll();
            verify(modeloRepository).findByAtivo(false);
            verify(modeloMapper).toResponse(entity1);
            verify(modeloMapper).toResponse(entity2);

            verifyNoMoreInteractions(modeloMapper, modeloRepository);
        }

        @Test
        @DisplayName("Deve listar todos os modelos")
        void deveListarModelos() {
            //Arrange
            var entity1 = criarModeloPadrao();
            var entity2 = ModeloTestContext.criaModelo(2L, "Celta", false);

            var entity = List.of(entity1, entity2);

            var response1 = criarModeloResponsePadrao();
            var response2 = ModeloTestContext.criaModeloResponse(2L, "Celta", false);

            when(modeloRepository.findAll())
                    .thenReturn(entity);

            when(modeloMapper.toResponse(entity1))
                    .thenReturn(response1);
            when(modeloMapper.toResponse(entity2))
                    .thenReturn(response2);
            //Act
            var resultado = modeloService.listarAdministracao(StatusFiltro.TODAS);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            ModeloResponse::id,
                            ModeloResponse::nome,
                            ModeloResponse::ativo
                    ).containsExactly(
                            tuple(ID_VALIDO, "Onix", true),
                            tuple(2L, "Celta", false)
                    );

            verify(modeloRepository).findAll();
            verify(modeloRepository, never()).findByAtivo(anyBoolean());
            verify(modeloMapper).toResponse(entity1);
            verify(modeloMapper).toResponse(entity2);

            verifyNoMoreInteractions(modeloMapper, modeloRepository);
        }

    }

    @DisplayName("Testes da listagem de modelos ativos")
    @Nested
    class ListarAtivos {
        @Test
        @DisplayName("Deve listar os modelos ativos")
        void deveListarModelosAtivos() {
            //Arrange
            var entity1 = criarModeloPadrao();
            var entity2 = ModeloTestContext.criaModelo(2L, "Celta", true);
            var entity = List.of(entity1, entity2);

            var response1 = criarModeloResponsePadrao();
            var response2 = ModeloTestContext.criaModeloResponse(2L, "Celta", true);

            when(modeloRepository.findByAtivoTrueAndMarca_AtivoTrue())
                    .thenReturn(entity);

            when(modeloMapper.toResponse(entity1))
                    .thenReturn(response1);
            when(modeloMapper.toResponse(entity2))
                    .thenReturn(response2);
            //Act
            var resultado = modeloService.listarModelosAtivos();
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            ModeloResponse::id,
                            ModeloResponse::nome,
                            ModeloResponse::ativo
                    ).containsExactly(
                            tuple(ID_VALIDO, "Onix", true),
                            tuple(2L, "Celta", true)
                    );

            verify(modeloRepository).findByAtivoTrueAndMarca_AtivoTrue();
            verify(modeloMapper).toResponse(entity1);
            verify(modeloMapper).toResponse(entity2);

            verifyNoMoreInteractions(modeloMapper, modeloRepository);
        }
    }

    @DisplayName("Testes da busca do modelo ADM")
    @Nested
    class BuscarModeloADM {
        @Test
        @DisplayName("Deve buscar um modelo")
        void deveBuscarModelo() {
            //Arrange
            var cx = new ModeloTestContext();
            var entity = criarModeloPadrao();

            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            when(modeloMapper.toResponse(entity))
                    .thenReturn(cx.response);
            //Act
            var resultado = modeloService.buscarPorIdAdministracao(ID_VALIDO);
            //Assert
            assertModeloResponse(resultado);

            verify(modeloRepository).findById(ID_VALIDO);
            verify(modeloMapper).toResponse(entity);

            verifyNoMoreInteractions(modeloMapper, modeloRepository);
        }

        @Test
        @DisplayName("Deve buscar um modelo inativo")
        void deveBuscarModeloInativo() {
            //Arrange
            var entity = criarModeloPadraoInativo();
            var response = criarModeloResponsePadraoInativo();
            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(modeloMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = modeloService.buscarPorIdAdministracao(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            ModeloResponse::id,
                            ModeloResponse::nome,
                            ModeloResponse::ativo
                    ).containsExactly(
                            ID_VALIDO,
                            "Onix",
                            false
                    );
            verify(modeloRepository).findById(ID_VALIDO);
            verify(modeloMapper).toResponse(entity);
            verifyNoMoreInteractions(modeloMapper, modeloRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar um modelo")
        void deveLancarExcecaoAoBuscarModelo() {
            //Arrange
            when(modeloRepository.findById(ID_INVALIDO))
                    .thenReturn(Optional.empty());
            //Act
            var exception = assertThrows(NotFoundException.class,
                    () -> modeloService.buscarPorIdAdministracao(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(exception, MODELO, ID_INVALIDO);

            verify(modeloRepository).findById(ID_INVALIDO);

            verifyNoMoreInteractions(modeloRepository);

            verifyNoInteractions(modeloMapper);
        }
    }

    @DisplayName("Testes da busca de modelos ativos")
    @Nested
    class BuscarModelosAtivos {
        @Test
        @DisplayName("Deve buscar modelos ativos")
        void deveBuscarModelosAtivos() {
            //Arrange
            var cx = new ModeloTestContext();
            var entity = criarModeloPadrao();

            when(modeloRepository.findByIdAndAtivoTrueAndMarca_AtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(modeloMapper.toResponse(entity))
                    .thenReturn(cx.response);
            //ACT
            var resultado = modeloService.buscarModeloAtivoPorId(ID_VALIDO);
            //Assert
            assertModeloResponse(resultado);

            verify(modeloRepository).findByIdAndAtivoTrueAndMarca_AtivoTrue(ID_VALIDO);
            verify(modeloMapper).toResponse(entity);

            verifyNoMoreInteractions(modeloMapper, modeloRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar modelo inativo")
        void deveLancarExcecaoAoBuscarModelo() {
            //Arrange
            when(modeloRepository.findByIdAndAtivoTrueAndMarca_AtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> modeloService.buscarModeloAtivoPorId(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, MODELO, ID_VALIDO);

            verify(modeloRepository).findByIdAndAtivoTrueAndMarca_AtivoTrue(ID_VALIDO);
            verifyNoMoreInteractions(modeloRepository);

            verifyNoInteractions(modeloMapper);
        }
    }

    @DisplayName("Testes da atualização do modelo")
    @Nested
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o modelo")
        void deveAtualizarModelo() {
            //Arrange
            var entity = criarModeloPadrao();
            var request = ModeloTestContext.criaModeloRequest("Palio", 2L);
            var marcaResponse = MarcaTestContext.criaMarcaResponse(2L, "Fiat", "fiat.com", true);
            var novaMarca = MarcaTestContext.criarMarca(
                    2L,
                    "Fiat",
                    "fiat.com",
                    true
            );
            var response = ModeloResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comNome(request.nome())
                    .comMarca(marcaResponse)
                    .build();

            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(modeloRepository.existsByNome(request.nome()))
                    .thenReturn(false);

            when(marcaService.buscaMarcaAtiva(request.idMarca()))
                    .thenReturn(novaMarca);

            when(modeloMapper.toResponse(entity))
                    .thenReturn(response);
            //Act
            var resultado = modeloService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            ModeloResponse::id,
                            ModeloResponse::nome,
                            ModeloResponse::ativo
                    ).containsExactly(
                            ID_VALIDO,
                            request.nome(),
                            true
                    );

            verify(modeloRepository).findById(ID_VALIDO);
            verify(modeloRepository).existsByNome(request.nome());
            verify(marcaService).buscaMarcaAtiva(request.idMarca());
            verify(modeloMapper).toUpdate(request, entity);
            verify(modeloMapper).toResponse(entity);

            verifyNoMoreInteractions(modeloMapper, marcaService, modeloRepository);
        }

        @Test
        @DisplayName("Deve atualizar um modelo inativo")
        void deveAtualizarModeloInativo() {
            //Arrange
            var entity = criarModeloPadraoInativo();
            var request = ModeloTestContext.criaModeloRequest("Palio", 2L);
            var marcaResponse = MarcaTestContext.criaMarcaResponse(2L, "Fiat", "fiat.com", true);
            var marcaEntity = MarcaTestContext.criarMarca(2L, "Fiat", "fiat.com", true);
            var response = ModeloResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comNome(request.nome())
                    .comMarca(marcaResponse)
                    .comAtivo(false)
                    .build();

            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(modeloRepository.existsByNome(request.nome()))
                    .thenReturn(false);

            when(marcaService.buscaMarcaAtiva(request.idMarca()))
                    .thenReturn(marcaEntity);

            when(modeloMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = modeloService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            ModeloResponse::id,
                            ModeloResponse::nome,
                            ModeloResponse::ativo
                    ).containsExactly(
                            ID_VALIDO,
                            request.nome(),
                            false
                    );

            verify(modeloRepository).findById(ID_VALIDO);
            verify(modeloRepository).existsByNome(request.nome());
            verify(marcaService).buscaMarcaAtiva(request.idMarca());
            verify(modeloMapper).toUpdate(request, entity);
            verify(modeloMapper).toResponse(entity);

            verifyNoMoreInteractions(modeloMapper, marcaService, modeloRepository);
        }

        @Test
        @DisplayName("Deve atualizar o nome do modelo")
        void deveAtualizarNomeDoModelo() {
            //Arrange
            var entity = criarModeloPadrao();
            var request = ModeloTestContext.criaModeloRequest("Palio", 1L);
            var response = ModeloResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comNome(request.nome())
                    .build();

            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(modeloRepository.existsByNome(request.nome()))
                    .thenReturn(false);

            when(modeloMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = modeloService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            ModeloResponse::id,
                            ModeloResponse::nome,
                            ModeloResponse::ativo
                    ).containsExactly(
                            ID_VALIDO,
                            request.nome(),
                            true
                    );
            verify(modeloRepository).findById(ID_VALIDO);
            verify(modeloRepository).existsByNome(request.nome());
            verify(marcaService, never()).buscaMarcaAtiva(anyLong());
            verify(modeloMapper).toUpdate(request, entity);
            verify(modeloMapper).toResponse(entity);

            verifyNoMoreInteractions(modeloMapper, marcaService, modeloRepository);
        }

        @Test
        @DisplayName("Deve atualizar a marca do modelo")
        void deveAtualizarMarcaDoModelo() {
            //Arrange
            var entity = criarModeloPadrao();
            var request = ModeloTestContext.criaModeloRequest("Onix", 2L);
            var marcaResponse = MarcaTestContext.criaMarcaResponse(2L, "Fiat", "fiat.com", true);
            var marcaEntity = MarcaTestContext.criarMarca(2L, "Fiat", "fiat.com", true);
            var response = ModeloResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comNome(request.nome())
                    .comMarca(marcaResponse)
                    .comAtivo(true)
                    .build();

            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaService.buscaMarcaAtiva(request.idMarca()))
                    .thenReturn(marcaEntity);

            when(modeloMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = modeloService.atualizar(request, ID_VALIDO);
            //Assert
            assertThat(resultado.marcaResponse())
                    .isNotNull()
                    .extracting(
                            MarcaResponse::id,
                            MarcaResponse::nome,
                            MarcaResponse::url,
                            MarcaResponse::ativo
                    ).containsExactly(
                            request.idMarca(),
                            "Fiat",
                            "fiat.com",
                            true
                    );

            verify(modeloRepository).findById(ID_VALIDO);
            verify(modeloRepository, never()).existsByNome(anyString());
            verify(marcaService).buscaMarcaAtiva(request.idMarca());
            verify(modeloMapper).toUpdate(request, entity);
            verify(modeloMapper).toResponse(entity);

            verifyNoMoreInteractions(modeloMapper, marcaService, modeloRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar o modelo")
        void deveLancarExcecaoAoBuscarModelo() {
            //Arrange
            var request = criarModeloRequest();

            when(modeloRepository.findById(ID_INVALIDO))
                    .thenReturn(Optional.empty());
            //Act
            var exception = assertThrows(
                    NotFoundException.class,
                    () -> modeloService.atualizar(request, ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(exception, MODELO, ID_INVALIDO);

            verify(modeloRepository).findById(ID_INVALIDO);

            verifyNoMoreInteractions(modeloRepository);

            verifyNoInteractions(modeloMapper, marcaService);
        }

        @Test
        @DisplayName("Deve lançar ao atualizar modelo com nome já existente")
        void deveLancarExcecaoAtualizarModeloComNomeJaExistente() {
            //Arrange
            var request = ModeloTestContext.criaModeloRequest("Palio", 1L);
            var entity = criarModeloPadraoInativo();

            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(modeloRepository.existsByNome(request.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> modeloService.atualizar(request, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage(MODELO.nomeJaExistente());

            verify(modeloRepository).findById(ID_VALIDO);
            verify(modeloRepository).existsByNome(request.nome());
            verifyNoMoreInteractions(modeloRepository);

            verifyNoInteractions(modeloMapper, marcaService);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar atualizar um modelo com marca inativa")
        void deveLancarExcecaoAtualizarMarcaInativa() {
            //Arrange
            var entity = criarModeloPadrao();
            var request = ModeloTestContext.criaModeloRequest("Onix", 2L);

            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaService.buscaMarcaAtiva(request.idMarca()))
                    .thenThrow(new NotFoundException(MARCA, request.idMarca()));
            //Act
            var excecao = assertThrows(NotFoundException.class,
                    (() -> modeloService.atualizar(request, ID_VALIDO)));
            //Assert
            assertNotFoundResponseError(excecao, MARCA, request.idMarca());

            verify(modeloRepository).findById(ID_VALIDO);
            verify(marcaService).buscaMarcaAtiva(request.idMarca());

            verifyNoMoreInteractions(modeloRepository);
            verifyNoMoreInteractions(marcaService);
            verifyNoInteractions(modeloMapper);
        }
    }

    @DisplayName("Testes da alteração de status do modelo")
    @Nested
    class AlterarStatus {
        @Test
        @DisplayName("Deve inativar o modelo")
        void deveInativarOModelo() {
            //Arrange
            var entity = criarModeloPadrao();
            var request = new StatusRequest(false);
            var response = criarModeloResponsePadraoInativo();

            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(modeloMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = modeloService.alterarStatus(ID_VALIDO, request);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo()).isFalse();
            assertThat(entity.isAtivo()).isFalse();

            verify(modeloRepository).findById(ID_VALIDO);
            verify(modeloMapper).toResponse(entity);

            verifyNoMoreInteractions(modeloRepository, modeloMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o status para inativo")
        void deveLancarExcecaoAoAlterarStatusParaInativo() {
            //Arrange
            var entity = criarModeloPadraoInativo();
            var request = new StatusRequest(false);

            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> modeloService.alterarStatus(ID_VALIDO, request));

            //Assert
            assertBusinessResponseErrorInativa(exception, MODELO);

            assertThat(entity.isAtivo()).isFalse();

            verify(modeloRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(modeloRepository);

            verifyNoInteractions(modeloMapper);
        }

        @Test
        @DisplayName("Deve ativar o modelo")
        void deveAtivarOModelo() {
            //Arrange
            var entity = criarModeloPadraoInativo();
            var request = new StatusRequest(true);
            var response = criarModeloResponsePadrao();

            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(modeloMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = modeloService.alterarStatus(ID_VALIDO, request);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo()).isTrue();
            assertThat(entity.isAtivo()).isTrue();

            verify(modeloRepository).findById(ID_VALIDO);
            verify(modeloMapper).toResponse(entity);

            verifyNoMoreInteractions(modeloRepository, modeloMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o status para ativo")
        void deveLancarExcecaoAoAlterarStatusParaAtivo() {
            //Arrange
            var entity = criarModeloPadrao();
            var request = new StatusRequest(true);

            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> modeloService.alterarStatus(ID_VALIDO, request));

            //Assert
            assertBusinessResponseError(exception, MODELO);

            assertThat(entity.isAtivo()).isTrue();

            verify(modeloRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(modeloRepository);

            verifyNoInteractions(modeloMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar modelo")
        void deveLancarExcecaoAoBuscarModelo() {
            //Arrange
            var request = new StatusRequest(true);
            when(modeloRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //Assert
            var exception = assertThrows(NotFoundException.class,
                    () -> modeloService.alterarStatus(ID_VALIDO, request));

            assertNotFoundResponseError(exception, MODELO, ID_VALIDO);

            verify(modeloRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(modeloRepository);
            verifyNoInteractions(modeloMapper);
        }
    }


    private Modelo criarModeloPadrao() {
        return ModeloTestContext.criaModelo(ID_VALIDO, "Onix", true);
    }

    private Modelo criarModeloPadraoInativo() {
        return ModeloTestContext.criaModelo(ID_VALIDO, "Onix", false);
    }

    private ModeloResponse criarModeloResponsePadrao() {
        return ModeloTestContext.criaModeloResponse(ID_VALIDO, "Onix", true);
    }

    private ModeloResponse criarModeloResponsePadraoInativo() {
        return ModeloTestContext.criaModeloResponse(ID_VALIDO, "Onix", false);
    }
}
