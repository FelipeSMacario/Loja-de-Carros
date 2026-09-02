package com.javacar.lojadecarro.controller.publica;

import com.javacar.lojadecarro.controller.BaseControllerTest;
import com.javacar.lojadecarro.controller.publico.UsuarioController;
import com.javacar.lojadecarro.dto.request.AlteracaoSenhaRequest;
import com.javacar.lojadecarro.dto.request.UsuarioUpdateRequest;
import com.javacar.lojadecarro.dto.response.AlteracaoSenhaResponse;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.usuario.UsuarioTestContext;
import com.javacar.lojadecarro.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.*;
import static com.javacar.lojadecarro.factory.usuario.UsuarioTestContext.atualizarUsuarioValido;
import static com.javacar.lojadecarro.support.TestConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest extends BaseControllerTest {
    private static final String URL = "/usuarios";
    private static final String URL_ME = "/usuarios/me";
    private static final String URL_ME_DESATIVAR = URL_ME + "/desativar";
    private static final String URL_ME_SENHA = URL_ME + "/senha";

    @MockitoBean
    private UsuarioService usuarioService;

    @Nested
    @DisplayName("Testes de criação")
    class Criar {
        @Test
        @DisplayName("Deve criar um usuário")
        void deveCriarUsuario() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.criar(cx.request))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performPost(URL, cx.request);
            assertUsuario(
                    resultado,
                    status().isCreated(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "12345678901"
            );
            resultado.andExpect(
                    header().string(
                            "Location",
                            "http://localhost/usuarios/" + ID_VALIDO
                    )
            );

            verify(usuarioService).criar(cx.request);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao criar um usuário")
        void deveRetornar400AoCriarUmUsuario() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();
            //Act + Assert
            var resultado = performPost(URL, cx.requestIncompleto);
            assertStatus400(resultado);

            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 500 ao acontecer um erro inesperado")
        void deveRetornar500aoAcontecerUmErro() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.criar(cx.request))
                    .thenThrow(new RuntimeException("Erro inesperado"));

            //Act + Assert
            var resultado = performPost(URL, cx.request);
            assertStatus500(resultado);

            verify(usuarioService).criar(cx.request);
            verifyNoMoreInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da busca do usuário autenticado")
    class BuscarUsuarioAutenticado {
        @Test
        @DisplayName("Deve buscar o usuário autenticado")
        void deveBuscarUsuarioAutenticado() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();

            when(usuarioService.buscarMeuUsuario(ID_VALIDO))
                    .thenReturn(cx.response);
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ME, ID_JWT, ROLE_USUARIO);

            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "12345678901"
            );

            verify(usuarioService).buscarMeuUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar o usuário autenticado")
        void deveRetornar404AoBuscarUmUsuario() throws Exception {
            //Arrange
            when(usuarioService.buscarMeuUsuario(ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performGetComAutenticacao(URL_ME, ID_JWT, ROLE_USUARIO);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).buscarMeuUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao buscar o usuário não autenticado")
        void deveRetornar401aoBuscarUsuarioNaoAutenticado() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performGet(URL_ME);
            assertStatus401(exception);
            verifyNoInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes de desativar o usuário autenticado")
    class DesativarUsuarioAutenticado {
        @Test
        @DisplayName("Deve desativar o usuário autenticado")
        void deveDesativarUsuarioAutenticado() throws Exception {
            //Arrange
            var response = criarUsuarioPadraoResponseInativo();

            when(usuarioService.desativarUsuario(ID_VALIDO))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_ME_DESATIVAR, ID_JWT, ROLE_USUARIO);
            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    false,
                    "felipesmacario@gmail.com",
                    "12345678901"
            );
            verify(usuarioService).desativarUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao não encontrar o usuário para desativar")
        void deveRetornar404AoDesativarUsuarioNaoEncontrado() throws Exception {
            //Arrange
            when(usuarioService.desativarUsuario(ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_ME_DESATIVAR, ID_JWT, ROLE_USUARIO);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).desativarUsuario(ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao desativar o usuário não autenticado")
        void deveRetornar401AoDesativarUsuarioSemAutenticacao() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performPatch(URL_ME_DESATIVAR);
            assertStatus401(exception);
            verifyNoInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da atualização")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar um usuário")
        void deveAtualizarUsuario() throws Exception {
            //Arrange
            var cx = new UsuarioTestContext();
            var request = atualizarUsuarioValido();

            when(usuarioService.atualizar(
                    request,
                    ID_VALIDO
            )).thenReturn(cx.response);
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ME, request, ID_JWT, ROLE_USUARIO);
            assertUsuario(
                    resultado,
                    status().isOk(),
                    ID_VALIDO,
                    "Felipe",
                    true,
                    "felipesmacario@gmail.com",
                    "12345678901"
            );

            verify(usuarioService).atualizar(request,
                    ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao atualizar usuário com dados inválidos")
        void deveRetornar400aoAtualizarUmUsuarioSemSenha() throws Exception {
            //Arrange
            var request = new UsuarioUpdateRequest(null, null, null);
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ME, request, ID_JWT, ROLE_USUARIO);
            assertStatus400(resultado);

            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 404 ao atualizar um usuário com ID errado")
        void deveRetornar404aoAtualizarUmUsuarioComIDErrado() throws Exception {
            //Arrange
            var request = atualizarUsuarioValido();
            when(usuarioService.atualizar(request,
                    ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performPutComAutenticacao(URL_ME, request, ID_JWT, ROLE_USUARIO);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).atualizar(request,
                    ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao atualizar usuário sem autenticação")
        void deveRetornar401AoAtualizarUsuarioSemAutenticacao() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performPut(URL_ME, atualizarUsuarioValido());

            assertStatus401(exception);
            verifyNoInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("Testes da alteração de senha")
    class AlterarSenha {
        @Test
        @DisplayName("Deve alterar a senha")
        void deveAlterarSenha() throws Exception {
            //Arrange
            var request = new AlteracaoSenhaRequest(
                    "senhaAntiga",
                    "senhaNova"
            );
            var response = new AlteracaoSenhaResponse("felipesmacario@gmail.com", "Senha atualizada com sucesso");

            when(usuarioService.alterarSenha(request, ID_VALIDO))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_ME_SENHA, request, ID_JWT, ROLE_USUARIO);
            assertAlteracaoSenha(resultado, "felipesmacario@gmail.com", "Senha atualizada com sucesso");

            verify(usuarioService).alterarSenha(request, ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);


        }

        @Test
        @DisplayName("Deve retornar 404 ao alterar a senha com dados inválidos")
        void deveRetornar404aoBuscarUsuario() throws Exception {
            //Arrange
            var request = new AlteracaoSenhaRequest(
                    "senhaAntiga",
                    "senhaNova"
            );
            when(usuarioService.alterarSenha(request, ID_VALIDO))
                    .thenThrow(new NotFoundException(USUARIO, ID_VALIDO));
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_ME_SENHA, request, ID_JWT, ROLE_USUARIO);
            assertStatus404(resultado, USUARIO, ID_VALIDO);

            verify(usuarioService).alterarSenha(request, ID_VALIDO);
            verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 400 ao alterar a senha com dados invalídos")
        void deveRetornar400AoAlterarSenhaComDadosInvalidos() throws Exception {
            //Arrange
            var request = new AlteracaoSenhaRequest(
                    null,
                    null
            );
            //Act + Assert
            var resultado = performPatchComAutenticacao(URL_ME_SENHA, request, ID_JWT, ROLE_USUARIO);
            assertStatus400(resultado);

            verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Deve retornar 401 ao alterar a senha sem autenticação")
        void deveRetornar401AoAlterarSenhaSemAutenticacao() throws Exception {
            //Arrange
            //Act + Assert
            var exception = performPatch(URL_ME_SENHA);
            assertStatus401(exception);
            verifyNoInteractions(usuarioService);
        }

    }

    private UsuarioResponse criarUsuarioPadraoResponseInativo() {
        return UsuarioTestContext.criaUsuarioResponse(
                ID_VALIDO,
                "Felipe",
                "felipesmacario@gmail.com",
                "12345678901",
                false
        );
    }
}
