package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaTestContext;
import com.javacar.lojadecarro.mapper.CarroceriaMapper;
import com.javacar.lojadecarro.repository.CarroceriaRepository;
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

import static com.javacar.lojadecarro.enums.Entidade.CARROCERIA;
import static com.javacar.lojadecarro.factory.helper.CarroceriaHelper.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Testes da service da carroceria")
@ExtendWith(MockitoExtension.class)
class CarroceriaServiceTest {

    @Mock
    private CarroceriaMapper carroceriaMapper;

    @Mock
    private CarroceriaRepository carroceriaRepository;

    @Spy
    private EntityValidation entityValidation;

    @InjectMocks
    private CarroceriaService carroceriaService;

    @DisplayName("Testes da criação da carroceria")
    @Nested
    class Criar {
        @Test
        @DisplayName("Deve validar a criação da carroceria")
        void deveCriarCarroceria() {
            // Arrange
            var cx = new CarroceriaTestContext();

            when(carroceriaRepository.existsByNome(cx.carroceriaRequest.nome()))
                    .thenReturn(false);
            when(carroceriaMapper.toEntity(cx.carroceriaRequest))
                    .thenReturn(cx.carroceria);
            when(carroceriaRepository.save(cx.carroceria))
                    .thenReturn(cx.carroceria);
            when(carroceriaMapper.toResponse(cx.carroceria))
                    .thenReturn(cx.carroceriaResponse);

            // Act
            var resultado = carroceriaService.criar(cx.carroceriaRequest);

            // Assert
            assertCarroceriaResponse(resultado);

            verify(carroceriaRepository).existsByNome(cx.carroceriaRequest.nome());
            verify(carroceriaMapper).toEntity(cx.carroceriaRequest);
            verify(carroceriaRepository).save(cx.carroceria);
            verify(carroceriaMapper).toResponse(cx.carroceria);

            verifyNoMoreInteractions(carroceriaMapper, carroceriaRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção de nome unico")
        void deveLancarExcecaoNomeUnico() {
            //Arrange
            var cx = new CarroceriaTestContext();
            when(carroceriaRepository.existsByNome(cx.carroceriaRequest.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> carroceriaService.criar(cx.carroceriaRequest));
            //Assert
            assertThat(exception)
                    .hasMessage(CARROCERIA.nomeJaExistente());

            verify(carroceriaRepository).existsByNome(cx.carroceriaRequest.nome());
            verify(carroceriaRepository, never()).save(any());

            verifyNoMoreInteractions(carroceriaRepository);
            verifyNoInteractions(carroceriaMapper);
        }
    }


    @DisplayName("Testes da listagem de carrocerias ADM")
    @Nested
    class ListarAdministrativo {
        @Test
        @DisplayName("Deve listar as carrocerias ativas")
        void deveListarCarroceriasAtivas() {
            //Arrange
            var carroceriaEntity1 = CarroceriaTestContext.carroceriaEntity(ID_VALIDO, "Hatch", true);
            var carroceriaEntity2 = CarroceriaTestContext.carroceriaEntity(2L, "SUV", true);
            var listaEntity = List.of(carroceriaEntity1, carroceriaEntity2);

            var carroceriaResponse1 = CarroceriaTestContext.carroceriaResponse(ID_VALIDO, "Hatch", true);
            var carroceriaResponse2 = CarroceriaTestContext.carroceriaResponse(2L, "SUV", true);

            when(carroceriaRepository.findByAtivo(true))
                    .thenReturn(listaEntity);

            when(carroceriaMapper.toResponse(carroceriaEntity1))
                    .thenReturn(carroceriaResponse1);

            when(carroceriaMapper.toResponse(carroceriaEntity2))
                    .thenReturn(carroceriaResponse2);


            //ACT
            var resultado = carroceriaService.listarAdministracao(StatusFiltro.ATIVAS);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(CarroceriaResponse::nome)
                    .containsExactly("Hatch", "SUV");

            verify(carroceriaRepository).findByAtivo(true);
            verify(carroceriaMapper).toResponse(carroceriaEntity1);
            verify(carroceriaMapper).toResponse(carroceriaEntity2);
            verify(carroceriaRepository, never()).findAll();

            verifyNoMoreInteractions(carroceriaMapper, carroceriaRepository);
        }

        @Test
        @DisplayName("Deve listar as carrocerias inativas")
        void deveListarCarroceriasInativas() {
            //Arrange

            var carroceriaEntity1 = CarroceriaTestContext.carroceriaEntity(ID_VALIDO, "Hatch", false);
            var carroceriaEntity2 = CarroceriaTestContext.carroceriaEntity(2L, "SUV", false);
            var listaEntity = List.of(carroceriaEntity1, carroceriaEntity2);

            var carroceriaResponse1 = CarroceriaTestContext.carroceriaResponse(ID_VALIDO, "Hatch", false);
            var carroceriaResponse2 = CarroceriaTestContext.carroceriaResponse(2L, "SUV", false);

            when(carroceriaRepository.findByAtivo(false))
                    .thenReturn(listaEntity);

            when(carroceriaMapper.toResponse(carroceriaEntity1))
                    .thenReturn(carroceriaResponse1);

            when(carroceriaMapper.toResponse(carroceriaEntity2))
                    .thenReturn(carroceriaResponse2);


            //ACT
            var resultado = carroceriaService.listarAdministracao(StatusFiltro.INATIVAS);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(CarroceriaResponse::nome)
                    .containsExactly("Hatch", "SUV");

            verify(carroceriaRepository).findByAtivo(false);
            verify(carroceriaMapper).toResponse(carroceriaEntity1);
            verify(carroceriaMapper).toResponse(carroceriaEntity2);
            verify(carroceriaRepository, never()).findAll();

            verifyNoMoreInteractions(carroceriaMapper, carroceriaRepository);
        }

        @Test
        @DisplayName("Deve listar todas as carrocerias")
        void deveListarTodasCarrocerias() {
            //Arrange
            var carroceriaEntity1 = CarroceriaTestContext.carroceriaEntity(ID_VALIDO, "Hatch", true);
            var carroceriaEntity2 = CarroceriaTestContext.carroceriaEntity(2L, "SUV", true);
            var carroceriaEntity3 = CarroceriaTestContext.carroceriaEntity(3L, "Sedan", false);
            var listaEntity = List.of(carroceriaEntity1, carroceriaEntity2, carroceriaEntity3);

            var carroceriaResponse1 = CarroceriaTestContext.carroceriaResponse(ID_VALIDO, "Hatch", true);
            var carroceriaResponse2 = CarroceriaTestContext.carroceriaResponse(2L, "SUV", true);
            var carroceriaResponse3 = CarroceriaTestContext.carroceriaResponse(3L, "Sedan", false);

            when(carroceriaRepository.findAll())
                    .thenReturn(listaEntity);

            when(carroceriaMapper.toResponse(carroceriaEntity1))
                    .thenReturn(carroceriaResponse1);

            when(carroceriaMapper.toResponse(carroceriaEntity2))
                    .thenReturn(carroceriaResponse2);

            when(carroceriaMapper.toResponse(carroceriaEntity3))
                    .thenReturn(carroceriaResponse3);


            //ACT
            var resultado = carroceriaService.listarAdministracao(StatusFiltro.TODAS);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(3)
                    .extracting(CarroceriaResponse::nome)
                    .containsExactly("Hatch", "SUV", "Sedan");

            verify(carroceriaRepository).findAll();
            verify(carroceriaMapper).toResponse(carroceriaEntity1);
            verify(carroceriaMapper).toResponse(carroceriaEntity2);
            verify(carroceriaMapper).toResponse(carroceriaEntity3);
            verify(carroceriaRepository, never()).findByAtivo(anyBoolean());

            verifyNoMoreInteractions(carroceriaMapper, carroceriaRepository);
        }
    }


    @DisplayName("Deve buscar as carrocerias ADM")
    @Nested
    class BuscarCarroceriasADM {
        @Test
        @DisplayName("Deve validar a busca de uma carroceria por ID")
        void deveBuscarCarroceriaPorId() {
            // Arrange
            var cx = new CarroceriaTestContext();

            when(carroceriaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.carroceria));

            when(carroceriaMapper.toResponse(cx.carroceria))
                    .thenReturn(cx.carroceriaResponse);

            // Act
            var resultado = carroceriaService.buscarPorIdAdministracao(ID_VALIDO);

            // Assert
            assertCarroceriaResponse(resultado);

            verify(carroceriaRepository).findById(ID_VALIDO);
            verify(carroceriaMapper).toResponse(cx.carroceria);

            verifyNoMoreInteractions(carroceriaMapper, carroceriaRepository);
        }

        @Test
        @DisplayName("Deve lançar uma exceção na busca por uma carroceria")
        void deveLancarExcecaoAoBuscarCarroceriaPorId() {
            // Arrange

            when(carroceriaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());

            // Assert
            var exception = assertThrows(
                    NotFoundException.class,
                    () -> carroceriaService.buscarPorIdAdministracao(ID_VALIDO)
            );

            assertNotFoundResponseError(exception, CARROCERIA, ID_VALIDO);

            verify(carroceriaRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(carroceriaRepository);

            verifyNoInteractions(carroceriaMapper);

        }
    }


    @DisplayName("Testes de atualização da carroceria")
    @Nested
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar uma carroceria pelo ID")
        void deveAtualizarCarroceriaPorId() {
            //Arrange
            var cx = new CarroceriaTestContext();

            when(carroceriaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.carroceria));

            when(carroceriaMapper.toResponse(cx.carroceria))
                    .thenReturn(cx.carroceriaResponse);

            // ACT
            var resultado = carroceriaService.atualizar(cx.carroceriaRequest, ID_VALIDO);

            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            CarroceriaResponse::id,
                            CarroceriaResponse::nome
                    )
                    .containsExactly(
                            ID_VALIDO,
                            "Hatch"
                    );

            verify(carroceriaRepository).findById(ID_VALIDO);
            verify(carroceriaMapper).toUpdate(cx.carroceriaRequest, cx.carroceria);
            verify(carroceriaMapper).toResponse(cx.carroceria);

            verifyNoMoreInteractions(carroceriaRepository, carroceriaMapper);
        }

        @Test
        @DisplayName("Deve lançar uma exceção durante a atualização de uma carroceria")
        void deveLancarExcecaoAoAtualizarCarroceriaPorId() {
            //Arrange
            var cx = new CarroceriaTestContext();

            when(carroceriaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());

            //Assert
            var exception = assertThrows(NotFoundException.class,
                    () -> carroceriaService.atualizar(cx.carroceriaRequest, ID_VALIDO));

            assertNotFoundResponseError(exception, CARROCERIA, ID_VALIDO);

            verify(carroceriaRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(carroceriaRepository);

            verifyNoInteractions(carroceriaMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar um nome já cadastrado")
        void deveLancarExcecaoAoInformarNomeCadastro() {
            //Arrange
            var cx = new CarroceriaTestContext();
            cx.carroceria.setNome("Conversivel");

            when(carroceriaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.carroceria));

            when(carroceriaRepository.existsByNome(cx.carroceriaRequest.nome()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> carroceriaService.atualizar(cx.carroceriaRequest, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage(CARROCERIA.nomeJaExistente());

            verify(carroceriaRepository).findById(ID_VALIDO);
            verify(carroceriaRepository).existsByNome(cx.carroceriaRequest.nome());
            verifyNoMoreInteractions(carroceriaRepository);

            verifyNoInteractions(carroceriaMapper);
        }

        @Test
        @DisplayName("Deve atualizar uma carroceria inativa")
        void deveAtualizarUmaCarroceriaInativa() {
            //Arrange
            var cx =  new CarroceriaTestContext();
            cx.carroceriaInativa.setNome("Conversivel");

            when(carroceriaRepository.findById(ID_VALIDO))
            .thenReturn(Optional.of(cx.carroceriaInativa));

            when(carroceriaRepository.existsByNome(cx.carroceriaRequest.nome()))
            .thenReturn(false);

            when(carroceriaMapper.toResponse(cx.carroceriaInativa))
            .thenReturn(cx.carroceriaResponseInativa);
            //ACT
            var resultado = carroceriaService.atualizar(cx.carroceriaRequest, ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            CarroceriaResponse::nome,
                            CarroceriaResponse::ativo
                    )
                    .containsExactly(
                            cx.carroceriaResponseInativa.nome(),
                            false
                    );
            assertThat(cx.carroceriaInativa.isAtivo()).isFalse();


            verify(carroceriaRepository).findById(ID_VALIDO);
            verify(carroceriaRepository).existsByNome(cx.carroceriaRequest.nome());
            verify(carroceriaMapper).toResponse(cx.carroceriaInativa);
            verify(carroceriaMapper).toUpdate(cx.carroceriaRequest, cx.carroceriaInativa);

            verifyNoMoreInteractions(carroceriaRepository, carroceriaMapper);

        }
    }

    @DisplayName("Testes da alteração do status")
    @Nested
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status da carroceria para inativo")
        void deveAlterarStatusDaCarroceriaInativo() {
            //Arrange
            var cx = new CarroceriaTestContext();
            var status = new StatusRequest(false);

            when(carroceriaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.carroceria));

            when(carroceriaMapper.toResponse(cx.carroceria))
                    .thenReturn(cx.carroceriaResponseInativa);
            //ACT
            var resultado = carroceriaService.alterarStatus(ID_VALIDO, status);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo()).isFalse();
            assertThat(cx.carroceria.isAtivo()).isFalse();

            verify(carroceriaRepository).findById(ID_VALIDO);
            verify(carroceriaMapper).toResponse(cx.carroceria);

            verifyNoMoreInteractions(carroceriaRepository, carroceriaMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o status para inativo")
        void deveLancarExcecaoAoInativarCarroceriaJaInativa() {
            //Arrange
            var cx = new CarroceriaTestContext();
            var status = new StatusRequest(false);

            when(carroceriaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.carroceriaInativa));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> carroceriaService.alterarStatus(ID_VALIDO, status));

            //Assert
            assertBusinessResponseErrorInativa(exception, CARROCERIA);

            verify(carroceriaRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(carroceriaRepository);

            verifyNoInteractions(carroceriaMapper);
        }

        @Test
        @DisplayName("Deve alterar o status da carroceria para ativo")
        void deveAlterarStatusDaCarroceriaAtivo() {
            //Arrange
            var cx = new CarroceriaTestContext();
            var status = new StatusRequest(true);

            when(carroceriaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.carroceriaInativa));

            when(carroceriaMapper.toResponse(cx.carroceriaInativa))
                    .thenReturn(cx.carroceriaResponse);
            //ACT
            var resultado = carroceriaService.alterarStatus(ID_VALIDO, status);
            //Assert
            assertThat(resultado.ativo()).isTrue();
            assertThat(cx.carroceriaInativa.isAtivo()).isTrue();

            verify(carroceriaRepository).findById(ID_VALIDO);
            verify(carroceriaMapper).toResponse(cx.carroceriaInativa);

            verifyNoMoreInteractions(carroceriaRepository, carroceriaMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar o status para ativo")
        void deveLancarExcecaoAoAtivarCarroceriaJaAtiva() {
            //Arrange
            var cx = new CarroceriaTestContext();
            var status = new StatusRequest(true);

            when(carroceriaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(cx.carroceria));
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> carroceriaService.alterarStatus(ID_VALIDO, status));

            //Assert
            assertBusinessResponseError(exception, CARROCERIA);

            verify(carroceriaRepository).findById(ID_VALIDO);

            verifyNoMoreInteractions(carroceriaRepository);

            verifyNoInteractions(carroceriaMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar carroceria")
        void deveLancarExcecaoNaoEncontrarCarroceria() {
            //Arrange
            var request = new StatusRequest(true);

            when(carroceriaRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //Assert
            var exception = assertThrows(NotFoundException.class,
                    () -> carroceriaService.alterarStatus(ID_VALIDO, request));

            assertNotFoundResponseError(exception, CARROCERIA, ID_VALIDO);

            verify(carroceriaRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(carroceriaRepository);
            verifyNoInteractions(carroceriaMapper);
        }
    }

    @DisplayName("Testes da busca da carroceria ativa")
    @Nested
    class BuscaCarroceriaAtiva {
        @Test
        @DisplayName("Deve buscar carroceria ativa")
        void deveBuscarCarroceriaAtiva() {
            //Arrange
            var cx = new CarroceriaTestContext();
            when(carroceriaRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.of(cx.carroceria));

            when(carroceriaMapper.toResponse(cx.carroceria))
                    .thenReturn(cx.carroceriaResponse);
            //ACT
            var resultado = carroceriaService.buscarCarroceriaAtivaPorId(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull();

            assertThat(resultado.ativo())
                    .isTrue();

            verify(carroceriaRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verify(carroceriaMapper).toResponse(cx.carroceria);

            verifyNoMoreInteractions(carroceriaRepository, carroceriaMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar carroceria ativa")
        void deveLancarExcecaoAoBuscarCarroceriaAtiva() {
            //Arrange
            when(carroceriaRepository.findByIdAndAtivoTrue(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> carroceriaService.buscarCarroceriaAtivaPorId(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, CARROCERIA, ID_VALIDO);

            verify(carroceriaRepository).findByIdAndAtivoTrue(ID_VALIDO);
            verifyNoMoreInteractions(carroceriaRepository);
            verifyNoInteractions(carroceriaMapper);
        }
    }

    @DisplayName("Deve listar carrocerias ativas")
    @Nested
    class ListarCarroceriasAtivas {
        @Test
        @DisplayName("Deve listar carrocerias ativas")
        void deveListarCarroceriasAtivas() {
            //Arrange
            var carroceriaEntity1 = CarroceriaTestContext.carroceriaEntity(ID_VALIDO, "Hatch", true);
            var carroceriaEntity2 = CarroceriaTestContext.carroceriaEntity(ID_VALIDO, "Sedan", true);
            var listaEntity = List.of(carroceriaEntity1, carroceriaEntity2);

            var carroceriaResponse1 = CarroceriaTestContext.carroceriaResponse(ID_VALIDO, "Hatch", true);
            var carroceriaResponse2 = CarroceriaTestContext.carroceriaResponse(ID_VALIDO, "Sedan", true);

            when(carroceriaRepository.findByAtivo(true))
                    .thenReturn(listaEntity);

            when(carroceriaMapper.toResponse(carroceriaEntity1))
                    .thenReturn(carroceriaResponse1);

            when(carroceriaMapper.toResponse(carroceriaEntity2))
                    .thenReturn(carroceriaResponse2);
            //ACT
            var resultado = carroceriaService.listarCarroceriasAtivas();
            //Assert
            assertThat(resultado)
                    .hasSize(2)
                    .allMatch(CarroceriaResponse::ativo)
                    .extracting(CarroceriaResponse::nome)
                    .containsExactly("Hatch", "Sedan");

            verify(carroceriaRepository).findByAtivo(true);
            verify(carroceriaMapper).toResponse(carroceriaEntity1);
            verify(carroceriaMapper).toResponse(carroceriaEntity2);
            verifyNoMoreInteractions(carroceriaRepository, carroceriaMapper);
        }
    }
}
