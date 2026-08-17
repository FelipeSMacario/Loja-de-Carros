package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.opcional.OpcionalEntityFactory;
import com.javacar.lojadecarro.factory.opcional.OpcionalResponseFactory;
import com.javacar.lojadecarro.factory.opcional.OpcionalTestContext;
import com.javacar.lojadecarro.mapper.OpcionalMapper;
import com.javacar.lojadecarro.repository.OpcionalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;
import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Testes da service de opcionais")
class OpcionalServiceTest extends BaseServiceTest {

    @Mock
    private OpcionalRepository opcionalRepository;
    @Mock
    private OpcionalMapper opcionalMapper;
    @InjectMocks
    private OpcionalService opcionalService;

    @DisplayName("Testes da criação do opcional")
    @Nested
    class Criar {
        @Test
        @DisplayName("Deve cadastrar um opcional")
        void deveCadastrarUmOpcional() {
            //Arrange
            var cx = new OpcionalTestContext();
            var entity = criarOpcionalPadrao();

            when(opcionalRepository.existsByNome(cx.request.nome()))
                    .thenReturn(false);
            when(opcionalMapper.toEntity(cx.request))
                    .thenReturn(entity);

            when(opcionalRepository.save(entity))
                    .thenReturn(entity);

            when(opcionalMapper.toResponse(entity))
                    .thenReturn(cx.response);
            //Act
            var resultado = opcionalService.criar(cx.request);
            //Assert
            assertOpcionalResponse(resultado);

            verify(opcionalRepository).existsByNome(cx.request.nome());
            verify(opcionalMapper).toEntity(cx.request);
            verify(opcionalRepository).save(entity);
            verify(opcionalMapper).toResponse(entity);

            verifyNoMoreInteractions(
                    opcionalMapper,
                    opcionalRepository
            );
        }

        @Test
        @DisplayName("Deve lancar excecao de nome unico")
        void deveLancarExcecaoDeNomeUnico() {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalRepository.existsByNome(cx.request.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class, () ->
                    opcionalService.criar(cx.request));
            //Assert
            assertThat(exception)
                    .hasMessage(OPCIONAL.nomeJaExistente());

            verify(opcionalRepository).existsByNome(cx.request.nome());
            verifyNoMoreInteractions(opcionalRepository);

            verifyNoInteractions(opcionalMapper);
        }
    }


    @DisplayName("Testes da listagem de opcionais ADM")
    @Nested
    class ListarAdm {
        @Test
        @DisplayName("Deve listar os opcionais ativos")
        void deveListarOpcionaisAtivos() {
            //Arrange
            var cx = new OpcionalTestContext();
            var entity1 = criarOpcionalPadrao();
            var entity2 = OpcionalTestContext.criarOpcional(2L, "Banco de couro", true);

            var entity = List.of(entity1, entity2);

            var response2 = OpcionalResponseFactory
                    .criarResponse()
                    .comNome("Banco de couro")
                    .comId(2L)
                    .comAtivo(true)
                    .build();

            when(opcionalRepository.findByAtivo(true))
                    .thenReturn(entity);

            when(opcionalMapper.toResponse(entity1))
                    .thenReturn(cx.response);
            when(opcionalMapper.toResponse(entity2))
                    .thenReturn(response2);
            //Act
            var resultado = opcionalService.listarAdministracao(StatusFiltro.ATIVAS);
            //Assert

            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            OpcionalResponse::id,
                            OpcionalResponse::nome,
                            OpcionalResponse::ativo
                    )
                    .containsExactly(
                            tuple(1L, "Freio Abs", true),
                            tuple(2L, "Banco de couro", true)
                    );

            verify(opcionalRepository, never()).findAll();
            verify(opcionalRepository).findByAtivo(true);
            verify(opcionalMapper).toResponse(entity1);
            verify(opcionalMapper).toResponse(entity2);

            verifyNoMoreInteractions(opcionalRepository, opcionalMapper);

        }

        @Test
        @DisplayName("Deve listar os opcionais inativos")
        void deveListarOpcionaisInativos() {
            //Arrange
            var entity1 = criarOpcionalPadrao();
            entity1.setAtivo(false);
            var entity2 = OpcionalEntityFactory
                    .criarEntity()
                    .comNome("Banco de couro")
                    .comId(2L)
                    .comAtivo(false)
                    .build();
            var entity = List.of(entity1, entity2);

            var response1 = OpcionalTestContext.criaOpcionalResponse(ID_VALIDO, "Freio ABS", false);
            var response2 = OpcionalTestContext.criaOpcionalResponse(2L, "Banco de couro", false);

            when(opcionalRepository.findByAtivo(false))
                    .thenReturn(entity);

            when(opcionalMapper.toResponse(entity1))
                    .thenReturn(response1);
            when(opcionalMapper.toResponse(entity2))
                    .thenReturn(response2);
            //Act
            var resultado = opcionalService.listarAdministracao(StatusFiltro.INATIVAS);
            //Assert

            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            OpcionalResponse::id,
                            OpcionalResponse::nome,
                            OpcionalResponse::ativo
                    )
                    .containsExactly(
                            tuple(1L, "Freio ABS", false),
                            tuple(2L, "Banco de couro", false)
                    );

            verify(opcionalRepository, never()).findAll();
            verify(opcionalRepository).findByAtivo(false);
            verify(opcionalMapper).toResponse(entity1);
            verify(opcionalMapper).toResponse(entity2);

            verifyNoMoreInteractions(opcionalRepository, opcionalMapper);
        }

        @Test
        @DisplayName("Deve listar todos os opcionais")
        void deveListarTodosOpcionais() {
            //Arrange
            var cx = new OpcionalTestContext();
            var entity1 = criarOpcionalPadrao();
            var entity2 = OpcionalTestContext.criarOpcional(2L, "Banco de couro", false);
            var entity = List.of(entity1, entity2);

            var response2 = OpcionalResponseFactory
                    .criarResponse()
                    .comNome("Banco de couro")
                    .comId(2L)
                    .comAtivo(false)
                    .build();

            when(opcionalRepository.findAll())
                    .thenReturn(entity);

            when(opcionalMapper.toResponse(entity1))
                    .thenReturn(cx.response);
            when(opcionalMapper.toResponse(entity2))
                    .thenReturn(response2);
            //Act
            var resultado = opcionalService.listarAdministracao(StatusFiltro.TODAS);
            //Assert

            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            OpcionalResponse::id,
                            OpcionalResponse::nome,
                            OpcionalResponse::ativo
                    )
                    .containsExactly(
                            tuple(1L, "Freio Abs", true),
                            tuple(2L, "Banco de couro", false)
                    );

            verify(opcionalRepository).findAll();
            verify(opcionalRepository, never()).findByAtivo(anyBoolean());
            verify(opcionalMapper).toResponse(entity1);
            verify(opcionalMapper).toResponse(entity2);

            verifyNoMoreInteractions(opcionalRepository, opcionalMapper);
        }
    }

    @DisplayName("Testes da listagem de opcionais")
    @Nested
    class ListarOpcionais {
        @Test
        @DisplayName("Deve listar opcionais ativas")
        void deveListarOpcionaisAtivas() {
            //Arrange
            var cx = new OpcionalTestContext();
            var entity1 = criarOpcionalPadrao();
            var entity2 = OpcionalTestContext.criarOpcional(2L, "Banco de couro", true);
            var listEntity = List.of(entity1, entity2);
            var response2 = OpcionalTestContext.criaOpcionalResponse(2L, "Banco de couro", true);


            when(opcionalRepository.findByAtivo(true))
                    .thenReturn(listEntity);

            when(opcionalMapper.toResponse(entity1))
                    .thenReturn(cx.response);
            when(opcionalMapper.toResponse(entity2))
                    .thenReturn(response2);
            //ACT
            var resultado = opcionalService.listarOpcionaisAtivas();
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            OpcionalResponse::id,
                            OpcionalResponse::nome,
                            OpcionalResponse::ativo
                    ).containsExactly(
                            tuple(ID_VALIDO, "Freio Abs", true),
                            tuple(2L, "Banco de couro", true)
                    );
            verify(opcionalRepository).findByAtivo(true);
            verify(opcionalMapper).toResponse(entity1);
            verify(opcionalMapper).toResponse(entity2);

            verifyNoMoreInteractions(opcionalRepository, opcionalMapper);
        }
    }

    @DisplayName("Testes da busca de opcionais ADM")
    @Nested
    class BuscarOpcionalADM {
        @Test
        @DisplayName("Deve buscar um opcional")
        void deveBuscarOpcional() {
            //Arrange
            var cx = new OpcionalTestContext();
            var entity = criarOpcionalPadrao();

            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            when(opcionalMapper.toResponse(entity))
                    .thenReturn(cx.response);
            //Act
            var resultado = opcionalService.buscarPorIdAdministracao(ID_VALIDO);
            //Assert
            assertOpcionalResponse(resultado);

            verify(opcionalRepository).findById(ID_VALIDO);
            verify(opcionalMapper).toResponse(entity);

            verifyNoMoreInteractions(
                    opcionalRepository,
                    opcionalMapper
            );
        }

        @Test
        @DisplayName("Deve buscar um opcional inativo")
        void deveBuscarOpcionalInativo() {
            //Arrange
            var entity = OpcionalTestContext.criarOpcional(ID_VALIDO, "Freio Abs", false);
            var response = OpcionalTestContext.criaOpcionalResponse(ID_VALIDO, "Freio Abs", false);

            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            when(opcionalMapper.toResponse(entity))
                    .thenReturn(response);
            //Act
            var resultado = opcionalService.buscarPorIdAdministracao(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            OpcionalResponse::id,
                            OpcionalResponse::nome,
                            OpcionalResponse::ativo
                    ).containsExactly(
                            ID_VALIDO, "Freio Abs", false
                    );

            verify(opcionalRepository).findById(ID_VALIDO);
            verify(opcionalMapper).toResponse(entity);

            verifyNoMoreInteractions(
                    opcionalRepository,
                    opcionalMapper
            );
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar opcional")
        void deveLancarExcecaoAoBuscarOpcional() {
            //Arrange
            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //Act
            var exception = assertThrows(NotFoundException.class,
                    () -> opcionalService.buscarPorIdAdministracao(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, OPCIONAL, ID_VALIDO);

            verify(opcionalRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(opcionalRepository);

            verifyNoInteractions(opcionalMapper);
        }
    }

    @DisplayName("Testes da busca de opcional")
    @Nested
    class BuscarOpcional {
        @Test
        @DisplayName("Deve buscar opcional ativo")
        void deveBuscarOpcionalAtivo() {
            //Arrange
            var cx = new OpcionalTestContext();
            var entity = criarOpcionalPadrao();
            when(opcionalRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(opcionalMapper.toResponse(entity))
                    .thenReturn(cx.response);
            //ACT
            var resultado = opcionalService.buscarOpcionalAtivoPorId(ID_VALIDO);
            //Assert
            assertOpcionalResponse(resultado);

            verify(opcionalRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(opcionalMapper).toResponse(entity);
            verifyNoMoreInteractions(opcionalRepository, opcionalMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar opcional inativo")
        void deveLancarExcecaoAoBuscarOpcionalInativo() {
            //Arrange
            when(opcionalRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> opcionalService.buscarOpcionalAtivoPorId(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, OPCIONAL, ID_VALIDO);

            verify(opcionalRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verifyNoMoreInteractions(opcionalRepository);

            verifyNoInteractions(opcionalMapper);
        }
    }

    @DisplayName("Testes da atualização do opcional")
    @Nested
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar um opcional")
        void deveAtualizarUmOpcional() {
            //Arrange
            var cx = new OpcionalTestContext();
            var entity = criarOpcionalPadrao();

            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(opcionalRepository.existsByNome(cx.request.nome()))
                    .thenReturn(false);

            when(opcionalMapper.toResponse(entity))
                    .thenReturn(cx.response);

            //Act
            var resultado = opcionalService.atualizar(cx.request, ID_VALIDO);
            //Assert
            assertOpcionalResponse(resultado);

            verify(opcionalRepository).findById(ID_VALIDO);
            verify(opcionalRepository).existsByNome(cx.request.nome());
            verify(opcionalMapper).toUpdate(cx.request, entity);
            verify(opcionalMapper).toResponse(entity);

            verifyNoMoreInteractions(
                    opcionalMapper,
                    opcionalRepository
            );
        }

        @Test
        @DisplayName("Deve atualizar um opcional inativo")
        void deveAtualizarUmOpcionalInativo() {
            //Arrange
            var cx = new OpcionalTestContext();
            var entity = OpcionalTestContext.criarOpcional(ID_VALIDO, "Freio Abs", false);
            var response = OpcionalTestContext.criaOpcionalResponse(ID_VALIDO, "Freio Abs", false);

            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(opcionalRepository.existsByNome(cx.request.nome()))
                    .thenReturn(false);

            when(opcionalMapper.toResponse(entity))
                    .thenReturn(response);

            //Act
            var resultado = opcionalService.atualizar(cx.request, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            OpcionalResponse::id,
                            OpcionalResponse::nome,
                            OpcionalResponse::ativo
                    ).containsExactly(
                            ID_VALIDO,
                            "Freio Abs",
                            false
                    );

            verify(opcionalRepository).findById(ID_VALIDO);
            verify(opcionalRepository).existsByNome(cx.request.nome());
            verify(opcionalMapper).toUpdate(cx.request, entity);
            verify(opcionalMapper).toResponse(entity);

            verifyNoMoreInteractions(
                    opcionalMapper,
                    opcionalRepository
            );
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao atualizar um opcional")
        void deveLancarExcecaoAtualizarUmOpcional() {
            //Arrange
            var cx = new OpcionalTestContext();

            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //Act
            var excecao = assertThrows(NotFoundException.class,
                    () -> opcionalService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, OPCIONAL, ID_VALIDO);


            verify(opcionalRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(opcionalRepository);

            verifyNoInteractions(opcionalMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar com nome já existente")
        void deveLancarExcecaoAtualizarNomeJaExistente() {
            //Arrange
            var cx = new OpcionalTestContext();
            var entity = criarOpcionalPadrao();

            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(opcionalRepository.existsByNome(cx.request.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> opcionalService.atualizar(cx.request, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage(OPCIONAL.nomeJaExistente());

            verify(opcionalRepository).findById(ID_VALIDO);
            verify(opcionalRepository).existsByNome(cx.request.nome());
            verifyNoMoreInteractions(opcionalRepository);

            verifyNoInteractions(opcionalMapper);
        }
    }

    @DisplayName("Deve alterar o status do opcional")
    @Nested
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status do opcional para inativo")
        void deveAlterarStatusDoOpcionalInativo() {
            //Arrange
            var entity = criarOpcionalPadrao();
            var request = new StatusRequest(false);
            var response = OpcionalTestContext.criaOpcionalResponse(ID_VALIDO, "Freio Abs", false);

            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(opcionalMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = opcionalService.alterarStatus(ID_VALIDO, request);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo()).isFalse();
            assertThat(entity.isAtivo()).isFalse();

            verify(opcionalRepository).findById(ID_VALIDO);
            verify(opcionalMapper).toResponse(entity);

            verifyNoMoreInteractions(opcionalRepository, opcionalMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o opcional já inativo")
        void deveLancarExcecaoAoAlterarStatusInativo() {
            //Arrange
            var entity = OpcionalTestContext.criarOpcional(ID_VALIDO, "Freio Abs", false);
            var request = new StatusRequest(false);

            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> opcionalService.alterarStatus(ID_VALIDO, request));

            //Assert
            assertBusinessResponseErrorInativa(exception, OPCIONAL);

            assertThat(entity.isAtivo()).isFalse();

            verify(opcionalRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(opcionalRepository);

            verifyNoInteractions(opcionalMapper);
        }

        @Test
        @DisplayName("Deve alterar o status do opcional para ativo")
        void deveAlterarStatusDoOpcionalAtivo() {
            //Arrange
            var entity = OpcionalTestContext.criarOpcional(ID_VALIDO, "Freio Abs", false);
            var request = new StatusRequest(true);
            var response = OpcionalTestContext.criaOpcionalResponse(ID_VALIDO, "Freio Abs", true);

            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));

            when(opcionalMapper.toResponse(entity))
                    .thenReturn(response);
            //ACT
            var resultado = opcionalService.alterarStatus(ID_VALIDO, request);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo()).isTrue();
            assertThat(entity.isAtivo()).isTrue();

            verify(opcionalRepository).findById(ID_VALIDO);
            verify(opcionalMapper).toResponse(entity);

            verifyNoMoreInteractions(opcionalRepository, opcionalMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o opcional já ativo")
        void deveLancarExcecaoAoAlterarStatusAtivo() {
            //Arrange
            var entity = criarOpcionalPadrao();
            var request = new StatusRequest(true);

            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(entity));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> opcionalService.alterarStatus(ID_VALIDO, request));

            //Assert
            assertBusinessResponseError(exception, OPCIONAL);

            assertThat(entity.isAtivo()).isTrue();

            verify(opcionalRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(opcionalRepository);

            verifyNoInteractions(opcionalMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção do opcional nao encontrada ao alterar status")
        void deveLancarExcecaoQuandoOpcionalNaoEncontradaAoAlterarStatus() {
            //Arrange
            var request = new StatusRequest(true);
            when(opcionalRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //Assert
            var exception = assertThrows(NotFoundException.class,
                    () -> opcionalService.alterarStatus(ID_VALIDO, request));

            assertNotFoundResponseError(exception, OPCIONAL, ID_VALIDO);
            verify(opcionalRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(opcionalRepository);

            verifyNoInteractions(opcionalMapper);
        }
    }

    @DisplayName("Testes da busca de opcionais por IDs")
    @Nested
    class BuscarPorIDs {
        @Test
        @DisplayName("Deve buscar opcionais por IDs")
        void deveBuscarPorIDs() {
            //Arrange
            var ids = List.of(ID_VALIDO, 2L, 3L);
            var entity1 = criarOpcionalPadrao();
            var entity2 = OpcionalTestContext.criarOpcional(2L, "Banco de couro", false);
            var entity3 = OpcionalTestContext.criarOpcional(3L, "Camera de ré", true);
            var entityList = List.of(entity1, entity2, entity3);

            when(opcionalRepository.findAllByIdIn(ids))
                    .thenReturn(entityList);
            //ACT
            var resultado = opcionalService.buscarOpcionais(ids);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .extracting(
                            Opcional::getId,
                            Opcional::getNome,
                            Opcional::isAtivo
                    ).containsExactlyInAnyOrder(
                            tuple(ID_VALIDO, "Freio Abs", true),
                            tuple(2L, "Banco de couro", false),
                            tuple(3L, "Camera de ré", true)
                    );

            verify(opcionalRepository).findAllByIdIn(ids);
            verifyNoMoreInteractions(opcionalRepository);
        }
    }

    @DisplayName("Testes da busca de opcionais ativos")
    @Nested
    class BuscarPorIdsAtivos {
        @Test
        @DisplayName("Deve buscar opcionais ativos")
        void deveBuscarOpcionaisPorIdsAtivos() {
            //Arrange
            var ids = List.of(ID_VALIDO, 2L, 3L);
            var entity1 = criarOpcionalPadrao();
            var entity2 = OpcionalTestContext.criarOpcional(2L, "Banco de couro", true);
            var entity3 = OpcionalTestContext.criarOpcional(3L, "Camera de ré", true);
            var entityList = List.of(entity1, entity2, entity3);

            when(opcionalRepository.findAllByIdInAndAtivoTrue(ids))
                    .thenReturn(entityList);
            //ACT
            var resultado = opcionalService.buscarOpcionaisAtivos(ids);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .extracting(
                            Opcional::getId,
                            Opcional::getNome,
                            Opcional::isAtivo
                    ).containsExactlyInAnyOrder(
                            tuple(ID_VALIDO, "Freio Abs", true),
                            tuple(2L, "Banco de couro", true),
                            tuple(3L, "Camera de ré", true)
                    );

            verify(opcionalRepository).findAllByIdInAndAtivoTrue(ids);
            verifyNoMoreInteractions(opcionalRepository);
        }

    }

    private Opcional criarOpcionalPadrao() {
        return OpcionalTestContext.criarOpcional(ID_VALIDO, "Freio Abs", true);
    }
}
