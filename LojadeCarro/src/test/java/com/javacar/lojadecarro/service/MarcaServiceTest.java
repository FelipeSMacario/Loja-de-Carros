package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.entity.Marca;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.marca.MarcaRequestFactory;
import com.javacar.lojadecarro.factory.marca.MarcaTestContext;
import com.javacar.lojadecarro.mapper.MarcaMapper;
import com.javacar.lojadecarro.repository.MarcaRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.MARCA;
import static com.javacar.lojadecarro.factory.helper.MarcaHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Testes da service da marca")
class MarcaServiceTest extends BaseServiceTest {

    @Mock
    private MarcaMapper marcaMapper;

    @Mock
    private MarcaRepository marcaRepository;

    @InjectMocks
    private MarcaService marcaService;


    @DisplayName("Testes da criação de marca")
    @Nested
    class Criar {
        @Test
        @DisplayName("Valida a criação da marca")
        void deveCriarMarcaComSucesso() {
            //Arrange
            var cx = new MarcaTestContext();
            var entity = criarMarcaPadrao();

            var response = criarMarcaResponse();

            when(marcaRepository.existsByNome(cx.request.nome())).thenReturn(false);
            when(marcaRepository.existsByUrl(cx.request.url())).thenReturn(false);
            when(marcaMapper.toEntity(cx.request)).thenReturn(entity);
            when(marcaRepository.save(entity)).thenReturn(entity);
            when(marcaMapper.toResponse(entity)).thenReturn(response);

            //ACT
            var resultado = marcaService.criar(cx.request);

            //Assert
            assertMarcaResponse(resultado);
            verify(marcaRepository).existsByNome(cx.request.nome());
            verify(marcaRepository).existsByUrl(cx.request.url());
            verify(marcaMapper).toEntity(cx.request);
            verify(marcaRepository).save(entity);
            verify(marcaMapper).toResponse(entity);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção de nome já utilizado")
        void deveLancarExcecaoNomeJaUtilizado() {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaRepository.existsByNome(cx.request.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class, () ->
                    marcaService.criar(cx.request));
            //Assert
            assertThat(exception)
                    .hasMessage(MARCA.nomeJaExistente());

            verify(marcaRepository).existsByNome(cx.request.nome());
            verifyNoMoreInteractions(marcaRepository);

            verifyNoInteractions(marcaMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção de URL já utilizado")
        void deveLancarExcecaoUrlJaUtilizado() {
            //Arrange
            var cx = new MarcaTestContext();

            when(marcaRepository.existsByNome(cx.request.nome()))
                    .thenReturn(false);

            when(marcaRepository.existsByUrl(cx.request.url()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class, () ->
                    marcaService.criar(cx.request));
            //Assert
            assertThat(exception)
                    .hasMessage("A URL informada já possui um cadastro.");

            verify(marcaRepository).existsByNome(cx.request.nome());
            verify(marcaRepository).existsByUrl(cx.request.url());
            verifyNoMoreInteractions(marcaRepository);

            verifyNoInteractions(marcaMapper);
        }
    }

    @DisplayName("Testes da listagem de marcas ADM")
    @Nested
    class Listar {
        @Test
        @DisplayName("Deve listar todas as marcas ativas")
        void deveListarTodasAsMarcasAtivas() {
            //Arrange
            var marca1 = criarMarcaPadrao();
            var marca2 = MarcaTestContext.criarMarca(2L, "Renault", "youtube.com", true);
            var entityList = List.of(marca1, marca2);
            var marcaResponse1 = criarMarcaResponsePadrao();
            var marcaResponse2 = MarcaTestContext.criaMarcaResponse(2L, "Renault", "youtube.com", true);

            when(marcaRepository.findByAtivo(true))
                    .thenReturn(entityList);

            when(marcaMapper.toResponse(marca1))
                    .thenReturn(marcaResponse1);

            when(marcaMapper.toResponse(marca2))
                    .thenReturn(marcaResponse2);

            //ACT
            var resultado = marcaService.listarAdministracao(StatusFiltro.ATIVAS);
            //Assert

            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            MarcaResponse::id,
                            MarcaResponse::nome,
                            MarcaResponse::url,
                            MarcaResponse::ativo
                    ).containsExactly(
                            Tuple.tuple(1L, "Ford", "https://www.ford.com", true),
                            Tuple.tuple(2L, "Renault", "youtube.com", true)
                    );

            verify(marcaRepository).findByAtivo(true);
            verify(marcaRepository, never()).findAll();
            verify(marcaMapper).toResponse(marca1);
            verify(marcaMapper).toResponse(marca2);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }

        @Test
        @DisplayName("Deve listar todas as marcas inativas")
        void deveListarAdministracaoTodasAsMarcasInativas() {
            //Arrange
            var marca1 = criarMarcaPadraoInativo();
            var marca2 = MarcaTestContext.criarMarca(2L, "Renault", "youtube.com", false);
            var entityList = List.of(marca1, marca2);


            var marcaResponse1 = criarMarcaResponsePadraoInativo();
            var marcaResponse2 = MarcaTestContext.criaMarcaResponse(2L, "Renault", "youtube.com", false);

            when(marcaRepository.findByAtivo(false))
                    .thenReturn(entityList);

            when(marcaMapper.toResponse(marca1))
                    .thenReturn(marcaResponse1);

            when(marcaMapper.toResponse(marca2))
                    .thenReturn(marcaResponse2);

            //ACT
            var resultado = marcaService.listarAdministracao(StatusFiltro.INATIVAS);
            //Assert

            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            MarcaResponse::id,
                            MarcaResponse::nome,
                            MarcaResponse::url,
                            MarcaResponse::ativo
                    ).containsExactly(
                            Tuple.tuple(1L, "Ford", "https://www.ford.com", false),
                            Tuple.tuple(2L, "Renault", "youtube.com", false)
                    );

            verify(marcaRepository).findByAtivo(false);
            verify(marcaRepository, never()).findAll();
            verify(marcaMapper).toResponse(marca1);
            verify(marcaMapper).toResponse(marca2);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }

        @Test
        @DisplayName("Deve listar todas as marcas")
        void deveListarAdministracaoTodasAsMarcas() {
            //Arrange
            var marca1 = criarMarcaPadrao();
            var marca2 = MarcaTestContext.criarMarca(2L, "Renault", "youtube.com", false);
            var entityList = List.of(marca1, marca2);
            var marcaResponse1 = criarMarcaResponsePadrao();
            var marcaResponse2 = MarcaTestContext.criaMarcaResponse(2L, "Renault", "youtube.com", false);

            when(marcaRepository.findAll())
                    .thenReturn(entityList);

            when(marcaMapper.toResponse(marca1))
                    .thenReturn(marcaResponse1);

            when(marcaMapper.toResponse(marca2))
                    .thenReturn(marcaResponse2);

            //ACT
            var resultado = marcaService.listarAdministracao(StatusFiltro.TODAS);
            //Assert

            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            MarcaResponse::id,
                            MarcaResponse::nome,
                            MarcaResponse::url,
                            MarcaResponse::ativo
                    ).containsExactly(
                            Tuple.tuple(1L, "Ford", "https://www.ford.com", true),
                            Tuple.tuple(2L, "Renault", "youtube.com", false)
                    );

            verify(marcaRepository, never()).findByAtivo(anyBoolean());
            verify(marcaRepository).findAll();
            verify(marcaMapper).toResponse(marca1);
            verify(marcaMapper).toResponse(marca2);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }
    }

    @DisplayName("Testes da listagem de marcas ativas")
    @Nested
    class ListarAtivas {
        @Test
        @DisplayName("Deve listar todas as marcas ativas")
        void deveListarTodasAsMarcasAtivas() {
            //Arrange
            var marca1 = criarMarcaPadrao();
            var marca2 = MarcaTestContext.criarMarca(2L, "Renault", "youtube.com", true);
            var entityList = List.of(marca1, marca2);
            var marcaResponse1 = criarMarcaResponsePadrao();
            var marcaResponse2 = MarcaTestContext.criaMarcaResponse(2L, "Renault", "youtube.com", true);

            when(marcaRepository.findByAtivo(true))
                    .thenReturn(entityList);

            when(marcaMapper.toResponse(marca1))
                    .thenReturn(marcaResponse1);

            when(marcaMapper.toResponse(marca2))
                    .thenReturn(marcaResponse2);

            //ACT
            var resultado = marcaService.listarMarcasAtivas();
            //Assert

            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            MarcaResponse::id,
                            MarcaResponse::nome,
                            MarcaResponse::url,
                            MarcaResponse::ativo
                    ).containsExactly(
                            Tuple.tuple(1L, "Ford", "https://www.ford.com", true),
                            Tuple.tuple(2L, "Renault", "youtube.com", true)
                    );

            verify(marcaRepository).findByAtivo(true);
            verify(marcaMapper).toResponse(marca1);
            verify(marcaMapper).toResponse(marca2);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }
    }

    @DisplayName("Testes da busca de marca ADM")
    @Nested
    class BuscarMarcaADM {
        @Test
        @DisplayName("Deve buscar a marca")
        void deveBuscarMarcaPorID() {
            // Arrange
            var cx = new MarcaTestContext();
            var entity = criarMarcaPadrao();

            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaMapper.toResponse(entity))
                    .thenReturn(cx.response);

            // Act
            var resultado = marcaService.buscarPorIdAdministracao(ID_VALIDO);

            // Assert

            assertMarcaResponse(resultado);

            verify(marcaRepository).findById(ID_VALIDO);
            verify(marcaMapper).toResponse(entity);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }

        @Test
        @DisplayName("Deve buscar a marca inativa")
        void deveBuscarMarcaInativa() {
            // Arrange
            var entity = criarMarcaPadraoInativo();
            var response = criarMarcaResponsePadraoInativo();

            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaMapper.toResponse(entity))
                    .thenReturn(response);

            // Act
            var resultado = marcaService.buscarPorIdAdministracao(ID_VALIDO);

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            MarcaResponse::id,
                            MarcaResponse::nome,
                            MarcaResponse::url,
                            MarcaResponse::ativo
                    ).containsExactly(
                            ID_VALIDO,
                            "Ford",
                            "https://www.ford.com",
                            false
                    );
            verify(marcaRepository).findById(ID_VALIDO);
            verify(marcaMapper).toResponse(entity);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar marca")
        void deveLancarExcecaoAoBuscarMarca() {
            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(
                    NotFoundException.class,
                    () -> marcaService.buscarPorIdAdministracao(ID_VALIDO)
            );

            assertNotFoundResponseError(exception, MARCA, ID_VALIDO);

            verify(marcaRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(marcaRepository);

            verifyNoInteractions(marcaMapper);

        }
    }

    @DisplayName("Testes da busca de marca ativa")
    @Nested
    class BuscarMarcaAtiva {
        @Test
        @DisplayName("Deve buscar marca ativa")
        void deveBuscarMarcaAtiva() {
            //Arrange
            var cx = new MarcaTestContext();
            var entity = criarMarcaPadrao();
            when(marcaRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaMapper.toResponse(entity))
                    .thenReturn(cx.response);
            //ACT
            var resultado = marcaService.buscarMarcaAtivaPorId(ID_VALIDO);
            //Assert
            assertMarcaResponse(resultado);

            verify(marcaRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(marcaMapper).toResponse(entity);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar marca inativa")
        void deveLancarExcecaoAoBuscarMarcaInativa() {
            //Arrange
            when(marcaRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> marcaService.buscarMarcaAtivaPorId(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, MARCA, ID_VALIDO);

            verify(marcaRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verifyNoMoreInteractions(marcaRepository);

            verifyNoInteractions(marcaMapper);
        }
    }

    @DisplayName("Testes da atualização da marca")
    @Nested
    class Atualiza {
        @Test
        @DisplayName("Deve atualizar a marca")
        void deveAtualizarMarca() {
            // Arrange
            var cx = new MarcaTestContext();
            var entity = criarMarcaPadrao();
            entity.setNome("Chevrolet");
            entity.setUrl("Chevrolet.com.br");

            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaRepository.existsByNome(cx.request.nome()))
                    .thenReturn(false);

            when(marcaRepository.existsByUrl(cx.request.url()))
                    .thenReturn(false);

            when(marcaMapper.toResponse(entity))
                    .thenReturn(cx.response);

            // Act
            var resultado = marcaService.atualizar(cx.request, ID_VALIDO);

            // Assert
            assertMarcaResponse(resultado);

            verify(marcaRepository).findById(ID_VALIDO);
            verify(marcaRepository).existsByNome(cx.request.nome());
            verify(marcaRepository).existsByUrl(cx.request.url());
            verify(marcaMapper).toUpdate(cx.request, entity);
            verify(marcaMapper).toResponse(entity);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }

        @Test
        @DisplayName("Deve atualizar a marca inativa")
        void deveAtualizarMarcaInativa() {
            // Arrange
            var cx = new MarcaTestContext();
            var entity = criarMarcaPadraoInativo();
            var response = criarMarcaResponsePadraoInativo();
            entity.setNome("Chevrolet");
            entity.setUrl("Chevrolet.com.br");

            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaRepository.existsByNome(cx.request.nome()))
                    .thenReturn(false);

            when(marcaRepository.existsByUrl(cx.request.url()))
                    .thenReturn(false);

            when(marcaMapper.toResponse(entity))
                    .thenReturn(response);

            // Act
            var resultado = marcaService.atualizar(cx.request, ID_VALIDO);

            // Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            MarcaResponse::id,
                            MarcaResponse::nome,
                            MarcaResponse::url,
                            MarcaResponse::ativo
                    ).containsExactly(
                            ID_VALIDO,
                            "Ford",
                            "https://www.ford.com",
                            false
                    );

            verify(marcaRepository).findById(ID_VALIDO);
            verify(marcaRepository).existsByNome(cx.request.nome());
            verify(marcaRepository).existsByUrl(cx.request.url());
            verify(marcaMapper).toUpdate(cx.request, entity);
            verify(marcaMapper).toResponse(entity);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }

        @Test
        @DisplayName("Deve atualizar o nome da marca")
        void deveAtualizarMarcaNomeDaMarca() {
            //Arrange
            var cx = new MarcaTestContext();
            var entity = criarMarcaPadrao();
            entity.setNome("Chevrolet");

            var response = MarcaTestContext.
                    criaMarcaResponse(ID_VALIDO, "Chevrolet", "https://www.ford.com", true);

            when(marcaRepository.findById(ID_VALIDO))
            .thenReturn(Optional.of(entity));

            when(marcaRepository.existsByNome(cx.request.nome()))
            .thenReturn(false);

            when(marcaMapper.toResponse(entity))
            .thenReturn(response);
            //ACT
            var resultado = marcaService.atualizar(cx.request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            MarcaResponse::id,
                            MarcaResponse::nome,
                            MarcaResponse::url,
                            MarcaResponse::ativo
                    ).containsExactly(
                            ID_VALIDO,
                    "Chevrolet",
                            "https://www.ford.com",
                            true
                    );

            verify(marcaRepository).findById(ID_VALIDO);
            verify(marcaRepository).existsByNome(cx.request.nome());
            verify(marcaRepository, never()).existsByUrl(cx.request.url());
            verify(marcaMapper).toUpdate(cx.request, entity);
            verify(marcaMapper).toResponse(entity);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }

        @Test
        @DisplayName("Deve atualizar a URL da marca")
        void deveAtualizarUrlDaMarca() {
            //Arrange
            var cx = new MarcaTestContext();
            var entity = criarMarcaPadrao();
            entity.setUrl("chevrolet.com.br");

            var response = MarcaTestContext.
                    criaMarcaResponse(ID_VALIDO, "ford", "chevrolet.com.br", true);

            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaRepository.existsByUrl(cx.request.url()))
                    .thenReturn(false);

            when(marcaMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = marcaService.atualizar(cx.request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            MarcaResponse::id,
                            MarcaResponse::nome,
                            MarcaResponse::url,
                            MarcaResponse::ativo
                    ).containsExactly(
                            ID_VALIDO,
                            "ford",
                            "chevrolet.com.br",
                            true
                    );

            verify(marcaRepository).findById(ID_VALIDO);
            verify(marcaRepository, never()).existsByNome(cx.request.nome());
            verify(marcaRepository).existsByUrl(cx.request.url());
            verify(marcaMapper).toUpdate(cx.request, entity);
            verify(marcaMapper).toResponse(entity);

            verifyNoMoreInteractions(marcaMapper, marcaRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção de nome já existente")
        void deveLancarExcecaoNomeJaExistente() {
            //Arrange
            var cx = new MarcaTestContext();
            var entity = criarMarcaPadrao();
            entity.setNome("Chevrolet");

            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaRepository.existsByNome(cx.request.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class, () ->
                    marcaService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage(MARCA.nomeJaExistente());

            verify(marcaRepository).findById(ID_VALIDO);
            verify(marcaRepository).existsByNome(cx.request.nome());
            verifyNoMoreInteractions(marcaRepository);

            verifyNoInteractions(marcaMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção de URL já existente")
        void deveLancarExcecaoUrlJaExistente() {
            //Arrange
            var cx = new MarcaTestContext();
            var entity = criarMarcaPadrao();
            entity.setNome("chevrolet");
            entity.setUrl("chevrolet.com.br");

            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaRepository.existsByNome(cx.request.nome()))
                    .thenReturn(false);

            when(marcaRepository.existsByUrl(cx.request.url()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class, () ->
                    marcaService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("A URL informada já possui um cadastro.");

            verify(marcaRepository).findById(ID_VALIDO);
            verify(marcaRepository).existsByNome(cx.request.nome());
            verify(marcaRepository).existsByUrl(cx.request.url());
            verifyNoMoreInteractions(marcaRepository);

            verifyNoInteractions(marcaMapper);
        }

        @Test
        @DisplayName("Deve lançar a exceção durante a atualização da marca")
        void deveLancarExcecaoAoAtualizarMarca() {
            var request = MarcaRequestFactory.criarRequest()
                    .comTodosOsCampos()
                    .build();

            when(marcaRepository.findById(ID_INVALIDO))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(
                    NotFoundException.class,
                    () -> marcaService.atualizar(request, ID_INVALIDO)
            );

            assertNotFoundResponseError(exception, MARCA, ID_INVALIDO);

            verify(marcaRepository).findById(ID_INVALIDO);

            verifyNoMoreInteractions(marcaRepository);

            verifyNoInteractions(marcaMapper);
        }
    }

    @DisplayName("Testes da alteração de status da marca")
    @Nested
    class AlterarStatus {
        @Test
        @DisplayName("Deve inativar a marca")
        void deveInativarAMarca() {
            //Arrange
            var entity = criarMarcaPadrao();
            var request = new StatusRequest(false);
            var response = criarMarcaResponsePadraoInativo();
            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = marcaService.alterarStatus(ID_VALIDO, request);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo()).isFalse();
            assertThat(entity.isAtivo()).isFalse();

            verify(marcaRepository).findById(ID_VALIDO);
            verify(marcaMapper).toResponse(entity);

            verifyNoMoreInteractions(marcaRepository, marcaMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o status para inativo")
        void deveLancarExcecaoAoAlterarMarcaInativo() {
            //Arrange
            var entity = criarMarcaPadraoInativo();
            var request = new StatusRequest(false);

            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> marcaService.alterarStatus(ID_VALIDO, request));

            //Assert
            assertBusinessResponseErrorInativa(exception, MARCA);

            assertThat(entity.isAtivo()).isFalse();

            verify(marcaRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(marcaRepository);

            verifyNoInteractions(marcaMapper);
        }

        @Test
        @DisplayName("Deve ativar a marca")
        void deveAtivarAMarca() {
            //Arrange
            var entity = criarMarcaPadraoInativo();
            var request = new StatusRequest(true);
            var response = criarMarcaResponsePadrao();
            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(marcaMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = marcaService.alterarStatus(ID_VALIDO, request);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo()).isTrue();
            assertThat(entity.isAtivo()).isTrue();

            verify(marcaRepository).findById(ID_VALIDO);
            verify(marcaMapper).toResponse(entity);

            verifyNoMoreInteractions(marcaRepository, marcaMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o status para ativo")
        void deveLancarExcecaoAoAlterarMarcaAtivo() {
            //Arrange
            var entity = criarMarcaPadrao();
            var request = new StatusRequest(true);

            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> marcaService.alterarStatus(ID_VALIDO, request));

            //Assert
            assertBusinessResponseError(exception, MARCA);

            assertThat(entity.isAtivo()).isTrue();

            verify(marcaRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(marcaRepository);

            verifyNoInteractions(marcaMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção de marca nao encontrada")
        void deveLancarExcecaoMarcaNaoEncontrada() {
            //Arrange
            var request = new StatusRequest(true);
            when(marcaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //Assert
            var exception = assertThrows(NotFoundException.class,
                    () -> marcaService.alterarStatus(ID_VALIDO, request));

            assertNotFoundResponseError(exception, MARCA, ID_VALIDO);

            verify(marcaRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(marcaRepository);
            verifyNoInteractions(marcaMapper);
        }
    }


    private Marca criarMarcaPadrao() {
        return MarcaTestContext.criarMarca(ID_VALIDO, "Ford", "https://www.ford.com", true);
    }

    private Marca criarMarcaPadraoInativo() {
        return MarcaTestContext.criarMarca(ID_VALIDO, "Ford", "https://www.ford.com", false);
    }

    private MarcaResponse criarMarcaResponsePadrao() {
        return MarcaTestContext.criaMarcaResponse(ID_VALIDO, "Ford", "https://www.ford.com", true);
    }

    private MarcaResponse criarMarcaResponsePadraoInativo() {
        return MarcaTestContext.criaMarcaResponse(ID_VALIDO, "Ford", "https://www.ford.com", false);
    }
}
