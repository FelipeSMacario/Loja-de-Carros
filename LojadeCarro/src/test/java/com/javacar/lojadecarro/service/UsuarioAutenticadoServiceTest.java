package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.exception.security.UsuarioNaoVinculadoException;
import com.javacar.lojadecarro.factory.usuario.UsuarioTestContext;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import com.javacar.lojadecarro.security.service.UsuarioAutenticadoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Testes do serviço do usuário autenticado")
class UsuarioAutenticadoServiceTest extends BaseServiceTest {

    private static final String SUBJECT = "keycloak-sub-usuario-1";

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Nested
    @DisplayName("Testes de autenticação")
    class Autenticacao {
        @Test
        @DisplayName("Deve buscar usuário autenticado pelo subject")
        void deveBuscarUsuarioAutenticadoPeloSubject() {
            //Arrange
            var entity = criarUsuarioPadrao();
            when(usuarioRepository.findByIdentityProviderId(SUBJECT))
                    .thenReturn(Optional.of(entity));
            //ACT
            var resultado = usuarioAutenticadoService.buscar(SUBJECT);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .isSameAs(entity);

            verify(usuarioRepository).findByIdentityProviderId(SUBJECT);
            verifyNoMoreInteractions(usuarioRepository);
        }

        @Test
        @DisplayName("Deve retornar o ID do usuário autenticado")
        void deveRetornarIdDoUsuarioAutenticado() {
            //Arrange
            var entity = criarUsuarioPadrao();
            when(usuarioRepository.findByIdentityProviderId(SUBJECT))
                    .thenReturn(Optional.of(entity));
            //ACT
            var resultado = usuarioAutenticadoService.buscarId(SUBJECT);
            //Assert
            assertThat(resultado)
                    .isNotNull();
            assertThat(resultado)
                    .isEqualTo(entity.getId());

            verify(usuarioRepository).findByIdentityProviderId(SUBJECT);
            verifyNoMoreInteractions(usuarioRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção quando o usuário autenticado não estiver vinculado")
        void deveLancarExcecaoQuandoUsuarioAutenticadoNaoEstiverVinculado() {
            //Arrange
            when(usuarioRepository.findByIdentityProviderId(SUBJECT))
                    .thenReturn(Optional.empty());
            //ACT
            var exception = assertThrows(UsuarioNaoVinculadoException.class,
                    () -> usuarioAutenticadoService.buscarId(SUBJECT));
            //Assert
            assertThat(exception)
                    .hasMessage("Usuário autenticado não possui cadastro local.");

            verify(usuarioRepository).findByIdentityProviderId(SUBJECT);
            verifyNoMoreInteractions(usuarioRepository);
        }
    }

    private Usuario criarUsuarioPadrao() {
        var usuario = UsuarioTestContext.criarUsuario(
                ID_VALIDO,
                "Felipe",
                "felipesmacario@gmail.com",
                "12345678901",
                "123456",
                true
        );
        usuario.setIdentityProviderId(SUBJECT);

        return usuario;
    }
}
