package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaRequestFactory;
import com.javacar.lojadecarro.factory.helper.CarroceriaHelper;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.CarroceriaRepository;
import com.javacar.lojadecarro.service.CarroceriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.javacar.lojadecarro.enums.Entidade.CARROCERIA;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes da service da carroceria")
public class CarroceriaServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private CarroceriaService carroceriaService;
    @Autowired
    private CarroceriaRepository carroceriaRepository;

    @Nested
    @DisplayName("Testes da criação da carroceria")
    class Criar {
        @Test
        @DisplayName("Deve criar uma carroceria")
        void deveCriarUmaCarroceria() {
            //Arrange
            var request = CarroceriaHelper.criarCarroceriaPorNome("Conversível");
            //ACT
            var response = carroceriaService.criar(request);
            var carroceria = buscarCarroceriaPorNome("Conversível");
            //Assert
            assertThat(response)
                    .isNotNull();
            assertThat(carroceria)
                    .isNotNull();

            assertThat(carroceria.getId())
                    .isNotNull();
            assertThat(carroceria.isAtivo())
                    .isTrue();
            assertThat(carroceria.getDataCadastro())
                    .isNotNull();

            assertThat(response.nome())
                    .isEqualTo(carroceria.getNome())
                    .isEqualTo(request.nome());
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar nome repetido")
        void deveLancarExcecaoNomeRepetido() {
            //Arrange
            var request = CarroceriaHelper.criarCarroceriaPorNome("Sedan");
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> carroceriaService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage(CARROCERIA.nomeJaExistente());
        }
    }

    @Nested
    @DisplayName("Testes da listagem de carrocerias")
    class Listar {
        @Test
        @DisplayName("Deve listar carrocerias ativas")
        void deveListarCombustiveisAtivas() {
            //Arrange
            var request = StatusFiltro.ATIVAS;
            //ACT
            var response = carroceriaService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(CarroceriaResponse::ativo);
        }

        @Test
        @DisplayName("Deve listar carrocerias inativas")
        void deveListarCombustiveisInativos() {
            //Arrange
            var request = StatusFiltro.INATIVAS;
            //ACT
            var response = carroceriaService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(m -> !m.ativo());
        }

        @Test
        @DisplayName("Deve listar todos as carrocerias")
        void deveListarTodasAsCombustiveis() {
            //Arrange
            var request = StatusFiltro.TODAS;
            //ACT
            var response = carroceriaService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .anyMatch(CarroceriaResponse::ativo)
                    .anyMatch(m -> !m.ativo());
        }
    }

    @Nested
    @DisplayName("Testes da busca da carroceria")
    class Buscar {
        @Test
        @DisplayName("Deve buscar uma carroceria")
        void deveBuscarCarroceria() {
            //Arrange
            var request = buscarCarroceriaPorNome("Hatch");
            //ACT
            var carroceria = carroceriaService.buscarPorId(request.getId());
            //Assert
            assertThat(carroceria)
                    .isNotNull();
            assertThat(carroceria)
                    .extracting(
                            CarroceriaResponse::id,
                            CarroceriaResponse::nome,
                            CarroceriaResponse::ativo
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exeção ao buscar carroceria")
        void deveLancarExcecaoBuscarCarroceria() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> carroceriaService.buscarPorId(-1L));
            //Assert
            assertThat(exception)
                    .hasMessage(CARROCERIA.naoEncontrada() + -1L);
        }
    }

    @Nested
    @DisplayName("Testes da atualização da carroceria")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o carroceria")
        void deveAtualizarCarroceria() {
            //Arrange
            var request = CarroceriaRequestFactory
                    .criarRequest()
                    .comNome("Sider +")
                    .build();
            var carroceria = buscarCarroceriaPorNome("Sider");
            var codId = carroceria.getId();
            //ACT
            var response = carroceriaService.atualizar(request, codId);
            var carroceriaAtualizada = buscaCarroceriaPorId(codId);
            //Assert
            assertThat(response)
                    .isNotNull();
            assertThat(response.nome())
                    .isEqualTo(carroceriaAtualizada.getNome())
                    .isEqualTo(request.nome());
            assertThat(carroceriaAtualizada.isAtivo())
                    .isTrue();
            assertThat(carroceriaAtualizada)
                    .extracting(
                            Carroceria::getId,
                            Carroceria::isAtivo,
                            Carroceria::getDataCadastro
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar carroceria")
        void deveLancarExcecaoBuscarCarroceria() {
            //Arrange
            var request = CarroceriaRequestFactory.criarRequest().comTodosOsCampos().build();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> carroceriaService.atualizar(request, -1L));
            //Assert
            assertThat(exception)
                    .hasMessage(CARROCERIA.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao inserir carroceria com nome existente")
        void deveLancarExcecaoInserirCarroceriaComNomeExistente() {
            //Arrange
            var request = CarroceriaHelper.criarCarroceriaPorNome("SUV");
            var carroceria = buscarCarroceriaPorNome("Hatch");
            var codId = carroceria.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> carroceriaService.atualizar(request, codId));
            //Assert
            assertThat(exception)
                    .hasMessage(CARROCERIA.nomeJaExistente());
        }
    }

    @Nested
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar status para ativo")
        void deveAlterarStatusAtivo() {
            //Arrange
            var status = new StatusRequest(true);
            var carroceria = buscarCarroceriaPorNome("SUV");
            var carroceriaId = carroceria.getId();
            //ACT
            var response = carroceriaService.alterarStatus(carroceriaId, status);
            var carroceriaAtualizada = buscaCarroceriaPorId(carroceriaId);
            //Assert
            assertThat(response.ativo())
                    .isTrue();
            assertThat(carroceriaAtualizada.isAtivo()).isTrue();
        }

        @Test
        @DisplayName("Deve alterar status para inativa")
        void deveAlterarStatusInativo() {
            //Arrange
            var status = new StatusRequest(false);
            var carroceria = buscarCarroceriaPorNome("Hatch");
            var carroceriaId = carroceria.getId();
            //ACT
            var response = carroceriaService.alterarStatus(carroceriaId, status);
            var carroceriaAtualizada = buscaCarroceriaPorId(carroceriaId);
            //Assert
            assertThat(response.ativo())
                    .isFalse();
            assertThat(carroceriaAtualizada.isAtivo())
                    .isFalse();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar carroceria")
        void deveLancarExcecaoBuscarCarroceria() {
            //Arrange
            var status = new StatusRequest(false);
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> carroceriaService.alterarStatus(-1L, status));
            //Assert
            assertThat(exception)
                    .hasMessage(CARROCERIA.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao ativar carroceria ativa")
        void deveLancarExcecaoCarroceriaAtiva() {
            //Arrange
            var status = new StatusRequest(true);
            var carroceria = buscarCarroceriaPorNome("Sedan");
            var carroceriaId = carroceria.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> carroceriaService.alterarStatus(carroceriaId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(CARROCERIA.jaAtiva());
        }

        @Test
        @DisplayName("Deve lançar exceção ao inativar carroceria inativa")
        void deveLancarExcecaoCarroceriaInativa() {
            //Arrange
            var status = new StatusRequest(false);
            var carroceria = buscarCarroceriaPorNome("Picape");
            var carroceriaId = carroceria.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> carroceriaService.alterarStatus(carroceriaId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(CARROCERIA.jaInativa());
        }
    }

    private Carroceria buscarCarroceriaPorNome(String nome) {
        return carroceriaRepository.findByNome(nome).orElseThrow();
    }

    private Carroceria buscaCarroceriaPorId(Long id) {
        return carroceriaRepository.findById(id).orElseThrow();
    }
}
