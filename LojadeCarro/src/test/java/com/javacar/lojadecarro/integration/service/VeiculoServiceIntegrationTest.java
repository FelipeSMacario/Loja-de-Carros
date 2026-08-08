package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.response.ImagemResponse;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.ImagemHelper;
import com.javacar.lojadecarro.factory.helper.VeiculoHelper;
import com.javacar.lojadecarro.factory.helper.VeiculoTestContext;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import com.javacar.lojadecarro.service.StorageService;
import com.javacar.lojadecarro.service.VeiculoService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.*;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.enums.StatusVeiculo.VENDIDO;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.criarVeiculoRequestComPlaca;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("Testes da service do veiculo")
public class VeiculoServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private VeiculoService veiculoService;
    @Autowired
    private VeiculoRepository veiculoRepository;
    @MockitoBean
    private StorageService storageService;

    @Nested
    @DisplayName("Testes do cadastro do veiculo")
    class Criar {
        @Test
        @DisplayName("Deve cadastrar um veiculo")
        @Transactional
        void deveCadastrarUmVeiculo() throws IOException {
            //Arrange
            var request = VeiculoTestContext.criarVeiculoValido();
            var imagemRequest = ImagemHelper.criarListImagemFile();

            var imagem1 = imagemRequest[0];
            var imagem2 = imagemRequest[1];

            when(storageService.upload(eq(imagem1), anyLong()))
                    .thenReturn(ImagemHelper.criarUploadValido());

            when(storageService.upload(eq(imagem2), anyLong()))
                    .thenReturn(ImagemHelper.criarUploadValido2());
            //Act
            var response = veiculoService.criar(request, imagemRequest);
            //Assert
            assertThat(response.id())
                    .isNotNull();

            assertThat(response)
                    .isNotNull()
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::marca,
                            VeiculoResponse::placa,
                            VeiculoResponse::modelo,
                            VeiculoResponse::statusVeiculo
                    )
                    .doesNotContainNull();


            var veiculo = veiculoRepository.findById(response.id()).orElseThrow();
            assertThat(veiculo)
                    .extracting(
                            Veiculo::getModelo,
                            Veiculo::getCombustivel,
                            Veiculo::getCarroceria,
                            Veiculo::getCor,
                            Veiculo::getVendedor
                    ).doesNotContainNull();

            assertThat(response.placa())
                    .isEqualTo(veiculo.getPlaca())
                    .isEqualTo(request.placa());

            assertThat(veiculo.getCor().getId())
                    .isEqualTo(request.idCores());

            assertThat(veiculo.getOpcionais())
                    .hasSize(3);

            assertThat(veiculo.getImagens())
                    .hasSize(2);


            assertThat(
                    veiculo.getImagens()
                            .stream()
                            .filter(Imagem::isPrincipal)
                            .count()
            ).isEqualTo(1);
            assertThat(
                    veiculo.getImagens()
                            .stream()
                            .filter(i -> !i.isPrincipal())
                            .count()
            ).isEqualTo(1);

        }

        @Test
        @DisplayName("Deve lançar exceção quando a carroceria não existir")
        void deveBuscarCarroceriaInexistente() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCarroceria(-1L)
                    .build();
            //Act
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(request, null));
            //Assert
            assertThat(exception)
                    .hasMessage(CARROCERIA.naoEncontrada() + request.idCarroceria());
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar uma cor inexistente")
        void deveBuscarCorInexistente() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCores(-1L)
                    .build();
            //Act
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(request, null));
            //Assert
            assertThat(exception)
                    .hasMessage(COR.naoEncontrada() + request.idCores());
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar um modelo inexistente")
        void deveBuscarModeloInexistente() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdModelo(-1L)
                    .build();
            //Act
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(request, null));
            //Assert
            assertThat(exception)
                    .hasMessage(MODELO.naoEncontrada() + request.idModelo());
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar um vendedor inexistente")
        void deveBuscarVendedorInexistente() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdUsuario(-1L)
                    .build();
            //Act
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(request, null));
            //Assert
            assertThat(exception)
                    .hasMessage(USUARIO.naoEncontrada() + request.idUsuario());
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar um combustivel inexistente")
        void deveBuscarCombustivelInexistente() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCombustivel(-1L)
                    .build();
            //Act
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(request, null));
            //Assert
            assertThat(exception)
                    .hasMessage(COMBUSTIVEL.naoEncontrada() + request.idCombustivel());
        }

        @Test
        @DisplayName("Deve lançar exceção ao fazer upload da imagem")
        @Transactional
        void deveLancarExcecaoQuandoUploadDaImagem() throws IOException {
            //Arrange

            var request = VeiculoTestContext.criarVeiculoValido();
            var imagemsRequest = ImagemHelper.criarListImagemFile();
            var imagem = imagemsRequest[0];

            when(storageService.upload(eq(imagem), anyLong()))
                    .thenThrow(new IOException("Erro ao realizar upload"));
            //Act
            var exception = assertThrows(IOException.class,
                    () -> veiculoService.criar(request, imagemsRequest));
            //Assert
            assertThat(exception)
                    .hasMessage("Erro ao realizar upload");
        }

        @Test
        @DisplayName("Deve validar o placa unica")
        void deveLancarExcecaoQuandoPlacaJaExistir() {
            //Arrange
            var request = criarVeiculoRequestComPlaca("HIJ7K89");
            //Act
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.criar(request, null));
            //Assert
            assertThat(exception)
                    .hasMessage("A placa informada já possui um cadastro.");
        }
    }

    @Nested
    @DisplayName("Testes da listagem de veiculos")
    class Listar {
        @Test
        @DisplayName("Deve listar todos os veiculos")
        @Transactional
        void deveListarTodosOsVeiculos() {
            //Act
            var veiculos = veiculoService.listar(Pageable.unpaged(), null);
            //Assert
            assertThat(veiculos)
                    .isNotEmpty()
                    .anyMatch(veiculo -> veiculo.statusVeiculo() == DISPONIVEL)
                    .anyMatch(veiculo -> veiculo.statusVeiculo() == VENDIDO);

        }

        @Test
        @DisplayName("Deve listar todos os veiculos disponiveis")
        @Transactional
        void deveListarOsVeiculosDisponiveis() {
            //Act
            var veiculos = veiculoService.listar(Pageable.unpaged(), DISPONIVEL);
            //Assert
            assertThat(veiculos)
                    .isNotEmpty()
                    .allMatch(veiculo -> veiculo.statusVeiculo() == DISPONIVEL);

        }
    }

    @Nested
    @DisplayName("Testes de busca do veiculo")
    class Buscar {
        @Test
        @DisplayName("Deve buscar o veiculo")
        @Transactional
        void deveBuscarVeiculo() {
            //Arrange
            var veiculo = veiculoRepository.findByPlaca("HIJ7K89").orElseThrow();

            //Act
            var response = veiculoService.buscarPorId(veiculo.getId());
            //Assert
            assertThat(response)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::placa,
                            VeiculoResponse::marca,
                            VeiculoResponse::modelo,
                            VeiculoResponse::valor,
                            VeiculoResponse::statusVeiculo
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exceção quando veiculo não existir")
        void deveLancarExcecaoQuandoVeiculoNaoExistir() {
            //Act
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.buscarPorId(-1L));
            //Assert
            assertThat(exception)
                    .hasMessage(VEICULO.naoEncontrada() + -1L);
        }
    }

    @Nested
    @DisplayName("Testes da atualização do veiculo")
    class Atualizar {
        @Test
        @DisplayName("Deve atualizar o veiculo")
        @Transactional
        void deveAtualizarOVeiculo() {
            //Arrange
            var request = VeiculoTestContext.criarVeiculoAtualizacaoValido();
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();

            //ACT
            var response = veiculoService.atualizar(request, veiculo.getId());
            //Assert
            assertThat(response)
                    .isNotNull();

            assertThat(response.placa())
                    .isEqualTo(veiculo.getPlaca())
                    .isEqualTo(request.placa());

            assertThat(response.statusVeiculo())
                    .isEqualTo(veiculo.getStatusVeiculo());
        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar a carroceria")
        void deveLancarExcecaoQuandoNenhumaCarroceria() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("HIJ7K89")
                    .comIdCarroceria(-1L)
                    .build();
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, idVeiculo));

            //Assert
            assertThat(exception)
                    .hasMessage(CARROCERIA.naoEncontrada() + -1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar a cor")
        void deveLancarExcecaoQuandoNenhumaCor() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("HIJ7K89")
                    .comIdCores(-1L)
                    .build();
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, idVeiculo));

            //Assert
            assertThat(exception)
                    .hasMessage(COR.naoEncontrada() + -1L);

        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar o modelo")
        void deveLancarExcecaoQuandoNenhumModelo() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("HIJ7K89")
                    .comIdModelo(-1L)
                    .build();
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, idVeiculo));

            //Assert
            assertThat(exception)
                    .hasMessage(MODELO.naoEncontrada() + -1L);

        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar o usuario")
        void deveLancarExcecaoQuandoNenhumUsuario() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("HIJ7K89")
                    .comIdUsuario(-1L)
                    .build();
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, idVeiculo));

            //Assert
            assertThat(exception)
                    .hasMessage(USUARIO.naoEncontrada() + -1L);

        }

        @Test
        @DisplayName("Deve lançar exceção quando não encontrar o combustivel")
        void deveLancarExcecaoQuandoNenhumCombustivel() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("HIJ7K89")
                    .comIdCombustivel(-1L)
                    .build();
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.atualizar(request, idVeiculo));

            //Assert
            assertThat(exception)
                    .hasMessage(COMBUSTIVEL.naoEncontrada() + -1L);

        }

        @Test
        @DisplayName("Deve validar o placa unica")
        void deveLancarExcecaoQuandoPlacaJaExistir() {
            //Arrange
            var request = VeiculoTestContext.criarVeiculoAtualizacaoValido();

            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();

            var requestIncorreto = criarVeiculoRequestComPlaca("DEF4G56");
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.atualizar(requestIncorreto, idVeiculo));
            //Assert
            assertThat(exception)
                    .hasMessage("A placa informada já possui um cadastro.");
        }
    }

    @Nested
    @DisplayName("Testes da alteração de status do veiculo")
    class AlterarStatus {
        @Test
        @DisplayName("Deve alterar o status do veiculo para vendido")
        void deveAlterarStatusDoVeiculoParaVendido() {
            //Arrange
            var request = criarVeiculoRequestComPlaca("HIJ7K89");
            var status = new AlterarStatusRequest(VENDIDO);

            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            //Act
            var response = veiculoService.pausarVeiculo(veiculo.getId(), status);
            //Assert
            assertThat(response.statusVeiculo()).isEqualTo(VENDIDO);
        }

        @Test
        @DisplayName("Deve alterar o status do veiculo para disponivel")
        void deveAlterarStatusDoVeiculoParaDisponivel() {
            //Arrange
            var request = criarVeiculoRequestComPlaca("KAZ-2Y5");
            var status = new AlterarStatusRequest(DISPONIVEL);

            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            //Act
            var response = veiculoService.pausarVeiculo(veiculo.getId(), status);
            //Assert
            assertThat(response.statusVeiculo()).isEqualTo(DISPONIVEL);
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar veiculo para disponivel quando o veiculo já está disponivel")
        void develancarExcecaoQuandoVeiculoJaDisponivel() {
            //Arrange
            var request = criarVeiculoRequestComPlaca("DEF4G56");
            var status = new AlterarStatusRequest(DISPONIVEL);

            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //Act
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.pausarVeiculo(idVeiculo, status));
            //Assert
            assertThat(exception)
                    .hasMessage("Status informado correspondente ao status atual");
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar alterar status de um veiculo vendido")
        void deveLancarExcecaoAoAlterarStatusDoVeiculoVendido() {
            //Arrange
            var request = criarVeiculoRequestComPlaca("LMN1O23");
            var status = new AlterarStatusRequest(DISPONIVEL);

            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var veiculoId = veiculo.getId();
            //Act
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.pausarVeiculo(veiculoId, status));
            //Assert
            assertThat(exception)
                    .hasMessage("Um veículo vendido não pode ter seu status alterado.");
        }
    }

    @Nested
    @DisplayName("Testes da listagem das imagens do veiculo")
    class ListarImagens {
        @Test
        @DisplayName("Deve listar as imagens")
        @Transactional
        void deveListarAsImagens() {
            //Arrange
            var request = criarVeiculoRequestComPlaca("ABC1D23");
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            //Act
            var resultado = veiculoService.listarImagens(veiculo.getId());
            //Assert
            assertThat(resultado)
                    .isNotEmpty()
                    .hasSize(3);

            assertThat(
                    resultado
                            .stream()
                            .filter(ImagemResponse::principal)
                            .count()
            ).isEqualTo(1);

            assertThat(
                    resultado
                            .stream()
                            .filter(i -> !i.principal())
                            .count()
            ).isEqualTo(2);
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar o veiculo")
        void deveLancarExcecaoQuandoNaoEncontrarVeiculo() {
            //Act
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.listarImagens(-1L));
            //Assert
            assertThat(excecao)
                    .hasMessage(VEICULO.naoEncontrada() + -1L);
        }
    }

    @Nested
    @DisplayName("Testes para desvincular os opcionais")
    class DesvincularOsOpcionais {
        @Test
        @DisplayName("Deve desvincular opcionais")
        @Transactional
        void deveDesvincularOsOpcionais() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comPlaca("ABC1D23")
                    .comOpcionais(List.of(1L, 3L))
                    .build();

            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            veiculoService.desvincularOpcionais(idVeiculo, request.idsOpcionais());

            var response = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            //Assert
            assertThat(response.getOpcionais())
                    .hasSize(1)
                    .extracting(opcionais -> opcionais.getOpcional().getId())
                    .containsExactly(5L);
        }
        @Test
        @DisplayName("Deve lançar exceção inserir opcionais duplicados")
        @Transactional
        void deveLancarExcecaoQuandoOpcionalInformadaNaoExiste() {
            //Arrange
            var listaOpcionais = List.of(1L, 1L, 3L);
            var request = VeiculoHelper.criarVeiculoRequestComPlaca("DEF4G56");
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertThat(exception)
                    .hasMessage("A requisição possui opcionais duplicadas.");
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar o veiculo")
        void deveLancarExcecaoQuandoNaoEncontrarVeiculo() {
            //Arrange
            var listaOpcionais = List.of(1L, 3L);
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.desvincularOpcionais(-1L, listaOpcionais));
            //Assert
            assertThat(exception)
            .hasMessage(VEICULO.naoEncontrada() + -1L);
        }
        @Test
        @DisplayName("Deve lançar exceção ao buscar um opcional inexistente")
        @Transactional
        void deveLancarExcecaoQuandoNaoExistente() {
            //Arrange
            var listaOpcionais = List.of(-1L, 1L, 3L);
            var request = VeiculoHelper.criarVeiculoRequestComPlaca("DEF4G56");
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertThat(exception)
            .hasMessage("Um ou mais opcionais não foram encontrados.");
        }
        @Test
        @DisplayName("Deve lançar exceção ao tentar desvincular um opcional que o veiculo não possui")
        @Transactional
        void deveLancarExcecaoAoDesvinvincularOpcionalQueVeiculoNaoPossui() {
            //Arrange
            var listaOpcionais = List.of(1L, 6L);
            var request = VeiculoHelper.criarVeiculoRequestComPlaca("DEF4G56");
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.desvincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertThat(exception)
                    .hasMessage("O Veiculo informado não possui esse opcional");
        }

    }

    @Nested
    @DisplayName("Testes para vinciluar os opcionais")
    class VincularOpcionais{
        @Test
        @DisplayName("Deve vincular opcionais")
        @Transactional
        void deveVincularOpcionais() {
            //Arrange
            var request = VeiculoHelper.criarVeiculoRequestComPlaca("KPB8712");
            var listOpcionais = List.of(2L, 3L, 4L);
            var veiculo =  veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            veiculoService.vincularOpcionais(idVeiculo, listOpcionais);
            var response = veiculoRepository.findById(idVeiculo).orElseThrow();
            //Assert
            assertThat(response.getOpcionais())
                    .isNotEmpty()
                    .hasSize(4)
                    .extracting(op -> op.getOpcional().getId())
                    .containsExactlyInAnyOrder(1L, 2L, 3L, 4L);
        }

        @Test
        @DisplayName("Deve lançar exceção inserir opcionais duplicados")
        @Transactional
        void deveLancarExcecaoQuandoRoleInformadaNaoExiste() {
            //Arrange
            var listaOpcionais = List.of(1L, 1L, 3L);
            var request = VeiculoHelper.criarVeiculoRequestComPlaca("DEF4G56");
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertThat(exception)
                    .hasMessage("A requisição possui opcionais duplicadas.");
        }

        @Test
        @DisplayName("Deve lançar exceção ao não encontrar o veiculo")
        void deveLancarExcecaoQuandoNaoEncontrarVeiculo() {
            //Arrange
            var listaOpcionais = List.of(1L, 3L);
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.vincularOpcionais(-1L, listaOpcionais));
            //Assert
            assertThat(exception)
                    .hasMessage(VEICULO.naoEncontrada() + -1L);
        }
        @Test
        @DisplayName("Deve lançar exceção ao buscar um opcional inexistente")
        @Transactional
        void deveLancarExcecaoQuandoNaoExistente() {
            //Arrange
            var listaOpcionais = List.of(-1L, 1L, 3L);
            var request = VeiculoHelper.criarVeiculoRequestComPlaca("DEF4G56");
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertThat(exception)
                    .hasMessage("Um ou mais opcionais não foram encontrados.");
        }
        @Test
        @DisplayName("Deve lançar exceção ao tentar vincular um opcional que o veiculo não possui")
        @Transactional
        void deveLancarExcecaoAoVinvincularOpcionalQueVeiculoNaoPossui() {
            //Arrange
            var listaOpcionais = List.of(1L, 3L);
            var request = VeiculoHelper.criarVeiculoRequestComPlaca("HIJ7K89");
            var veiculo = veiculoRepository.findByPlaca(request.placa()).orElseThrow();
            var idVeiculo = veiculo.getId();
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.vincularOpcionais(idVeiculo, listaOpcionais));
            //Assert
            assertThat(exception)
                    .hasMessage(OPCIONAL.jaAtiva());
        }
    }
}
