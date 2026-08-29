package com.javacar.lojadecarro.integration.repository;

import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class UsuarioRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveBuscarUsuariosAtivos(){
        var usuarios = usuarioRepository.findByAtivo(true);

        assertThat(usuarios)
                .isNotEmpty()
                .allMatch(Usuario::isAtivo);
    }

    @Test
    @Transactional
    void deveBuscarUsuarioComSuasRoles(){
        var usuario = usuarioRepository.findByEmail("felipe.vendedor@gmail.com")
                .orElseThrow();

        var roles = usuario.getRoles();

        assertThat(roles)
                .isNotEmpty()
                .allMatch(ur -> ur.getRole() != null)
                .allSatisfy(ur -> assertThat(ur.getRole().getNome()).isNotBlank());

    }

    @Test
    void deveVerificarExistenciaPorEmail() {
        assertThat(usuarioRepository.existsByEmail("felipe.vendedor@gmail.com"))
                .isTrue();
    }

    @Test
    void deveVerificarExistenciaPorCpf() {
        assertThat(usuarioRepository.existsByCpf("15052036000"))
                .isTrue();
    }


}
