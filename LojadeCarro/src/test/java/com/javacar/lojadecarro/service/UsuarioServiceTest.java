package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.exception.UsuarioException;
import com.javacar.lojadecarro.factory.usuario.UsuarioEntityFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioRequestFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioResponseFactory;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.support.ErrorMessages.ID_NOT_FOUND;
import static com.javacar.lojadecarro.support.ErrorMessages.USUARIO;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private BCryptPasswordEncoder encoder;
    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve cadastrar um usuario")
    void deveCadastrarUmUsuario() {
        //Arrange
        var request = UsuarioRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();

        var entity = UsuarioEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        var response = UsuarioResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();

        when(usuarioMapper.toEntity(request))
                .thenReturn(entity);
        when(encoder.encode(request.password()))
                .thenReturn("senhaCriptografada");
        when(usuarioRepository.save(entity))
                .thenReturn(entity);
        when(usuarioMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = usuarioService.createUsuario(request);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        UsuarioResponse::id,
                        UsuarioResponse::nome,
                        UsuarioResponse::email
                ).containsExactly(
                        ID_VALIDO,
                        "Felipe",
                        "felipesmacario@gmail.com"
                );
        assertThat(entity.getPassword())
                .isNotNull()
                .isEqualTo("senhaCriptografada")
                .isNotEqualTo(request.password());

        verify(usuarioMapper).toEntity(request);
        verify(usuarioMapper).toResponse(entity);
        verify(usuarioRepository).save(entity);
        verify(encoder).encode(request.password());

        verifyNoMoreInteractions(
                usuarioRepository,
                usuarioMapper,
                encoder
        );
    }

    @Test
    @DisplayName("Listar usuários")
    void listarUsuarios() {
        //Arrange
        var felipeEntity = UsuarioEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var cristianoRonaldoEntity = UsuarioEntityFactory
                .criarEntity()
                .comId(2L)
                .comNome("Cristiano Ronaldo")
                .comPassword("Siiiiiiimmmmm")
                .comEmail("cristianoRonaldo@gmail.com")
                .comDataNascimento(LocalDate.of(1970, Month.JANUARY, 1))
                .build();

        var felipeResponse = UsuarioResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        var cristianoRonaldoResponse = UsuarioResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Cristiano Ronaldo")
                .comEmail("cristianoRonaldo@gmail.com")
                .build();

        var entity = List.of(felipeEntity, cristianoRonaldoEntity);

        when(usuarioRepository.findAll())
                .thenReturn(entity);

        when(usuarioMapper.toResponse(felipeEntity))
                .thenReturn(felipeResponse);
        when(usuarioMapper.toResponse(cristianoRonaldoEntity))
                .thenReturn(cristianoRonaldoResponse);
        //Act

        var resultado = usuarioService.listarUsuario();
        //Assert
        assertThat(resultado)
                .isNotNull()
                .hasSize(2)
                .extracting(
                        UsuarioResponse::id,
                        UsuarioResponse::nome,
                        UsuarioResponse::email
                ).containsExactly(
                        tuple(ID_VALIDO, "Felipe", "felipesmacario@gmail.com"),
                        tuple(2L, "Cristiano Ronaldo", "cristianoRonaldo@gmail.com")
                );

        verify(usuarioMapper).toResponse(felipeEntity);
        verify(usuarioMapper).toResponse(cristianoRonaldoEntity);
        verify(usuarioRepository).findAll();

        verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
    }

    @Test
    @DisplayName("Deve buscar o usuário por ID")
    void deveBuscarUmUsuarioPorId() {
        //Arrange
        var entity = UsuarioEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var response = UsuarioResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        when(usuarioRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));

        when(usuarioMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = usuarioService.findUsuarioBId(ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        UsuarioResponse::id,
                        UsuarioResponse::nome,
                        UsuarioResponse::email
                ).containsExactly(
                        ID_VALIDO,
                        "Felipe",
                        "felipesmacario@gmail.com"
                );

        verify(usuarioRepository).findById(ID_VALIDO);
        verify(usuarioMapper).toResponse(entity);
        verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao buscar um usuário por ID")
    void deveLancarUmaExcecaoAoBuscarUmUsuarioPorId() {
        //Arrange
        when(usuarioRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var excecao = assertThrows(UsuarioException.class,
                () -> usuarioService.buscaUsuario(ID_INVALIDO));
        //Assert
        assertThat(excecao)
                .hasMessage(String.format(ID_NOT_FOUND, USUARIO, ID_INVALIDO));

        verify(usuarioRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Deve atualizar o usuário por ID")
    void deveAtualizarUmUsuarioPorId() {
        //Arrange
        var request = UsuarioRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        var entity = UsuarioEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
        var response = UsuarioResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
        when(usuarioRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        when(encoder.encode(request.password()))
                .thenReturn("senhaCriptografada");
        when(usuarioRepository.save(entity))
                .thenReturn(entity);
        when(usuarioMapper.toResponse(entity))
                .thenReturn(response);
        //Act
        var resultado = usuarioService.updateUsuario(request, ID_VALIDO);
        //Assert
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        UsuarioResponse::id,
                        UsuarioResponse::nome,
                        UsuarioResponse::email
                ).containsExactly(
                        ID_VALIDO,
                        "Felipe",
                        "felipesmacario@gmail.com"
                );

        assertThat(entity.getPassword())
                .isNotNull()
                .isEqualTo("senhaCriptografada");
        verify(usuarioRepository).findById(ID_VALIDO);
        verify(encoder).encode(request.password());
        verify(usuarioRepository).save(entity);
        verify(usuarioMapper).toResponse(entity);
        verifyNoMoreInteractions(usuarioRepository, usuarioMapper);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao atualizar o usuário por ID")
    void deveLancarUmaExcecaoAoAtualizarUmUsuarioPorId() {
        //Arrange
        var request = UsuarioRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
        when(usuarioRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var excecao = assertThrows(UsuarioException.class,
                () -> usuarioService.updateUsuario(request, ID_INVALIDO));
        //Assert
        assertThat(excecao)
                .hasMessage(String.format(ID_NOT_FOUND, USUARIO, ID_INVALIDO));

        verify(usuarioRepository).findById(ID_INVALIDO);
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Deve deletar um usuário por ID")
    void deveDeletarUmUsuarioPorId() {
        //Arrange
        var entity = UsuarioEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();

        when(usuarioRepository.findById(ID_VALIDO))
                .thenReturn(Optional.of(entity));
        //Act
        usuarioService.deleteUsuario(ID_VALIDO);
        //Assert
        verify(usuarioRepository).findById(ID_VALIDO);
        verify(usuarioRepository).deleteById(ID_VALIDO);

        verifyNoMoreInteractions(usuarioRepository);

    }

    @Test
    @DisplayName("Deve lançar uma exceção ao deletar um usuário por ID")
    void deveLancarUmaExcecaoAoDeletarUmUsuarioPorId() {
        //Arrange
        when(usuarioRepository.findById(ID_INVALIDO))
                .thenReturn(Optional.empty());
        //Act
        var excecao = assertThrows(UsuarioException.class,
                () -> usuarioService.deleteUsuario(ID_INVALIDO));
        //Assert
        assertThat(excecao)
                .hasMessage(String.format(ID_NOT_FOUND, USUARIO, ID_INVALIDO));
        verify(usuarioRepository).findById(ID_INVALIDO);
        verify(usuarioRepository, never())
                .deleteById(anyLong());
    }
}
