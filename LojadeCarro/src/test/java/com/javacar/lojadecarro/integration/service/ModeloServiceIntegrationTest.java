package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.ModeloHelper;
import com.javacar.lojadecarro.factory.modelo.ModeloRequestFactory;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.ModeloRepository;
import com.javacar.lojadecarro.service.ModeloService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.javacar.lojadecarro.enums.Entidade.MARCA;
import static com.javacar.lojadecarro.enums.Entidade.MODELO;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes da service do modelo")
public class ModeloServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ModeloService modeloService;
    @Autowired
    private ModeloRepository modeloRepository;

    @Nested
    @DisplayName("Testes da criação da modelo")
    class Criar {
        @Test
        @DisplayName("Deve criar uma modelo")
        void deveCriarUmaModelo() {
            //Arrange
            var request = ModeloHelper.criarModeloRequestComNome("Celta");
            //ACT
            var response = modeloService.criar(request);
            var modelo = buscarModeloPorNome("Celta");
            //Assert
            assertThat(response)
                    .isNotNull();
            assertThat(modelo)
                    .isNotNull();

            assertThat(modelo.getId())
                    .isNotNull();
            assertThat(modelo.isAtivo())
                    .isTrue();
            assertThat(modelo.getDataCadastro())
                    .isNotNull();

            assertThat(response.nome())
                    .isEqualTo(modelo.getNome())
                    .isEqualTo(request.nome());
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar nome repetido")
        void deveLancarExcecaoNomeRepetido() {
            //Arrange
            var request = ModeloHelper.criarModeloRequestComNome("Onix");
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> modeloService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage(MODELO.nomeJaExistente());
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar marca")
        void deveLancarExcecaoQuandoBuscarMarca() {
            //Arrange
            var marca = ModeloHelper.criarMarcaInvalida();
            var request = ModeloHelper.criarModeloRequest("Mustang", marca.getId());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> modeloService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage(MARCA.naoEncontrada() + request.idMarca());
        }
    }

    @Nested
    @DisplayName("Testes da listagem de modelos")
    class Listar {
        @Test
        @DisplayName("Deve listar modelos ativos")
        @Transactional
        void deveListarModeloesAtivas() {
            //Arrange
            var request = StatusFiltro.ATIVAS;
            //ACT
            var response = modeloService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(ModeloResponse::ativo);
        }

        @Test
        @DisplayName("Deve listar modelos inativos")
        @Transactional
        void deveListarModeloesInativas() {
            //Arrange
            var request = StatusFiltro.INATIVAS;
            //ACT
            var response = modeloService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(m -> !m.ativo());
        }

        @Test
        @DisplayName("Deve listar todos os modelos")
        @Transactional
        void deveListarTodasAsModeloes() {
            //Arrange
            var request = StatusFiltro.TODAS;
            //ACT
            var response = modeloService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .anyMatch(ModeloResponse::ativo)
                    .anyMatch(m -> !m.ativo());
        }
    }

    @Nested
    @DisplayName("Testes da busca do modelo")
    class Buscar {
        @Test
        @DisplayName("Deve buscar um modelo")
        @Transactional
        void deveBuscarModelo() {
            //Arrange
            var request = buscarModeloPorNome("City");
            //ACT
            var modelo = modeloService.buscarPorId(request.getId());
            //Assert
            assertThat(modelo)
                    .isNotNull();
            assertThat(modelo)
                    .extracting(
                            ModeloResponse::id,
                            ModeloResponse::nome,
                            ModeloResponse::ativo
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exeção ao buscar modelo")
        void deveLancarExcecaoBuscarModelo() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> modeloService.buscarPorId(-1L));
            //Assert
            assertThat(exception)
                    .hasMessage(MODELO.naoEncontrada() + -1L);
        }
    }

    @Nested
    @DisplayName("Testes da atualização do modelo")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o modelo")
        @Transactional
        void deveAtualizarModelo() {
            //Arrange
            var request = ModeloRequestFactory
                    .criarRequest()
                    .comIdMarca(6L)
                    .comNome("Renegade Fit")
                    .build();
            var modelo = buscarModeloPorNome("Renegade");
            var codId = modelo.getId();
            //ACT
            var response = modeloService.atualizar(request, codId);
            var modeloAtualizada = buscaModeloPorId(codId);
            //Assert
            assertThat(response)
                    .isNotNull();
            assertThat(response.nome())
                    .isEqualTo(modeloAtualizada.getNome())
                    .isEqualTo(request.nome());
            assertThat(modeloAtualizada.isAtivo())
                    .isTrue();
            assertThat(modeloAtualizada)
                    .extracting(
                            Modelo::getId,
                            Modelo::isAtivo,
                            Modelo::getDataCadastro
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar modelo")
        void deveLancarExcecaoBuscarModelo() {
            //Arrange
            var request = ModeloRequestFactory.criarRequest().comTodosOsCampos().build();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> modeloService.atualizar(request, -1L));
            //Assert
            assertThat(exception)
                    .hasMessage(MODELO.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar marca")
        void deveLancarExcecaoMarca() {
            //Arrange
            var marca = ModeloHelper.criarMarcaInvalida();
            var request = ModeloHelper.criarModeloRequest("Mustang", marca.getId());
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> modeloService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage(MARCA.naoEncontrada() + request.idMarca());
        }

        @Test
        @DisplayName("Deve lançar exceção ao inserir modelo com nome existente")
        void deveLancarExcecaoInserirModeloComNomeExistente() {
            //Arrange
            var request = ModeloHelper.criarModeloRequestComNome("Captur");
            var modelo = buscarModeloPorNome("Kwid");
            var codId = modelo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> modeloService.atualizar(request, codId));
            //Assert
            assertThat(exception)
                    .hasMessage(MODELO.nomeJaExistente());
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
            var modelo = buscarModeloPorNome("Fit");
            var modeloId = modelo.getId();
            //ACT
            var response = modeloService.alterarStatus(modeloId, status);
            var modeloAtualizada = buscaModeloPorId(modeloId);
            //Assert
            assertThat(response.ativo())
                    .isTrue();
            assertThat(modeloAtualizada.isAtivo()).isTrue();
        }

        @Test
        @DisplayName("Deve alterar status para inativo")
        void deveAlterarStatusInativo() {
            //Arrange
            var status = new StatusRequest(false);
            var modelo = buscarModeloPorNome("Sandero");
            var modeloId = modelo.getId();
            //ACT
            var response = modeloService.alterarStatus(modeloId, status);
            var modeloAtualizada = buscaModeloPorId(modeloId);
            //Assert
            assertThat(response.ativo())
                    .isFalse();
            assertThat(modeloAtualizada.isAtivo())
                    .isFalse();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar modelo")
        void deveLancarExcecaoBuscarModelo() {
            //Arrange
            var status = new StatusRequest(false);
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> modeloService.alterarStatus(-1L, status));
            //Assert
            assertThat(exception)
                    .hasMessage(MODELO.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao ativar modelo ativo")
        void deveLancarExcecaoModeloAtiva() {
            //Arrange
            var status = new StatusRequest(true);
            var modelo = buscarModeloPorNome("Cherokee");
            var modeloId = modelo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> modeloService.alterarStatus(modeloId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(MODELO.jaAtiva());
        }

        @Test
        @DisplayName("Deve lançar exceção ao inativar modelo inativo")
        void deveLancarExcecaoModeloInativa() {
            //Arrange
            var status = new StatusRequest(false);
            var modelo = buscarModeloPorNome("March");
            var modeloId = modelo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> modeloService.alterarStatus(modeloId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(MODELO.jaInativa());
        }
    }

    private Modelo buscarModeloPorNome(String nome) {
        return modeloRepository.findByNome(nome).orElseThrow();
    }

    private Modelo buscaModeloPorId(Long id) {
        return modeloRepository.findById(id).orElseThrow();
    }
}
