package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioRolesResponse;
import com.javacar.lojadecarro.entity.Role;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.UsuarioRole;
import com.javacar.lojadecarro.factory.usuario.UsuarioRequestFactory;
import com.javacar.lojadecarro.mapper.RoleMapper;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioEntity;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class UsuarioMapperTest extends MapperTest {
    @Autowired
    private UsuarioMapper mapper;

    @Autowired
    private RoleMapper roleMapper;

    @Test
    @DisplayName("Deve converter a requisição para entidade")
    void deveConverterRequestParaEntity() {
        var request = criarUsuarioRequest();

        var entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Usuario::getNome,
                        Usuario::getPassword,
                        Usuario::getCpf,
                        Usuario::getDataNascimento,
                        Usuario::getEmail
                )
                .containsExactly(
                        "Felipe Soares Macário",
                        "123456",
                        "15152736799",
                        LocalDate.of(1991, Month.MAY, 14),
                        "felipesmacario@gmail.com"

                );

    }

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarUsuarioEntity();
        var response = mapper.toResponse(entity);

        assertThat(response)
                .isNotNull()
                .extracting(
                        UsuarioResponse::id,
                        UsuarioResponse::nome,
                        UsuarioResponse::cpf,
                        UsuarioResponse::email,
                        UsuarioResponse::ativo
                )
                .containsExactly(
                        1L,
                        "Felipe Soares Macário",
                        "1234567890",
                        "felipesmacario@gmail.com",
                        true
                );
    }

    @Test
    @DisplayName("Deve atualizar a entidade")
    void deveAtualizarEntity() {
        var request = UsuarioRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .comNome("Cristiano Ronaldo")
                .comEmail("cristianoRonaldo@sim.com")
                .build();

        var entity = criarUsuarioEntity();

        mapper.toUpdate(request, entity);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Usuario::getId,
                        Usuario::getCpf,
                        Usuario::getDataNascimento,
                        Usuario::getEmail,
                        Usuario::getNome,
                        Usuario::getPassword,
                        Usuario::isAtivo
                )
                .containsExactly(
                        entity.getId(),
                        entity.getCpf(),
                        entity.getDataNascimento(),
                        request.email(),
                        request.nome(),
                        entity.getPassword(),
                        entity.isAtivo()
                );
    }

    @Test
    @DisplayName("Deve converter o usuarioRole para uma response")
    void deveConverterUsuarioRoleParaResposta() {
        var usuario = criarUsuarioEntity();
        var role = new Role();
        role.setId(1L);
        role.setNome("ROLE_USER");
        role.setAtivo(true);

        var role2 = new Role();
        role2.setId(2L);
        role2.setNome("ADMIN");
        role2.setAtivo(true);

        var usuarioRole = new UsuarioRole(usuario, role);
        var usuarioRole2 = new UsuarioRole(usuario, role2);

        Set<UsuarioRole> roles = new LinkedHashSet<>();
        roles.add(usuarioRole);
        roles.add(usuarioRole2);

        usuario.setRoles(roles);

        var resultado = mapper.toUsuarioRoleResponse(usuario);

        assertThat(resultado)
                .isNotNull()
                .extracting(
                        UsuarioRolesResponse::id,
                        UsuarioRolesResponse::nome,
                        UsuarioRolesResponse::cpf
                )
                .containsExactly(
                        1L,
                        "Felipe Soares Macário",
                        "1234567890"
                );
        assertThat(resultado.roles())
                .isNotNull()
                .hasSize(2)
                .extracting(
                        RoleResponse::id,
                        RoleResponse::nome,
                        RoleResponse::ativo
                ).containsExactly(
                        tuple(1L, "ROLE_USER", true),
                        tuple(2L, "ADMIN", true)

                );
    }

}
