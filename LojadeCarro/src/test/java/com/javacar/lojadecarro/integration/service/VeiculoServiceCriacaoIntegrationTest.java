package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.ImagemHelper;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static com.javacar.lojadecarro.enums.Entidade.*;
import static com.javacar.lojadecarro.enums.Entidade.COMBUSTIVEL;
import static com.javacar.lojadecarro.enums.Entidade.MODELO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.criarVeiculoRequestComPlaca;
import static com.javacar.lojadecarro.factory.helper.VeiculoTestContext.criarVeiculoValido;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("Testes da criação de veículos")
public class VeiculoServiceCriacaoIntegrationTest extends AbstractVeiculoServiceIntegrationTest{
    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes do cadastro do veiculo")
    class Criar {
        @Test
        @DisplayName("Deve cadastrar um veiculo")
        void deveCadastrarUmVeiculo() throws IOException {
            //Arrange
            var request = criarVeiculoValido();
            var imagemRequest = ImagemHelper.criarListImagemFile();

            var imagem1 = imagemRequest[0];
            var imagem2 = imagemRequest[1];

            when(storageService.upload(eq(imagem1), anyLong()))
                    .thenReturn(ImagemHelper.criarUploadValido());

            when(storageService.upload(eq(imagem2), anyLong()))
                    .thenReturn(ImagemHelper.criarUploadValido2());
            //Act

            var response = veiculoService.criar(request, imagemRequest, ID_VALIDO);
            entityManager.flush();
            entityManager.clear();
            //Assert
            var veiculo = veiculoRepository
                    .findById(response.id())
                    .orElseThrow();
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


            assertThat(veiculo)
                    .extracting(
                            Veiculo::getModelo,
                            Veiculo::getCombustivel,
                            Veiculo::getCarroceria,
                            Veiculo::getCor,
                            Veiculo::getVendedor
                    ).doesNotContainNull();
            assertThat(veiculo.getStatusVeiculo())
                    .isEqualTo(DISPONIVEL)
                    .isEqualTo(response.statusVeiculo());

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
        @DisplayName("Deve lançar exceção quando o vendedor for inexistente")
        void deveLancarExceptionQuandoVendedorForInexistente() {
            //Arrange
            var request = criarVeiculoValido();
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(request, null, ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(exception, USUARIO, ID_INVALIDO);
        }

        @Test
        @DisplayName("Deve lançar exceção quando a carroceria inexistente")
        void deveBuscarCarroceriaInexistente() {
            //Arrange
            var request = VeiculoRequestFactory
                    .criarRequest()
                    .comTodosOsCampos()
                    .comIdCarroceria(-1L)
                    .build();
            //Act
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.criar(request, null, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, CARROCERIA, request.idCarroceria());
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
                    () -> veiculoService.criar(request, null, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, COR, request.idCores());
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
                    () -> veiculoService.criar(request, null, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, MODELO, request.idModelo());
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
                    () -> veiculoService.criar(request, null, ID_VALIDO));
            //Assert
            assertNotFoundResponseError(exception, COMBUSTIVEL, request.idCombustivel());
        }

        @Test
        @DisplayName("Deve lançar exceção ao fazer upload da imagem")
        void deveLancarExcecaoQuandoUploadDaImagem() throws IOException {
            //Arrange

            var request = criarVeiculoValido();
            var imagemsRequest = ImagemHelper.criarListImagemFile();
            var imagem = imagemsRequest[0];

            when(storageService.upload(eq(imagem), anyLong()))
                    .thenThrow(new IOException("Erro ao realizar upload"));
            //Act
            var exception = assertThrows(IOException.class,
                    () -> veiculoService.criar(request, imagemsRequest, ID_VALIDO));
            //Assert
            assertThat(exception)
                    .hasMessage("Erro ao realizar upload");
        }

        @Test
        @DisplayName("Deve validar o placa unica")
        void deveLancarExcecaoQuandoPlacaJaExistir() {
            //Arrange
            criarVeiculoPersistido("FEL123H", DISPONIVEL, null);
            var request = criarVeiculoRequestComPlaca("FEL123H");
            //Act
            var exception = assertThrows(BusinessException.class,
                    () -> veiculoService.criar(request, null, ID_VALIDO));
            //Assert
            assertBusinessResponseError(exception, "A placa informada já possui um cadastro.");
        }
    }
}
