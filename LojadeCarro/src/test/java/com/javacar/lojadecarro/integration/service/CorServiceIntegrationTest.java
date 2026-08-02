package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.entity.Cor;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.cor.CorRequestFactory;
import com.javacar.lojadecarro.factory.helper.CorHelper;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.CoresRepository;
import com.javacar.lojadecarro.service.CoresService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.javacar.lojadecarro.enums.Entidade.COR;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes da service da cor")
public class CorServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private CoresService coresService;
    @Autowired
    private CoresRepository coresRepository;

    @Nested
    @DisplayName("Testes da criação da cor")
    class Criar {
        @Test
        @DisplayName("Deve criar uma cor")
        void deveCriarUmaCor() {
            //Arrange
            var request = CorHelper.criarCorRequestComNome("Lilás");
            //ACT
            var response = coresService.criar(request);
            var cor = buscarCorPorNome("Lilás");
            //Assert
            assertThat(response)
                    .isNotNull();
            assertThat(cor)
                    .isNotNull();

            assertThat(cor.getId())
                    .isNotNull();
            assertThat(cor.isAtivo())
                    .isTrue();
            assertThat(cor.getDataCadastro())
                    .isNotNull();

            assertThat(response.nome())
                    .isEqualTo(cor.getNome())
                    .isEqualTo(request.nome());
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar nome repetido")
        void deveLancarExcecaoNomeRepetido() {
            //Arrange
            var request = CorHelper.criarCorRequestComNome("Vermelho");
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> coresService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage(COR.nomeJaExistente());
        }
    }

    @Nested
    @DisplayName("Testes da listagem de cores")
    class Listar {
        @Test
        @DisplayName("Deve listar cores ativas")
        void deveListarCoresAtivas() {
            //Arrange
            var request = StatusFiltro.ATIVAS;
            //ACT
            var response = coresService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(CorResponse::ativo);
        }

        @Test
        @DisplayName("Deve listar cores inativas")
        void deveListarCoresInativas() {
            //Arrange
            var request = StatusFiltro.INATIVAS;
            //ACT
            var response = coresService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(m -> !m.ativo());
        }

        @Test
        @DisplayName("Deve listar todas as cores")
        void deveListarTodasAsCores() {
            //Arrange
            var request = StatusFiltro.TODAS;
            //ACT
            var response = coresService.listar(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .anyMatch(CorResponse::ativo)
                    .anyMatch(m -> !m.ativo());
        }
    }

    @Nested
    @DisplayName("Testes da busca da cor")
    class Buscar {
        @Test
        @DisplayName("Deve buscar uma cor")
        void deveBuscarCor() {
            //Arrange
            var request = buscarCorPorNome("Vermelho");
            //ACT
            var cor = coresService.buscarPorId(request.getId());
            //Assert
            AssertionsForClassTypes.assertThat(cor)
                    .isNotNull();
            AssertionsForClassTypes.assertThat(cor)
                    .extracting(
                            CorResponse::id,
                            CorResponse::nome,
                            CorResponse::ativo
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exeção ao buscar cor")
        void deveLancarExcecaoBuscarCor() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> coresService.buscarPorId(-1L));
            //Assert
            assertThat(exception)
                    .hasMessage(COR.naoEncontrada() + -1L);
        }
    }

    @Nested
    @DisplayName("Testes da atualização da cor")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar a cor")
        void deveAtualizarCor() {
            //Arrange
            var request = CorRequestFactory
                    .criarRequest()
                    .comNome("Verde Água")
                    .build();
            var cor = buscarCorPorNome("Verde");
            var codId = cor.getId();
            //ACT
            var response = coresService.atualizar(request, codId);
            var corAtualizada = buscaCorPorId(codId);
            //Assert
            assertThat(response)
                    .isNotNull();
            assertThat(response.nome())
                    .isEqualTo(corAtualizada.getNome())
                    .isEqualTo(request.nome());
            assertThat(corAtualizada.isAtivo())
                    .isTrue();
            assertThat(corAtualizada)
                    .extracting(
                            Cor::getId,
                            Cor::isAtivo,
                            Cor::getDataCadastro
                    ).doesNotContainNull();
        }
        @Test
        @DisplayName("Deve lançar exceção ao buscar cor")
        void deveLancarExcecaoBuscarCor() {
            //Arrange
            var request = CorRequestFactory.criarRequest().comTodosOsCampos().build();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> coresService.atualizar(request, -1L));
            //Assert
            assertThat(exception)
            .hasMessage(COR.naoEncontrada() + -1L);
        }
        @Test
        @DisplayName("Deve lançar exceção ao inserir cor com nome existente")
        void deveLancarExcecaoInserirCorComNomeExistente() {
            //Arrange
            var request = CorHelper.criarCorRequestComNome("Azul");
            var cor = buscarCorPorNome("Laranja");
            var codId = cor.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> coresService.atualizar(request, codId));
            //Assert
            assertThat(exception)
            .hasMessage(COR.nomeJaExistente());
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
            var cor = buscarCorPorNome("Cinza");
            var corId = cor.getId();
            //ACT
            var response = coresService.alterarStatus(corId, status);
            var corAtualizada = buscaCorPorId(corId);
            //Assert
            assertThat(response.ativo())
                    .isTrue();
            assertThat(corAtualizada.isAtivo()).isTrue();
        }

        @Test
        @DisplayName("Deve alterar status para inativo")
        void deveAlterarStatusInativo() {
            //Arrange
            var status = new StatusRequest(false);
            var cor = buscarCorPorNome("Marrom");
            var corId = cor.getId();
            //ACT
            var response = coresService.alterarStatus(corId, status);
            var corAtualizada = buscaCorPorId(corId);
            //Assert
            AssertionsForClassTypes.assertThat(response.ativo())
                    .isFalse();
            AssertionsForClassTypes.assertThat(corAtualizada.isAtivo())
                    .isFalse();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar cor")
        void deveLancarExcecaoBuscarCor() {
            //Arrange
            var status = new StatusRequest(false);
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> coresService.alterarStatus(-1L, status));
            //Assert
            assertThat(exception)
                    .hasMessage(COR.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao ativar cor ativa")
        void deveLancarExcecaoCorAtiva() {
            //Arrange
            var status = new StatusRequest(true);
            var cor = buscarCorPorNome("Azul");
            var corId = cor.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> coresService.alterarStatus(corId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(COR.jaAtiva());
        }

        @Test
        @DisplayName("Deve lançar exceção ao inativar cor inativa")
        void deveLancarExcecaoCorInativa() {
            //Arrange
            var status = new StatusRequest(false);
            var cor = buscarCorPorNome("Azul Perolado");
            var corId = cor.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> coresService.alterarStatus(corId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(COR.jaInativa());
        }
    }

    private Cor buscarCorPorNome(String nome) {
        return coresRepository.findByNome(nome).orElseThrow();
    }

    private Cor buscaCorPorId(Long id) {
        return coresRepository.findById(id).orElseThrow();
    }
}
