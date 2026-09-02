package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.cor.CorTestContext;
import com.javacar.lojadecarro.mapper.CorMapper;
import com.javacar.lojadecarro.repository.CoresRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.COR;
import static com.javacar.lojadecarro.factory.helper.CorHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Testes da service da cor")
class CorServiceTest extends BaseServiceTest {

    @Mock
    private CorMapper corMapper;

    @Mock
    private CoresRepository coresRepository;

    @InjectMocks
    private CoresService coresService;

    @DisplayName("Testes da criação da cor")
    @Nested
    class Criar {
        @Test
        @DisplayName("Deve validar a criação da cor")
        void deveCriarCor() {
            // Arrange
            var cx = new CorTestContext();

            when(coresRepository.existsByNome(cx.corRequest.nome()))
                    .thenReturn(false);
            when(corMapper.toEntity(cx.corRequest))
                    .thenReturn(cx.cor);
            when(coresRepository.save(cx.cor))
                    .thenReturn(cx.cor);
            when(corMapper.toResponse(cx.cor))
                    .thenReturn(cx.corResponse);

            // Act
            var resultado = coresService.criar(cx.corRequest);

            // Assert
            assertCorResponse(resultado);

            verify(coresRepository).existsByNome(cx.corRequest.nome());
            verify(corMapper).toEntity(cx.corRequest);
            verify(coresRepository).save(cx.cor);
            verify(corMapper).toResponse(cx.cor);

            verifyNoMoreInteractions(corMapper, coresRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção de nome único")
        void deveLancarExcecaoNomeUnico() {
            //Arrange
            var cx = new CorTestContext();
            when(coresRepository.existsByNome(cx.corRequest.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> coresService.criar(cx.corRequest));
            //Assert
            assertThat(exception)
                    .hasMessage(COR.nomeJaExistente());

            verify(coresRepository).existsByNome(cx.corRequest.nome());
            verify(coresRepository, never()).save(any());

            verifyNoMoreInteractions(coresRepository);
            verifyNoInteractions(corMapper);
        }
    }


    @DisplayName("Testes da listagem de cores ADM")
    @Nested
    class ListarAdministrativo {
        @Test
        @DisplayName("Deve listar as cores ativas")
        void deveListarCoresAtivas() {
            //Arrange
            var corEntity1 = CorTestContext.corEntity(ID_VALIDO, "Branco", true);
            var corEntity2 = CorTestContext.corEntity(2L, "Preto", true);
            var listaEntity = List.of(corEntity1, corEntity2);

            var corResponse1 = CorTestContext.corResponse(ID_VALIDO, "Branco", true);
            var corResponse2 = CorTestContext.corResponse(2L, "Preto", true);

            when(coresRepository.findByAtivo(true))
                    .thenReturn(listaEntity);

            when(corMapper.toResponse(corEntity1))
                    .thenReturn(corResponse1);

            when(corMapper.toResponse(corEntity2))
                    .thenReturn(corResponse2);


            //ACT
            var resultado = coresService.listarAdministracao(StatusFiltro.ATIVAS);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(CorResponse::nome)
                    .containsExactly("Branco", "Preto");

            verify(coresRepository).findByAtivo(true);
            verify(corMapper).toResponse(corEntity1);
            verify(corMapper).toResponse(corEntity2);
            verify(coresRepository, never()).findAll();

            verifyNoMoreInteractions(corMapper, coresRepository);
        }

        @Test
        @DisplayName("Deve listar as cores inativas")
        void deveListarCoresInativas() {
            //Arrange

            var corEntity1 = CorTestContext.corEntity(ID_VALIDO, "Branco", false);
            var corEntity2 = CorTestContext.corEntity(2L, "Preto", false);
            var listaEntity = List.of(corEntity1, corEntity2);

            var corResponse1 = CorTestContext.corResponse(ID_VALIDO, "Branco", false);
            var corResponse2 = CorTestContext.corResponse(2L, "Preto", false);

            when(coresRepository.findByAtivo(false))
                    .thenReturn(listaEntity);

            when(corMapper.toResponse(corEntity1))
                    .thenReturn(corResponse1);

            when(corMapper.toResponse(corEntity2))
                    .thenReturn(corResponse2);


            //ACT
            var resultado = coresService.listarAdministracao(StatusFiltro.INATIVAS);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(CorResponse::nome)
                    .containsExactly("Branco", "Preto");

            verify(coresRepository).findByAtivo(false);
            verify(corMapper).toResponse(corEntity1);
            verify(corMapper).toResponse(corEntity2);
            verify(coresRepository, never()).findAll();

            verifyNoMoreInteractions(corMapper, coresRepository);
        }

        @Test
        @DisplayName("Deve listar todas as cores")
        void deveListarTodasCores() {
            //Arrange
            var corEntity1 = CorTestContext.corEntity(ID_VALIDO, "Branco", true);
            var corEntity2 = CorTestContext.corEntity(2L, "Preto", true);
            var corEntity3 = CorTestContext.corEntity(3L, "Vermelho", false);
            var listaEntity = List.of(corEntity1, corEntity2, corEntity3);

            var corResponse1 = CorTestContext.corResponse(ID_VALIDO, "Branco", true);
            var corResponse2 = CorTestContext.corResponse(2L, "Preto", true);
            var corResponse3 = CorTestContext.corResponse(3L, "Vermelho", false);

            when(coresRepository.findAll())
                    .thenReturn(listaEntity);

            when(corMapper.toResponse(corEntity1))
                    .thenReturn(corResponse1);

            when(corMapper.toResponse(corEntity2))
                    .thenReturn(corResponse2);

            when(corMapper.toResponse(corEntity3))
                    .thenReturn(corResponse3);


            //ACT
            var resultado = coresService.listarAdministracao(StatusFiltro.TODAS);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .extracting(CorResponse::nome)
                    .containsExactly("Branco", "Preto", "Vermelho");

            verify(coresRepository).findAll();
            verify(corMapper).toResponse(corEntity1);
            verify(corMapper).toResponse(corEntity2);
            verify(corMapper).toResponse(corEntity3);
            verify(coresRepository, never()).findByAtivo(anyBoolean());

            verifyNoMoreInteractions(corMapper, coresRepository);
        }
    }


    @DisplayName("Deve buscar as cores ADM")
    @Nested
    class BuscarCoresADM {
        @Test
        @DisplayName("Deve validar a busca de uma cor por ID")
        void deveBuscarCorPorId() {
            // Arrange
            var cx = new CorTestContext();

            when(coresRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.cor));

            when(corMapper.toResponse(cx.cor))
                    .thenReturn(cx.corResponse);

            // Act
            var resultado = coresService.buscarPorIdAdministracao(ID_VALIDO);

            // Assert
            assertCorResponse(resultado);

            verify(coresRepository).findById(ID_VALIDO);
            verify(corMapper).toResponse(cx.cor);

            verifyNoMoreInteractions(corMapper, coresRepository);
        }

        @Test
        @DisplayName("Deve lançar uma exceção na busca por uma cor")
        void deveLancarExcecaoAoBuscarCorPorId() {
            // Arrange

            when(coresRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());

            // Assert
            var exception = assertThrows(
                    NotFoundException.class,
                    () -> coresService.buscarPorIdAdministracao(ID_VALIDO)
            );

            assertNotFoundResponseError(exception, COR, ID_VALIDO);

            verify(coresRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(coresRepository);

            verifyNoInteractions(corMapper);

        }
    }


    @DisplayName("Testes de atualização da cor")
    @Nested
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar uma cor pelo ID")
        void deveAtualizarCorPorId() {
            //Arrange
            var cx = new CorTestContext();

            when(coresRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.cor));

            when(corMapper.toResponse(cx.cor))
                    .thenReturn(cx.corResponse);

            // ACT
            var resultado = coresService.atualizar(cx.corRequest, ID_VALIDO);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            CorResponse::id,
                            CorResponse::nome
                    )
                    .containsExactly(
                            ID_VALIDO,
                            "Branco"
                    );

            verify(coresRepository).findById(ID_VALIDO);
            verify(corMapper).toUpdate(cx.corRequest, cx.cor);
            verify(corMapper).toResponse(cx.cor);

            verifyNoMoreInteractions(coresRepository, corMapper);
        }

        @Test
        @DisplayName("Deve lançar uma exceção durante a atualização de uma cor")
        void deveLancarExcecaoAoAtualizarCorPorId() {
            //Arrange
            var cx = new CorTestContext();

            when(coresRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());

            //Assert
            var exception = assertThrows(NotFoundException.class,
                    () -> coresService.atualizar(cx.corRequest, ID_VALIDO));

            assertNotFoundResponseError(exception, COR, ID_VALIDO);

            verify(coresRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(coresRepository);

            verifyNoInteractions(corMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar um nome já cadastrado")
        void deveLancarExcecaoAoInformarNomeCadastro() {
            //Arrange
            var cx = new CorTestContext();
            cx.cor.setNome("Azul");

            when(coresRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.cor));

            when(coresRepository.existsByNome(cx.corRequest.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> coresService.atualizar(cx.corRequest, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage(COR.nomeJaExistente());

            verify(coresRepository).findById(ID_VALIDO);
            verify(coresRepository).existsByNome(cx.corRequest.nome());
            verifyNoMoreInteractions(coresRepository);

            verifyNoInteractions(corMapper);
        }

        @Test
        @DisplayName("Deve atualizar uma cor inativa")
        void deveAtualizarUmaCorInativa() {
            //Arrange
            var cx = new CorTestContext();
            cx.corInativa.setNome("Azul");

            when(coresRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.corInativa));

            when(coresRepository.existsByNome(cx.corRequest.nome()))
                    .thenReturn(false);

            when(corMapper.toResponse(cx.corInativa))
                    .thenReturn(cx.corResponseInativa);
            //ACT
            var resultado = coresService.atualizar(cx.corRequest, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            CorResponse::nome,
                            CorResponse::ativo
                    )
                    .containsExactly(
                            cx.corResponseInativa.nome(),
                            false
                    );
            assertThat(cx.corInativa.isAtivo()).isFalse();


            verify(coresRepository).findById(ID_VALIDO);
            verify(coresRepository).existsByNome(cx.corRequest.nome());
            verify(corMapper).toResponse(cx.corInativa);
            verify(corMapper).toUpdate(cx.corRequest, cx.corInativa);

            verifyNoMoreInteractions(coresRepository, corMapper);

        }
    }

    @DisplayName("Testes da alteração do status")
    @Nested
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status da cor para inativo")
        void deveAlterarStatusDaCorInativo() {
            //Arrange
            var cx = new CorTestContext();
            var status = new StatusRequest(false);

            when(coresRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.cor));

            when(corMapper.toResponse(cx.cor))
                    .thenReturn(cx.corResponseInativa);
            //ACT
            var resultado = coresService.alterarStatus(ID_VALIDO, status);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo()).isFalse();
            assertThat(cx.cor.isAtivo()).isFalse();

            verify(coresRepository).findById(ID_VALIDO);
            verify(corMapper).toResponse(cx.cor);

            verifyNoMoreInteractions(coresRepository, corMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o status para inativo")
        void deveLancarExcecaoAoInativarCorJaInativa() {
            //Arrange
            var cx = new CorTestContext();
            var status = new StatusRequest(false);

            when(coresRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.corInativa));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> coresService.alterarStatus(ID_VALIDO, status));

            //Assert
            assertBusinessResponseErrorInativa(exception, COR);

            verify(coresRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(coresRepository);

            verifyNoInteractions(corMapper);
        }

        @Test
        @DisplayName("Deve alterar o status da cor para ativo")
        void deveAlterarStatusDaCorAtivo() {
            //Arrange
            var cx = new CorTestContext();
            var status = new StatusRequest(true);

            when(coresRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.corInativa));

            when(corMapper.toResponse(cx.corInativa))
                    .thenReturn(cx.corResponse);
            //ACT
            var resultado = coresService.alterarStatus(ID_VALIDO, status);
            //Assert
            assertThat(resultado.ativo()).isTrue();
            assertThat(cx.corInativa.isAtivo()).isTrue();

            verify(coresRepository).findById(ID_VALIDO);
            verify(corMapper).toResponse(cx.corInativa);

            verifyNoMoreInteractions(coresRepository, corMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o status para ativo")
        void deveLancarExcecaoAoAtivarCorJaAtiva() {
            //Arrange
            var cx = new CorTestContext();
            var status = new StatusRequest(true);

            when(coresRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.cor));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> coresService.alterarStatus(ID_VALIDO, status));

            //Assert
            assertBusinessResponseError(exception, COR);

            verify(coresRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(coresRepository);

            verifyNoInteractions(corMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar cor")
        void deveLancarExcecaoNaoEncontrarCor() {
            //Arrange
            var request = new StatusRequest(true);

            when(coresRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //Assert
            var exception = assertThrows(NotFoundException.class,
                    () -> coresService.alterarStatus(ID_VALIDO, request));

            assertNotFoundResponseError(exception, COR, ID_VALIDO);

            verify(coresRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(coresRepository);
            verifyNoInteractions(corMapper);
        }
    }

    @DisplayName("Testes da busca da cor ativa")
    @Nested
    class BuscaCorAtiva {
        @Test
        @DisplayName("Deve buscar cor ativa")
        void deveBuscarCorAtiva() {
            //Arrange
            var cx = new CorTestContext();
            when(coresRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(cx.cor));

            when(corMapper.toResponse(cx.cor))
                    .thenReturn(cx.corResponse);
            //ACT
            var resultado = coresService.buscarCorAtivaPorId(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo())
                    .isTrue();

            verify(coresRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(corMapper).toResponse(cx.cor);

            verifyNoMoreInteractions(coresRepository, corMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar cor ativa")
        void deveLancarExcecaoAoBuscarCorAtiva() {
            //Arrange
            when(coresRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> coresService.buscarCorAtivaPorId(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, COR, ID_VALIDO);

            verify(coresRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verifyNoMoreInteractions(coresRepository);
            verifyNoInteractions(corMapper);
        }
    }

    @DisplayName("Deve listar cores ativas")
    @Nested
    class ListarCoresAtivas {
        @Test
        @DisplayName("Deve listar cores ativas")
        void deveListarCoresAtivas() {
            //Arrange
            var corEntity1 = CorTestContext.corEntity(ID_VALIDO, "Branco", true);
            var corEntity2 = CorTestContext.corEntity(ID_VALIDO, "Vermelho", true);
            var listaEntity = List.of(corEntity1, corEntity2);

            var corResponse1 = CorTestContext.corResponse(ID_VALIDO, "Branco", true);
            var corResponse2 = CorTestContext.corResponse(ID_VALIDO, "Vermelho", true);

            when(coresRepository.findByAtivo(true))
                    .thenReturn(listaEntity);

            when(corMapper.toResponse(corEntity1))
                    .thenReturn(corResponse1);

            when(corMapper.toResponse(corEntity2))
                    .thenReturn(corResponse2);
            //ACT
            var resultado = coresService.listarCoresAtivas();
            //Assert
            assertThat(resultado)
                    .hasSize(2)
                    .allMatch(CorResponse::ativo)
                    .extracting(CorResponse::nome)
                    .containsExactly("Branco", "Vermelho");

            verify(coresRepository).findByAtivo(true);
            verify(corMapper).toResponse(corEntity1);
            verify(corMapper).toResponse(corEntity2);
            verifyNoMoreInteractions(coresRepository, corMapper);
        }
    }
}
