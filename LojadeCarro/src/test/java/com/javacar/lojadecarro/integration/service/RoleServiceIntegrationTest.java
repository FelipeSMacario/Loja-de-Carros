package com.javacar.lojadecarro.integration.service;

import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.entity.EntidadeBase;
import com.javacar.lojadecarro.entity.Role;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.integration.config.AbstractIntegrationTest;
import com.javacar.lojadecarro.repository.RoleRepository;
import com.javacar.lojadecarro.service.RolesService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static com.javacar.lojadecarro.enums.Entidade.ROLE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RoleServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private RolesService rolesService;

    @Autowired
    private RoleRepository roleRepository;

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da listagem de roles")
    class Listar {
        @Test
        @DisplayName("Deve listar marcas ativas")
        void deveListarRolesAtivas() {
            //Arrange
            var request = StatusFiltro.ATIVAS;
            //ACT
            var response = rolesService.listarAdministracao(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(RoleResponse::ativo);
        }

        @Test
        @DisplayName("Deve listar roles inativas")
        void deveListarRolesInativas() {
            //Arrange
            var request = StatusFiltro.INATIVAS;
            //ACT
            var response = rolesService.listarAdministracao(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .allMatch(m -> !m.ativo());
        }

        @Test
        @DisplayName("Deve listar todas as roles")
        void deveListarTodasAsRoles() {
            //Arrange
            var request = StatusFiltro.TODAS;
            //ACT
            var response = rolesService.listarAdministracao(request);
            //Assert
            assertThat(response)
                    .isNotEmpty()
                    .anyMatch(RoleResponse::ativo)
                    .anyMatch(m -> !m.ativo());
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da busca da role")
    class Buscar {
        @Test
        @DisplayName("Deve buscar uma role")
        void deveBuscarRole() {
            //Arrange
            var request = buscaRolePorNome("ROLE_ADMIN");
            //ACT
            var marca = rolesService.buscarPorId(request.getId());
            //Assert
            AssertionsForClassTypes.assertThat(marca)
                    .isNotNull();
            AssertionsForClassTypes.assertThat(marca)
                    .extracting(
                            Role::getId,
                            Role::getNome,
                            Role::isAtivo,
                            Role::getDataCadastro
                    ).doesNotContainNull();
        }

        @Test
        @DisplayName("Deve lançar exeção ao buscar role")
        void deveLancarExcecaoBuscarRole() {
            //Arrange
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> rolesService.buscarPorId(-1L));
            //Assert
            AssertionsForClassTypes.assertThat(exception)
                    .hasMessage(ROLE.naoEncontrada() + -1L);
        }
    }

    @Nested
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Testes da buscar de roles")
    class BuscarRoles {
        @Test
        @DisplayName("Deve listar roles")
        void  deveListarRoles() {
            //Arrange
            var roleAdmin = buscaRolePorNome("ROLE_ADMIN");
            var roleVendedor = buscaRolePorNome("ROLE_USUARIO");

            var request = List.of(roleAdmin.getId(), roleVendedor.getId());
            //ACT
            var response = rolesService.buscaRoles(request);
            //Assert
            assertThat(response)
                    .isNotEmpty();

            assertThat(response)
                    .hasSize(2)
                    .extracting(EntidadeBase::getId)
                    .anyMatch(r -> r.equals(roleAdmin.getId()))
                    .anyMatch(r -> r.equals(roleVendedor.getId()));
        }
        @Test
        @DisplayName("Deve lançar exceção ao buscar role invalida")
        void deveLancarExcecaoBuscarRoleInvalida() {
            //Arrange
            var request = List.of(-1L, 1L);
            //ACT
            var exception = assertThrows(BusinessException.class,
                    () -> rolesService.buscaRoles(request));
            //Assert
            assertThat(exception)
            .hasMessage("Uma ou mais roles informadas não foram encontradas.");
        }
    }

    private Role buscaRolePorNome(String nome) {
        return roleRepository.findByNome(nome).orElseThrow();
    }
}
