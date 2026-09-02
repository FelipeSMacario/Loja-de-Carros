package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.OpcionalHelper;
import com.javacar.lojadecarro.factory.opcional.OpcionalRequestFactory;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.OpcionalRepository;
import com.javacar.lojadecarro.service.OpcionalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static com.javacar.lojadecarro.enums.Entidade.OPCIONAL;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes da service do opcional")
public class OpcionalServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private OpcionalService opcionalService;
    @Autowired
    private OpcionalRepository opcionalRepository;

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da criação do opcional")
    class Criar {
        @Test
        @DisplayName("Deve criar um opcional")
        void deveCriarUmaOpcional() {
            //Arrange
            var request = OpcionalHelper.criarOpcionalPorNome("DVD");
            //ACT
            var response = opcionalService.criar(request);
            var opcional = buscarOpcionalPorNome("DVD");
            //Assert
            assertThat(response)
                    .isNotNull();
            assertThat(opcional)
                    .isNotNull();

            assertThat(opcional.getId())
                    .isNotNull();
            assertThat(opcional.isAtivo())
                    .isTrue();
            assertThat(opcional.getDataCadastro())
                    .isNotNull();

            assertThat(response.nome())
                    .isEqualTo(opcional.getNome())
                    .isEqualTo(request.nome());
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar nome repetido")
        void deveLancarExcecaoNomeRepetido() {
            //Arrange
            var request = OpcionalHelper.criarOpcionalPorNome("Android Auto");
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> opcionalService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage(OPCIONAL.nomeJaExistente());
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da listagem de opcionais")
    class Listar {
        @Test
        @DisplayName("Deve listar opcionais ativos")
        void deveListarOpcionaisAtivos() {
            //Arrange
            var request = StatusFiltro.ATIVAS;
            //ACT
            var response = opcionalService.listarAdministracao(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(OpcionalResponse::ativo);
        }

        @Test
        @DisplayName("Deve listar opcionais inativos")
        void deveListarOpcionaisInativos() {
            //Arrange
            var request = StatusFiltro.INATIVAS;
            //ACT
            var response = opcionalService.listarAdministracao(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(m -> !m.ativo());
        }

        @Test
        @DisplayName("Deve listar todos os opcionais")
        void deveListarTodasAsOpcionais() {
            //Arrange
            var request = StatusFiltro.TODAS;
            //ACT
            var response = opcionalService.listarAdministracao(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .anyMatch(OpcionalResponse::ativo)
                    .anyMatch(m -> !m.ativo());
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da busca do opcional")
    class Buscar {
        @Test
        @DisplayName("Deve buscar um opcional")
        void deveBuscarOpcional() {
            //Arrange
            var request = buscarOpcionalPorNome("Rodas esportivas");
            //ACT
            var opcional = opcionalService.buscarOpcionalAtivoPorId(request.getId());
            //Assert
            assertThat(opcional)
                    .isNotNull();
            assertThat(opcional)
                    .extracting(
                            OpcionalResponse::id,
                            OpcionalResponse::nome,
                            OpcionalResponse::ativo
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exeção ao buscar opcional")
        void deveLancarExcecaoBuscarOpcional() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> opcionalService.buscarOpcionalAtivoPorId(-1L));
            //Assert
            assertThat(exception)
                    .hasMessage(OPCIONAL.naoEncontrada() + -1L);
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da atualização do opcional")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o opcional")
        void deveAtualizarOpcional() {
            //Arrange
            var request = OpcionalRequestFactory
                    .criarRequest()
                    .comNome("Isofix +")
                    .build();
            var opcional = buscarOpcionalPorNome("Isofix");
            var codId = opcional.getId();
            //ACT
            var response = opcionalService.atualizar(request, codId);
            var opcionalAtualizada = buscaOpcionalPorId(codId);
            //Assert
            assertThat(response)
                    .isNotNull();
            assertThat(response.nome())
                    .isEqualTo(opcionalAtualizada.getNome())
                    .isEqualTo(request.nome());
            assertThat(opcionalAtualizada.isAtivo())
                    .isTrue();
            assertThat(opcionalAtualizada)
                    .extracting(
                            Opcional::getId,
                            Opcional::isAtivo,
                            Opcional::getDataCadastro
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar opcional")
        void deveLancarExcecaoBuscarOpcional() {
            //Arrange
            var request = OpcionalRequestFactory.criarRequest().comTodosOsCampos().build();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> opcionalService.atualizar(request, -1L));
            //Assert
            assertThat(exception)
                    .hasMessage(OPCIONAL.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao inserir opcional com nome existente")
        void deveLancarExcecaoInserirOpcionalComNomeExistente() {
            //Arrange
            var request = OpcionalHelper.criarOpcionalPorNome("Rodas de liga leve");
            var opcional = buscarOpcionalPorNome("Faróis de LED");
            var codId = opcional.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> opcionalService.atualizar(request, codId));
            //Assert
            assertThat(exception)
                    .hasMessage(OPCIONAL.nomeJaExistente());
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da alteração do status")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar status para ativo")
        void deveAlterarStatusAtivo() {
            //Arrange
            var status = new StatusRequest(true);
            var opcional = buscarOpcionalPorNome("Carregador sem fio");
            var opcionalId = opcional.getId();
            //ACT
            var response = opcionalService.alterarStatus(opcionalId, status);
            var opcionalAtualizada = buscaOpcionalPorId(opcionalId);
            //Assert
            assertThat(response.ativo())
                    .isTrue();
            assertThat(opcionalAtualizada.isAtivo()).isTrue();
        }

        @Test
        @DisplayName("Deve alterar status para inativo")
        void deveAlterarStatusInativo() {
            //Arrange
            var status = new StatusRequest(false);
            var opcional = buscarOpcionalPorNome("Bancos de couro");
            var opcionalId = opcional.getId();
            //ACT
            var response = opcionalService.alterarStatus(opcionalId, status);
            var opcionalAtualizada = buscaOpcionalPorId(opcionalId);
            //Assert
            assertThat(response.ativo())
                    .isFalse();
            assertThat(opcionalAtualizada.isAtivo())
                    .isFalse();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar opcional")
        void deveLancarExcecaoBuscarOpcional() {
            //Arrange
            var status = new StatusRequest(false);
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> opcionalService.alterarStatus(-1L, status));
            //Assert
            assertThat(exception)
                    .hasMessage(OPCIONAL.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao ativar opcional ativo")
        void deveLancarExcecaoOpcionalAtiva() {
            //Arrange
            var status = new StatusRequest(true);
            var opcional = buscarOpcionalPorNome("Câmera de ré");
            var opcionalId = opcional.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> opcionalService.alterarStatus(opcionalId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(OPCIONAL.jaAtiva());
        }

        @Test
        @DisplayName("Deve lançar exceção ao inativar opcional inativo")
        void deveLancarExcecaoOpcionalInativa() {
            //Arrange
            var status = new StatusRequest(false);
            var opcional = buscarOpcionalPorNome("GPS integrado");
            var opcionalId = opcional.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> opcionalService.alterarStatus(opcionalId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(OPCIONAL.jaInativa());
        }
    }

    private Opcional buscarOpcionalPorNome(String nome) {
        return opcionalRepository.findByNome(nome).orElseThrow();
    }

    private Opcional buscaOpcionalPorId(Long id) {
        return opcionalRepository.findById(id).orElseThrow();
    }
}
