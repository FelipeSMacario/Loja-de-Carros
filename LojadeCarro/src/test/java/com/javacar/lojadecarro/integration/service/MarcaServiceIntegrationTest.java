package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.request.StatusRequest;
import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.entity.Marca;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.marca.MarcaRequestFactory;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.MarcaRepository;
import com.javacar.lojadecarro.service.MarcaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static com.javacar.lojadecarro.enums.Entidade.MARCA;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes da service da marca")
public class MarcaServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MarcaService marcaService;
    @Autowired
    private MarcaRepository marcaRepository;

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da criação da marca")
    class Criar {
        @Test
        @DisplayName("Deve criar uma marca")
        void deveCriarUmaMarca() {
            //Arrange
            var request = MarcaRequestFactory
                    .criarRequest()
                    .comNome("Peugeot")
                    .comUrl("https://www.peugeot.com")
                    .build();
            //ACT
            var response = marcaService.criar(request);
            //Assert
            assertThat(response)
                    .isNotNull();

            assertThat(response)
                    .extracting(
                            MarcaResponse::id,
                            MarcaResponse::ativo
                    )
                    .containsExactly(
                            response.id(),
                            true
                    );

            assertThat(response)
                    .extracting(
                            MarcaResponse::nome,
                            MarcaResponse::url
                    ).containsExactly(
                            request.nome(),
                            request.url()
                    );

            var marca = buscaMarca(response.id());
            assertThat(marca)
                    .extracting(
                            Marca::getId,
                            Marca::getNome,
                            Marca::getUrl,
                            Marca::isAtivo,
                            Marca::getDataCadastro
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao inserir um nome já existente")
        void deveLancarExcecaoNomeJaExistente() {
            //Arrange
            var request = MarcaRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comNome("Fiat")
                    .build();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> marcaService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage(MARCA.nomeJaExistente());
        }

        @Test
        @DisplayName("Deve lançar uma exceção ao inserir uma url já existente")
        void deveLancarExcecaoUrlJaExistente() {
            //Arrange
            var request = MarcaRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comNome("BYD")
                    .comUrl("https://www.ford.com.br")
                    .build();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> marcaService.criar(request));
            //Assert
            assertThat(exception)
                    .hasMessage("A URL informada já possui um cadastro.");
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da listagem de marcas")
    class Listar {
        @Test
        @DisplayName("Deve listar marcas ativas")
        void deveListarMarcasAtivas() {
            //Arrange
            var request = StatusFiltro.ATIVAS;
            //ACT
            var response = marcaService.listarAdministracao(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(MarcaResponse::ativo);
        }

        @Test
        @DisplayName("Deve listar marcas inativas")
        void deveListarMarcasInativas() {
            //Arrange
            var request = StatusFiltro.INATIVAS;
            //ACT
            var response = marcaService.listarAdministracao(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(m -> !m.ativo());
        }

        @Test
        @DisplayName("Deve listar todas as marcas")
        void deveListarTodasAsMarcas() {
            //Arrange
            var request = StatusFiltro.TODAS;
            //ACT
            var response = marcaService.listarAdministracao(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .anyMatch(MarcaResponse::ativo)
                    .anyMatch(m -> !m.ativo());
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da busca de marca")
    class Buscar {
        @Test
        @DisplayName("Deve buscar uma marca")
        void deveBuscarMarca() {
            //Arrange
            var request = buscaMarcaPorNome("Fiat");
            //ACT
            var marca = marcaService.buscarPorIdAdministracao(request.getId());
            //Assert
            assertThat(marca)
                    .isNotNull();
            assertThat(marca)
                    .extracting(
                            MarcaResponse::id,
                            MarcaResponse::nome,
                            MarcaResponse::url,
                            MarcaResponse::ativo
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exeção ao buscar marca")
        void deveLancarExcecaoBuscarMarca() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> marcaService.buscarPorIdAdministracao(-1L));
            //Assert
            assertThat(exception)
                    .hasMessage(MARCA.naoEncontrada() + -1L);
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da atualização da marca")
    class Atualizacao {
        @Test
        @DisplayName("Deve atualizar a marca")
        void deveAtualizarMarca() {
            //Arrange
            var request = MarcaRequestFactory
                    .criarRequest()
                    .comNome("Fiat")
                    .comUrl("https://www.fiat.com.eua")
                    .build();
            var marca = buscaMarcaPorNome(request.nome());
            var marcaId = marca.getId();
            //ACT
            var response = marcaService.atualizar(request, marcaId);
            var marcaAtualizada = buscaMarca(marcaId);
            //Assert
            assertThat(marcaAtualizada)
                    .extracting(
                            Marca::getNome,
                            Marca::getUrl
                    )
                    .containsExactly(
                            request.nome(),
                            request.url()
                    );
            assertThat(marcaAtualizada.getId())
                    .isNotNull();
            assertThat(marcaAtualizada.isAtivo())
                    .isTrue();
            assertThat(marcaAtualizada.getDataCadastro())
                    .isNotNull();

            assertThat(response)
                    .isNotNull()
                    .extracting(
                            MarcaResponse::nome,
                            MarcaResponse::url
                    ).containsExactly(
                            request.nome(),
                            request.url()
                    );
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar nome existente")
        void deveLancarExcecaoNomeExistente() {
            //Arrange
            var request = MarcaRequestFactory
                    .criarRequest()
                    .comNome("Fiat")
                    .build();
            var marca = buscaMarcaPorNome("Chevrolet");
            var marcaId =  marca.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> marcaService.atualizar(request, marcaId));
            //Assert
            assertThat(exception)
                    .hasMessage(MARCA.nomeJaExistente());
        }

        @Test
        @DisplayName("Deve lançar exceção ao informar url existente")
        void deveLancarExcecaoUrlExistente() {
            //Arrange
            var request = MarcaRequestFactory
                    .criarRequest()
                    .comNome("Chevrolet")
                    .comUrl("https://www.honda.com.br")
                    .build();
            var marca = buscaMarcaPorNome("Chevrolet");
            var marcaId =  marca.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> marcaService.atualizar(request, marcaId));
            //Assert
            assertThat(exception)
                    .hasMessage("A URL informada já possui um cadastro.");
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
            var marca = buscaMarcaPorNome("Nissan");
            var marcaId = marca.getId();
            //ACT
            var response = marcaService.alterarStatus(marcaId, status);
            var marcaAtualizada = buscaMarca(marcaId);
            //Assert
            assertThat(response.ativo())
                    .isTrue();
            assertThat(marcaAtualizada.isAtivo()).isTrue();
        }

        @Test
        @DisplayName("Deve alterar status para inativo")
        void deveAlterarStatusInativo() {
            //Arrange
            var status = new StatusRequest(false);
            var marca = buscaMarcaPorNome("Jeep");
            var marcaId = marca.getId();
            //ACT
            var response = marcaService.alterarStatus(marcaId, status);
            var marcaAtualizada = buscaMarca(marcaId);
            //Assert
            assertThat(response.ativo())
                    .isFalse();
            assertThat(marcaAtualizada.isAtivo())
                    .isFalse();
        }

        @Test
        @DisplayName("Deve lançar exceção buscar marca")
        void deveLancarExcecaoBuscarMarca() {
            //Arrange
            var status = new StatusRequest(false);
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> marcaService.alterarStatus(-1L, status));
            //Assert
            assertThat(exception)
                    .hasMessage(MARCA.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção ao ativar marca ativa")
        void deveLancarExcecaoMarcaAtiva() {
            //Arrange
            var status = new StatusRequest(true);
            var marca = buscaMarcaPorNome("Hyundai");
            var marcaId = marca.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> marcaService.alterarStatus(marcaId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(MARCA.jaAtiva());
        }

        @Test
        @DisplayName("Deve lançar exceção ao inativar marca inativa")
        void deveLancarExcecaoMarcaInativa() {
            //Arrange
            var status = new StatusRequest(false);
            var marca = buscaMarcaPorNome("Renault");
            var marcaId = marca.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> marcaService.alterarStatus(marcaId, status));
            //Assert
            assertThat(exception)
                    .hasMessage(MARCA.jaInativa());
        }
    }

    private Marca buscaMarca(Long idMarca) {
        return marcaRepository.findById(idMarca).orElseThrow();
    }

    private Marca buscaMarcaPorNome(String nome) {
        return marcaRepository.findByNome(nome).orElseThrow();
    }
}
