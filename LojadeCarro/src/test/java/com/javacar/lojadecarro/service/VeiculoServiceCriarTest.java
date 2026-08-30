package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.VeiculoTestContext;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.*;
import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;
import static com.javacar.lojadecarro.enums.Entidade.COR;
import static com.javacar.lojadecarro.enums.Entidade.MODELO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.assertVeiculoResponse;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Testes da criação de veículos")
public class VeiculoServiceCriarTest extends AbstractVeiculoServiceTest{
    @Nested
    @DisplayName("Testes da criação do veículo")
    class Criar {
        @Test
        @DisplayName("Deve cadastrar um veiculo")
        void deveCadastrarUmVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(cx.request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(cx.request.idModelo()))
                    .thenReturn(cx.modelo);

            when(combustivelService.buscaCombustivelAtivo(cx.request.idCombustivel()))
                    .thenReturn(cx.combustivel);

            when(opcionalService.buscarOpcionaisAtivos(cx.request.idsOpcionais()))
                    .thenReturn(cx.opcionais);

            when(veiculoMapper.toResponse(cx.entity))
                    .thenReturn(cx.response);

            when(imagensService.criar(cx.imagemFile, cx.entity))
                    .thenReturn(cx.imagens);

            when(veiculoRepository.save(cx.entity))
                    .thenReturn(cx.entity);

            //ACT
            var resultado = veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO);
            //Assert
            assertVeiculoResponse(resultado);
            assertThat(cx.entity.getStatusVeiculo())
                    .isEqualTo(DISPONIVEL);

            assertThat(cx.entity.getOpcionais())
                    .extracting(vo -> vo.getOpcional().getId())
                    .containsExactlyElementsOf(
                            cx.opcionais.stream()
                                    .map(Opcional::getId)
                                    .toList()
                    );

            assertThat(cx.entity.getImagens())
                    .containsExactlyElementsOf(cx.imagens);

            assertThat(cx.entity.getVendedor())
                    .isSameAs(cx.usuario);

            assertThat(cx.entity.getCarroceria())
                    .isSameAs(cx.carroceria);

            assertThat(cx.entity.getCor())
                    .isSameAs(cx.cor);

            assertThat(cx.entity.getModelo())
                    .isSameAs(cx.modelo);

            assertThat(cx.entity.getCombustivel())
                    .isSameAs(cx.combustivel);

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper).toEntity(cx.request);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService).buscaCorAtiva(cx.request.idCores());
            verify(modeloService).buscaModeloAtivo(cx.request.idModelo());
            verify(combustivelService).buscaCombustivelAtivo(cx.request.idCombustivel());
            verify(opcionalService).buscarOpcionaisAtivos(cx.request.idsOpcionais());
            verify(imagensService).criar(cx.imagemFile, cx.entity);
            verify(veiculoRepository).save(cx.entity);
            verify(veiculoMapper).toResponse(cx.entity);

            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção de usuário inativo ao criar veiculo")
        void deveLancarExcecaoUsuarioInativo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, USUARIO, ID_VALIDO);

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository, never()).existsByPlaca(anyString());
            verify(veiculoMapper, never()).toEntity(any());
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção quando a placa já estiver cadastrada")
        void deveLancarExcecaoPlacaJaEstiverCadastrado() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(true);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "A placa informada já possui um cadastro.");

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper, never()).toEntity(any());
            verify(carroceriaService, never()).buscaCarroceriaAtiva(anyLong());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção de carroceria não encontrada ao criar veiculo")
        void deveLancarExcecaoCarroceriaNaoEncontradaAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);
            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);
            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenThrow(new NotFoundException(CARROCERIA, cx.request.idCarroceria()));

            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, CARROCERIA, cx.request.idCarroceria());

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper).toEntity(cx.request);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService, never()).buscaCorAtiva(anyLong());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar cor não encontrada ao criar veiculo")
        void deveLancarExcecaoCorNaoEncontradaAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);
            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);
            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenReturn(cx.carroceria);
            when(coresService.buscaCorAtiva(cx.request.idCores()))
                    .thenThrow(new NotFoundException(COR, cx.request.idCores()));

            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, COR, cx.request.idCores());

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper).toEntity(cx.request);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService).buscaCorAtiva(cx.request.idCores());
            verify(modeloService, never()).buscaModeloAtivo(anyLong());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar modelo não encontrado ao criar veiculo")
        void deveLancarExcecaoModeloNaoEncontradoAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(cx.request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(cx.request.idModelo()))
                    .thenThrow(new NotFoundException(MODELO, cx.request.idModelo()));
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, MODELO, cx.request.idModelo());

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper).toEntity(cx.request);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService).buscaCorAtiva(cx.request.idCores());
            verify(modeloService).buscaModeloAtivo(cx.request.idModelo());
            verify(combustivelService, never()).buscaCombustivelAtivo(anyLong());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractionsCriar();

        }


        @Test
        @DisplayName("Deve lançar exceção ao buscar combustível não encontrado ao criar veiculo")
        void deveLancarExcecaoCombustivelNaoEncontradoAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(cx.request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(cx.request.idModelo()))
                    .thenReturn(cx.modelo);

            when(combustivelService.buscaCombustivelAtivo(cx.request.idCombustivel()))
                    .thenThrow(new NotFoundException(COMBUSTIVEL, cx.request.idCombustivel()));

            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, COMBUSTIVEL, cx.request.idCombustivel());

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoMapper).toEntity(cx.request);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService).buscaCorAtiva(cx.request.idCores());
            verify(modeloService).buscaModeloAtivo(cx.request.idModelo());
            verify(combustivelService).buscaCombustivelAtivo(cx.request.idCombustivel());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoRepository, never()).save(any());
            verify(veiculoMapper, never()).toResponse(any());
            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção informar opcionais duplicados ao criar veiculo")
        void deveLancarExcecaoOpcionalDuplicadoAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            var idsRepetido = List.of(ID_VALIDO, 2L, ID_VALIDO);
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comOpcionais(idsRepetido)
                    .build();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(any()))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(request.idModelo()))
                    .thenReturn(cx.modelo);

            when(combustivelService.buscaCombustivelAtivo(request.idCombustivel()))
                    .thenReturn(cx.combustivel);

            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.criar(request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "A requisição possui opcionais duplicadas.");


            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(request.placa());
            verify(veiculoMapper).toEntity(any());
            verify(carroceriaService).buscaCarroceriaAtiva(request.idCarroceria());
            verify(coresService).buscaCorAtiva(request.idCores());
            verify(modeloService).buscaModeloAtivo(request.idModelo());
            verify(combustivelService).buscaCombustivelAtivo(request.idCombustivel());
            verify(veiculoRepository, never()).save(any());
            verify(opcionalService, never()).buscarOpcionaisAtivos(any());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar opcional não encontrado ao criar veiculo")
        void deveLancarExcecaoOpcionalNaoEncontradoAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();
            var idsRepetido = List.of(ID_VALIDO, 2L, ID_INVALIDO);
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comOpcionais(idsRepetido)
                    .build();
            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoRepository.existsByPlaca(request.placa()))
                    .thenReturn(false);
            when(veiculoMapper.toEntity(any()))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(request.idModelo()))
                    .thenReturn(cx.modelo);

            when(combustivelService.buscaCombustivelAtivo(request.idCombustivel()))
                    .thenReturn(cx.combustivel);

            when(opcionalService.buscarOpcionaisAtivos(request.idsOpcionais()))
                    .thenReturn(cx.opcionais);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.criar(request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "Um ou mais opcionais não foram encontrados.");


            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).existsByPlaca(request.placa());
            verify(veiculoMapper).toEntity(any());
            verify(carroceriaService).buscaCarroceriaAtiva(request.idCarroceria());
            verify(coresService).buscaCorAtiva(request.idCores());
            verify(modeloService).buscaModeloAtivo(request.idModelo());
            verify(combustivelService).buscaCombustivelAtivo(request.idCombustivel());
            verify(veiculoRepository, never()).save(any());
            verify(opcionalService).buscarOpcionaisAtivos(request.idsOpcionais());
            verify(imagensService, never()).criar(any(), any());
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractionsCriar();
        }

        @Test
        @DisplayName("Deve lançar exceção informar imagens invalidas  ao criar veiculo")
        void deveLancarExcecaoImagemInvalidaAoCriarVeiculo() throws IOException {
            //Arrange
            var cx = new VeiculoTestContext();

            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);

            when(veiculoMapper.toEntity(cx.request))
                    .thenReturn(cx.entity);

            when(carroceriaService.buscaCarroceriaAtiva(cx.request.idCarroceria()))
                    .thenReturn(cx.carroceria);

            when(coresService.buscaCorAtiva(cx.request.idCores()))
                    .thenReturn(cx.cor);

            when(modeloService.buscaModeloAtivo(cx.request.idModelo()))
                    .thenReturn(cx.modelo);

            when(combustivelService.buscaCombustivelAtivo(cx.request.idCombustivel()))
                    .thenReturn(cx.combustivel);

            when(veiculoRepository.existsByPlaca(cx.request.placa()))
                    .thenReturn(false);
            when(veiculoRepository.save(cx.entity))
                    .thenReturn(cx.entity);

            when(opcionalService.buscarOpcionaisAtivos(cx.request.idsOpcionais()))
                    .thenReturn(cx.opcionais);

            when(imagensService.criar(cx.imagemFile, cx.entity))
                    .thenThrow(new IOException("Erro ao fazer upload"));
            //ACT
            var excecao = assertThrows(IOException.class,
                    () -> veiculoService.criar(cx.request, cx.imagemFile, ID_VALIDO));
            //Assert
            assertThat(excecao)
                    .hasMessage("Erro ao fazer upload");

            verify(veiculoMapper).toEntity(cx.request);
            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(carroceriaService).buscaCarroceriaAtiva(cx.request.idCarroceria());
            verify(coresService).buscaCorAtiva(cx.request.idCores());
            verify(modeloService).buscaModeloAtivo(cx.request.idModelo());
            verify(combustivelService).buscaCombustivelAtivo(cx.request.idCombustivel());
            verify(veiculoRepository).existsByPlaca(cx.request.placa());
            verify(veiculoRepository).save(cx.entity);
            verify(opcionalService).buscarOpcionaisAtivos(cx.request.idsOpcionais());
            verify(imagensService).criar(cx.imagemFile, cx.entity);
            verify(veiculoMapper, never()).toResponse(any());

            verifyNoMoreInteractionsCriar();
        }
    }

    private void verifyNoMoreInteractionsCriar() {
        verifyNoMoreInteractions(
                veiculoMapper,
                carroceriaService,
                coresService,
                modeloService,
                usuarioService,
                combustivelService,
                opcionalService,
                imagensService,
                veiculoRepository
        );
    }
}
