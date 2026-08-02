package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.combustivel.CombustivelRequestFactory;
import com.javacar.lojadecarro.factory.helper.CombustivelHelper;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.CombustivelRepository;
import com.javacar.lojadecarro.service.CombustivelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes da service do combustível")
public class CombustivelServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private CombustivelService combustivelService;
    @Autowired
    private CombustivelRepository combustivelRepository;

    @Nested
    @DisplayName("Testes da criação do combustivel")
    class Criar {
        @Test
        @DisplayName("Deve criar um combustivel")
        void deveCriarUmaCombustivel() {
            //Arrange
            var request = CombustivelHelper.criarCombustivelPorNome("Água");
            //ACT
            var response = combustivelService.criar(request);
            var combustivel = buscarCombustivelPorNome("Água");
            //Assert
            assertThat(response)
                    .isNotNull();
            assertThat(combustivel)
                    .isNotNull();

            assertThat(combustivel.getId())
                    .isNotNull();
            assertThat(combustivel.isAtivo())
                    .isTrue();
            assertThat(combustivel.getDataCadastro())
                    .isNotNull();

            assertThat(response.nome())
                    .isEqualTo(combustivel.getNome())
                    .isEqualTo(request.nome());
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar nome repetido")
        void deveLancarExcecaoNomeRepetido() {
            //Arrange
            var request = CombustivelHelper.criarCombustivelPorNome("Gasolina");
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> combustivelService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage(COMBUSTIVEL.nomeJaExistente());
        }
    }

    @Nested
    @DisplayName("Testes da listagem de combustiveis")
    class Listar {
        @Test
        @DisplayName("Deve listar combustiveis ativos")
        void deveListarCombustiveisAtivos() {
            //Arrange
            var request = StatusFiltro.ATIVAS;
            //ACT
            var response = combustivelService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(CombustivelResponse::ativo);
        }

        @Test
        @DisplayName("Deve listar combustiveis inativos")
        void deveListarCombustiveisInativos() {
            //Arrange
            var request = StatusFiltro.INATIVAS;
            //ACT
            var response = combustivelService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(m -> !m.ativo());
        }

        @Test
        @DisplayName("Deve listar todos os combustiveis")
        void deveListarTodasAsCombustiveis() {
            //Arrange
            var request = StatusFiltro.TODAS;
            //ACT
            var response = combustivelService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .anyMatch(CombustivelResponse::ativo)
                    .anyMatch(m -> !m.ativo());
        }
    }

    @Nested
    @DisplayName("Testes da busca do combustivel")
    class Buscar {
        @Test
        @DisplayName("Deve buscar um combustivel")
        void deveBuscarCombustivel() {
            //Arrange
            var request = buscarCombustivelPorNome("Flex");
            //ACT
            var combustivel = combustivelService.buscarPorId(request.getId());
            //Assert
            assertThat(combustivel)
                    .isNotNull();
            assertThat(combustivel)
                    .extracting(
                            CombustivelResponse::id,
                            CombustivelResponse::nome,
                            CombustivelResponse::ativo
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exeção ao buscar combustivel")
        void deveLancarExcecaoBuscarCombustivel() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> combustivelService.buscarPorId(-1L));
            //Assert
            assertThat(exception)
                    .hasMessage(COMBUSTIVEL.naoEncontrada() + -1L);
        }
    }

    @Nested
    @DisplayName("Testes da atualização do combustivel")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o combustivel")
        void deveAtualizarCombustivel() {
            //Arrange
            var request = CombustivelRequestFactory
                    .criarRequest()
                    .comNome("Elétrico +")
                    .build();
            var combustivel = buscarCombustivelPorNome("Elétrico");
            var codId = combustivel.getId();
            //ACT
            var response = combustivelService.atualizar(request, codId);
            var combustivelAtualizada = buscaCombustivelPorId(codId);
            //Assert
            assertThat(response)
                    .isNotNull();
            assertThat(response.nome())
                    .isEqualTo(combustivelAtualizada.getNome())
                    .isEqualTo(request.nome());
            assertThat(combustivelAtualizada.isAtivo())
                    .isTrue();
            assertThat(combustivelAtualizada)
                    .extracting(
                            Combustivel::getId,
                            Combustivel::isAtivo,
                            Combustivel::getDataCadastro
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar combustivel")
        void deveLancarExcecaoBuscarCombustivel() {
            //Arrange
            var request = CombustivelRequestFactory.criarRequest().comTodosOsCampos().build();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> combustivelService.atualizar(request, -1L));
            //Assert
            assertThat(exception)
                    .hasMessage(COMBUSTIVEL.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao inserir combustivel com nome existente")
        void deveLancarExcecaoInserirCombustivelComNomeExistente() {
            //Arrange
            var request = CombustivelHelper.criarCombustivelPorNome("Flex");
            var combustivel = buscarCombustivelPorNome("Diesel");
            var codId = combustivel.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> combustivelService.atualizar(request, codId));
            //Assert
            assertThat(exception)
                    .hasMessage(COMBUSTIVEL.nomeJaExistente());
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
            var combustivel = buscarCombustivelPorNome("Elétrico");
            var combustivelId = combustivel.getId();
            //ACT
            var response = combustivelService.alterarStatus(combustivelId, status);
            var combustivelAtualizada = buscaCombustivelPorId(combustivelId);
            //Assert
            assertThat(response.ativo())
                    .isTrue();
            assertThat(combustivelAtualizada.isAtivo()).isTrue();
        }

        @Test
        @DisplayName("Deve alterar status para inativo")
        void deveAlterarStatusInativo() {
            //Arrange
            var status = new StatusRequest(false);
            var combustivel = buscarCombustivelPorNome("Etanol");
            var combustivelId = combustivel.getId();
            //ACT
            var response = combustivelService.alterarStatus(combustivelId, status);
            var combustivelAtualizada = buscaCombustivelPorId(combustivelId);
            //Assert
            assertThat(response.ativo())
                    .isFalse();
            assertThat(combustivelAtualizada.isAtivo())
                    .isFalse();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar combustivel")
        void deveLancarExcecaoBuscarCombustivel() {
            //Arrange
            var status = new StatusRequest(false);
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> combustivelService.alterarStatus(-1L, status));
            //Assert
            assertThat(exception)
                    .hasMessage(COMBUSTIVEL.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao ativar combustivel ativo")
        void deveLancarExcecaoCombustivelAtiva() {
            //Arrange
            var status = new StatusRequest(true);
            var combustivel = buscarCombustivelPorNome("Gasolina");
            var combustivelId = combustivel.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> combustivelService.alterarStatus(combustivelId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(COMBUSTIVEL.jaAtiva());
        }

        @Test
        @DisplayName("Deve lançar exceção ao inativar combustivel inativo")
        void deveLancarExcecaoCombustivelInativa() {
            //Arrange
            var status = new StatusRequest(false);
            var combustivel = buscarCombustivelPorNome("Hibrido");
            var combustivelId = combustivel.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> combustivelService.alterarStatus(combustivelId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(COMBUSTIVEL.jaInativa());
        }
    }

    private Combustivel buscarCombustivelPorNome(String nome) {
        return combustivelRepository.findByNome(nome).orElseThrow();
    }

    private Combustivel buscaCombustivelPorId(Long id) {
        return combustivelRepository.findById(id).orElseThrow();
    }
}
