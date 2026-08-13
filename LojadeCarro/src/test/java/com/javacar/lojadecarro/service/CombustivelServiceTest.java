package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.combustivel.CombustivelTestContext;
import com.javacar.lojadecarro.factory.helper.CombustivelHelper;
import com.javacar.lojadecarro.mapper.CombustivelMapper;
import com.javacar.lojadecarro.repository.CombustivelRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;
import static com.javacar.lojadecarro.factory.helper.CombustivelHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Testes da service do combustível")
@ExtendWith(MockitoExtension.class)
class CombustivelServiceTest {

    @Mock
    private CombustivelMapper combustivelMapper;

    @Mock
    private CombustivelRepository combustivelRepository;

    @Spy
    private EntityValidation entityValidation;

    @InjectMocks
    private CombustivelService combustivelService;

    @DisplayName("Testes da criação do combustível")
    @Nested
    class Criar {
        @Test
        @DisplayName("Deve validar a criação do combustível")
        void deveCriarCombustivel() {
            // Arrange
            var cx = new CombustivelTestContext();

            when(combustivelRepository.existsByNome(cx.combustivelRequest.nome()))
                    .thenReturn(false);
            when(combustivelMapper.toEntity(cx.combustivelRequest))
                    .thenReturn(cx.combustivel);
            when(combustivelRepository.save(cx.combustivel))
                    .thenReturn(cx.combustivel);
            when(combustivelMapper.toResponse(cx.combustivel))
                    .thenReturn(cx.combustivelResponse);

            // Act
            var resultado = combustivelService.criar(cx.combustivelRequest);

            // Assert
            assertCombustivelResponse(resultado);

            verify(combustivelRepository).existsByNome(cx.combustivelRequest.nome());
            verify(combustivelMapper).toEntity(cx.combustivelRequest);
            verify(combustivelRepository).save(cx.combustivel);
            verify(combustivelMapper).toResponse(cx.combustivel);

            verifyNoMoreInteractions(combustivelMapper, combustivelRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção de nome único")
        void deveLancarExcecaoNomeUnico() {
            //Arrange
            var cx = new CombustivelTestContext();
            when(combustivelRepository.existsByNome(cx.combustivelRequest.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> combustivelService.criar(cx.combustivelRequest));
            //Assert
            assertThat(exception)
                    .hasMessage(COMBUSTIVEL.nomeJaExistente());

            verify(combustivelRepository).existsByNome(cx.combustivelRequest.nome());
            verify(combustivelRepository, never()).save(any());

            verifyNoMoreInteractions(combustivelRepository);
            verifyNoInteractions(combustivelMapper);
        }
    }


    @DisplayName("Testes da listagem de combustíveis ADM")
    @Nested
    class ListarAdministrativo {
        @Test
        @DisplayName("Deve listar os combustíveis ativos")
        void deveListarCombustiveisAtivas() {
            //Arrange
            var combustivelEntity1 = CombustivelTestContext.combustivelEntity(ID_VALIDO, "Gasolina", true);
            var combustivelEntity2 = CombustivelTestContext.combustivelEntity(2L, "Etanol", true);
            var listaEntity = List.of(combustivelEntity1, combustivelEntity2);

            var combustivelResponse1 = CombustivelTestContext.combustivelResponse(ID_VALIDO, "Gasolina", true);
            var combustivelResponse2 = CombustivelTestContext.combustivelResponse(2L, "Etanol", true);

            when(combustivelRepository.findByAtivo(true))
                    .thenReturn(listaEntity);

            when(combustivelMapper.toResponse(combustivelEntity1))
                    .thenReturn(combustivelResponse1);

            when(combustivelMapper.toResponse(combustivelEntity2))
                    .thenReturn(combustivelResponse2);


            //ACT
            var resultado = combustivelService.listarAdministracao(StatusFiltro.ATIVAS);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(CombustivelResponse::nome)
                    .containsExactly("Gasolina", "Etanol");

            verify(combustivelRepository).findByAtivo(true);
            verify(combustivelMapper).toResponse(combustivelEntity1);
            verify(combustivelMapper).toResponse(combustivelEntity2);
            verify(combustivelRepository, never()).findAll();

            verifyNoMoreInteractions(combustivelMapper, combustivelRepository);
        }

        @Test
        @DisplayName("Deve listar os combustíveis inativos")
        void deveListarCombustiveisInativas() {
            //Arrange

            var combustivelEntity1 = CombustivelTestContext.combustivelEntity(ID_VALIDO, "Gasolina", false);
            var combustivelEntity2 = CombustivelTestContext.combustivelEntity(2L, "Etanol", false);
            var listaEntity = List.of(combustivelEntity1, combustivelEntity2);

            var combustivelResponse1 = CombustivelTestContext.combustivelResponse(ID_VALIDO, "Gasolina", false);
            var combustivelResponse2 = CombustivelTestContext.combustivelResponse(2L, "Etanol", false);

            when(combustivelRepository.findByAtivo(false))
                    .thenReturn(listaEntity);

            when(combustivelMapper.toResponse(combustivelEntity1))
                    .thenReturn(combustivelResponse1);

            when(combustivelMapper.toResponse(combustivelEntity2))
                    .thenReturn(combustivelResponse2);


            //ACT
            var resultado = combustivelService.listarAdministracao(StatusFiltro.INATIVAS);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(CombustivelResponse::nome)
                    .containsExactly("Gasolina", "Etanol");

            verify(combustivelRepository).findByAtivo(false);
            verify(combustivelMapper).toResponse(combustivelEntity1);
            verify(combustivelMapper).toResponse(combustivelEntity2);
            verify(combustivelRepository, never()).findAll();

            verifyNoMoreInteractions(combustivelMapper, combustivelRepository);
        }

        @Test
        @DisplayName("Deve listar todos os combustíveis")
        void deveListarTodasCombustiveis() {
            //Arrange
            var combustivelEntity1 = CombustivelTestContext.combustivelEntity(ID_VALIDO, "Gasolina", true);
            var combustivelEntity2 = CombustivelTestContext.combustivelEntity(2L, "Etanol", true);
            var combustivelEntity3 = CombustivelTestContext.combustivelEntity(3L, "Diesel", false);
            var listaEntity = List.of(combustivelEntity1, combustivelEntity2, combustivelEntity3);

            var combustivelResponse1 = CombustivelTestContext.combustivelResponse(ID_VALIDO, "Gasolina", true);
            var combustivelResponse2 = CombustivelTestContext.combustivelResponse(2L, "Etanol", true);
            var combustivelResponse3 = CombustivelTestContext.combustivelResponse(3L, "Diesel", false);

            when(combustivelRepository.findAll())
                    .thenReturn(listaEntity);

            when(combustivelMapper.toResponse(combustivelEntity1))
                    .thenReturn(combustivelResponse1);

            when(combustivelMapper.toResponse(combustivelEntity2))
                    .thenReturn(combustivelResponse2);

            when(combustivelMapper.toResponse(combustivelEntity3))
                    .thenReturn(combustivelResponse3);


            //ACT
            var resultado = combustivelService.listarAdministracao(StatusFiltro.TODAS);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .extracting(CombustivelResponse::nome)
                    .containsExactly("Gasolina", "Etanol", "Diesel");

            verify(combustivelRepository).findAll();
            verify(combustivelMapper).toResponse(combustivelEntity1);
            verify(combustivelMapper).toResponse(combustivelEntity2);
            verify(combustivelMapper).toResponse(combustivelEntity3);
            verify(combustivelRepository, never()).findByAtivo(anyBoolean());

            verifyNoMoreInteractions(combustivelMapper, combustivelRepository);
        }
    }


    @DisplayName("Deve buscar os combustíveis ADM")
    @Nested
    class BuscarCombustiveisADM {
        @Test
        @DisplayName("Deve validar a busca de um combustível por ID")
        void deveBuscarCombustivelPorId() {
            // Arrange
            var cx = new CombustivelTestContext();

            when(combustivelRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.combustivel));

            when(combustivelMapper.toResponse(cx.combustivel))
                    .thenReturn(cx.combustivelResponse);

            // Act
            var resultado = combustivelService.buscarPorIdAdministracao(ID_VALIDO);

            // Assert
            assertCombustivelResponse(resultado);

            verify(combustivelRepository).findById(ID_VALIDO);
            verify(combustivelMapper).toResponse(cx.combustivel);

            verifyNoMoreInteractions(combustivelMapper, combustivelRepository);
        }

        @Test
        @DisplayName("Deve lançar uma exceção na busca por um combustível")
        void deveLancarExcecaoAoBuscarCombustivelPorId() {
            // Arrange

            when(combustivelRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());

            // Assert
            var exception = assertThrows(
                    NotFoundException.class,
                    () -> combustivelService.buscarPorIdAdministracao(ID_VALIDO)
            );

            assertNotFoundResponseError(exception, COMBUSTIVEL, ID_VALIDO);

            verify(combustivelRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(combustivelRepository);

            verifyNoInteractions(combustivelMapper);

        }
    }


    @DisplayName("Testes de atualização do combustível")
    @Nested
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar um combustível pelo ID")
        void deveAtualizarCombustivelPorId() {
            //Arrange
            var cx = new CombustivelTestContext();

            when(combustivelRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.combustivel));

            when(combustivelMapper.toResponse(cx.combustivel))
                    .thenReturn(cx.combustivelResponse);

            // ACT
            var resultado = combustivelService.atualizar(cx.combustivelRequest, ID_VALIDO);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            CombustivelResponse::id,
                            CombustivelResponse::nome
                    )
                    .containsExactly(
                            ID_VALIDO,
                            "Gasolina"
                    );

            verify(combustivelRepository).findById(ID_VALIDO);
            verify(combustivelMapper).toUpdate(cx.combustivelRequest, cx.combustivel);
            verify(combustivelMapper).toResponse(cx.combustivel);

            verifyNoMoreInteractions(combustivelRepository, combustivelMapper);
        }

        @Test
        @DisplayName("Deve lançar uma exceção durante a atualização de um combustível")
        void deveLancarExcecaoAoAtualizarCombustivelPorId() {
            //Arrange
            var cx = new CombustivelTestContext();

            when(combustivelRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());

            //Assert
            var exception = assertThrows(NotFoundException.class,
                    () -> combustivelService.atualizar(cx.combustivelRequest, ID_VALIDO));

            assertNotFoundResponseError(exception, COMBUSTIVEL, ID_VALIDO);

            verify(combustivelRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(combustivelRepository);

            verifyNoInteractions(combustivelMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar um nome já cadastrado")
        void deveLancarExcecaoAoInformarNomeCadastro() {
            //Arrange
            var cx = new CombustivelTestContext();
            cx.combustivel.setNome("Eletrico");

            when(combustivelRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.combustivel));

            when(combustivelRepository.existsByNome(cx.combustivelRequest.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> combustivelService.atualizar(cx.combustivelRequest, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage(COMBUSTIVEL.nomeJaExistente());

            verify(combustivelRepository).findById(ID_VALIDO);
            verify(combustivelRepository).existsByNome(cx.combustivelRequest.nome());
            verifyNoMoreInteractions(combustivelRepository);

            verifyNoInteractions(combustivelMapper);
        }

        @Test
        @DisplayName("Deve atualizar um combustível inativo")
        void deveAtualizarUmaCombustivelInativa() {
            //Arrange
            var cx =  new CombustivelTestContext();
            cx.combustivelInativa.setNome("Eletrico");

            when(combustivelRepository.findById(ID_VALIDO))
            .thenReturn(Optional.of(cx.combustivelInativa));

            when(combustivelRepository.existsByNome(cx.combustivelRequest.nome()))
            .thenReturn(false);

            when(combustivelMapper.toResponse(cx.combustivelInativa))
            .thenReturn(cx.combustivelResponseInativa);
            //ACT
            var resultado = combustivelService.atualizar(cx.combustivelRequest, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            CombustivelResponse::nome,
                            CombustivelResponse::ativo
                    )
                    .containsExactly(
                            cx.combustivelResponseInativa.nome(),
                            false
                    );
            assertThat(cx.combustivelInativa.isAtivo()).isFalse();


            verify(combustivelRepository).findById(ID_VALIDO);
            verify(combustivelRepository).existsByNome(cx.combustivelRequest.nome());
            verify(combustivelMapper).toResponse(cx.combustivelInativa);
            verify(combustivelMapper).toUpdate(cx.combustivelRequest, cx.combustivelInativa);

            verifyNoMoreInteractions(combustivelRepository, combustivelMapper);

        }
    }

    @DisplayName("Testes da alteração do status")
    @Nested
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status do combustível para inativo")
        void deveAlterarStatusDaCombustivelInativo() {
            //Arrange
            var cx = new CombustivelTestContext();
            var status = new StatusRequest(false);

            when(combustivelRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.combustivel));

            when(combustivelMapper.toResponse(cx.combustivel))
                    .thenReturn(cx.combustivelResponseInativa);
            //ACT
            var resultado = combustivelService.alterarStatus(ID_VALIDO, status);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo()).isFalse();
            assertThat(cx.combustivel.isAtivo()).isFalse();

            verify(combustivelRepository).findById(ID_VALIDO);
            verify(combustivelMapper).toResponse(cx.combustivel);

            verifyNoMoreInteractions(combustivelRepository, combustivelMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o status para inativo")
        void deveLancarExcecaoAoInativarCombustivelJaInativa() {
            //Arrange
            var cx = new CombustivelTestContext();
            var status = new StatusRequest(false);

            when(combustivelRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.combustivelInativa));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> combustivelService.alterarStatus(ID_VALIDO, status));

            //Assert
            assertBusinessResponseErrorInativa(exception, COMBUSTIVEL);

            verify(combustivelRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(combustivelRepository);

            verifyNoInteractions(combustivelMapper);
        }

        @Test
        @DisplayName("Deve alterar o status do combustível para ativo")
        void deveAlterarStatusDaCombustivelAtivo() {
            //Arrange
            var cx = new CombustivelTestContext();
            var status = new StatusRequest(true);

            when(combustivelRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.combustivelInativa));

            when(combustivelMapper.toResponse(cx.combustivelInativa))
                    .thenReturn(cx.combustivelResponse);
            //ACT
            var resultado = combustivelService.alterarStatus(ID_VALIDO, status);
            //Assert
            assertThat(resultado.ativo()).isTrue();
            assertThat(cx.combustivelInativa.isAtivo()).isTrue();

            verify(combustivelRepository).findById(ID_VALIDO);
            verify(combustivelMapper).toResponse(cx.combustivelInativa);

            verifyNoMoreInteractions(combustivelRepository, combustivelMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o status para ativo")
        void deveLancarExcecaoAoAtivarCombustivelJaAtiva() {
            //Arrange
            var cx = new CombustivelTestContext();
            var status = new StatusRequest(true);

            when(combustivelRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.combustivel));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> combustivelService.alterarStatus(ID_VALIDO, status));

            //Assert
            assertBusinessResponseError(exception, COMBUSTIVEL);

            verify(combustivelRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(combustivelRepository);

            verifyNoInteractions(combustivelMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar combustível")
        void deveLancarExcecaoNaoEncontrarCombustivel() {
            //Arrange
            var request = new StatusRequest(true);

            when(combustivelRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //Assert
            var exception = assertThrows(NotFoundException.class,
                    () -> combustivelService.alterarStatus(ID_VALIDO, request));

            assertNotFoundResponseError(exception, COMBUSTIVEL, ID_VALIDO);

            verify(combustivelRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(combustivelRepository);
            verifyNoInteractions(combustivelMapper);
        }
    }

    @DisplayName("Testes da busca do combustível ativo")
    @Nested
    class BuscaCombustivelAtiva {
        @Test
        @DisplayName("Deve buscar combustível ativo")
        void deveBuscarCombustivelAtiva() {
            //Arrange
            var cx = new CombustivelTestContext();
            when(combustivelRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(cx.combustivel));

            when(combustivelMapper.toResponse(cx.combustivel))
                    .thenReturn(cx.combustivelResponse);
            //ACT
            var resultado = combustivelService.buscarCombustivelAtivaPorId(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo())
                    .isTrue();

            verify(combustivelRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(combustivelMapper).toResponse(cx.combustivel);

            verifyNoMoreInteractions(combustivelRepository, combustivelMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar combustível ativo")
        void deveLancarExcecaoAoBuscarCombustivelAtiva() {
            //Arrange
            when(combustivelRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> combustivelService.buscarCombustivelAtivaPorId(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, COMBUSTIVEL, ID_VALIDO);

            verify(combustivelRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verifyNoMoreInteractions(combustivelRepository);
            verifyNoInteractions(combustivelMapper);
        }
    }

    @DisplayName("Deve listar combustíveis ativos")
    @Nested
    class ListarCombustiveisAtivas {
        @Test
        @DisplayName("Deve listar combustíveis ativos")
        void deveListarCombustiveisAtivas() {
            //Arrange
            var combustivelEntity1 = CombustivelTestContext.combustivelEntity(ID_VALIDO, "Gasolina", true);
            var combustivelEntity2 = CombustivelTestContext.combustivelEntity(ID_VALIDO, "Diesel", true);
            var listaEntity = List.of(combustivelEntity1, combustivelEntity2);

            var combustivelResponse1 = CombustivelTestContext.combustivelResponse(ID_VALIDO, "Gasolina", true);
            var combustivelResponse2 = CombustivelTestContext.combustivelResponse(ID_VALIDO, "Diesel", true);

            when(combustivelRepository.findByAtivo(true))
                    .thenReturn(listaEntity);

            when(combustivelMapper.toResponse(combustivelEntity1))
                    .thenReturn(combustivelResponse1);

            when(combustivelMapper.toResponse(combustivelEntity2))
                    .thenReturn(combustivelResponse2);
            //ACT
            var resultado = combustivelService.listarCombustiveisAtivas();
            //Assert
            assertThat(resultado)
                    .hasSize(2)
                    .allMatch(CombustivelResponse::ativo)
                    .extracting(CombustivelResponse::nome)
                    .containsExactly("Gasolina", "Diesel");

            verify(combustivelRepository).findByAtivo(true);
            verify(combustivelMapper).toResponse(combustivelEntity1);
            verify(combustivelMapper).toResponse(combustivelEntity2);
            verifyNoMoreInteractions(combustivelRepository, combustivelMapper);
        }
    }


}
